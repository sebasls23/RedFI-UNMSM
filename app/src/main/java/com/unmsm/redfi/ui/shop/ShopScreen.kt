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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unmsm.redfi.ui.theme.*

@Composable
fun ShopScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RedFiLightBackground)
            .padding(16.dp)
    ) {
        Text(
            text = "RedFi Shop 🛒",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = RedFiGuinda
        )
        Text(
            text = "Artículos de estudiantes para estudiantes",
            fontSize = 14.sp,
            color = RedFiTextGray
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(6) { index ->
                ProductCard(title = "Libro Cálculo 2", price = "S/ 25.00")
            }
        }
    }
}

@Composable
fun ProductCard(title: String, price: String) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = RedFiCardBackground),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(RedFiGuinda.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("📦", fontSize = 32.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = RedFiTextDark
            )
            Text(
                text = price,
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp,
                color = RedFiGuinda
            )
        }
    }
}