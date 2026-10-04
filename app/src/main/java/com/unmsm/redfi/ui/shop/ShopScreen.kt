package com.unmsm.redfi.ui.shop

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unmsm.redfi.ui.theme.RedFiGuinda

@Composable
fun ShopScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F6F8)) // Fondo gris muy suave y moderno
            .padding(16.dp)
    ) {
        // Encabezado moderno
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "ShopFi 🛍️",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = RedFiGuinda
                )
                Text(
                    text = "Merch oficial UNMSM",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Cuadrícula de productos estilo tarjeta moderna
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            val productos = listOf(
                ("Polo Oficial FISI" to "S/ 35.00"),
                ("Hoodie RedFi" to "S/ 75.00"),
                ("Pad de Mouse RGB" to "S/ 40.00"),
                ("Taza Térmica UNMSM" to "S/ 25.00")
            )

            items(productos.size) { index ->
                val (nombre, precio) = productos[index]
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Contenedor de la imagen/icono con fondo sutil
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(90.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(RedFiGuinda.copy(alpha = 0.08f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("📦", fontSize = 36.sp)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = nombre,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.DarkGray,
                            maxLines = 1
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = precio,
                            fontSize = 13.sp,
                            color = RedFiGuinda,
                            fontWeight = FontWeight.ExtraBold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Botón moderno pequeño
                        Button(
                            onClick = { },
                            colors = ButtonDefaults.buttonColors(containerColor = RedFiGuinda),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Text("Ver", fontSize = 11.sp, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}