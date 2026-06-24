package com.bloodconnect.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
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
import com.bloodconnect.app.ui.DonorCard
import com.bloodconnect.app.ui.SectionTitle
import com.bloodconnect.app.ui.theme.Body
import com.bloodconnect.app.ui.theme.Ink
import com.bloodconnect.app.ui.theme.Muted
import com.bloodconnect.app.ui.theme.Rausch
import com.bloodconnect.app.ui.theme.RauschSoft
import com.bloodconnect.app.ui.theme.SurfaceSoft

@Composable
fun HomeScreen(nav: NavController, vm: AppViewModel) {
    // Recompose when donor list changes.
    val donors by vm.donors.collectAsState()
    val session by vm.session.collectAsState()
    val available = remember(donors) { vm.availableDonors(6) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Button(
            onClick = { nav.navigate("search") },
            colors = ButtonDefaults.buttonColors(containerColor = Rausch),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) { Text("Cari Donor Sekarang", fontWeight = FontWeight.SemiBold, fontSize = 16.sp) }
        if (session == null) {
            Spacer(Modifier.height(10.dp))
            OutlinedButton(
                onClick = { nav.navigate("register") },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) { Text("Jadi Pendonor", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = Ink) }
        }

        Spacer(Modifier.height(30.dp))
        SectionTitle("Pendonor tersedia", "Mereka yang siap membantu saat ini")
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            available.forEach { d ->
                val elig = vm.isEligible(d)
                DonorCard(
                    donor = d,
                    statusText = if (elig) "Siap donor" else "Jeda ${vm.nextEligibleDays(d)} hari",
                    statusPositive = elig,
                    tierLabel = null,
                    onClick = { nav.navigate("donor/${d.id}") }
                )
            }
        }

        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun StepRow(num: Int, title: String, body: String) {
    Surface(color = SurfaceSoft, shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp)) {
            Surface(color = RauschSoft, shape = CircleShape, modifier = Modifier.size(34.dp)) {
                androidx.compose.foundation.layout.Box(contentAlignment = Alignment.Center) {
                    Text(num.toString(), color = Rausch, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.width(14.dp))
            Column {
                Text(title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                Spacer(Modifier.height(4.dp))
                Text(body, color = Muted, fontSize = 13.sp, lineHeight = 19.sp)
            }
        }
    }
}
