package com.unmsm.redfi.ui.community

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unmsm.redfi.ui.theme.*

@Composable
fun CommunityScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RedFiLightBackground)
            .padding(16.dp)
    ) {
        Text(
            text = "Comunidad FISI 💬",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = RedFiGuinda
        )
        Text(
            text = "Conéctate con tus compañeros de facultad",
            fontSize = 14.sp,
            color = RedFiTextGray
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Lista de publicaciones de prueba
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(4) { index ->
                PostCard(
                    author = "Estudiante FISI",
                    time = "Hace ${index + 1} horas",
                    content = "¡Hola a todos! ¿Alguien tiene material o resúmenes de estudio para los cursos del ciclo actual? Agradezco cualquier ayuda."
                )
            }
        }
    }
}

@Composable
fun PostCard(author: String, time: String, content: String) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = RedFiCardBackground),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = author,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = RedFiGuinda
                )
                Text(
                    text = time,
                    fontSize = 12.sp,
                    color = RedFiTextGray
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = content,
                fontSize = 14.sp,
                color = RedFiTextDark
            )
        }
    }
}