package com.udacity.project4.utils

import com.google.android.gms.maps.model.LatLng
import com.udacity.project4.R
import java.util.concurrent.TimeUnit

val REQUEST_LOCATION_PERMISSION = 0
val REQUEST_LOCATION_AND_BACKGROUND_LOCATION_PERMISSION = 1
val REQUEST_TURN_DEVICE_LOCATION_ON = 29
val ACTION_GEOFENCE_EVENT = "project4.action.ACTION_GEOFENCE_EVENT"


internal object GeofencingConstants {
    val GEOFENCE_EXPIRATION_IN_MILLISECONDS: Long = TimeUnit.HOURS.toMillis(1)
    const val GEOFENCE_RADIUS_IN_METERS = 100f
    const val EXTRA_GEOFENCE_INDEX = "GEOFENCE_INDEX"
}
