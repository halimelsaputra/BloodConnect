package com.bloodconnect.app.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bloodconnect.app.data.Regions
import com.bloodconnect.app.ui.AppViewModel
import com.bloodconnect.app.ui.Dropdown
import com.bloodconnect.app.ui.theme.Muted
import com.bloodconnect.app.ui.theme.Rausch

@Composable
fun RegisterScreen(nav: NavController, vm: AppViewModel) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var bloodType by remember { mutableStateOf("") }
    var lastDonation by remember { mutableStateOf("") }
    var province by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var district by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(20.dp)
    ) {
        Text("Daftar sebagai Pendonor", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
        Text("Isi data dirimu agar bisa ditemukan pencari darah.", color = Muted, fontSize = 14.sp)
        Spacer(Modifier.height(16.dp))

        error?.let {
            Surface(color = androidx.compose.ui.graphics.Color(0xFFFDECEA), shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth()) {
                Text(it, color = androidx.compose.ui.graphics.Color(0xFFC13515), fontSize = 13.sp, modifier = Modifier.padding(12.dp))
            }
            Spacer(Modifier.height(12.dp))
        }

        Field("Nama lengkap", name) { name = it }
        Field("Email", email, KeyboardType.Email) { email = it }
        Field("No. HP / WhatsApp", phone, KeyboardType.Phone) { phone = it }
        Dropdown("Golongan darah", Regions.BLOOD_TYPES, bloodType, { bloodType = it }, Modifier.fillMaxWidth(), "Pilih golongan")
        Spacer(Modifier.height(10.dp))
        Field("Donor terakhir (yyyy-MM-dd, opsional)", lastDonation) { lastDonation = it }
        Dropdown("Provinsi", Regions.provinces(), province, { province = it; city = ""; district = "" }, Modifier.fillMaxWidth(), "Pilih provinsi")
        Spacer(Modifier.height(10.dp))
        Dropdown("Kota/Kabupaten", Regions.cities(province), city, { city = it; district = "" }, Modifier.fillMaxWidth(), "Pilih kota")
        Spacer(Modifier.height(10.dp))
        Dropdown("Kecamatan", Regions.districts(province, city), district, { district = it }, Modifier.fillMaxWidth(), "Pilih kecamatan")
        Spacer(Modifier.height(10.dp))
        Field("Password", password, KeyboardType.Password, isPassword = true) { password = it }
        Field("Catatan (opsional)", note) { note = it }

        Spacer(Modifier.height(8.dp))
        Button(
            onClick = {
                val v = validate(name, email, phone, password, bloodType, province, city, district)
                if (v != null) {
                    error = v
                } else {
                    error = null
                    loading = true
                    vm.register(
                        name, email, phone, password, bloodType, lastDonation, province, city, district, note,
                        onError = { loading = false; error = it },
                        onSuccess = { loading = false; nav.navigate("dashboard") { popUpTo("home") } }
                    )
                }
            },
            enabled = !loading,
            colors = ButtonDefaults.buttonColors(containerColor = Rausch),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) { Text(if (loading) "Mendaftarkan..." else "Daftar & Mulai", fontWeight = FontWeight.SemiBold, fontSize = 16.sp) }

        TextButton(onClick = { nav.navigate("login") }, modifier = Modifier.fillMaxWidth()) {
            Text("Sudah punya akun? Masuk di sini", color = Rausch)
        }
        Spacer(Modifier.height(20.dp))
    }
}

private fun validate(
    name: String, email: String, phone: String, password: String,
    bloodType: String, province: String, city: String, district: String
): String? {
    if (name.isBlank() || email.isBlank() || phone.isBlank()) return "Nama, email, dan no. HP wajib diisi."
    if (!email.contains("@")) return "Format email tidak valid."
    if (password.length < 6) return "Password minimal 6 karakter."
    if (bloodType.isBlank()) return "Pilih golongan darah."
    if (province.isBlank() || city.isBlank() || district.isBlank()) return "Lengkapi wilayah (provinsi, kota, kecamatan)."
    return null
}

@Composable
private fun Field(
    label: String,
    value: String,
    keyboard: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    onChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboard),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)
    )
}
