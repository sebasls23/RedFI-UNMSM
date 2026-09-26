package com.unmsm.redfi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.unmsm.redfi.ui.burrito.BurritoScreen
import com.unmsm.redfi.ui.community.CommunityScreen
import com.unmsm.redfi.ui.shop.ShopScreen
import com.unmsm.redfi.ui.theme.RedFiGuinda
import com.unmsm.redfi.ui.theme.RedFiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RedFiTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                NavigationBarItem(
                    icon = { Text("🚌", fontSize = 20.sp) },
                    label = { Text("Bus Burrito") },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = RedFiGuinda,
                        indicatorColor = RedFiGuinda.copy(alpha = 0.15f)
                    )
                )
                NavigationBarItem(
                    icon = { Text("💬", fontSize = 20.sp) },
                    label = { Text("Comunidad") },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = RedFiGuinda,
                        indicatorColor = RedFiGuinda.copy(alpha = 0.15f)
                    )
                )
                NavigationBarItem(
                    icon = { Text("🛒", fontSize = 20.sp) },
                    label = { Text("RedFi Shop") },
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = RedFiGuinda,
                        indicatorColor = RedFiGuinda.copy(alpha = 0.15f)
                    )
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> BurritoScreen()
                1 -> CommunityScreen()
                2 -> ShopScreen()
            }
        }
    }
}