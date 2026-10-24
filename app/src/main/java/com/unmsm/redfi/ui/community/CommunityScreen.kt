package com.unmsm.redfi.ui.community

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.unmsm.redfi.ui.theme.*

// 1. Modelo de datos Post
data class Post(
    val id: String = "",
    val author: String = "",
    val facultyOrRole: String = "",
    val facultyTarget: String = "FISI",
    val time: Long = 0L,
    val content: String = "",
    val isOfficial: Boolean = false
)

val facultadesList = listOf(
    "FISI", "FCB", "FCF", "FCM", "FO", "FPsi",
    "FCA", "FCC", "FCE", "FE", "FCS", "FDCP",
    "FLCH", "FIEE", "FQIQ", "FIGMMG", "FII"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen() {
    val currentUser = FirebaseAuth.getInstance().currentUser

    var selectedMainTab by remember { mutableStateOf(0) } // 0: Oficiales, 1: Estudiantes
    var selectedFaculty by remember { mutableStateOf("FISI") }

    var postsList by remember { mutableStateOf<List<Post>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var showAddDialog by remember { mutableStateOf(false) }
    var showNotAuthorizedDialog by remember { mutableStateOf(false) } // Controla el diálogo informativo

    var isUserOfficialAuthorized by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        currentUser?.uid?.let { uid ->
            FirebaseFirestore.getInstance().collection("users").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        isUserOfficialAuthorized = document.getBoolean("isOfficialVerified") == true
                    }
                }
        }
    }

    DisposableEffect(Unit) {
        val db = FirebaseFirestore.getInstance()
        val listener = db.collection("posts")
            .orderBy("time", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    isLoading = false
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val loadedPosts = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Post::class.java)?.copy(id = doc.id)
                    }
                    postsList = loadedPosts
                    isLoading = false
                }
            }
        onDispose {
            listener.remove()
        }
    }

    val filteredPosts = postsList.filter { post ->
        if (selectedMainTab == 0) {
            post.isOfficial
        } else {
            !post.isOfficial && post.facultyTarget == selectedFaculty
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (selectedMainTab == 0 && !isUserOfficialAuthorized) {
                        showNotAuthorizedDialog = true
                    } else {
                        showAddDialog = true
                    }
                },
                containerColor = RedFiGuinda,
                contentColor = Color.White
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Nuevo Post")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // --- ENCABEZADO DE COMUNIDAD ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Comunidad UNMSM 💬",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    color = RedFiGuinda,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Comunicados oficiales y espacio estudiantil por facultad",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                TabRow(
                    selectedTabIndex = selectedMainTab,
                    containerColor = Color.Transparent,
                    contentColor = RedFiGuinda
                ) {
                    Tab(
                        selected = selectedMainTab == 0,
                        onClick = { selectedMainTab = 0 },
                        text = { Text("📢 Oficiales", fontWeight = FontWeight.Bold, color = if (selectedMainTab == 0) RedFiGuinda else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)) }
                    )
                    Tab(
                        selected = selectedMainTab == 1,
                        onClick = { selectedMainTab = 1 },
                        text = { Text("💬 Estudiantes", fontWeight = FontWeight.Bold, color = if (selectedMainTab == 1) RedFiGuinda else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)) }
                    )
                }

                if (selectedMainTab == 1) {
                    Spacer(modifier = Modifier.height(8.dp))
                    ScrollableTabRow(
                        selectedTabIndex = facultadesList.indexOf(selectedFaculty).coerceAtLeast(0),
                        containerColor = Color.Transparent,
                        contentColor = RedFiGuinda,
                        edgePadding = 0.dp,
                        divider = {}
                    ) {
                        facultadesList.forEach { facultad ->
                            val isSelected = selectedFaculty == facultad
                            Tab(
                                selected = isSelected,
                                onClick = { selectedFaculty = facultad },
                                text = {
                                    Text(
                                        text = facultad,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp,
                                        color = if (isSelected) RedFiGuinda else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = RedFiGuinda)
                    }
                } else if (filteredPosts.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = if (selectedMainTab == 0) "No hay comunicados oficiales por el momento." else "No hay publicaciones en la facultad de $selectedFaculty.",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(filteredPosts) { post ->
                            PostCard(post = post)
                        }
                        item {
                            Spacer(modifier = Modifier.height(80.dp))
                        }
                    }
                }
            }
        }
    }

    // Diálogo para crear publicación si está autorizado
    if (showAddDialog) {
        AddPostDialog(
            isOfficialContext = (selectedMainTab == 0),
            isUserAuthorized = isUserOfficialAuthorized,
            currentFaculty = selectedFaculty,
            onDismiss = { showAddDialog = false },
            onPostCreated = { newPost ->
                FirebaseFirestore.getInstance().collection("posts")
                    .add(newPost)
                showAddDialog = false
            }
        )
    }

    // Diálogo informativo cuando un usuario normal intenta publicar en Oficiales
    if (showNotAuthorizedDialog) {
        AlertDialog(
            onDismissRequest = { showNotAuthorizedDialog = false },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(20.dp),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(imageVector = Icons.Default.VerifiedUser, contentDescription = null, tint = RedFiGuinda)
                    Text("Acceso Restringido", color = RedFiGuinda, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            },
            text = {
                Text(
                    text = "Esta sección está reservada exclusivamente para comunicados oficiales de autoridades, decanatura y delegados de la UNMSM.\n\nSi representas a un tercio estudiantil, centro federado o facultad, puedes solicitar tu verificación institucional directamente desde la sección de **Mi Perfil** usando el botón 'Pedir Oficial'.",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = { showNotAuthorizedDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = RedFiGuinda),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Entendido", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

// --- COMPONENTES AUXILIARES ---

@Composable
fun PostCard(post: Post) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = post.author,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = RedFiGuinda
                    )
                    if (post.isOfficial) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Verificado Oficial",
                            tint = RedFiGuinda,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = RedFiGuinda.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = post.facultyTarget,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = RedFiGuinda,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Text(
                text = post.facultyOrRole,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = post.content,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 20.sp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPostDialog(
    isOfficialContext: Boolean,
    isUserAuthorized: Boolean,
    currentFaculty: String,
    onDismiss: () -> Unit,
    onPostCreated: (Post) -> Unit
) {
    var textContent by remember { mutableStateOf("") }
    var publishAsOfficial by remember { mutableStateOf(isOfficialContext && isUserAuthorized) }
    var selectedTargetFaculty by remember { mutableStateOf(currentFaculty) }
    var expandedDropdown by remember { mutableStateOf(false) }

    val currentUser = FirebaseAuth.getInstance().currentUser
    var userName by remember { mutableStateOf(currentUser?.displayName ?: "Estudiante UNMSM") }
    var userRole by remember { mutableStateOf("Estudiante UNMSM") }

    // Obtenemos de forma dinámica los datos reales del usuario desde Firestore
    LaunchedEffect(Unit) {
        currentUser?.uid?.let { uid ->
            FirebaseFirestore.getInstance().collection("users").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val fetchedName = document.getString("name") ?: document.getString("fullName")
                        if (!fetchedName.isNullOrBlank()) {
                            userName = fetchedName
                        }
                        val fetchedRole = document.getString("role") ?: document.getString("career")
                        if (!fetchedRole.isNullOrBlank()) {
                            userRole = fetchedRole
                        }
                    }
                }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Text(
                text = if (isOfficialContext) "Crear Comunicado Oficial" else "Crear Publicación Estudiantil",
                color = RedFiGuinda,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = textContent,
                    onValueChange = { textContent = it },
                    placeholder = { Text("Escribe tu consulta o aviso...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RedFiGuinda,
                        cursorColor = RedFiGuinda,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    )
                )

                if (!isOfficialContext) {
                    Text(text = "Facultad de destino:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))

                    ExposedDropdownMenuBox(
                        expanded = expandedDropdown,
                        onExpandedChange = { expandedDropdown = !expandedDropdown }
                    ) {
                        OutlinedTextField(
                            value = selectedTargetFaculty,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDropdown) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedDropdown,
                            onDismissRequest = { expandedDropdown = false }
                        ) {
                            facultadesList.forEach { fac ->
                                DropdownMenuItem(
                                    text = { Text(fac) },
                                    onClick = {
                                        selectedTargetFaculty = fac
                                        expandedDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }

                if (isUserAuthorized) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Checkbox(
                            checked = publishAsOfficial,
                            onCheckedChange = { publishAsOfficial = it },
                            colors = CheckboxDefaults.colors(checkedColor = RedFiGuinda)
                        )
                        Text(text = "Publicar como Comunicado Oficial", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (textContent.isNotBlank()) {
                        val finalIsOfficial = if (isUserAuthorized) publishAsOfficial else false
                        val newPost = Post(
                            id = "",
                            author = userName,
                            facultyOrRole = userRole,
                            facultyTarget = if (finalIsOfficial) "UNMSM" else selectedTargetFaculty,
                            time = System.currentTimeMillis(),
                            content = textContent,
                            isOfficial = finalIsOfficial
                        )
                        onPostCreated(newPost)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = RedFiGuinda),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Publicar", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                Text("Cancelar", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
        }
    )
}