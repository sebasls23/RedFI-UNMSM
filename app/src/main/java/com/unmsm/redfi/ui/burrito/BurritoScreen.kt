package com.unmsm.redfi.ui.burrito

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unmsm.redfi.ui.theme.*

@Composable
fun BurritoScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RedFiLightBackground)
            .padding(16.dp)
    ) {
        Text(
            text = "Bus Burrito 🚌",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = RedFiGuinda
        )
        Text(
            text = "Rastreo y estado en tiempo real dentro de San Marcos",
            fontSize = 14.sp,
            color = RedFiTextGray
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Tarjeta principal de estado del bus
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = RedFiCardBackground),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Unidad Activa #01",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = RedFiTextDark
                    )
                    // Indicador de Estado (En ruta)
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = RedFiGold.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = " En Ruta ",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = RedFiGuinda,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Próxima parada estimada:",
                    fontSize = 13.sp,
                    color = RedFiTextGray
                )
                Text(
                    text = "Facultad de Ingeniería de Sistemas e Informática (FISI)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = RedFiGuinda
                )

                Spacer(modifier = Modifier.height(16.dp))

                LinearProgressIndicator(
                    progress = { 0.7f },
                    modifier = Modifier.fillMaxWidth(),
                    color = RedFiGuinda,
                    trackColor = RedFiGuinda.copy(alpha = 0.1f),
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Tiempo estimado de llegada: 4 mins",
                    fontSize = 12.sp,
                    color = RedFiTextGray
                )
            }
        }
    }
}