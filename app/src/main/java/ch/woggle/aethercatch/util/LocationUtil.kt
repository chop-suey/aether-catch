package ch.woggle.aethercatch.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager

fun hasFineLocationPermission(context: Context) = context
    .checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

fun isLocationEnabled(context: Context): Boolean {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return locationManager.isLocationEnabled
}
