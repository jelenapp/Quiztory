package com.example.quiztory.ui.components


import MapScreenViewModel
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
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
    val properties by remember { mutableStateOf(MapProperties(mapType = MapType.NORMAL)) }

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

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = properties,
        uiSettings = uiSettings,
        onMapClick = {latLng -> onMapLongClick(latLng)},
        onPOIClick = {POI -> onMapLongClick(POI.latLng)}
    )
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
                    LatLng(cevm.lat.doubleValue, cevm.lng.doubleValue),
                    15f,
                    0f,
                    0f
                )
            )
        )
    }

    GoogleMap(
        modifier = Modifier
            .height(350.dp)
            .fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = properties,
        uiSettings = uiSettings,
        onMapLongClick = { navController.navigate(Screen.AddEventMapLocation.name) }
    ) {
        Marker(
            state = MarkerState(LatLng(cevm.lat.doubleValue, cevm.lng.doubleValue)),
            //title = cevm.title.value, // Naslov koji korisnik unosi
            // snippet = cevm.description.value // Snipet koji korisnik unosi
        )
    }
}

//@Composable
//fun EventLocationPreviewMap(event:Event,Height:Int = 250){
//    val uiSettings by remember { mutableStateOf(MapUiSettings(
//        zoomControlsEnabled = true,
//        mapToolbarEnabled = false,
//        scrollGesturesEnabled = true,
//        tiltGesturesEnabled = false,
//        scrollGesturesEnabledDuringRotateOrZoom = false,
//        zoomGesturesEnabled = true,
//        myLocationButtonEnabled = true,
//        compassEnabled = true,
//        rotationGesturesEnabled = true,
//        indoorLevelPickerEnabled = false
//    )) }
//    val properties by remember { mutableStateOf(MapProperties(
//        mapType = MapType.NORMAL,
//    )) }
//
//    val cameraPositionState by remember { mutableStateOf(
//        CameraPositionState(
//            CameraPosition(
//                LatLng(event.lat, event.lng),
//                15f,
//                0f,
//                0f
//            )
//        )
//    )}
//
//    GoogleMap(
//        modifier = Modifier
//            .height(Height.dp)
//            .fillMaxSize(),
//        cameraPositionState = cameraPositionState,
//        properties = properties,
//        uiSettings = uiSettings,
//    ){
//        Marker(
//            state = MarkerState(LatLng(event.lat, event.lng)),
//            //title = Marker
//        )
//    }
//}