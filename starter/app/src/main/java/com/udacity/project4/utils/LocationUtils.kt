package com.udacity.project4.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager

class LocationUtils(private val context: Context){
    @SuppressLint("MissingPermission")
    fun getBestLocation(): Location? {
        val mLocationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
        val providers = mLocationManager!!.getProviders(true)
        var bestLocation: Location? = null
        for (provider in providers) {
            val l = mLocationManager.getLastKnownLocation(provider) ?: continue
            if (bestLocation == null || l.accuracy < bestLocation.accuracy) { // Found best last known location: %s", l);
                bestLocation = l
            }
        }
        return bestLocation
    }
}