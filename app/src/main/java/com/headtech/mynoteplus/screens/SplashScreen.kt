package com.headtech.mynoteplus.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import com.headtech.mynoteplus.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()

    var isLoading by remember { mutableStateOf(true) }
    var isMaintenance by remember { mutableStateOf(false) }
    var maintenanceMessage by remember { mutableStateOf("") }
    var forceUpdate by remember { mutableStateOf(false) }
    var updateUrl by remember { mutableStateOf("") }

    // ✅ Ganti ini setiap kamu upload APK baru (manual cocokkan Firestore)
    val currentVersion = "1.0"

    LaunchedEffect(Unit) {
        db.collection("app_config").document("status")
            .get()
            .addOnSuccessListener { doc ->
                val maintenance = doc.getBoolean("is_maintenance") ?: false
                val message = doc.getString("message") ?: ""
                val latestVersion = doc.getString("latest_version") ?: currentVersion
                val url = doc.getString("update_url") ?: ""

                isMaintenance = maintenance
                maintenanceMessage = message
                forceUpdate = latestVersion != currentVersion
                updateUrl = url

                isLoading = false
            }
            .addOnFailureListener {
                // Gagal ambil data -> tetap izinkan masuk
                navController.navigate("login") {
                    popUpTo("splash") { inclusive = true }
                }
            }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Box(contentAlignment = Alignment.Center) {
            when {
                isLoading -> {
                    CircularProgressIndicator()
                }

                isMaintenance -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = painterResource(id = R.drawable.mynoteplus),
                            contentDescription = "App Logo",
                            modifier = Modifier.size(120.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("🚧 Maintenance", style = MaterialTheme.typography.headlineSmall)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(maintenanceMessage, style = MaterialTheme.typography.bodyMedium)
                    }
                }

                forceUpdate -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = painterResource(id = R.drawable.mynoteplus),
                            contentDescription = "App Logo",
                            modifier = Modifier.size(120.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("🔄 Update Tersedia", style = MaterialTheme.typography.headlineSmall)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Silakan update ke versi terbaru sebelum melanjutkan.", style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(updateUrl))
                            context.startActivity(intent)
                        }) {
                            Text("Download Update")
                        }
                    }
                }

                else -> {
                    // ✅ Lanjut ke login
                    LaunchedEffect(Unit) {
                        delay(1000)
                        navController.navigate("login") {
                            popUpTo("splash") { inclusive = true }
                        }
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = painterResource(id = R.drawable.mynoteplus),
                            contentDescription = "App Logo",
                            modifier = Modifier.size(120.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("MyNotePlus", style = MaterialTheme.typography.headlineMedium)
                    }
                }
            }
        }
    }
}
