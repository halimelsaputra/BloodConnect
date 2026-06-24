package com.bloodconnect.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bloodconnect.app.data.Regions
import com.bloodconnect.app.ui.AppViewModel
import com.bloodconnect.app.ui.DonorCard
import com.bloodconnect.app.ui.Dropdown
import com.bloodconnect.app.ui.SearchFilter
import com.bloodconnect.app.ui.theme.Body
import com.bloodconnect.app.ui.theme.Muted
import com.bloodconnect.app.ui.theme.RauschSoft

private const val ALL_TYPES = "Semua golongan"

@Composable
fun SearchScreen(nav: NavController, vm: AppViewModel) {
    val filter by vm.filter.collectAsState()
    val donors by vm.donors.collectAsState()
    val results = remember(filter, donors) { vm.results(filter) }
    val showTier = filter.province.isNotBlank()

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        Spacer(Modifier.height(16.dp))
        Text("Cari Donor", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text(
            "${results.size} pendonor ditemukan" +
                if (filter.recipientType.isNotBlank()) " \u00B7 kompatibel untuk ${filter.recipientType}" else "",
            color = Muted, fontSize = 14.sp
        )
        Spacer(Modifier.height(14.dp))

        // ----- Filters -----
        Dropdown(
            label = "Golongan darah dibutuhkan",
            options = listOf(ALL_TYPES) + Regions.BLOOD_TYPES,
            selected = if (filter.recipientType.isBlank()) ALL_TYPES else filter.recipientType,
            onSelect = { v -> vm.setFilter(filter.copy(recipientType = if (v == ALL_TYPES) "" else v)) }
        )
        Spacer(Modifier.height(10.dp))
        Dropdown(
            label = "Provinsi",
            options = listOf("") + Regions.provinces(),
            selected = filter.province,
            placeholder = "Semua provinsi",
            onSelect = { v -> vm.setFilter(filter.copy(province = v, city = "", district = "")) }
        )
        Spacer(Modifier.height(10.dp))
        Dropdown(
            label = "Kota/Kabupaten",
            options = listOf("") + Regions.cities(filter.province),
            selected = filter.city,
            placeholder = "Semua kota",
            onSelect = { v -> vm.setFilter(filter.copy(city = v, district = "")) }
        )
        Spacer(Modifier.height(10.dp))
        Dropdown(
            label = "Kecamatan",
            options = listOf("") + Regions.districts(filter.province, filter.city),
            selected = filter.district,
            placeholder = "Semua kecamatan",
            onSelect = { v -> vm.setFilter(filter.copy(district = v)) }
        )
        Spacer(Modifier.height(10.dp))
        Button(
            onClick = { vm.setFilter(SearchFilter()) },
            colors = ButtonDefaults.outlinedButtonColors(),
            shape = RoundedCornerShape(12.dp)
        ) { Text("Reset filter", color = Body) }

        if (filter.recipientType.isNotBlank()) {
            Spacer(Modifier.height(12.dp))
            Surface(color = RauschSoft, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Menampilkan donor dengan golongan darah yang kompatibel untuk penerima ${filter.recipientType}, diurutkan dari wilayah terdekat.",
                    fontSize = 13.sp, color = Body, modifier = Modifier.padding(12.dp)
                )
            }
        }

        Spacer(Modifier.height(16.dp))
        if (results.isEmpty()) {
            Text("Belum ada donor yang cocok. Coba longgarkan filter golongan darah atau perluas wilayah.", color = Muted, fontSize = 14.sp)
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                items(results) { match ->
                    val d = match.donor
                    val elig = vm.isEligible(d)
                    val positive = d.available && elig
                    DonorCard(
                        donor = d,
                        statusText = if (!d.available) "Tidak tersedia" else if (elig) "Siap donor" else "Jeda ${vm.nextEligibleDays(d)} hari",
                        statusPositive = positive,
                        tierLabel = if (showTier) vm.tierLabel(match.tier) else null,
                        onClick = { nav.navigate("donor/${d.id}") }
                    )
                }
                item { Spacer(Modifier.height(20.dp)) }
            }
        }
    }
}
