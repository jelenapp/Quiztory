package com.example.quiztory
//import com.example.quiztory.services.location.LocationService

import MapScreen
import MapScreenViewModel
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.quiztory.services.location.LocationService
import com.example.quiztory.ui.StartScreen.StartScreen
//import com.example.quiztory.ui.components.AddEventLocationMap
import com.example.quiztory.ui.components.LocationMap
import com.example.quiztory.ui.list.HistoricalLocationsScreen
import com.example.quiztory.ui.login.SignInScreen
import com.example.quiztory.ui.quiz.QuizScreen
import com.example.quiztory.ui.signup.SignUpScreen
import com.example.quiztory.ui.theme.QuiztoryTheme
import com.google.android.gms.location.LocationServices
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    private val LOCATION_PERMISSION_REQUEST_CODE = 1000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       FirebaseApp.initializeApp(this)

       enableEdgeToEdge()

        setContent {
            QuiztoryTheme {
                QuiztoryApp(this)
            }
        }
    }
    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            // Ako dozvola nije dodeljena, zatražite je
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            // Ako su dozvole dodeljene, startujte servis
            startLocationService()
        }
    }

    private fun startLocationService() {
        val intent = Intent(this, LocationService::class.java)
        startService(intent)
    }

    private fun stopLocationService() {
        val intent = Intent(this, LocationService::class.java)
        stopService(intent)
    }

}

@Composable
fun QuiztoryApp(context: Context) {
    val navController = rememberNavController()
    val createEventVM = MapScreenViewModel

    Box(modifier = Modifier.fillMaxSize()) {
        Surface(modifier = Modifier.fillMaxSize()) {
            NavHost(navController = navController, startDestination = Screen.Start.name) {
                composable("quiz/{locationId}/{isUserLocation}") { backStackEntry ->
                    val locationId = backStackEntry.arguments?.getString("locationId")
                    val isUserLocation = backStackEntry.arguments?.getString("isUserLocation")?.toBoolean() ?: false
                    QuizScreen(locationId = locationId, navController = navController, isUserLocation = isUserLocation)
                }

                composable(Screen.SignIn.name) {
                    SignInScreen(navController = navController, context = context)
                }
                composable(Screen.SignUp.name) {
                    SignUpScreen(navController = navController, context = context)
                }
                composable(Screen.Start.name) {
                    StartScreen(navController = navController)
                }
                composable(Screen.HistoricalLocationsList.name) {
                    HistoricalLocationsScreen(navController = navController)
                }
                composable(Screen.Map.name) {
                    val context = LocalContext.current
                    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
                    val viewModel = remember { MapScreenViewModel.getInstance(context, fusedLocationClient) }
                    MapScreen(viewModel = viewModel, navController = navController, context = context)
                }

                composable(route = Screen.LocationMap.name) {
                    val mapScreenViewModel: MapScreenViewModel= viewModel()
                    LocationMap(
                        onMapLongClick = { latLng ->
                            createEventVM.setCoordinates(latLng)
                            //navController.navigate(Screen.CreateEvent.name)
                        },
                        viewModel = mapScreenViewModel,
                        navController=navController
                    )
                }
                composable(
                    route = "quiz/{locationId}",
                    arguments = listOf(navArgument("locationId") { type = NavType.LongType }
                        //navArgument("userId") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val locationId = backStackEntry.arguments?.getString("locationId")
                    //val userId = backStackEntry.arguments?.getString("userId")
                    if (locationId != null ) {
                        QuizScreen(locationId = locationId, navController = navController, isUserLocation = true)
                    }
                }
            }
        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp), // Dodavanje padding-a ako je potrebno
            verticalArrangement = Arrangement.spacedBy(8.dp) // Razmak između dugmadi
        ) {
            Button(onClick = {
                val intent = Intent(context, LocationService::class.java)
                context.startService(intent)

            }) {
                Text("Start Service")
            }
            Button(onClick = {
                val intent = Intent(context, LocationService::class.java)
                context.stopService(intent)
            }) {
                Text("Stop Service")
            }
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            )
        }
    }
}
enum class Screen {
    SignIn,
    SignUp,
    Start,
    Map,
    CreateEvent,
    Quiz,
    HistoricalLocationsList,
    LocationMap
}
