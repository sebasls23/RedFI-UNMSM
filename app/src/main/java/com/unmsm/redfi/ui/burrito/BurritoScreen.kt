package com.unmsm.redfi.ui.burrito

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBus
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
import org.osmdroid.views.overlay.Polyline
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
    val ptoZonaPuerta3 = GeoPoint(-12.057408, -77.080280)
    val ptoIndustrial = GeoPoint(-12.060407, -77.081071)
    val ptoGeologica = GeoPoint(-12.060846, -77.083764)
    val ptoZonaPuerta7 = GeoPoint(-12.054147, -77.084564)

    val curvaNorteSistemasBiblioteca = GeoPoint(-12.054200, -77.086000)
    val curvaSurMetalurgica = GeoPoint(-12.060500, -77.084100)
    val curvaEstePuertas = GeoPoint(-12.058500, -77.079800)

    val rutaDetallada = listOf(
        ptoSistemas,
        curvaNorteSistemasBiblioteca,
        ptoOdontologia,
        ptoBiblioteca,
        ptoMetalurgica,
        curvaSurMetalurgica,
        ptoGeologica,
        ptoIndustrial,
        ptoZonaPuerta2,
        curvaEstePuertas,
        ptoZonaPuerta3,
        ptoZonaPuerta7,
        ptoSistemas
    )

    val nombresParaderos = listOf(
        "Facultad de Sistemas",
        "Facultad de Odontología",
        "Biblioteca Central",
        "Ing. Metalúrgica",
        "Ing. Geológica",
        "Ing. Industrial",
        "Puerta 2",
        "Puerta 3",
        "Puerta 7"
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

                    nextStopText = nombresParaderos.getOrElse(i) { "Paradero interno" }

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

        Spacer(modifier = Modifier.height(12.dp))

        // Selector horizontal interactivo de paraderos para centrar el mapa al hacer clic
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(nombresParaderos.size) { index ->
                val puntoParadero = rutaDetallada[index]
                AssistChip(
                    onClick = {
                        mapViewReference?.controller?.animateTo(puntoParadero)
                        mapViewReference?.controller?.setZoom(17.5)
                    },
                    label = { Text(nombresParaderos[index], fontSize = 11.sp) },
                    colors = AssistChipDefaults.assistChipColors(containerColor = Color.White)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

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

                        if (hasLocationPermission) {
                            val locationProvider = GpsMyLocationProvider(ctx).apply {
                                addLocationSource(android.location.LocationManager.NETWORK_PROVIDER)
                            }

                            val locationOverlay = MyLocationNewOverlay(locationProvider, this).apply {
                                enableMyLocation()
                                disableFollowLocation()
                            }
                            overlays.add(locationOverlay)
                            myLocationOverlayRef = locationOverlay
                        }
                    }
                },
                update = { mapView ->
                    mapView.overlays.removeAll { it is Marker || it is Polyline }
                    val ctx = mapView.context

                    // 1. Trazar la línea de la ruta en el mapa (Polyline)
                    val lineaRuta = Polyline().apply {
                        setPoints(rutaDetallada)
                        outlinePaint.color = android.graphics.Color.parseColor("#800020") // Guinda institucional
                        outlinePaint.strokeWidth = 9f
                    }
                    mapView.overlays.add(lineaRuta)

                    // 2. Marcadores fijos de facultades y paraderos
                    mapView.overlays.add(Marker(mapView).apply {
                        position = ptoSistemas
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        title = "Facultad de Ing. de Sistemas"
                        snippet = "Paradero zona norte"
                        icon = scaleIcon(ctx, R.drawable.fisilogo, 40, 40)
                    })

                    mapView.overlays.add(Marker(mapView).apply {
                        position = ptoMetalurgica
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        title = "Facultad de Ing. Metalúrgica"
                        snippet = "Paradero zona comedor"
                        icon = scaleIcon(ctx, R.drawable.metalurgicalogo, 40, 40)
                    })

                    mapView.overlays.add(Marker(mapView).apply {
                        position = ptoZonaPuerta2
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        title = "Paradero Puerta 2"
                        snippet = "Zona de entrada del campus"
                        icon = scaleIcon(ctx, R.drawable.puertadoslogo, 40, 40)
                    })

                    mapView.overlays.add(Marker(mapView).apply {
                        position = ptoBiblioteca
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        title = "Biblioteca Central"
                        snippet = "Zona académica principal"
                        icon = scaleIcon(ctx, R.drawable.bibliotecalogo, 40, 40)
                    })

                    mapView.overlays.add(Marker(mapView).apply {
                        position = ptoOdontologia
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        title = "Facultad de Odontología"
                        snippet = "Zona académica principal"
                        icon = scaleIcon(ctx, R.drawable.odontologialogo, 40, 40)
                    })

                    mapView.overlays.add(Marker(mapView).apply {
                        position = ptoComedor
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        title = "Comedor Universitario"
                        snippet = "Paradero zona de abastos"
                        icon = scaleIcon(ctx, R.drawable.comedorlogo, 40, 40)
                    })

                    mapView.overlays.add(Marker(mapView).apply {
                        position = ptoIndustrial
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                        title = "Facultad de Ing. Industrial"
                        snippet = "Paradero zona sur"
                        icon = scaleIcon(ctx, R.drawable.industriallogo, 40, 40)
                    })

                    mapView.overlays.add(Marker(mapView).apply {
                        position = ptoZonaPuerta3
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                        title = "Paradero Puerta 3"
                        snippet = "Zona de entrada del campus"
                        icon = scaleIcon(ctx, R.drawable.puertatreslogo, 40, 40)
                    })

                    mapView.overlays.add(Marker(mapView).apply {
                        position = ptoGeologica
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                        title = "Facultad de Ing. Geológica"
                        snippet = "Paradero zona suroeste"
                        icon = scaleIcon(ctx, R.drawable.geologicalogo, 40, 40)
                    })

                    mapView.overlays.add(Marker(mapView).apply {
                        position = ptoZonaPuerta7
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                        title = "Paradero Puerta 7"
                        snippet = "Zona de entrada del campus"
                        icon = scaleIcon(ctx, R.drawable.puertasietelogo, 40, 40)
                    })

                    // 3. Bus Burrito en movimiento
                    val busMarker = Marker(mapView).apply {
                        position = busPosition
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        title = "Bus Burrito 🚌"
                        snippet = "En recorrido interno por el campus"
                        icon = scaleIcon(ctx, R.drawable.burritologo, 44, 22)
                    }
                    mapView.overlays.add(busMarker)

                    mapView.invalidate()
                },
                modifier = Modifier.fillMaxSize()
            )

            // Botón flotante superior para seguir al Bus Burrito en tiempo real
            FloatingActionButton(
                onClick = {
                    mapViewReference?.controller?.animateTo(busPosition)
                    mapViewReference?.controller?.setZoom(17.5)
                },
                containerColor = RedFiGuinda,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 80.dp, end = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DirectionsBus,
                    contentDescription = "Seguir Bus Burrito"
                )
            }

            // Botón flotante inferior para centrar en la ubicación propia del usuario
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

            // Etiqueta flotante superior de estado GPS
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

// Función auxiliar para escalar manteniendo la proporción original (sin deformar)
fun scaleIcon(context: Context, drawableId: Int, targetWidth: Int, targetHeight: Int): Drawable {
    val drawable = ContextCompat.getDrawable(context, drawableId)!!
    val bitmap = if (drawable is BitmapDrawable) {
        drawable.bitmap
    } else {
        val bmp = Bitmap.createBitmap(
            if (drawable.intrinsicWidth > 0) drawable.intrinsicWidth else targetWidth,
            if (drawable.intrinsicHeight > 0) drawable.intrinsicHeight else targetHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bmp)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        bmp
    }

    val originalWidth = bitmap.width.toFloat()
    val originalHeight = bitmap.height.toFloat()

    var newWidth = targetWidth.toFloat()
    var newHeight = targetHeight.toFloat()

    if (originalWidth > originalHeight) {
        newHeight = (originalHeight * targetWidth) / originalWidth
    } else {
        newWidth = (originalWidth * targetHeight) / originalHeight
    }

    val scaledBitmap = Bitmap.createScaledBitmap(bitmap, newWidth.toInt(), newHeight.toInt(), true)
    return BitmapDrawable(context.resources, scaledBitmap)
}