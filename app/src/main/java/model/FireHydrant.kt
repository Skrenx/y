package com.example.hydrant.model

import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.PropertyName

@IgnoreExtraProperties
data class FireHydrant(
    @get:PropertyName("Id") @set:PropertyName("Id")
    var id: String = "",

    @get:PropertyName("HydrantName") @set:PropertyName("HydrantName")
    var hydrantName: String = "",

    @get:PropertyName("ExactLocation") @set:PropertyName("ExactLocation")
    var exactLocation: String = "",

    @get:PropertyName("Latitude") @set:PropertyName("Latitude")
    var latitude: String = "",

    @get:PropertyName("Longitude") @set:PropertyName("Longitude")
    var longitude: String = "",

    @get:PropertyName("TypeColor") @set:PropertyName("TypeColor")
    var typeColor: String = "",

    @get:PropertyName("ServiceStatus") @set:PropertyName("ServiceStatus")
    var serviceStatus: String = "In Service",

    @get:PropertyName("Remarks") @set:PropertyName("Remarks")
    var remarks: String = "",

    @get:PropertyName("Municipality") @set:PropertyName("Municipality")
    var municipality: String = "",

    @get:PropertyName("Timestamp") @set:PropertyName("Timestamp")
    var timestamp: Long = System.currentTimeMillis()
) {
    // No-argument constructor required for Firebase
    constructor() : this("", "", "", "", "", "", "In Service", "", "", 0L)
}