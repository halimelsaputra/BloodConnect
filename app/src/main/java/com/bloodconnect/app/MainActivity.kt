package com.bloodconnect.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavController
import com.bloodconnect.app.ui.AppViewModel
import com.bloodconnect.app.ui.screens.DashboardScreen
import com.bloodconnect.app.ui.screens.DonorDetailScreen
import com.bloodconnect.app.ui.screens.HomeScreen
import com.bloodconnect.app.ui.screens.LoginScreen
import com.bloodconnect.app.ui.screens.RegisterScreen
import com.bloodconnect.app.ui.screens.SearchScreen
import com.bloodconnect.app.ui.theme.BloodConnectTheme
import com.bloodconnect.app.ui.theme.Rausch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BloodConnectTheme {
                val nav = rememberNavController()
                val vm: AppViewModel = viewModel()
                LaunchedEffect(Unit) { vm.start() }
                Scaffold(
                    topBar = { TopBar(nav, vm) }
                ) { padding ->
                    NavHost(
                        navController = nav,
                        startDestination = "home",
                        modifier = Modifier.padding(padding)
                    ) {
                        composable("home") { HomeScreen(nav, vm) }
                        composable("search") { SearchScreen(nav, vm) }
                        composable("register") { RegisterScreen(nav, vm) }
                        composable("login") { LoginScreen(nav, vm) }
                        composable("dashboard") { DashboardScreen(nav, vm) }
                        composable("donor/{id}") { backStackEntry ->
                            DonorDetailScreen(nav, vm, backStackEntry.arguments?.getString("id") ?: "")
                        }
                    }
                }
            }
        }
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(nav: NavController, vm: AppViewModel) {
    val session by vm.session.collectAsState()
    TopAppBar(
        title = {
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Icon(Icons.Filled.Bloodtype, contentDescription = null, tint = Color.White)
                Spacer(Modifier.width(8.dp))
                Text("BloodConnect", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        },
        actions = {
            TextButton(onClick = { nav.navigate("home") }) { Text("Beranda", color = Color.White) }
            if (session != null) {
                TextButton(onClick = { nav.navigate("dashboard") }) { Text("Profil", color = Color.White) }
            } else {
                TextButton(onClick = { nav.navigate("login") }) { Text("Masuk", color = Color.White) }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Rausch)
    )
}
