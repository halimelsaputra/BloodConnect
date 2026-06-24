package com.bloodconnect.app.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bloodconnect.app.ui.AppViewModel
import com.bloodconnect.app.ui.theme.Body
import com.bloodconnect.app.ui.theme.Muted
import com.bloodconnect.app.ui.theme.Rausch
import com.bloodconnect.app.ui.theme.RauschSoft

@Composable
fun LoginScreen(nav: NavController, vm: AppViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
        Spacer(Modifier.height(20.dp))
        Text("Masuk ke BloodConnect", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))

        error?.let {
            Surface(color = Color(0xFFFDECEA), shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth()) {
                Text(it, color = Color(0xFFC13515), fontSize = 13.sp, modifier = Modifier.padding(12.dp))
            }
            Spacer(Modifier.height(12.dp))
        }

        OutlinedTextField(
            value = email, onValueChange = { email = it }, label = { Text("Email") },
            singleLine = true, shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)
        )
        OutlinedTextField(
            value = password, onValueChange = { password = it }, label = { Text("Password") },
            singleLine = true, shape = RoundedCornerShape(12.dp),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp)
        )
        Button(
            onClick = {
                error = null
                loading = true
                vm.login(
                    email.trim(), password,
                    onError = { loading = false; error = it },
                    onSuccess = { loading = false; nav.navigate("dashboard") { popUpTo("home") } }
                )
            },
            enabled = !loading,
            colors = ButtonDefaults.buttonColors(containerColor = Rausch),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) { Text(if (loading) "Memproses..." else "Masuk", fontWeight = FontWeight.SemiBold, fontSize = 16.sp) }

        Spacer(Modifier.height(8.dp))
        TextButton(onClick = { nav.navigate("register") }, modifier = Modifier.fillMaxWidth()) {
            Text(
                buildAnnotatedString {
                    append("Belum punya akun? ")
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold, textDecoration = TextDecoration.Underline)) {
                        append("Daftar")
                    }
                },
                color = Rausch
            )
        }
    }
}
