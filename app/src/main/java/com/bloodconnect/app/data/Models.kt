package com.bloodconnect.app.data

import com.google.firebase.firestore.IgnoreExtraProperties

// All fields have default values so Cloud Firestore can deserialize
// documents via toObject(Donor::class.java).

@IgnoreExtraProperties
data class Location(
    val country: String = "Indonesia",
    val province: String = "",
    val city: String = "",
    val district: String = ""
)

@IgnoreExtraProperties
data class Donor(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val bloodType: String = "",
    val available: Boolean = true,
    val lastDonation: String? = null, // ISO format yyyy-MM-dd
    val location: Location = Location(),
    val note: String = ""
)

/** A donor paired with its computed proximity tier (1 = closest .. 5 = farthest, 0 = no region filter). */
data class DonorMatch(
    val donor: Donor,
    val tier: Int
)
