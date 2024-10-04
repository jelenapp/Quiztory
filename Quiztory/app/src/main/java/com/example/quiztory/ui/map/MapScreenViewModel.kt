import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quiztory.ui.list.HistoricalLocation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MapScreenViewModel (
    private val fusedLocationClient: FusedLocationProviderClient,
    private val appContext: Context
) : ViewModel() {

    private val _userLocation = MutableStateFlow<Location?>(null)
    val userLocation: StateFlow<Location?> = _userLocation

    private val _historicalLocations = MutableStateFlow<List<HistoricalLocation>>(emptyList())
    val historicalLocations: StateFlow<List<HistoricalLocation>> = _historicalLocations

    private val _userAddedLocations = MutableStateFlow<List<HistoricalLocation>>(emptyList())
    val userAddedLocations: StateFlow<List<HistoricalLocation>> = _userAddedLocations

    init {
        loadHistoricalLocations()
        loadUserHistoricalLocations()

    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            appContext,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    var lat = mutableStateOf(43.321445)  // Inicijalno postavljeno na Niš, kasnije se ažurira
        private set
    var lng = mutableStateOf(21.896104)  // Inicijalno postavljeno na Niš, kasnije se ažurira
        private set

    fun addHistoricalLocationToFirestore(location: HistoricalLocation) {
        val firestore = FirebaseFirestore.getInstance()
        val documentReference = if (location.id != null) {
            firestore.collection("historical_locations").document(location.id.toString())
        } else {
            firestore.collection("historical_locations").document()
        }

        documentReference.set(location)
            .addOnSuccessListener {
                //Log.d("Firestore", "Lokacija uspešno dodata: ${location.title}")
            }
            .addOnFailureListener { e ->
                //Log.e("Firestore", "Greška prilikom dodavanja lokacije: ", e)
            }
    }

    fun loadUserLocation(onPermissionDenied: () -> Unit) {
        viewModelScope.launch {
            if (hasLocationPermission()) {
                try {
                    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                        location?.let {
                            lat.value = it.latitude
                            lng.value = it.longitude
                            _userLocation.value = it
                        }
                    }
                } catch (e: SecurityException) {
                    e.printStackTrace()
                }
            } else {
                onPermissionDenied()
            }
        }
    }

    private fun loadUserHistoricalLocations() {
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("users_historical_locations")
            .get()
            .addOnSuccessListener { result ->
                val locations = result.mapNotNull { document ->
                    document.toObject(HistoricalLocation::class.java)
                }
                _userAddedLocations.value = locations
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Greška prilikom učitavanja lokacija: ", e)
            }
    }

    fun loadHistoricalLocations() {
        viewModelScope.launch {
            val preloadedLocations = listOf(
                HistoricalLocation(
                    id = 1L.toString(),
                    title = "Spomenik oslobodiocima Nisa",
                    description = "Trg kralja Milana",
                    // position = LatLng(43.32224428625793, 21.895896158653333)
                    latitude = 43.32224428625793,
                    longitude = 21.895896158653333
                    //  , question = ""
                ),
                HistoricalLocation(
                    id = "2L",
                    title = "Trg kralja Milana",
                    description = "Generala Milojka Lesjanina 8, Nis",
                    latitude = 43.3233682386596,
                    longitude = 21.896067821731087
                    //, question = ""
                ),
                HistoricalLocation(
                    id = "3L",
                    title = "Niska tvrdjava",
                    description = "Djuke Dinic, Nis",
                    latitude = 43.32723947110277,
                    longitude = 21.89546700694617
                    //, question = ""

                ),
                HistoricalLocation(
                    id = "4L",
                    title = "Park Svetog Save",
                    description = "Pariske Komune 11, Nis",
                    latitude = 43.3219055325647,
                    longitude = 21.91938212968041
                    //, question = ""

                ),
                HistoricalLocation(
                    id = " 5L",
                    title = "Cele Kula",
                    description = "Bulevar dr Zorana Djindjica, Nis",
                    latitude = 43.31353763111081,
                    longitude = 21.922901187706334
                    // , question = ""

                ),
                HistoricalLocation(
                    id = "6L",
                    title = "Spomenik Stevanu Sremcu i Kalci",
                    description = "Kopitareva, Nis",
                    latitude = 43.31891327204544,
                    longitude = 21.895280939483666
                    //, question = ""

                ),
                HistoricalLocation(
                    id = "7L",
                    title = "Spomenik palim vazduhoplovcima",
                    description = "Episkopska, Nis",
                    latitude = 43.314835237721184,
                    longitude = 21.89508144220371
                    //, question = ""

                ),
                HistoricalLocation(
                    id = "8L",
                    title = "Spomenik poginulim Crvenoarmejcima",
                    description = "Trg Kralja Aleksandra Ujedinitelja 11, Nis",
                    latitude = 43.318287722136866,
                    longitude = 21.890703826861937
                    // , question = ""

                ),
                HistoricalLocation(
                    id = "9L",
                    title = "Spomenik Sabanu Bajramovicu",
                    description = "Nisavski kej, Nis",
                    latitude = 43.32356292937282,
                    longitude = 21.897844084713718
                    //, question = ""

                ),
                HistoricalLocation(
                    id = "10L",
                    title = "Test",
                    description = "Pirot",
                    latitude = 43.16276273763927,
                    longitude = 22.600519012015237
                    //, question = ""

                )

            )
            _historicalLocations.value = preloadedLocations
            // Dodajem svaku lokaciju u Firestore
            for (location in preloadedLocations) {
                addHistoricalLocationToFirestore(location)
            }

        }
    }

    // Funkcija za ažuriranje liste istorijskih lokacija
    fun addHistoricalLocation(location: HistoricalLocation) {
        _historicalLocations.value = _historicalLocations.value + location
    }

    fun addUserHistoricalLocation(location: HistoricalLocation) {
        _userAddedLocations.value = _userAddedLocations.value + location
    }

    fun checkProximityToLocations(
        userLocation: Location,
        historicalLocations: List<HistoricalLocation>
    ): Boolean {
        val thresholdDistance = 50  // Prag u metrima

        for (location in historicalLocations) {
            val locationPosition = Location("").apply {
                latitude = location.latitude
                longitude = location.longitude
            }

            val distance = userLocation.distanceTo(locationPosition)

            if (distance <= thresholdDistance) {
                // Korisnik je u blizini lokacije, može da odgovori na pitanje
                return true
            }
        }
        return false
    }


    companion object {
        var lat = mutableDoubleStateOf(43.321445)
            private set
        var lng = mutableDoubleStateOf(21.896104)
            private set
        private var INSTANCE: MapScreenViewModel? = null

        fun getInstance(
            context: Context,
            fusedLocationClient: FusedLocationProviderClient
        ): MapScreenViewModel {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: MapScreenViewModel(
                    fusedLocationClient = fusedLocationClient,
                    appContext = context.applicationContext // Koristi applicationContext da izbegneš memory leak
                ).also { INSTANCE = it }
            }
        }

        fun setCoordinates(latLng: LatLng) {
            lat.value = latLng.latitude
            lng.value = latLng.longitude
        }
    }
}
