package com.unmsm.redfi.ui.burrito

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.unmsm.redfi.R
import com.unmsm.redfi.ui.theme.RedFiGuinda
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

@Composable
fun BurritoScreen() {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // Centro aproximado de la ciudad universitaria para el zoom inicial
    val centroCampus = GeoPoint(-12.0564, -77.0843)

    // Coordenadas de los paraderos internos
    val ptoSistemas = GeoPoint(-12.053688, -77.085771)
    val ptoBiblioteca = GeoPoint(-12.055980, -77.084944)
    val ptoMetalurgica = GeoPoint(-12.060075, -77.084436)
    val ptoZonaPuerta2 = GeoPoint(-12.059417, -77.079626)
    val ptoOdontologia = GeoPoint(-12.054886, -77.086256)
    val ptoComedor = GeoPoint(-12.059324, -77.083105)

    val rutaDetallada = listOf(
        ptoSistemas,
        ptoMetalurgica,
        ptoZonaPuerta2,
        ptoBiblioteca,
        ptoOdontologia,
        ptoComedor,
        ptoSistemas
    )

    val nombresParaderos = listOf(
        "Facultad de Ing. Metalúrgica (#2)",
        "Paradero Zona Puerta 2",
        "Biblioteca Central (#16)",
        "Facultad de Odontología (#18)",
        "Comedor Universitario",
        "Facultad de Ing. de Sistemas (FISI #20)"
    )

    var busPosition by remember { mutableStateOf(ptoSistemas) }
    var nextStopText by remember { mutableStateOf("Calculando siguiente paradero...") }
    var estimatedTimeText by remember { mutableStateOf("Calculando...") }

    // Estado para verificar si tenemos permiso de ubicación concedido
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Lanzador para pedir el permiso en caliente
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasLocationPermission = isGranted
    }

    // Pedir permiso automáticamente al abrir la pantalla si no se tiene
    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    var mapViewReference by remember { mutableStateOf<MapView?>(null) }
    var myLocationOverlayRef by remember { mutableStateOf<MyLocationNewOverlay?>(null) }

    // Simulación del bus en movimiento
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            while (true) {
                for (i in 0 until rutaDetallada.size - 1) {
                    val start = rutaDetallada[i]
                    val end = rutaDetallada[i + 1]

                    nextStopText = nombresParaderos[i]

                    val pasos = 20
                    for (step in 1..pasos) {
                        val lat = start.latitude + (end.latitude - start.latitude) * (step / pasos.toDouble())
                        val lon = start.longitude + (end.longitude - start.longitude) * (step / pasos.toDouble())
                        busPosition = GeoPoint(lat, lon)

                        val pasosRestantes = pasos - step
                        val segundosAprox = (pasosRestantes * 0.15).toInt() + 1
                        estimatedTimeText = "Aprox. $segundosAprox seg"

                        delay(120)
                    }

                    estimatedTimeText = "¡Llegando al paradero!"
                    delay(1000)
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F6F8))
            .padding(16.dp)
    ) {
        // Tarjeta de Estado del Bus Burrito
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🚌 Estado del Bus Burrito",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = RedFiGuinda
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFE8F5E9)
                    ) {
                        Text(
                            text = " EN RUTA ",
                            color = Color(0xFF2E7D32),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Próximo paradero: $nextStopText",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.DarkGray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Tiempo estimado: $estimatedTimeText",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Contenedor del Mapa
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
        ) {
            AndroidView(
                factory = { ctx: Context ->
                    val prefs = ctx.getSharedPreferences("osmdroid_prefs", Context.MODE_PRIVATE)
                    Configuration.getInstance().load(ctx, prefs)
                    Configuration.getInstance().setUserAgentValue(ctx.packageName)

                    MapView(ctx).apply {
                        setTileSource(TileSourceFactory.MAPNIK)
                        setMultiTouchControls(true)
                        controller.setZoom(16.5)
                        controller.setCenter(centroCampus)
                        mapViewReference = this

                        // Reemplaza esta parte dentro de tu factory { ctx: Context -> ... }
                        if (hasLocationPermission) {
                            // Usamos el proveedor combinado de red y GPS para una respuesta inmediata
                            val locationProvider = org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider(ctx).apply {
                                // Permitir proveedor de red ayuda a que el emulador detecte ubicación más rápido
                                addLocationSource(android.location.LocationManager.NETWORK_PROVIDER)
                            }

                            val locationOverlay = MyLocationNewOverlay(locationProvider, this).apply {
                                enableMyLocation()     // Habilita el punto azul
                                enableFollowLocation() // Centra la cámara automáticamente en ti
                            }
                            overlays.add(locationOverlay)
                            myLocationOverlayRef = locationOverlay
                        }
                    }
                },
                update = { mapView ->
                    mapView.overlays.removeAll { it is Marker }
                    val ctx = mapView.context

                    // Marcadores fijos de facultades con iconos personalizados
                    mapView.overlays.add(Marker(mapView).apply {
                        position = ptoSistemas
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        title = "Facultad de Ing. de Sistemas (FISI #20)"
                        snippet = "Paradero inicial"
                        icon = ContextCompat.getDrawable(ctx, R.drawable.fisilogo)
                    })

                    mapView.overlays.add(Marker(mapView).apply {
                        position = ptoMetalurgica
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        title = "Facultad de Ing. Metalúrgica (#2)"
                        snippet = "Paradero zona norte"
                        icon = ContextCompat.getDrawable(ctx, R.drawable.metalurgicalogo)
                    })

                    mapView.overlays.add(Marker(mapView).apply {
                        position = ptoZonaPuerta2
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        title = "Paradero Zona Puerta 2"
                        snippet = "Zona oeste del campus"
                        icon = ContextCompat.getDrawable(ctx, R.drawable.puertadoslogo)
                    })

                    mapView.overlays.add(Marker(mapView).apply {
                        position = ptoBiblioteca
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        title = "Biblioteca Central (#16)"
                        snippet = "Zona académica principal"
                        icon = ContextCompat.getDrawable(ctx, R.drawable.bibliotecalogo)
                    })

                    mapView.overlays.add(Marker(mapView).apply {
                        position = ptoOdontologia
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        title = "Facultad de Odontología (#18)"
                        snippet = "Paradero zona sur"
                        icon = ContextCompat.getDrawable(ctx, R.drawable.odontologialogo)
                    })

                    mapView.overlays.add(Marker(mapView).apply {
                        position = ptoComedor
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        title = "Comedor Universitario"
                        snippet = "Paradero zona de abastos"
                        icon = ContextCompat.getDrawable(ctx, R.drawable.comedorlogo)
                    })

                    // Bus Burrito en movimiento
                    val busMarker = Marker(mapView).apply {
                        position = busPosition
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        title = "Bus Burrito 🚌"
                        snippet = "En recorrido interno por el campus"
                        icon = ContextCompat.getDrawable(ctx, R.drawable.burritologo)
                    }
                    mapView.overlays.add(busMarker)

                    mapView.invalidate()
                },
                modifier = Modifier.fillMaxSize()
            )

            // Botón flotante de ubicación
            FloatingActionButton(
                onClick = {
                    myLocationOverlayRef?.myLocation?.let { myGeoPoint ->
                        mapViewReference?.controller?.animateTo(myGeoPoint)
                        mapViewReference?.controller?.setZoom(17.0)
                    }
                },
                containerColor = Color.White,
                contentColor = RedFiGuinda,
                shape = CircleShape,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MyLocation,
                    contentDescription = "Mi Ubicación"
                )
            }

            // Etiqueta flotante superior
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp),
                shape = RoundedCornerShape(8.dp),
                color = Color.White.copy(alpha = 0.9f),
                shadowElevation = 4.dp
            ) {
                Text(
                    text = "📍 GPS Activo",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = RedFiGuinda,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                )
            }
        }
    }
}