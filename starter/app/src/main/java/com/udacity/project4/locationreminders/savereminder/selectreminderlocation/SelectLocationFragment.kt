package com.udacity.project4.locationreminders.savereminder.selectreminderlocation

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.databinding.FragmentSelectLocationBinding
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.utils.LocationUtils
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import org.koin.android.ext.android.inject

@Suppress("DEPRECATED_IDENTITY_EQUALS", "DEPRECATION")
class SelectLocationFragment : BaseFragment(), OnMapReadyCallback {

    //Use Koin to get the view model of the SaveReminder
    //private val TAG = SelectLocationFragment::class.java.simpleName

    override val _viewModel: SaveReminderViewModel by inject()
    private val locationUtils: LocationUtils by inject()
    private lateinit var binding: FragmentSelectLocationBinding
    private lateinit var map: GoogleMap
    private val REQUEST_LOCATION_PERMISSION = 1
    //private var poi: PointOfInterest? = null
  // private lateinit var fusedLocationClient: FusedLocationProviderClient
    //private val zoomLevel = 15f



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_select_location, container, false)

        binding.viewModel = _viewModel
        binding.lifecycleOwner = this

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


//        map = googleMap
//
//        //These coordinates represent the default location, which is my company, Critical Techworks
//        val latitude = 41.15003800241415
//        val longitude = -8.609281195141374
//        val zoomLevel = 15f
//
//        val ctw = LatLng(latitude, longitude)
//        map.moveCamera(CameraUpdateFactory.newLatLngZoom(ctw, zoomLevel))
//        map.addMarker(MarkerOptions().position(ctw))
////
//        //Set selected poi marker if exists
//        _viewModel.selectedPOI.value?.let { poi ->
//            val poiMarker = map.addMarker(
//                    MarkerOptions()
//                            .position(poi.latLng)
//                            .title(poi.name)
//            )
//            poiMarker.showInfoWindow()
//        }
//
//      setMapLongClick(map)
//        setPoiClick(map)
        setMapStyle(map)
        enableMyLocation()


    }

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<String>,
//        grantResults: IntArray) {
//        // Check if location permissions are granted and if so enable the
//        // location data layer.
//        if (requestCode == REQUEST_LOCATION_PERMISSION) {
//            if (grantResults.size > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
//                enableMyLocation()
//            } else {
//                // Permission denied.
//                Snackbar.make(
//                    requireView(),
//                    R.string.permission_denied_explanation, Snackbar.LENGTH_LONG
//                )
//                    .setAction(R.string.settings) {
//                        // Displays App settings screen.
//                        startActivity(Intent().apply {
//                            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
//                            data = Uri.fromParts("package", requireActivity().packageName, null)
//                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                        })
//
//                    }.show()
//            }
//        }
//    }
//
//    private fun onLocationSelected() {
//        _viewModel.showToast.postValue("Point of interest selected")
//        _viewModel.selectedPOI.postValue(poi)
//        _viewModel.latitude.postValue(poi?.latLng?.latitude)
//        _viewModel.longitude.postValue(poi?.latLng?.longitude)
//        _viewModel.reminderSelectedLocationStr.postValue(poi?.name)
//        _viewModel.navigationCommand.postValue(
//            NavigationCommand.Back
//        )
//    }

//    private fun setMapLongClick(map: GoogleMap) {
//        map.setOnMapLongClickListener { latLng ->
//            map.clear()
//            // A Snippet is Additional text that's displayed below the title.
//            val snippet = String.format(
//                Locale.getDefault(),
//                "Lat: %1$.5f, Long: %2$.5f",
//                latLng.latitude,
//                latLng.longitude
//            )
//
//
//            map.addMarker(
//                MarkerOptions()
//                    .position(latLng)
//                    .title(getString(R.string.dropped_pin))
//                    .snippet(snippet)
//                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
//
//            )
//            //var completeAddress = latLng.latitude.toString() + ", " + latLng.longitude.toString()
//            val location = PointOfInterest(latLng, UUID.randomUUID().toString(), "Custom POI")
//            this.poi = location
//
//
//            binding.saveButton.isEnabled = true
//        }
//    }

//    private fun setPoiClick(map: GoogleMap) {
//        map.setOnPoiClickListener { poi ->
//            map.clear()
//            this.poi = poi
//            val poiMarker = map.addMarker(
//                MarkerOptions()
//                    .position(poi.latLng)
//                    .title(poi.name)
//            )
//            poiMarker.showInfoWindow()
//
//            binding.saveButton.isEnabled = true
//        }
//    }

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
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        if (isPermissionGranted()) {
            map.isMyLocationEnabled = true
            val location = locationUtils.getBestLocation()
            val homeLatLng = LatLng(
                location?.latitude ?: Constants.DEFAULT_LAT,
                location?.longitude ?: Constants.DEFAULT_LNG
            )
            val zoom = 15f
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(homeLatLng, zoom))
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }
object Constants{
    const val DEFAULT_LAT = 37.422
    const val DEFAULT_LNG = -122.08
}
}

