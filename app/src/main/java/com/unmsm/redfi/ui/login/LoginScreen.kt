package com.unmsm.redfi.ui.login

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.unmsm.redfi.ui.theme.RedFiGuinda

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()
    var isLoading by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D1B2A)) // Fondo institucional oscuro
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Título de la App
                Text(
                    text = "RedFi & ShopFi",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = RedFiGuinda
                )

                Text(
                    text = "Comunidad UNMSM",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // Descripción institucional
                Text(
                    text = "Para acceder al Bus Burrito y al Marketplace, es obligatorio iniciar sesión con tu cuenta institucional oficial (@unmsm.edu.pe).",
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    color = Color.DarkGray,
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                if (isLoading) {
                    CircularProgressIndicator(color = RedFiGuinda)
                } else {
                    Button(
                        onClick = {
                            isLoading = true

                            // Correo institucional para validación y registro en pruebas/emulador
                            val correoInstitucional = "alumno.fisi@unmsm.edu.pe"

                            // FILTRO ESTRICTO SANMARQUINO
                            if (!correoInstitucional.endsWith("@unmsm.edu.pe")) {
                                isLoading = false
                                Toast.makeText(context, "Acceso denegado: Solo cuentas oficiales @unmsm.edu.pe", Toast.LENGTH_LONG).show()
                                return@Button
                            }

                            // Datos del usuario para guardar en Cloud Firestore
                            val usuarioMap = hashMapOf(
                                "correo" to correoInstitucional,
                                "facultad" to "FISI",
                                "fechaIngreso" to Timestamp.now()
                            )

                            // Guardar perfil en la colección "usuarios" usando el correo como ID
                            db.collection("usuarios")
                                .document(correoInstitucional)
                                .set(usuarioMap)
                                .addOnSuccessListener {
                                    isLoading = false
                                    Toast.makeText(context, "¡Bienvenido a RedFi, sanmarquino!", Toast.LENGTH_SHORT).show()
                                    onLoginSuccess() // Da acceso directo a la aplicación principal
                                }
                                .addOnFailureListener { e ->
                                    isLoading = false
                                    Toast.makeText(context, "Error al registrar perfil: ${e.message}", Toast.LENGTH_LONG).show()
                                }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RedFiGuinda)
                    ) {
                        Text(
                            text = "Iniciar Sesión con Google UNMSM",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}