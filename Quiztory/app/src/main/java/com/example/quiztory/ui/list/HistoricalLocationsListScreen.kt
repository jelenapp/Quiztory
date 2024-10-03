package com.example.quiztory.ui.list
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

data class HistoricalLocation(
    val id: String = "",
    val title: String,
    val name: String = "",
    val description: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val question: String = "",
    val answers: List<String> = emptyList(), // Nova lista odgovora
    val correctAnswerIndex: Int = -1 // Indeks tačnog odgovora

){
    constructor() : this(
        id = "",
        title = "",
        name = "",
        description = "",
        latitude = 0.0,
        longitude = 0.0,
        question = "",
        answers = emptyList(),
        correctAnswerIndex = -1
    )
}

@Composable
fun HistoricalLocationsScreen(navController: NavController) {
    val viewModel = remember { HistoricalLocationsViewModel() }
    val locations = viewModel.historicalLocations.collectAsState(initial = emptyList())

    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(20.dp))
        Text("Istorijske lokacije", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(16.dp))

        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(locations.value) { location ->
                ListItem(
                    modifier = Modifier.clickable {
                        navController.navigate("quiz/${location.id}")
                    },
                    headlineContent = { Text(location.title) },
                    supportingContent = { Text(location.description) }
                )
            }
        }
    }
}

class HistoricalLocationsViewModel : ViewModel() {
    private val _historicalLocations = MutableStateFlow<List<HistoricalLocation>>(emptyList())
    val historicalLocations: StateFlow<List<HistoricalLocation>> = _historicalLocations
    private val _userAddedLocations = MutableStateFlow<List<HistoricalLocation>>(emptyList())
    val userAddedLocations: StateFlow<List<HistoricalLocation>> = _userAddedLocations

    private val firestore = FirebaseFirestore.getInstance()

    init {
        fetchHistoricalLocations()
        fetchUserAddedLocations()
    }
    private fun fetchHistoricalLocations() {
        viewModelScope.launch {
            val historicalLocations = fetchLocationsFromCollection("historical_locations")
            //val userAddedLocations = fetchLocationsFromCollection("users_historical_locations")

            // Kombinovanje rezultata
            val allLocations = historicalLocations //+ userAddedLocations
            //displayMarkersOnMap(map, allLocations)
            // Postavljanje svih lokacija u MutableState
            _historicalLocations.value = allLocations
            Log.e("Učitane lokacije"," ${_historicalLocations.value}")

        }
    }
    private fun fetchUserAddedLocations() {
        viewModelScope.launch {
            Log.d("FetchUserAddedLocations", "Fetching user-added locations...")
            firestore.collection("users_historical_locations").get()
                .addOnSuccessListener { documents ->
                    Log.d("FetchUserAddedLocations", "Successfully fetched user-added locations. Document count: ${documents.size()}")
                    // Pretvori dokumente u listu korisničkih istorijskih lokacija
                    val userLocations = documents.map { document ->
                        HistoricalLocation(
                            id = (document.getLong("id") ?: 0).toString(),
                            name = document.getString("name") ?: "",
                            description = document.getString("description") ?: "",
                            latitude = document.getDouble("latitude") ?: 0.0,
                            longitude = document.getDouble("longitude") ?: 0.0,
                            title = document.getString("title") ?: "",
                            answers = (document.get("answers") as? List<String>) ?: emptyList(),
                            correctAnswerIndex = (document.getLong("correctAnswerIndex")?.toInt() ?: -1)
                        )
                    }
                    if (userLocations.isNotEmpty()) {
                        Log.d("FetchUserAddedLocations", "User locations: $userLocations")
                    } else {
                        Log.d("FetchUserAddedLocations", "No user-added locations found.")
                    }
                    // Kombinuj korisničke lokacije i hardkodirane lokacije
                    //val combinedLocations = _historicalLocations.value + userLocations
                    //_historicalLocations.value = combinedLocations // Ažuriraj stanje sa kombinovanim lokacijama
                    _userAddedLocations.value = userLocations // Sačuvaj korisničke lokacije
                }
                .addOnFailureListener { exception ->
                    // Logovanje greške
                    Log.e("FetchUserAddedLocations", "Error getting user locations: ", exception)
                }
        }
    }


    private suspend fun fetchLocationsFromCollection(collectionName: String): List<HistoricalLocation> {
        Log.d("FetchLocations", "fetchLocationsFromCollection pozvana za kolekciju: $collectionName")
        return suspendCancellableCoroutine { continuation ->
            firestore.collection(collectionName).get()
                .addOnSuccessListener { documents ->
                    Log.d("FetchLocations", "Dokumenti učitani iz Firestore-a")
                    val locations = documents.map { document ->
                        HistoricalLocation(
                            id = (document.getLong("id") ?: 0).toString(),
                            name = document.getString("name") ?: "",
                            description = document.getString("description") ?: "",
                            latitude = document.getDouble("latitude") ?: 0.0,
                            longitude = document.getDouble("longitude") ?: 0.0,
                            title = document.getString("title") ?: "",
                            answers = (document.get("answers") as? List<String>) ?: emptyList(),
                            correctAnswerIndex = (document.getLong("correctAnswerIndex")?.toInt() ?: -1)
                        )
                    }
                    continuation.resume(locations)
                    Log.d("FetchLocations", "Učitane korisničke lokacije: $locations")
                }

                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                    Log.e("FetchLocations", "Greška prilikom učitavanja korisničkih lokacija: ${exception.message}")
                }
        }
    }
    fun displayMarkersOnMap(map: GoogleMap, locations: List<HistoricalLocation>) {
        // Prolazimo kroz sve lokacije i dodajemo markere na mapu
        for (location in locations) {
            val latLng = LatLng(location.latitude, location.longitude)
            map.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(location.name)
                    .snippet(location.description)
            )
        }
    }

//    private fun fetchHistoricalLocations() {
//        viewModelScope.launch {
//            firestore.collection("historical_locations").get()
//                .addOnSuccessListener { documents ->
//                    val locations = documents.map { document ->
//                        HistoricalLocation(
//                            id = (document.getLong("id") ?: 0).toString(),
//                            name = document.getString("name") ?: "",
//                            description = document.getString("description") ?: "",
//                            latitude = document.getDouble("latitude") ?: 0.0,
//                            longitude = document.getDouble("longitude") ?: 0.0,
//                            title = document.getString("title") ?: "",
//                            answers = (document.get("answers") as? List<String>) ?: emptyList(), // Učitavanje odgovora
//                            correctAnswerIndex = (document.getLong("correctAnswerIndex")?.toInt() ?: -1) // Učitavanje indeksa tačnog odgovora
//
//                        )
//                    }
//                    _historicalLocations.value = locations
//                }
//                .addOnFailureListener { exception ->
//                }
//
//        }
//    }
}
