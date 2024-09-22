
import android.Manifest
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.quiztory.ui.components.NewEventLocationPreviewMap
import com.google.android.gms.location.LocationServices

@Composable
fun MapScreen(viewModel: MapScreenViewModel,
              navController: NavController,
              modifier: Modifier = Modifier,
              context: Context) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    // Permission launcher za runtime zahtev
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

    // Proveri i učitaj korisničku lokaciju
    LaunchedEffect(key1 = Unit) {
        viewModel.loadUserLocation {
            // Ako dozvola nije odobrena, zatraži dozvolu
            requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // Tvoja UI logika za prikaz mape i lokacija
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                9.dp,
                color = Color.Black
            )
    ) {
        NewEventLocationPreviewMap(navController, viewModelz)
    }
}