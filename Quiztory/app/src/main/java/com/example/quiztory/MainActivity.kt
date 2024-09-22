package com.example.quiztory
import MapScreen
import MapScreenViewModel
import com.google.firebase.FirebaseApp
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.quiztory.ui.login.SignInScreen
import com.example.quiztory.ui.StartScreen.StartScreen
import com.example.quiztory.ui.components.NavigationBar
import com.example.quiztory.ui.signup.SignUpScreen
import com.example.quiztory.ui.theme.QuiztoryTheme
import com.google.android.gms.location.LocationServices

class MainActivity : ComponentActivity() {
   // private lateinit var firestore: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       FirebaseApp.initializeApp(this)

       enableEdgeToEdge()

        setContent {
            QuiztoryTheme {
                QuiztoryApp(this)
            }
        }

//        // Inicijalizacija Firestore-a
//        firestore = FirebaseFirestore.getInstance()
//
//        // Kreiramo test podatak
//        val testData = hashMapOf(
//            "message" to "pliiiiiz"
//        )

//        // Upisujemo podatak u kolekciju "testCollection"
//        firestore.collection("testCollection")
//            .add(testData)
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    Toast.makeText(this, "Stored in Firestore", Toast.LENGTH_SHORT).show()
//                } else {
//                    Toast.makeText(this, "Failed to store in Firestore", Toast.LENGTH_SHORT).show()
//                }
//            }
    }
}



@Composable
fun QuiztoryApp(context: Context) {
    val navController = rememberNavController()
   // val createEventVM = CreateEventViewModel.getInstance()
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
            NavHost(navController = navController, startDestination = Screen.Start.name) {
//                composable(Screen.Home.name) {
//                    DrawHomePage(navigationBar,navController = navController)
//                }
//                composable(Screen.Search.name) {
//                    DrawSearchPage(navigationBar, navController)
//                }
//                composable(Screen.EventReminders.name) {
//                    DrawReportsPage(navigationBar,navController)
//                }
//                composable(Screen.FriendsList.name) {
//                    DrawFriendsListPage(navigationBar,navController)
//                }
//                composable(Screen.Profile.name) {
//                    DrawProfilePage(navigationBar, navController = navController,context)
//                }
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
//                composable(Screen.Map.name) {
//                    MapScreen(navController,navigationBar = navigationBar)
//                }
//                composable(Screen.CreateEvent.name) {
//                    DrawCreateEventPage(navController,context = context)
//                }
//                composable(Screen.OtherProfile.name) {
//                    DrawOtherProfilePage(navController)
//                }
                composable(
                    route = "${Screen.Event.name}/{event}",
                    arguments = listOf(navArgument("event") { type = NavType.StringType })
                ) { backStackEntry ->
                    val eventJson = backStackEntry.arguments?.getString("event")
                    //val event = Gson().fromJson(eventJson, Event::class.java)
                  //  event?.let { DrawEventPage(event = it,navController = navController,context = context) }

                }
//                composable(route = Screen.AddEventMapLocation.name) {
//                    AddEventLocationMap(
//                        onMapLongClick = { latLng ->
//                            createEventVM.setCoordinates(latLng)
//                            navController.navigate(Screen.CreateEvent.name)
//                        }
//                    )
//                }
//                composable(Screen.EditProfile.name) {
//                    DrawEditProfile(navController = navController)
//                }
//                composable(Screen.Penalties.name) {
//                    DrawPenaltiesPage(navController = navController, navigationBar = navigationBar)
//                }
//
//                composable(Screen.AddReminder.name){
//                    AddReminderPage(navController = navController)
//                }
//
//                composable(Screen.MyPosts.name){
//                    DrawMyPostsPage(navController = navController)
//                }

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