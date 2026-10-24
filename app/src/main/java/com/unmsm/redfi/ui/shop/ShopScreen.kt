package com.unmsm.redfi.ui.shop

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.unmsm.redfi.ui.theme.RedFiGuinda

// --- Modelo de Datos ---
data class ShopItem(
    val id: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val category: String = "Merch",
    val condition: String = "Nuevo",
    val iconEmoji: String = "📦",
    val description: String = "",
    val seller: String = "",
    val sellerPhone: String = "",
    val timestamp: Long = 0L
)

val categoriesList = listOf("Todo", "Merch", "Libros", "Electrónica", "Útiles")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopScreen() {
    var selectedCategory by remember { mutableStateOf("Todo") }
    var selectedProduct by remember { mutableStateOf<ShopItem?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    var productsList by remember { mutableStateOf<List<ShopItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Cargar productos desde Firebase Firestore (colección "marketplace")
    DisposableEffect(Unit) {
        val db = FirebaseFirestore.getInstance()
        val listener = db.collection("marketplace")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    isLoading = false
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val loadedProducts = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(ShopItem::class.java)?.copy(id = doc.id)
                    }
                    productsList = loadedProducts
                    isLoading = false
                }
            }
        onDispose {
            listener.remove()
        }
    }

    val filteredItems = if (selectedCategory == "Todo") {
        productsList
    } else {
        productsList.filter { it.category == selectedCategory }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = RedFiGuinda,
                contentColor = Color.White,
                shape = CircleShape,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Vender artículo")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // --- ENCABEZADO DE SHOPFI ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "ShopFi UNMSM",
                    fontWeight = FontWeight.Black,
                    color = RedFiGuinda,
                    fontSize = 28.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Mercadillo estudiantil verificado",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Contenedor principal para el resto de la interfaz
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // --- BARRA DE PESTAÑAS (Tabs) ---
                ScrollableTabRow(
                    selectedTabIndex = categoriesList.indexOf(selectedCategory),
                    containerColor = Color.Transparent,
                    contentColor = RedFiGuinda,
                    edgePadding = 16.dp,
                    divider = {},
                    modifier = Modifier.fillMaxWidth()
                ) {
                    categoriesList.forEach { category ->
                        val isSelected = selectedCategory == category
                        Tab(
                            selected = isSelected,
                            onClick = { selectedCategory = category },
                            modifier = Modifier.padding(horizontal = 4.dp),
                            text = {
                                Text(
                                    text = category,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (isSelected) RedFiGuinda else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // --- CUADRÍCULA DE PRODUCTOS ---
                if (isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = RedFiGuinda)
                    }
                } else if (filteredItems.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "No hay productos en esta categoría.", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(filteredItems) { item ->
                            PremiumProductCard(
                                item = item,
                                onVerDetalles = { selectedProduct = item }
                            )
                        }
                        item(span = { GridItemSpan(2) }) {
                            Spacer(modifier = Modifier.height(80.dp))
                        }
                    }
                }
            }
        }
    }

    if (selectedProduct != null) {
        ProductDetailDialog(
            product = selectedProduct!!,
            onDismiss = { selectedProduct = null }
        )
    }

    if (showAddDialog) {
        AddProductDialog(
            onDismiss = { showAddDialog = false }
        )
    }
}

// --- COMPONENTES AUXILIARES ---

@Composable
fun PremiumProductCard(item: ShopItem, onVerDetalles: () -> Unit) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(18.dp))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(RedFiGuinda.copy(alpha = 0.05f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = item.iconEmoji, fontSize = 50.sp)

                Surface(
                    color = if (item.condition == "Nuevo") Color(0xFF4CAF50) else Color(0xFFFF9800),
                    shape = RoundedCornerShape(bottomEnd = 14.dp, topStart = 14.dp),
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Text(
                        text = item.condition,
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = item.name,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                minLines = 2,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "S/ ${"%.2f".format(item.price)}",
                fontSize = 18.sp,
                color = RedFiGuinda,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onVerDetalles,
                colors = ButtonDefaults.buttonColors(containerColor = RedFiGuinda),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
            ) {
                Text("Ver detalles", fontSize = 13.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun ProductDetailDialog(product: ShopItem, onDismiss: () -> Unit) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    val telefono = if (product.sellerPhone.isNotBlank()) product.sellerPhone else "51999999999"
                    val mensaje = "Hola! Vi tu producto *${product.name}* en ShopFi UNMSM y estoy interesado."

                    try {
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse("https://api.whatsapp.com/send?phone=$telefono&text=${Uri.encode(mensaje)}")
                        }
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        // Excepción si no tiene WhatsApp instalado
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = RedFiGuinda),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("💬 Contactar al vendedor por WhatsApp", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Column {
                Text(text = product.name, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = RedFiGuinda)
                Text(text = "Categoría: ${product.category}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(RedFiGuinda.copy(alpha = 0.05f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = product.iconEmoji, fontSize = 60.sp)
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Precio:", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                    Text(text = "S/ ${"%.2f".format(product.price)}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = RedFiGuinda)
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Estado:", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                    Text(text = product.condition, fontWeight = FontWeight.Bold, color = if (product.condition == "Nuevo") Color(0xFF4CAF50) else Color(0xFFFF9800))
                }

                Text(text = "Vendedor: ${if (product.seller.isNotBlank()) product.seller else "Estudiante UNMSM"}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), fontWeight = FontWeight.Medium)

                Divider(color = Color.LightGray.copy(alpha = 0.5f))

                Text(text = "Descripción:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                Text(text = product.description, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f), lineHeight = 18.sp)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                Text("Cerrar", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductDialog(onDismiss: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var priceStr by remember { mutableStateOf("") }
    var phoneStr by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Merch") }
    var condition by remember { mutableStateOf("Nuevo") }
    var description by remember { mutableStateOf("") }
    var iconEmoji by remember { mutableStateOf("🛍️") }

    val categories = listOf("Merch", "Libros", "Electrónica", "Útiles")
    val conditions = listOf("Nuevo", "Usado", "Como nuevo")
    val emojis = listOf("🛍️", "👕", "📚", "🧮", "☕", "💻", "⚡")

    val currentUser = FirebaseAuth.getInstance().currentUser
    var sellerName by remember { mutableStateOf(currentUser?.displayName ?: "Estudiante UNMSM") }

    // Cargar datos dinámicos del usuario desde Firestore
    LaunchedEffect(Unit) {
        currentUser?.uid?.let { uid ->
            FirebaseFirestore.getInstance().collection("users").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val fetchedName = document.getString("name") ?: document.getString("fullName")
                        val fetchedCareer = document.getString("career") ?: document.getString("role")
                        if (!fetchedName.isNullOrBlank()) {
                            sellerName = if (!fetchedCareer.isNullOrBlank()) {
                                "$fetchedName ($fetchedCareer)"
                            } else {
                                fetchedName
                            }
                        }
                    }
                }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    val price = priceStr.toDoubleOrNull() ?: 0.0

                    if (name.isNotBlank()) {
                        val celularFinal = if (phoneStr.isNotBlank()) "51${phoneStr.trim()}" else "51999999999"

                        val newItem = ShopItem(
                            name = name,
                            price = price,
                            category = category,
                            condition = condition,
                            iconEmoji = iconEmoji,
                            description = description,
                            seller = sellerName, // <-- Nombre dinámico y real del usuario
                            sellerPhone = celularFinal,
                            timestamp = System.currentTimeMillis()
                        )

                        FirebaseFirestore.getInstance().collection("marketplace")
                            .add(newItem)
                            .addOnSuccessListener {
                                onDismiss()
                            }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = RedFiGuinda),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Publicar", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        title = { Text("Vender un artículo", color = RedFiGuinda, fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre del producto") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = priceStr, onValueChange = { priceStr = it }, label = { Text("Precio S/ (Ej: 35.00)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = phoneStr, onValueChange = { phoneStr = it }, label = { Text("Tu Celular / WhatsApp (Ej: 987654321)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth(), minLines = 2)

                Text("Ícono:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), modifier = Modifier.padding(top = 4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    emojis.forEach { emoji ->
                        FilterChip(
                            selected = iconEmoji == emoji,
                            onClick = { iconEmoji = emoji },
                            label = { Text(emoji, fontSize = 16.sp) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = RedFiGuinda.copy(alpha = 0.2f))
                        )
                    }
                }

                Text("Categoría:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), modifier = Modifier.padding(top = 4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    categories.forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = RedFiGuinda.copy(alpha = 0.2f), selectedLabelColor = RedFiGuinda)
                        )
                    }
                }

                Text("Estado:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), modifier = Modifier.padding(top = 4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    conditions.forEach { cond ->
                        FilterChip(
                            selected = condition == cond,
                            onClick = { condition = cond },
                            label = { Text(cond, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = RedFiGuinda.copy(alpha = 0.2f), selectedLabelColor = RedFiGuinda)
                        )
                    }
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)) }
        }
    )
}