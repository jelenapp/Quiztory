import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class HistoricalLocation(
    val id: Long,
    val title: String,
    val description: String,
    val position: LatLng
)

class MapScreenViewModel private constructor(
    private val fusedLocationClient: FusedLocationProviderClient,
    private val appContext: Context // Kontekst aplikacije
) : ViewModel() {

    // Trenutna lokacija korisnika
    private val _userLocation = MutableStateFlow<Location?>(null)
    val userLocation: StateFlow<Location?> = _userLocation

    // Lista istorijskih lokacija
    private val _historicalLocations = MutableStateFlow<List<HistoricalLocation>>(emptyList())
    val historicalLocations: StateFlow<List<HistoricalLocation>> = _historicalLocations

    // Provera dozvola
    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            appContext,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
    var lat = mutableDoubleStateOf(43.321445)
        private set
    var lng = mutableDoubleStateOf(21.896104)
        private set

    // Učitavanje trenutne lokacije korisnika uz proveru dozvola
    fun loadUserLocation(onPermissionDenied: () -> Unit) {
        viewModelScope.launch {
            if (hasLocationPermission()) {
                try {
                    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                        _userLocation.value = location
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
                    title = "Kalemegdan Fortress",
                    description = "Historic fortress in Belgrade.",
                    position = LatLng(44.8221, 20.4517)
                ),
                HistoricalLocation(
                    id = 2L,
                    title = "Novi Sad Fortress",
                    description = "Petrovaradin Fortress in Novi Sad.",
                    position = LatLng(45.2517, 19.8622)
                )
            )
            _historicalLocations.value = preloadedLocations
        }
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
    }
}
