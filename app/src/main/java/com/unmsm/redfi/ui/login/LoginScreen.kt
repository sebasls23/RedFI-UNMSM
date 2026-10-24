package com.unmsm.redfi.ui.login

import android.content.Context
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
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.unmsm.redfi.ui.theme.RedFiGuinda
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

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
                            coroutineScope.launch {
                                isLoading = true
                                try {
                                    val credentialManager = CredentialManager.create(context)

                                    // ⚠️ REEMPLAZA ESTO CON TU WEB CLIENT ID REAL DE LA CONSOLA DE FIREBASE
                                    val serverClientId = "522858268121-1jvesml4is3nmg1mlm9fffohkcue9r9u.apps.googleusercontent.com"

                                    val googleIdOption = GetGoogleIdOption.Builder()
                                        .setFilterByAuthorizedAccounts(false)
                                        .setServerClientId(serverClientId)
                                        .setAutoSelectEnabled(false)
                                        .build()

                                    val request = GetCredentialRequest.Builder()
                                        .addCredentialOption(googleIdOption)
                                        .build()

                                    val result = credentialManager.getCredential(
                                        context = context,
                                        request = request
                                    )

                                    handleGoogleSignInResult(result, auth, db, context, onLoginSuccess) {
                                        isLoading = false
                                    }
                                } catch (e: GetCredentialException) {
                                    isLoading = false
                                    // Si el usuario cancela la ventana flotante de Google, no mostramos error
                                } catch (e: Exception) {
                                    isLoading = false
                                    Toast.makeText(context, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                                }
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

// Función encargada de procesar la respuesta de Google y verificar el dominio @unmsm.edu.pe
private fun handleGoogleSignInResult(
    result: GetCredentialResponse,
    auth: FirebaseAuth,
    db: FirebaseFirestore,
    context: Context,
    onLoginSuccess: () -> Unit,
    onComplete: () -> Unit
) {
    val credential = result.credential
    if (credential is androidx.credentials.CustomCredential &&
        credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
    ) {
        try {
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            val googleIdToken = googleIdTokenCredential.idToken
            val email = googleIdTokenCredential.id

            // 🔒 FILTRO ESTRICTO SANMARQUINO
            if (!email.endsWith("@unmsm.edu.pe")) {
                auth.signOut()
                Toast.makeText(context, "Acceso denegado: Solo cuentas oficiales @unmsm.edu.pe", Toast.LENGTH_LONG).show()
                onComplete()
                return
            }

            val firebaseCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
            auth.signInWithCredential(firebaseCredential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        if (user != null) {
                            val userId = user.uid
                            val userRef = db.collection("users").document(userId)

                            // Verificamos si ya existe su documento único en Firestore
                            userRef.get().addOnSuccessListener { doc ->
                                if (!doc.exists()) {
                                    val rawName = email.substringBefore("@")
                                    val formattedName = user.displayName ?: rawName.split(".").joinToString(" ") {
                                        it.replaceFirstChar { char -> char.uppercase() }
                                    }

                                    val usuarioMap = hashMapOf(
                                        "name" to formattedName,
                                        "email" to email,
                                        "faculty" to "Facultad de Ingeniería de Sistemas e Informática (FISI)",
                                        "major" to "Ingeniería de Software",
                                        "cycle" to "III Ciclo",
                                        "isVerified" to false,
                                        "timestamp" to Timestamp.now()
                                    )
                                    userRef.set(usuarioMap).addOnSuccessListener {
                                        Toast.makeText(context, "¡Bienvenido a RedFi, sanmarquino!", Toast.LENGTH_SHORT).show()
                                        onComplete()
                                        onLoginSuccess()
                                    }.addOnFailureListener { e ->
                                        Toast.makeText(context, "Error al registrar perfil: ${e.message}", Toast.LENGTH_LONG).show()
                                        onComplete()
                                    }
                                } else {
                                    // Ya tiene su cuenta creada previamente
                                    Toast.makeText(context, "¡Bienvenido de nuevo!", Toast.LENGTH_SHORT).show()
                                    onComplete()
                                    onLoginSuccess()
                                }
                            }.addOnFailureListener {
                                onComplete()
                                onLoginSuccess()
                            }
                        } else {
                            onComplete()
                        }
                    } else {
                        Toast.makeText(context, "Error de autenticación: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                        onComplete()
                    }
                }
        } catch (e: Exception) {
            Toast.makeText(context, "Error al procesar credencial: ${e.message}", Toast.LENGTH_SHORT).show()
            onComplete()
        }
    } else {
        Toast.makeText(context, "Tipo de credencial no soportado", Toast.LENGTH_SHORT).show()
        onComplete()
    }
}