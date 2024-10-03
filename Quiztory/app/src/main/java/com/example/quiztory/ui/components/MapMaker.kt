package com.example.quiztory.ui.components

import MapScreenViewModel
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.quiztory.Screen
import com.example.quiztory.ui.list.HistoricalLocation
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID



@Composable
fun LocationMap(
    onMapLongClick:(LatLng)->Unit,
    viewModel: MapScreenViewModel,
    navController: NavController,
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
                isMyLocationEnabled = true
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
    val useraddedlocations by viewModel.userAddedLocations.collectAsState()

    Log.d("HistoricalLocations", "Loaded locations: $historicalLocations")

    var showDialog by remember { mutableStateOf(false) }
    var locationTitle by remember { mutableStateOf("") }
    var locationQuestion by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = {
                    navController.navigate(Screen.HistoricalLocationsList.name)
                },
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Prikaži istorijske lokacije")
            }
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = properties,
                uiSettings = uiSettings,
                onMapClick = { latLng -> onMapLongClick(latLng) },
                onPOIClick = { POI -> onMapLongClick(POI.latLng) },

                ) {
                Marker(
                    state = MarkerState(LatLng(viewModel.lat.value, viewModel.lng.value)),
                    title = "Moja trenutna lokacija"

                )
                historicalLocations.forEach { location ->
                    Marker(
                        state = MarkerState(
                            LatLng(
                                location.latitude,
                                location.longitude
                            )
                        ),
                        title = location.title,
                        snippet = location.description,
                        onInfoWindowClick = {
                            Log.d("MarkerClick", "Navigating to quiz/${location.id}")

                            try {
                                navController.navigate("quiz/${location.id}")
                            } catch (e: Exception) {
                                Log.e(
                                    "NavigationError", "Error navigating to quiz screen: ${e.localizedMessage}"
                                )
                            }
                        }
                    )
                }
                useraddedlocations.forEach { location ->
                    Marker(
                        state = MarkerState(
                            LatLng(
                                location.latitude,
                                location.longitude
                            )
                        ),
                        title = location.title,
                        snippet = location.description,
                        onInfoWindowClick = {
                            Log.d("MarkerClick", "Navigating to quiz/${location.id}")

                            try {
                               // navController.navigate("quiz/${location.id}")
                            } catch (e: Exception) {
                                Log.e(
                                    "NavigationError", "Error navigating to quiz screen: ${e.localizedMessage}"
                                )
                            }
                        }
                    )
                }
            }
        }
        FloatingActionButton(
            onClick = {
                showDialog = true
            },
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Dodaj pitanje")
        }
        if (showDialog) {
            AlertDialog(
                onDismissRequest = {
                    showDialog = false
                },
                title = { Text(text = "Dodaj novu istorijsku lokaciju") },
                text = {
                    Column {
                        TextField(
                            value = locationTitle,
                            onValueChange = { locationTitle = it },
                            label = { Text("Naslov lokacije") }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        TextField(
                            value = locationQuestion,
                            onValueChange = { locationQuestion = it },
                            label = { Text("Pitanje za lokaciju") }
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        val answers = remember { mutableStateListOf("", "", "") } // Lista za odgovore
                        for (i in answers.indices) {
                            TextField(
                                value = answers[i],
                                onValueChange = { answers[i] = it },
                                label = { Text("Odgovor ${i + 1}") }
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Unos tačnog odgovora
                        var correctAnswerIndex by remember { mutableStateOf(0) }
                        Text(text = "Izaberite tačan odgovor:")
                        answers.forEachIndexed { index, answer ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = (correctAnswerIndex == index),
                                    onClick = { correctAnswerIndex = index }
                                )
                                Text(answer)
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            Log.d("AddLocation", "Attempting to add location: Title=$locationTitle, Question=$locationQuestion")

                            val currentLocation = LatLng(viewModel.lat.value, viewModel.lng.value)
                            val answers = listOf("Odgovor 1", "Odgovor 2", "Odgovor 3", "Odgovor 4") // Zamenite stvarnim odgovorima
                            val correctAnswerIndex = 0 // Pretpostavljamo da je prvi odgovor tačan

                            addHistoricalLocationToFirestore(currentLocation, locationTitle, locationQuestion, viewModel,answers, correctAnswerIndex)
                            showDialog = false
                        }
                    ) {
                        Text("Dodaj")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            showDialog = false
                        }
                    ) {
                        Text("Otkaži")
                    }
                }
            )
        }
    }
}
fun addHistoricalLocationToFirestore(latLng: LatLng, title: String, question: String, viewModel: MapScreenViewModel, answers: List<String>, correctAnswerIndex: Int, ) {
    val firestore = FirebaseFirestore.getInstance()
    // Proveri da li lokacija već postoji
    val existingLocations = viewModel.historicalLocations.value
    if (existingLocations.any { it.title == title }) {
        Log.d("Firestore", "Lokacija već postoji: $title")
        return // Prekidamo funkciju da ne bismo dodavali duplikate
    }
    // Kreiramo novu istorijsku lokaciju
    val historicalLocation = HistoricalLocation(
        id = UUID.randomUUID().toString(),  // Generišemo jedinstveni ID
        title = title,
        description = question,  // Ovde dodajemo pitanje kao description
        latitude = latLng.latitude,
        longitude = latLng.longitude,
        question = question,
        answers = answers, // Dodajemo listu odgovora
        correctAnswerIndex = correctAnswerIndex // Dodajemo indeks tačnog odgovora

    )
    firestore.collection("users_historical_locations")
        .document(historicalLocation.id) // Koristimo jedinstveni ID kao ključ
        .set(historicalLocation)
        .addOnSuccessListener {
            viewModel.addHistoricalLocation(historicalLocation)
            //viewModel.addUserHistoricalLocation(userr)
            Log.d("Firestore", "Lokacija uspešno dodata: ${historicalLocation.title}")
        }
        .addOnFailureListener { e ->
            Log.e("Firestore", "Greška prilikom dodavanja lokacije: ", e)
        }
}

