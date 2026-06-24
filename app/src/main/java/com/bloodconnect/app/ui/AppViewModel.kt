package com.bloodconnect.app.ui

import androidx.lifecycle.ViewModel
import com.bloodconnect.app.data.Donor
import com.bloodconnect.app.data.DonorMatch
import com.bloodconnect.app.data.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class SearchFilter(
    val recipientType: String = "",
    val province: String = "",
    val city: String = "",
    val district: String = ""
)

/** Activity-scoped ViewModel exposing repository state and domain actions to the UI (MVVM). */
class AppViewModel : ViewModel() {

    val donors: StateFlow<List<Donor>> = Repository.donors
    val session: StateFlow<Donor?> = Repository.session

    private val _filter = MutableStateFlow(SearchFilter())
    val filter: StateFlow<SearchFilter> = _filter

    /** Start Firebase realtime sync (safe to call multiple times). */
    fun start() = Repository.start()

    fun setFilter(filter: SearchFilter) { _filter.value = filter }

    fun availableDonors(limit: Int): List<Donor> =
        Repository.search(null, null, null, null, null, onlyAvailable = true, excludeId = null)
            .map { it.donor }
            .take(limit)

    fun results(filter: SearchFilter): List<DonorMatch> = Repository.search(
        recipientType = filter.recipientType.ifBlank { null },
        bloodType = null,
        province = filter.province.ifBlank { null },
        city = filter.city.ifBlank { null },
        district = filter.district.ifBlank { null },
        onlyAvailable = false,
        excludeId = null
    )

    fun getDonor(id: String): Donor? = Repository.getDonor(id)

    fun projectId() = Repository.projectId()
    fun testServer(onResult: (String) -> Unit) = Repository.testServer(onResult)

    fun register(
        name: String, email: String, phone: String, password: String, bloodType: String,
        lastDonation: String?, province: String, city: String, district: String, note: String,
        onError: (String) -> Unit, onSuccess: () -> Unit
    ) = Repository.register(name, email, phone, password, bloodType, lastDonation, province, city, district, note, onError, onSuccess)

    fun login(email: String, password: String, onError: (String) -> Unit, onSuccess: () -> Unit) =
        Repository.login(email, password, onError, onSuccess)

    fun logout() = Repository.logout()

    fun saveDonor(donor: Donor) = Repository.saveDonor(donor)

    fun isEligible(donor: Donor) = Repository.isEligible(donor)
    fun nextEligibleDays(donor: Donor) = Repository.nextEligibleDays(donor)
    fun daysSince(date: String?) = Repository.daysSince(date)
    fun tierLabel(tier: Int) = Repository.tierLabel(tier)
    fun matchCountFor(donor: Donor): Int =
        Repository.search(donor.bloodType, null, null, null, null, onlyAvailable = false, excludeId = donor.id).size
}
