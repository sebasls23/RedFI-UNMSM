package com.unmsm.redfi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.unmsm.redfi.ui.burrito.BurritoScreen
import com.unmsm.redfi.ui.community.CommunityScreen
import com.unmsm.redfi.ui.shop.ShopScreen
import com.unmsm.redfi.ui.login.LoginScreen
import com.unmsm.redfi.ui.theme.RedFiGuinda
import com.unmsm.redfi.ui.theme.RedFiTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RedFiTheme {
                // Verificamos de forma persistente si Firebase Auth ya tiene una sesión activa
                val currentUser = FirebaseAuth.getInstance().currentUser
                var isLoggedIn by remember { mutableStateOf(currentUser != null) }

                if (!isLoggedIn) {
                    LoginScreen(
                        onLoginSuccess = {
                            isLoggedIn = true
                        }
                    )
                } else {
                    MainScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    var selectedTab by remember { mutableStateOf(0) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        // Se desactivan los gestos de deslizamiento si estás en la pestaña 0 (Bus Burrito)
        gesturesEnabled = selectedTab != 0,
        drawerContent = {
            ModalDrawerSheet {
                // Cabecera del menú lateral
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(RedFiGuinda)
                        .padding(24.dp)
                ) {
                    Column {
                        Image(
                            painter = painterResource(id = R.drawable.logo),
                            contentDescription = "Logo RedFi",
                            modifier = Modifier.size(50.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "RedFi UNMSM",
                            color = Color.White,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "Estudiante FISI",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 14.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))

                // Opciones del menú
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
                    label = { Text("Mi Perfil") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Configuración") },
                    label = { Text("Configuración") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.logo),
                                contentDescription = "Logo RedFi",
                                modifier = Modifier.size(32.dp)
                            )
                            Text(
                                text = "RedFi",
                                color = RedFiGuinda,
                                fontSize = 20.sp
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menú",
                                tint = RedFiGuinda
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.White
                    )
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
                        icon = { Text("🚌", fontSize = 20.sp) },
                        label = { Text("Bus Burrito", fontSize = 11.sp) },
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = RedFiGuinda,
                            selectedTextColor = RedFiGuinda,
                            indicatorColor = RedFiGuinda.copy(alpha = 0.12f)
                        )
                    )
                    NavigationBarItem(
                        icon = { Text("💬", fontSize = 20.sp) },
                        label = { Text("Comunidad", fontSize = 11.sp) },
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = RedFiGuinda,
                            selectedTextColor = RedFiGuinda,
                            indicatorColor = RedFiGuinda.copy(alpha = 0.12f)
                        )
                    )
                    NavigationBarItem(
                        icon = { Text("🛒", fontSize = 20.sp) },
                        label = { Text("ShopFi", fontSize = 11.sp) },
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = RedFiGuinda,
                            selectedTextColor = RedFiGuinda,
                            indicatorColor = RedFiGuinda.copy(alpha = 0.12f)
                        )
                    )
                }
            }
        ) { innerPadding ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                color = MaterialTheme.colorScheme.background
            ) {
                when (selectedTab) {
                    0 -> BurritoScreen()
                    1 -> CommunityScreen()
                    2 -> ShopScreen()
                }
            }
        }
    }
}