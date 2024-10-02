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
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class HistoricalLocation(
    val id: Long,
    val title: String,
    val description: String,
    val position: LatLng
)

class MapScreenViewModel (
    private val fusedLocationClient: FusedLocationProviderClient,
    private val appContext: Context // Kontekst aplikacije
) : ViewModel() {

    // Trenutna lokacija korisnika
    private val _userLocation = MutableStateFlow<Location?>(null)
    val userLocation: StateFlow<Location?> = _userLocation

    // Lista istorijskih lokacija
    private val _historicalLocations = MutableStateFlow<List<HistoricalLocation>>(emptyList())
    val historicalLocations: StateFlow<List<HistoricalLocation>> = _historicalLocations
    init {
        loadHistoricalLocations() // Pozivanje funkcije prilikom inicijalizacije ViewModel-a
    }
    // Provera dozvola
    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            appContext,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
    // Latitude i longitude su sada u okviru instance ViewModel-a
    var lat = mutableStateOf(43.321445)  // Inicijalno postavljeno na Niš, kasnije se ažurira
        private set
    var lng = mutableStateOf(21.896104)  // Inicijalno postavljeno na Niš, kasnije se ažurira
        private set

    fun addHistoricalLocationToFirestore(location: HistoricalLocation) {
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("historical_locations")
            .document(location.id.toString()) // Možete koristiti ID ili generisati novi
            .set(location)
            .addOnSuccessListener {
                Log.d("Firestore", "Lokacija uspešno dodata: ${location.title}")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Greška prilikom dodavanja lokacije: ", e)
            }
    }
    // Učitavanje trenutne lokacije korisnika uz proveru dozvola
    fun loadUserLocation(onPermissionDenied: () -> Unit) {
        viewModelScope.launch {
            if (hasLocationPermission()) {
                try {
                    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                        location?.let {
                            // Ažuriraj latitude i longitude prema korisnikovoj trenutnoj lokaciji
                            lat.value = it.latitude
                            lng.value = it.longitude
                            _userLocation.value = it
                        }
                    }
                } catch (e: SecurityException) {
                    // Obrada SecurityException u slučaju da korisnik odbije dozvolu u runtime-u
                    e.printStackTrace()
                }
            } else {
                // Ako dozvola nije odobrena, zovi callback za traženje dozvole
                onPermissionDenied()
            }
        }
    }

    // Dodavanje nove istorijske lokacije
    fun addHistoricalLocation(location: HistoricalLocation) {
        viewModelScope.launch {
            val updatedList = _historicalLocations.value.toMutableList()
            updatedList.add(location)
            _historicalLocations.value = updatedList
        }
    }

    // Učitavanje unapred definisanih istorijskih lokacija (demo podaci, mogu se zameniti podacima iz Firebase-a)
    fun loadHistoricalLocations() {
        viewModelScope.launch {
            val preloadedLocations = listOf(
                HistoricalLocation(
                    id = 1L,
                    title = "Spomenik oslobodiocima Nisa",
                    description = "Trg kralja Milana",
                    position = LatLng(43.32224428625793, 21.895896158653333)
                ),
                HistoricalLocation(
                    id = 2L,
                    title = "Trg kralja Milana",
                    description = "Generala Milojka Lesjanina 8, Nis",
                    position = LatLng(43.3233682386596, 21.896067821731087)
                ),
                HistoricalLocation(
                    id = 3L,
                    title = "Niska tvrdjava",
                    description = "Djuke Dinic, Nis",
                    position = LatLng(43.32723947110277, 21.89546700694617)
                ),
                HistoricalLocation(
                    id = 4L,
                    title = "Park Svetog Save",
                    description = "Pariske Komune 11, Nis",
                    position = LatLng(43.3219055325647, 21.91938212968041)
                ),
                HistoricalLocation(
                    id = 5L,
                    title = "Cele Kula",
                    description = "Bulevar dr Zorana Djindjica, Nis",
                    position = LatLng(43.31353763111081, 21.922901187706334)
                ),
                HistoricalLocation(
                    id = 6L,
                    title = "Spomenik Stevanu Sremcu i Kalci",
                    description = "Kopitareva, Nis",
                    position = LatLng(43.31891327204544, 21.895280939483666)
                ),
                HistoricalLocation(
                    id = 7L,
                    title = "Spomenik palim vazduhoplovcima",
                    description = "Episkopska, Nis",
                    position = LatLng(43.314835237721184, 21.89508144220371)
                ),
                HistoricalLocation(
                    id = 8L,
                    title = "Spomenik poginulim Crvenoarmejcima",
                    description = "Trg Kralja Aleksandra Ujedinitelja 11, Nis",
                    position = LatLng(43.318287722136866, 21.890703826861937)
                ),
                HistoricalLocation(
                    id = 9L,
                    title = "Spomenik Sabanu Bajramovicu",
                    description = "Nisavski kej, Nis",
                    position = LatLng(43.32356292937282, 21.897844084713718)
                ),
                HistoricalLocation(
                    id = 10L,
                    title = "Test",
                    description = "Pirot",
                    position = LatLng(43.16276273763927, 22.600519012015237)
                )

            )
            _historicalLocations.value = preloadedLocations
            // Dodajem svaku lokaciju u Firestore
            for (location in preloadedLocations) {
                addHistoricalLocationToFirestore(location)
            }
        }
    }
    fun checkProximityToLocations(userLocation: Location, historicalLocations: List<HistoricalLocation>): Boolean {
        val thresholdDistance = 50  // Prag u metrima

        for (location in historicalLocations) {
            val locationPosition = Location("").apply {
                latitude = location.position.latitude
                longitude = location.position.longitude
            }

            val distance = userLocation.distanceTo(locationPosition)

            if (distance <= thresholdDistance) {
                // Korisnik je u blizini lokacije, može da odgovori na pitanje
                return true
            }
        }
        return false
    }
    fun getLocationById(id: String): HistoricalLocation? {
        return _historicalLocations.value.find { it.id.toString() == id }
    }

    fun addPointsToUser(points: Int) {
        // Ažuriranje bodova korisnika u Firebase ili lokalnoj bazi
    }


    companion object {
        var lat = mutableDoubleStateOf(43.321445)
            private set
        var lng = mutableDoubleStateOf(21.896104)
            private set
        private var INSTANCE: MapScreenViewModel? = null

        fun getInstance(context: Context, fusedLocationClient: FusedLocationProviderClient): MapScreenViewModel {
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
