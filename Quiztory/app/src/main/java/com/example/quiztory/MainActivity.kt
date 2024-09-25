package com.example.quiztory
import MapScreen
import MapScreenViewModel
import com.google.firebase.FirebaseApp
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
//import com.example.quiztory.services.location.LocationService
import com.example.quiztory.ui.login.SignInScreen
import com.example.quiztory.ui.StartScreen.StartScreen
import com.example.quiztory.ui.components.AddEventLocationMap
import com.example.quiztory.ui.components.NavigationBar
import com.example.quiztory.ui.signup.SignUpScreen
import com.example.quiztory.ui.theme.QuiztoryTheme
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.FirebaseFirestore
import android.Manifest

import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import com.example.quiztory.services.location.LocationService


class MainActivity : ComponentActivity() {
    private val LOCATION_PERMISSION_REQUEST_CODE = 1000 // Definišite konstantu za kod zahteva
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContent {
//            MainScreen(
//                onStartService = { checkLocationPermission() }, // Pozovite funkciju ovde
//                onStopService = { stopLocationService() }
//            )
//        }
       // createNotificationChannel()
       FirebaseApp.initializeApp(this)
       // firestore = FirebaseFirestore.getInstance()

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
    //val context = LocalContext.current

    val navController = rememberNavController()
    val createEventVM = MapScreenViewModel
    val navigationBar = NavigationBar(
        navigateToHomePage = {
            navController.popBackStack(Screen.Home.name, inclusive = true)
            navController.navigate(Screen.Home.name)
        },
        navigateToFilterPage = {
            navController.popBackStack(Screen.Filter.name, inclusive = true)
            navController.navigate(Screen.Filter.name)
        },
        navigateToEventRemindersPage = {
            navController.popBackStack(Screen.EventReminders.name, inclusive = true)
            navController.navigate(Screen.EventReminders.name)
        },
        navigateToFriendsPage = {
            navController.popBackStack(Screen.FriendsList.name, inclusive = true)
            navController.navigate(Screen.FriendsList.name)
        },
        navigateToProfilePage = {
            navController.popBackStack(Screen.Profile.name, inclusive = true)
            navController.navigate(Screen.Profile.name)
        },
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Surface(modifier = Modifier.fillMaxSize()) {
//            MainScreen(
//                onStartService = {
//                    val intent = Intent(context, LocationService::class.java)
//                    context.startService(intent)
//                },
//                onStopService = {
//                    val intent = Intent(context, LocationService::class.java)
//                    context.stopService(intent)
//                }
//            )
            NavHost(navController = navController, startDestination = Screen.Start.name) {

                composable(Screen.SignIn.name) {
                    SignInScreen(navController = navController, context = context)
                }
                composable(Screen.SignUp.name) {
                    SignUpScreen(navController = navController, context = context)
                }
                composable(Screen.Start.name) {
                    StartScreen(navController = navController)
                }
                composable(Screen.Map.name) {
                    // Uzimanje konteksta i instanciranja ViewModel-a
                    val context = LocalContext.current
                    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

                    // Instanciraj MapScreenViewModel
                    val viewModel = remember { MapScreenViewModel.getInstance(context, fusedLocationClient) }

                    // Pozovi MapScreen
                    MapScreen(viewModel = viewModel, navController = navController, context = context)
                }

                composable(
                    route = "${Screen.Event.name}/{event}",
                    arguments = listOf(navArgument("event") { type = NavType.StringType })
                ) { backStackEntry ->
                    val eventJson = backStackEntry.arguments?.getString("event")
                    //val event = Gson().fromJson(eventJson, Event::class.java)
                  //  event?.let { DrawEventPage(event = it,navController = navController,context = context) }

                }
                composable(route = Screen.AddEventMapLocation.name) {
                    val mapScreenViewModel: MapScreenViewModel= viewModel()
                    AddEventLocationMap(
                        onMapLongClick = { latLng ->
                            createEventVM.setCoordinates(latLng)
                            navController.navigate(Screen.CreateEvent.name)
                        },
                        viewModel = mapScreenViewModel
                    )
                }
            }
        }
//        Column(
//            modifier = Modifier.fillMaxSize(),
//            verticalArrangement = Arrangement.Center,
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Button(onClick = onStartService) {
//                Text("Start Service")
//            }
//            Button(onClick = onStopService) {
//                Text("Stop Service")
//            }
//        }
        // Postavljanje dugmadi iznad mape
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
        }
    }
}
enum class Screen {
    EventReminders,//TODO: refactor this
    FriendsList,
    Home,
    Profile,
    Search,
    SignIn,
    SignUp,
    Start,
    Map,
    Filter,
    CreateEvent,
    OtherProfile,
    Event,
    AddEventMapLocation,
    EditProfile,
    Penalties,
    AddReminder,
    MyPosts
}
@Composable
fun MainScreen(onStartService: () -> Unit, onStopService: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = onStartService) {
            Text("Start Service")
        }
        Button(onClick = onStopService) {
            Text("Stop Service")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MainScreen(onStartService = {}, onStopService = {})
}