package com.unmsm.redfi.ui.settings

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.unmsm.redfi.ui.theme.RedFiGuinda
import com.unmsm.redfi.ui.theme.RedFiTextGray

@Composable
fun SettingsScreen(
    isDarkMode: Boolean,
    onDarkModeChanged: (Boolean) -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    var notificationsEnabled by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background) // Se adapta dinámicamente al tema
    ) {
        // --- ENCABEZADO ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface) // Se adapta dinámicamente al tema
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Configuración ⚙️",
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
                color = RedFiGuinda,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Personaliza tu experiencia en RedFi",
                fontSize = 14.sp,
                color = RedFiTextGray,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // --- CONTENIDO ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // Sección: Preferencias
            Text(text = "PREFERENCIAS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = RedFiTextGray)

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    SettingSwitchItem(
                        icon = Icons.Default.Notifications,
                        title = "Notificaciones",
                        subtitle = "Alertas de ShopFi y Comunidad",
                        checked = notificationsEnabled,
                        onCheckedChange = {
                            notificationsEnabled = it
                            Toast.makeText(context, if(it) "Notificaciones activadas" else "Notificaciones desactivadas", Toast.LENGTH_SHORT).show()
                        }
                    )

                    Divider(color = Color.LightGray.copy(alpha = 0.2f), modifier = Modifier.padding(horizontal = 16.dp))

                    SettingSwitchItem(
                        icon = Icons.Default.DarkMode,
                        title = "Modo Oscuro Institucional",
                        subtitle = "Activar tema nocturno",
                        checked = isDarkMode, // Conectado al estado global recibido de MainActivity
                        onCheckedChange = { enabled ->
                            onDarkModeChanged(enabled) // Modifica el estado global al instante
                            Toast.makeText(context, if (enabled) "Modo oscuro activado" else "Modo claro activado", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }

            // Sección: Información y Ayuda
            Text(text = "ACERCA DE", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = RedFiTextGray)

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    SettingClickableItem(
                        icon = Icons.Default.Info,
                        title = "Versión de la App",
                        subtitle = "RedFi v1.0.0 (FISI - UNMSM)",
                        onClick = { Toast.makeText(context, "RedFi & ShopFi - San Marcos", Toast.LENGTH_SHORT).show() }
                    )

                    Divider(color = Color.LightGray.copy(alpha = 0.2f), modifier = Modifier.padding(horizontal = 16.dp))

                    SettingClickableItem(
                        icon = Icons.Default.Lock,
                        title = "Política de Privacidad",
                        subtitle = "Uso de datos institucionales",
                        onClick = { Toast.makeText(context, "Tus datos están protegidos en Firebase", Toast.LENGTH_SHORT).show() }
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Botón de Cerrar Sesión
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
                Icon(imageVector = Icons.Default.ExitToApp, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Cerrar Sesión", color = Color.White, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun SettingSwitchItem(icon: ImageVector, title: String, subtitle: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(imageVector = icon, contentDescription = null, tint = RedFiGuinda)
            Column {
                Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Text(text = subtitle, fontSize = 11.sp, color = RedFiTextGray)
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = RedFiGuinda)
        )
    }
}

@Composable
fun SettingClickableItem(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = Color.Transparent,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Icon(imageVector = icon, contentDescription = null, tint = RedFiGuinda)
                Column {
                    Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Text(text = subtitle, fontSize = 11.sp, color = RedFiTextGray)
                }
            }
            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = RedFiTextGray)
        }
    }
}