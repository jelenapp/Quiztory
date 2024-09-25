package com.example.quiztory.ui.components


import HistoricalLocation
import MapScreenViewModel
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState

@Composable
fun AddEventLocationMap(
    onMapLongClick:(LatLng)->Unit,
    viewModel: MapScreenViewModel,
    navController: NavController, // Dodaj navController kao parametar
){

    val cevm = MapScreenViewModel
    val uiSettings by remember { mutableStateOf(MapUiSettings(
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
    )) }
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

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = properties,
        uiSettings = uiSettings,
        onMapClick = {latLng -> onMapLongClick(latLng)},
        onPOIClick = {POI -> onMapLongClick(POI.latLng)},

    ) {
        Marker(
            state = MarkerState(LatLng(viewModel.lat.value, viewModel.lng.value))
        )
        // Prikazivanje svih istorijskih lokacija kao markere
        historicalLocations.forEach { location ->
            Marker(
                state = MarkerState(LatLng(location.position.latitude, location.position.longitude)),
                title = location.title,
                snippet = location.description,
                onInfoWindowClick = {
                    // Kada korisnik klikne na marker, prikaz kviz pitanja
                    //navController.navigate("quiz/${location.id}")
                    navController.navigate(Screen.Quiz.name)
                }
            )
    }
}
@Composable
fun NewEventLocationPreviewMap(navController: NavController, cevm: MapScreenViewModel) {
    val uiSettings by remember {
        mutableStateOf(
            MapUiSettings(
                zoomControlsEnabled = true,
                mapToolbarEnabled = false,
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
            )
        )
    }

    val cameraPositionState by remember {
        mutableStateOf(
            CameraPositionState(
                CameraPosition(
                    LatLng(cevm.lat.value, cevm.lng.value),
                    15f,
                    0f,
                    0f
                )
            )
        )
    }

    GoogleMap(
        modifier = Modifier
            //.height(350.dp)
            .fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = properties,
        uiSettings = uiSettings,
       // onPOIClick = { poi -> onMapLongClick(poi.latLng) },
        onMapLongClick = { navController.navigate(Screen.AddEventMapLocation.name) }
    ) {
        Marker(
            state = MarkerState(LatLng(cevm.lat.value, cevm.lng.value)),
            //title = cevm.title.value, // Naslov koji korisnik unosi
            // snippet = cevm.description.value // Snipet koji korisnik unosi
        )
    }
}
}

