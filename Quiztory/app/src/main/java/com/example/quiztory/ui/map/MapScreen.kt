
import android.Manifest
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.quiztory.Screen
import com.example.quiztory.ui.components.LocationMap
//import com.example.quiztory.ui.components.AddEventLocationMap
import com.google.android.gms.location.LocationServices

@Composable
fun MapScreen(viewModel: MapScreenViewModel,
              navController: NavController,
              modifier: Modifier = Modifier,
              context: Context) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val requestLocationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            viewModel.loadUserLocation {
                Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.loadUserLocation {
            requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
    val userLocation by viewModel.userLocation.collectAsState()
    val historicalLocations by viewModel.historicalLocations.collectAsState()

    // Provera da li je lokacija učitana pre nego što se prikaže mapa
    if (userLocation != null) {
        LocationMap(
            onMapLongClick = { latLng ->
                navController.navigate(Screen.LocationMap.name)
            },
            viewModel = viewModel,
            navController=navController,
        )
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Loading map...")
        }
    }
    val viewModelInstance = remember { MapScreenViewModel(fusedLocationClient, context) }
}