//package com.example.quiztory.ui.quiz
//
//import MapScreenViewModel
//import androidx.compose.foundation.layout.Column
//import androidx.compose.material3.Button
//import androidx.compose.material3.Text
//import androidx.compose.material3.TextField
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.navigation.NavController
//
//@Composable
//    fun QuizScreen(
//    quiz: Long,
//    locationId: String,
//    viewModel: MapScreenViewModel,
//    navController: NavController) {
//        val location = viewModel.getLocationById(locationId)
//
//        if (location != null) {
//            var userAnswer by remember { mutableStateOf("") }
//
//            Column {
//                Text(text = quiz.question)
//                TextField(value = userAnswer, onValueChange = { userAnswer = it })
//
//                Button(onClick = {
//                    if (userAnswer == quiz.correctAnswer) {
//                        viewModel.addPointsToUser(50) // Dodavanje bodova korisniku
//                        navController.popBackStack() // Vraćanje na mapu
//                    } else {
//                       // Toast.makeText(LocalContext.current, "Wrong answer!", Toast.LENGTH_SHORT) .show()
//                    }
//                }) {
//                    Text("Submit")
//                }
//            }
//        } else {
//            Text("Location not found")
//        }
//    }
//
