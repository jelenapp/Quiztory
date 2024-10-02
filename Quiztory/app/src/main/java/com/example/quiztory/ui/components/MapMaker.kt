package com.example.quiztory.ui.components

import MapScreenViewModel
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.quiztory.Screen
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState


@Composable
fun LocationMap(
    onMapLongClick:(LatLng)->Unit,
    viewModel: MapScreenViewModel,
    navController: NavController, // Dodaj navController kao parametar
) {

    val cevm = MapScreenViewModel
    val uiSettings by remember {
        mutableStateOf(
            MapUiSettings(
                zoomControlsEnabled = true,
                mapToolbarEnabled = true,
                scrollGesturesEnabled = true,
                tiltGesturesEnabled = false,
                scrollGesturesEnabledDuringRotateOrZoom = false,
                zoomGesturesEnabled = true,
                myLocationButtonEnabled = true,
                compassEnabled = true,
                rotationGesturesEnabled = true,
                indoorLevelPickerEnabled = false
            )
        )
    }
    val properties by remember {
        mutableStateOf(
            MapProperties(
                mapType = MapType.NORMAL,
                isMyLocationEnabled = true  // Omogućeno praćenje trenutne lokacije
            )
        )
    }
    val cameraPositionState by remember {
        mutableStateOf(
            CameraPositionState(
                CameraPosition(
                    LatLng(cevm.lat.doubleValue, cevm.lng.doubleValue),
                    15f,
                    0f,
                    0f
                )
            )
        )
    }
    Log.d("MapViewModel", "Lat: ${viewModel.lat.value}, Lng: ${viewModel.lng.value}")

    val historicalLocations by viewModel.historicalLocations.collectAsState()
    Column {
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = {
                // Navigacija na ekran liste istorijskih lokacija
                navController.navigate(Screen.HistoricalLocationsList.name)
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Prikaži istorijske lokacije")
        }
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            //.weight(1f),
            cameraPositionState = cameraPositionState,
            properties = properties,
            uiSettings = uiSettings,
            onMapClick = { latLng -> onMapLongClick(latLng) },
            onPOIClick = { POI -> onMapLongClick(POI.latLng) },

            ) {
            Marker(
                state = MarkerState(LatLng(viewModel.lat.value, viewModel.lng.value))
            )
            // Prikazivanje svih istorijskih lokacija kao markere
            historicalLocations.forEach { location ->
                Marker(
                    state = MarkerState(
                        LatLng(
                            location.position.latitude,
                            location.position.longitude
                        )
                    ),
                    title = location.title,
                    snippet = location.description,
                    onInfoWindowClick = {
                        Log.d("MarkerClick", "Navigating to quiz/${location.id}")
                        try {
                            // Navigacija ka QuizScreen pri kliku na marker
                            navController.navigate("quiz/${location.id}")
                        } catch (e: Exception) {
                            Log.e(
                                "NavigationError",
                                "Error navigating to quiz screen: ${e.localizedMessage}"
                            )
                        }
                    }
                )
            }
        }
    }
}