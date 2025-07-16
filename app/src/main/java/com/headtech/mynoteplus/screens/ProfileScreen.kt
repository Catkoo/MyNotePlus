package com.headtech.mynoteplus.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.ui.text.font.FontWeight

@Composable
fun ProfileScreen(navController: NavController) {
    val context = LocalContext.current
    val user = FirebaseAuth.getInstance().currentUser
    val db = FirebaseFirestore.getInstance()

    var displayName by remember { mutableStateOf("") }
    var showEmailDialog by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        user?.uid?.let { uid ->
            db.collection("users").document(uid).get()
                .addOnSuccessListener {
                    displayName = it.getString("name") ?: ""
                }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "Profil",
            style = MaterialTheme.typography.headlineMedium
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Email", style = MaterialTheme.typography.labelMedium)
                Text(text = user?.email ?: "-", style = MaterialTheme.typography.bodyMedium)

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = displayName,
                    onValueChange = { displayName = it },
                    label = { Text("Nama") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        user?.uid?.let { uid ->
                            db.collection("users").document(uid)
                                .update("name", displayName)
                                .addOnSuccessListener {
                                    Toast.makeText(context, "Nama diperbarui", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(context, "Gagal memperbarui nama", Toast.LENGTH_SHORT).show()
                                }
                        }
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Simpan Nama")
                }
            }
        }

        Divider()

        Text("Keamanan", style = MaterialTheme.typography.titleSmall)

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = { showEmailDialog = true }, modifier = Modifier.fillMaxWidth()) {
                Text("Ubah Email")
            }
            Button(onClick = { showPasswordDialog = true }, modifier = Modifier.fillMaxWidth()) {
                Text("Ubah Password")
            }
            TextButton(onClick = {
                val email = user?.email
                if (!email.isNullOrBlank()) {
                    FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    Toast.makeText(context, "Link reset dikirim ke email", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Email tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
            }) {
                Text("Lupa Password?")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = {
                FirebaseAuth.getInstance().signOut()
                Toast.makeText(context, "Berhasil logout", Toast.LENGTH_SHORT).show()
                navController.navigate("login") {
                    popUpTo("home") { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.error
            )
        ) {
            Icon(Icons.Default.Logout, contentDescription = "Logout")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Logout")
        }

        Spacer(modifier = Modifier.weight(1f))

        // Versi 0.1 di tengah dan bold
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Versi 0.1",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
            )
        }
    }

    if (showEmailDialog) ChangeEmailDialog(onDismiss = { showEmailDialog = false })
    if (showPasswordDialog) ChangePasswordDialog(onDismiss = { showPasswordDialog = false })
}


@Composable
fun ChangeEmailDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser

    var oldEmail by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var newEmail by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = {
                val credential = EmailAuthProvider.getCredential(oldEmail, password)
                user?.reauthenticate(credential)?.addOnSuccessListener {
                    user.updateEmail(newEmail).addOnSuccessListener {
                        user.sendEmailVerification()
                        Toast.makeText(context, "Cek email baru untuk verifikasi.", Toast.LENGTH_LONG).show()
                        auth.signOut()
                        onDismiss()
                    }.addOnFailureListener {
                        Toast.makeText(context, "Gagal update email", Toast.LENGTH_SHORT).show()
                    }
                }?.addOnFailureListener {
                    Toast.makeText(context, "Verifikasi email lama gagal", Toast.LENGTH_SHORT).show()
                }
            }) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        },
        title = { Text("Ganti Email") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = oldEmail, onValueChange = { oldEmail = it }, label = { Text("Email lama") }, singleLine = true)
                OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, visualTransformation = PasswordVisualTransformation(), singleLine = true)
                OutlinedTextField(value = newEmail, onValueChange = { newEmail = it }, label = { Text("Email baru") }, singleLine = true)
            }
        }
    )
}

@Composable
fun ChangePasswordDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    val user = FirebaseAuth.getInstance().currentUser

    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = {
                val email = user?.email ?: ""
                val credential = EmailAuthProvider.getCredential(email, oldPassword)
                if (newPassword.length < 8 || newPassword != confirmPassword) {
                    Toast.makeText(context, "Password tidak valid", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                user?.reauthenticate(credential)?.addOnSuccessListener {
                    user.updatePassword(newPassword).addOnSuccessListener {
                        Toast.makeText(context, "Password berhasil diubah", Toast.LENGTH_SHORT).show()
                        onDismiss()
                    }.addOnFailureListener {
                        Toast.makeText(context, "Gagal ubah password", Toast.LENGTH_SHORT).show()
                    }
                }?.addOnFailureListener {
                    Toast.makeText(context, "Password lama salah", Toast.LENGTH_SHORT).show()
                }
            }) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        },
        title = { Text("Ganti Password") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = oldPassword, onValueChange = { oldPassword = it }, label = { Text("Password lama") }, visualTransformation = PasswordVisualTransformation(), singleLine = true)
                OutlinedTextField(value = newPassword, onValueChange = { newPassword = it }, label = { Text("Password baru") }, visualTransformation = PasswordVisualTransformation(), singleLine = true)
                OutlinedTextField(value = confirmPassword, onValueChange = { confirmPassword = it }, label = { Text("Konfirmasi password") }, visualTransformation = PasswordVisualTransformation(), singleLine = true)
            }
        }
    )
}

