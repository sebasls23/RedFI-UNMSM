package com.unmsm.redfi.ui.profile

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.unmsm.redfi.ui.theme.RedFiGuinda
import com.unmsm.redfi.ui.theme.RedFiTextGray

@Composable
fun ProfileScreen(onLogout: () -> Unit) {
    val context = LocalContext.current
    val currentUser = FirebaseAuth.getInstance().currentUser

    // CORREGIDO: Valores limpios por defecto para evitar mostrar datos de otra persona
    var faculty by remember { mutableStateOf("Facultad no especificada") }
    var major by remember { mutableStateOf("Escuela no especificada") }
    var cycle by remember { mutableStateOf("Ciclo no especificado") }
    var userName by remember {
        mutableStateOf(currentUser?.displayName ?: currentUser?.email?.substringBefore("@") ?: "Estudiante UNMSM")
    }
    var showEditDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        currentUser?.uid?.let { uid ->
            val db = FirebaseFirestore.getInstance()
            db.collection("users").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        faculty = document.getString("faculty") ?: document.getString("facultad") ?: faculty
                        major = document.getString("major") ?: document.getString("carrera") ?: major
                        cycle = document.getString("cycle") ?: document.getString("ciclo") ?: cycle
                        userName = document.getString("name") ?: document.getString("nombre") ?: userName
                    }
                }
                .addOnFailureListener { }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // --- ENCABEZADO FIJO ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Mi Perfil 👤",
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
                color = RedFiGuinda,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Información académica y personal",
                fontSize = 14.sp,
                color = RedFiTextGray,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // --- CONTENIDO DEL PERFIL ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(RedFiGuinda.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = RedFiGuinda,
                    modifier = Modifier.size(50.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = userName,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = currentUser?.email ?: "correo@unmsm.edu.pe",
                fontSize = 13.sp,
                color = RedFiTextGray
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Fila de botones de acción (Editar y Solicitar cuenta oficial)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { showEditDialog = true },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = RedFiGuinda),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Editar Datos", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = {
                        val correoSoporte = "soporte.redfi@unmsm.edu.pe"
                        val asunto = Uri.encode("Solicitud de acceso a Comunicados Oficiales - RedFi")
                        val cuerpo = Uri.encode("Hola, soy $userName, estudiante de la carrera de $major ($cycle) en la facultad de $faculty. Solicito la verificación de mi cuenta para poder publicar comunicados oficiales en representación de mi área o facultad.")

                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:$correoSoporte?subject=$asunto&body=$cuerpo")
                        }
                        try {
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "No se encontró una aplicación de correo instalada", Toast.LENGTH_SHORT).show()
                        }
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = RedFiGuinda),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Default.Verified, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Pedir Oficial", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tarjeta con detalles académicos
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.School, contentDescription = null, tint = RedFiGuinda)
                        Column {
                            Text(text = "Facultad", fontSize = 12.sp, color = RedFiTextGray, fontWeight = FontWeight.Medium)
                            Text(text = faculty, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                        }
                    }

                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.2f))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Email, contentDescription = null, tint = RedFiGuinda)
                        Column {
                            Text(text = "Escuela Profesional", fontSize = 12.sp, color = RedFiTextGray, fontWeight = FontWeight.Medium)
                            Text(text = major, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                        }
                    }

                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.2f))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.DateRange, contentDescription = null, tint = RedFiGuinda)
                        Column {
                            Text(text = "Ciclo Académico", fontSize = 12.sp, color = RedFiTextGray, fontWeight = FontWeight.Medium)
                            Text(text = cycle, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    FirebaseAuth.getInstance().signOut()
                    onLogout()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(text = "Cerrar Sesión", color = Color.White, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showEditDialog) {
        EditProfileDialog(
            currentMajor = major,
            currentCycle = cycle,
            currentFaculty = faculty,
            onDismiss = { showEditDialog = false },
            onSave = { newMajor, newCycle, newFaculty ->
                major = newMajor
                cycle = newCycle
                faculty = newFaculty
                showEditDialog = false
                Toast.makeText(context, "Información académica actualizada", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
fun EditProfileDialog(
    currentMajor: String,
    currentCycle: String,
    currentFaculty: String,
    onDismiss: () -> Unit,
    onSave: (String, String, String) -> Unit
) {
    var major by remember { mutableStateOf(currentMajor) }
    var cycle by remember { mutableStateOf(currentCycle) }
    var faculty by remember { mutableStateOf(currentFaculty) }
    val currentUser = FirebaseAuth.getInstance().currentUser

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(20.dp),
        title = { Text("Editar Datos Académicos", color = RedFiGuinda, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "🔒 El nombre y correo están vinculados a tu cuenta y no pueden modificarse libremente.",
                    fontSize = 11.sp,
                    color = RedFiTextGray,
                    lineHeight = 15.sp
                )

                Spacer(modifier = Modifier.height(2.dp))

                OutlinedTextField(
                    value = major,
                    onValueChange = { major = it },
                    label = { Text("Escuela Profesional") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = cycle,
                    onValueChange = { cycle = it },
                    label = { Text("Ciclo Académico (Ej: III Ciclo)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = faculty,
                    onValueChange = { faculty = it },
                    label = { Text("Facultad") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    currentUser?.uid?.let { uid ->
                        val updatedData = mapOf(
                            "major" to major,
                            "cycle" to cycle,
                            "faculty" to faculty
                        )
                        FirebaseFirestore.getInstance().collection("users").document(uid)
                            .set(updatedData, com.google.firebase.firestore.SetOptions.merge())
                    }
                    onSave(major, cycle, faculty)
                },
                colors = ButtonDefaults.buttonColors(containerColor = RedFiGuinda),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar cambios", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                Text("Cancelar", color = RedFiTextGray)
            }
        }
    )
}