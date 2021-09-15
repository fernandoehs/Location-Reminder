package com.udacity.project4.locationreminders.savereminder.selectreminderlocation

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.udacity.project4.BuildConfig
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.databinding.FragmentSelectLocationBinding
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.locationreminders.savereminder.selectreminderlocation.SelectLocationFragment.Constants.ZOOM
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import org.koin.android.ext.android.inject



class SelectLocationFragment : BaseFragment(), OnMapReadyCallback {

    //Use Koin to get the view model of the SaveReminder

    override val _viewModel: SaveReminderViewModel by inject()
    //private val locationUtils: LocationUtils by inject()
    private lateinit var binding: FragmentSelectLocationBinding
    private lateinit var map: GoogleMap
    private val REQUEST_LOCATION_PERMISSION = 1
    private var permSnackbar: Snackbar? = null
    private val runningQOrLater = android.os.Build.VERSION.SDK_INT >=
            android.os.Build.VERSION_CODES.Q

    private lateinit var fusedLocationClient: FusedLocationProviderClient


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_select_location, container, false)

        binding.viewModel = _viewModel
        binding.lifecycleOwner = this
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        binding.saveButton.setOnClickListener {

            if (_viewModel.latLng.value != null){
                findNavController().popBackStack()
            }else{
                _viewModel.showSnackBar.value = getString(R.string.select_location_err)
            }


        }
        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(true)

        val mapFragment =
                childFragmentManager.findFragmentById(R.id.googleMapSupport) as SupportMapFragment
        mapFragment.getMapAsync(this)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.normal_map -> {
            map.mapType = GoogleMap.MAP_TYPE_NORMAL
            true
        }
        R.id.hybrid_map -> {
            map.mapType = GoogleMap.MAP_TYPE_HYBRID
            true
        }
        R.id.satellite_map -> {
            map.mapType = GoogleMap.MAP_TYPE_SATELLITE
            true
        }
        R.id.terrain_map -> {
            map.mapType = GoogleMap.MAP_TYPE_TERRAIN
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        val homeLatLng = LatLng(
                Constants.DEFAULT_LAT,
                Constants.DEFAULT_LNG)

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(homeLatLng, ZOOM))
        googleMap.let {
            map = it
            setMapStyle(map)

            map.setOnMapClickListener { latlng ->
                map.clear()
                val marker = MarkerOptions()
                    .position(latlng)
                map.addMarker(marker)
                _viewModel.locationSelected(latlng)
            }
            map.setOnPoiClickListener { poi ->
                map.clear()
                val marker = MarkerOptions()
                    .position(poi.latLng)
                map.addMarker(marker)
                _viewModel.poiSelected(poi)
            }
        }
        enableMyLocation()
    }


    private fun setMapStyle(map: GoogleMap) {
        try {
            map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    requireContext(),
                    R.raw.map_style
                )
            )
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    private fun isPermissionGranted() : Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION) === PackageManager.PERMISSION_GRANTED
    }

override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray) {
    if (requestCode == REQUEST_LOCATION_PERMISSION) {
        if (grantResults.isNotEmpty() && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            enableMyLocation()
        }else{
            permSnackbar = Snackbar.make(
            binding.root,
            R.string.permission_denied_explanation,
            Snackbar.LENGTH_LONG
        ) .setAction(R.string.settings){
                startActivity(Intent().apply {
                    action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                })
            }
        permSnackbar!!.show()
        }
    }
}


    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
    //if (isPermissionGranted()) {
        if (
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            //map.setMyLocationEnabled(true)
            map.isMyLocationEnabled = true
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                        map.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(
                                    location.latitude,
                                    location.longitude
                                ),
                                ZOOM
                            )
                        )

                    }
                }
            return
      //  }

    }else {
        requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION)
    }
}


    object Constants{
    const val DEFAULT_LAT = 37.422
    const val DEFAULT_LNG = -122.08
    const val ZOOM = 15f

}
}

