package com.example.quiztory.ui.list
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

data class HistoricalLocation(
    val id: String = "",
    val title: String,
    val name: String = "",
    val description: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val question: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
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

    private val firestore = FirebaseFirestore.getInstance()

    init {
        fetchHistoricalLocations()
    }

    private fun fetchHistoricalLocations() {
        viewModelScope.launch {
            firestore.collection("historical_locations")
                .get()
                .addOnSuccessListener { documents ->
                    val locations = documents.map { document ->
                        HistoricalLocation(
                            id = (document.getLong("id") ?: 0).toString(),
                            name = document.getString("name") ?: "",
                            description = document.getString("description") ?: "",
                            latitude = document.getDouble("latitude") ?: 0.0,
                            longitude = document.getDouble("longitude") ?: 0.0,
                            title = document.getString("title") ?: "",
                            )
                    }
                    _historicalLocations.value = locations
                }
                .addOnFailureListener { exception ->
                    // Obradi grešku ako je potrebno
                }
        }
    }
}
