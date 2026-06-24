package com.bloodconnect.app.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bloodconnect.app.ui.AppViewModel
import com.bloodconnect.app.ui.theme.Body
import com.bloodconnect.app.ui.theme.Hairline
import com.bloodconnect.app.ui.theme.Muted
import com.bloodconnect.app.ui.theme.Rausch
import com.bloodconnect.app.ui.theme.RauschSoft
import com.bloodconnect.app.ui.theme.Success
import com.bloodconnect.app.ui.theme.SurfaceSoft

@Composable
fun DashboardScreen(nav: NavController, vm: AppViewModel) {
    val session by vm.session.collectAsState()
    val user = session

    if (user == null) {
        Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
            Spacer(Modifier.height(40.dp))
            Text("Kamu belum masuk", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Text("Masuk untuk mengelola profil donor kamu.", color = Muted)
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = { nav.navigate("login") },
                colors = ButtonDefaults.buttonColors(containerColor = Rausch),
                shape = RoundedCornerShape(12.dp)
            ) { Text("Masuk") }
        }
        return
    }

    val elig = vm.isEligible(user)
    var lastDonation by remember(user.id) { mutableStateOf(user.lastDonation ?: "") }
    var phone by remember(user.id) { mutableStateOf(user.phone) }
    var note by remember(user.id) { mutableStateOf(user.note) }

    Column(
        modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(20.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Halo, ${user.name.split(" ").first()} \uD83D\uDC4B", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            OutlinedButton(onClick = { vm.logout(); nav.navigate("home") { popUpTo("home") } }, shape = RoundedCornerShape(10.dp)) {
                Text("Keluar", color = Body)
            }
        }
        Spacer(Modifier.height(16.dp))

        // Profile card
        Surface(shape = RoundedCornerShape(16.dp), color = Color.White, border = androidx.compose.foundation.BorderStroke(1.dp, Hairline), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(64.dp).clip(CircleShape).background(Rausch)) {
                    Text(initials(user.name), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                }
                Spacer(Modifier.height(10.dp))
                Text(user.name, fontWeight = FontWeight.SemiBold, fontSize = 17.sp)
                Text("${user.location.district}, ${user.location.city}", color = Muted, fontSize = 13.sp)
                Spacer(Modifier.height(10.dp))
                Surface(color = RauschSoft, shape = RoundedCornerShape(999.dp)) {
                    Text("Golongan ${user.bloodType}", color = Rausch, fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp))
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        // Availability
        Surface(color = SurfaceSoft, shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text("Status ketersediaan", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(
                        checked = user.available,
                        onCheckedChange = { vm.saveDonor(user.copy(available = it)) },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Rausch)
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(if (user.available) "Siap donor" else "Tidak tersedia", fontWeight = FontWeight.Medium)
                }
                Spacer(Modifier.height(10.dp))
                val msg = if (elig) "Kamu layak untuk donor saat ini." else "Masa jeda donor: ${vm.nextEligibleDays(user)} hari lagi sebelum layak donor kembali."
                Surface(color = if (elig) Color(0xFFE7F8EE) else RauschSoft, shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth()) {
                    Text(msg, color = if (elig) Success else Rausch, fontSize = 13.sp, modifier = Modifier.padding(12.dp))
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        // Stats
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard("Permintaan cocok", vm.matchCountFor(user).toString(), "butuh ${user.bloodType}", Modifier.weight(1f))
            StatCard("Donor terakhir", if (user.lastDonation != null) "${vm.daysSince(user.lastDonation)} hari" else "-", "lalu", Modifier.weight(1f))
            StatCard("Kelayakan", if (elig) "Layak" else "${vm.nextEligibleDays(user)} hari", "jeda 75 hari", Modifier.weight(1f))
        }

        Spacer(Modifier.height(16.dp))
        // Edit
        Surface(shape = RoundedCornerShape(16.dp), color = Color.White, border = androidx.compose.foundation.BorderStroke(1.dp, Hairline), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text("Perbarui profil", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(value = lastDonation, onValueChange = { lastDonation = it }, label = { Text("Tanggal donor terakhir (yyyy-MM-dd)") }, singleLine = true, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp))
                OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("No. HP / WhatsApp") }, singleLine = true, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp))
                OutlinedTextField(value = note, onValueChange = { note = it }, label = { Text("Catatan") }, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp))
                Button(
                    onClick = { vm.saveDonor(user.copy(lastDonation = lastDonation.ifBlank { null }, phone = phone, note = note)) },
                    colors = ButtonDefaults.buttonColors(containerColor = Rausch),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) { Text("Simpan perubahan", fontWeight = FontWeight.SemiBold) }
            }
        }
        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun StatCard(label: String, value: String, sub: String, modifier: Modifier = Modifier) {
    Surface(color = SurfaceSoft, shape = RoundedCornerShape(14.dp), modifier = modifier) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(label, color = Muted, fontSize = 12.sp)
            Spacer(Modifier.height(6.dp))
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(sub, color = Muted, fontSize = 11.sp)
        }
    }
}

private fun initials(name: String): String =
    name.split(" ").take(2).mapNotNull { it.firstOrNull()?.toString() }.joinToString("").uppercase()
