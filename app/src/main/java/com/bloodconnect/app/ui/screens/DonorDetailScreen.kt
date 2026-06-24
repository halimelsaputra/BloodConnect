package com.bloodconnect.app.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bloodconnect.app.data.Donor
import com.bloodconnect.app.ui.AppViewModel
import com.bloodconnect.app.ui.BloodBadge
import com.bloodconnect.app.ui.StatusPill
import com.bloodconnect.app.ui.theme.Body
import com.bloodconnect.app.ui.theme.Hairline
import com.bloodconnect.app.ui.theme.Muted
import com.bloodconnect.app.ui.theme.Rausch
import com.bloodconnect.app.ui.theme.RauschSoft
import com.bloodconnect.app.ui.theme.SurfaceSoft

@Composable
fun DonorDetailScreen(nav: NavController, vm: AppViewModel, donorId: String) {
    val donor = remember(donorId) { vm.getDonor(donorId) }
    val context = LocalContext.current
    var revealed by remember { mutableStateOf(false) }

    if (donor == null) {
        Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
            Text("Donor tidak ditemukan.", fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(12.dp))
            Button(onClick = { nav.popBackStack() }) { Text("Kembali") }
        }
        return
    }

    val elig = vm.isEligible(donor)
    val canContact = donor.available && elig

    Column(
        modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(20.dp)
    ) {
        OutlinedButton(onClick = { nav.popBackStack() }, shape = RoundedCornerShape(10.dp)) {
            Text("\u2190 Kembali", color = Body)
        }
        Spacer(Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(64.dp).clip(CircleShape).background(RauschSoft)
            ) { Text(donor.bloodType, color = Rausch, fontWeight = FontWeight.Bold, fontSize = 18.sp) }
            Spacer(Modifier.width(14.dp))
            Column {
                Text(donor.name, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.LocationOn, null, tint = Muted, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(3.dp))
                    Text("${donor.location.district}, ${donor.location.city}, ${donor.location.province}", color = Muted, fontSize = 13.sp)
                }
            }
        }

        Spacer(Modifier.height(20.dp))
        FactRow(Icons.Filled.CalendarMonth, "Donor terakhir", formatDate(donor.lastDonation))
        FactRow(Icons.Filled.LocationOn, "Wilayah", "${donor.location.district}, ${donor.location.city}")
        Row(modifier = Modifier.padding(vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            BloodBadge(donor.bloodType)
            Spacer(Modifier.width(10.dp))
            StatusPill(
                if (!donor.available) "Tidak tersedia" else if (elig) "Siap donor" else "Jeda ${vm.nextEligibleDays(donor)} hari",
                canContact
            )
        }
        if (donor.note.isNotBlank()) {
            Surface(color = SurfaceSoft, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                Text(donor.note, fontSize = 14.sp, color = Body, modifier = Modifier.padding(14.dp))
            }
        }

        Spacer(Modifier.height(20.dp))
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = androidx.compose.ui.graphics.Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, Hairline),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text("Hubungi pendonor", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(Modifier.height(6.dp))
                Text(
                    "Demi privasi, kontak hanya ditampilkan setelah kamu mengajukan permintaan menghubungi.",
                    color = Body, fontSize = 13.sp, lineHeight = 19.sp
                )
                Spacer(Modifier.height(14.dp))
                if (!revealed) {
                    Button(
                        onClick = { revealed = true },
                        enabled = canContact,
                        colors = ButtonDefaults.buttonColors(containerColor = Rausch),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) { Text("Minta Kontak Donor", fontWeight = FontWeight.SemiBold) }
                    if (!canContact) {
                        Spacer(Modifier.height(8.dp))
                        Text("Donor sedang tidak tersedia.", color = Muted, fontSize = 13.sp)
                    }
                } else {
                    ContactLine(Icons.Filled.Phone, donor.phone)
                    Spacer(Modifier.height(8.dp))
                    ContactLine(Icons.Filled.Email, donor.email)
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = {
                            val wa = "https://wa.me/62" + donor.phone.removePrefix("0")
                            runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(wa))) }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Rausch),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) { Text("Chat via WhatsApp", fontWeight = FontWeight.SemiBold) }
                    Spacer(Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = {
                            runCatching { context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + donor.phone))) }
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) { Text("Telepon", color = Body, fontWeight = FontWeight.SemiBold) }
                }
            }
        }
        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun FactRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = Rausch, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(12.dp))
        Column {
            Text(label, color = Muted, fontSize = 12.sp)
            Text(value, fontSize = 15.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun ContactLine(icon: androidx.compose.ui.graphics.vector.ImageVector, value: String) {
    Surface(color = SurfaceSoft, shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = Rausch, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(10.dp))
            Text(value, fontSize = 15.sp, fontWeight = FontWeight.Medium)
        }
    }
}

private fun formatDate(iso: String?): String {
    if (iso.isNullOrBlank()) return "Belum pernah"
    return iso
}
