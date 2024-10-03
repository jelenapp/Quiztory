
package com.example.quiztory.ui.quiz

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import com.google.firebase.firestore.FirebaseFirestore

data class Question(
    val text: String,
    val options: List<String>,  // Opcije za odgovore
    val correctAnswer: String    // Tačan odgovor
)
data class Quiz(
    val id: Long,
    val locationId: Long,
    val questions: List<Question>
)

// Pretpostavimo da imamo repozitorijum za kvizove
object QuizRepository {
    private val quizzes = listOf(
        Quiz(
            id = 1L,
            locationId = 1L,
            questions = listOf(
                Question(
                    text = "Spomenik oslobodiocima Nisa, lokalno poznat i kao Konj, obelezava period oslobodilackih ratova protiv koga?",
                    options = listOf("Francuza i Bugara", "Turaka, Bugara i Nemaca", "Crnogoraca", "Nista od ponudjenog"),
                    correctAnswer = "Turaka, Bugara i Nemaca"
                )
            )
        ),
        Quiz(
            id = 2L,
            locationId = 2L,
            questions = listOf(
                Question(
                    text = "Trg kralja Milana se poceo smatrati gradskim trgom posle oslobodjenja od Osmanlija koje godine?",
                    options = listOf("1692", "1750", "1878", "Nista od ponudjenog"),
                    correctAnswer = "1878"
                )
            )
        ),
        Quiz(
            id = 3L,
            locationId = 3L,
            questions = listOf(
                Question(
                    text = "?",
                    options = listOf("1692", "1750", "1878", "1900"),
                    correctAnswer = "1878"
                )
            )
        )
    )

    fun getQuizByLocationId(locationId: Long): Quiz? {
        return quizzes.find { it.locationId == locationId }
    }
}

class QuizViewModel : ViewModel() {
    fun getQuizForLocation(locationId: Long, onQuizLoaded: (Quiz?) -> Unit) {
        viewModelScope.launch {
            // Učitavanje kviza iz repozitorijuma
            val quiz = QuizRepository.getQuizByLocationId(locationId)
            onQuizLoaded(quiz)
        }
    }

    fun loadPointsFromFirestore(userId: String, onPointsLoaded: (Int) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val userRef = db.collection("users").document(userId)

        userRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val points = document.getLong("points")?.toInt() ?: 0
                    onPointsLoaded(points) // Pozovi callback funkciju sa bodovima
                } else {
                    onPointsLoaded(0) // Ako nema podataka, bodovi su 0
                }
            }
            .addOnFailureListener { exception ->
                // Obradi grešku
                onPointsLoaded(0)
            }
    }
    fun updatePointsInFirestore(userId: String, newPoints: Int) {
        val db = FirebaseFirestore.getInstance()
        val userRef = db.collection("users").document(userId)

        // Ažuriraj bodove
        userRef.update("points", newPoints)
            .addOnSuccessListener {
                // Uspešno ažuriranje
            }
            .addOnFailureListener { exception ->
                // Obradi grešku
            }
    }


}

@Composable
fun QuizScreen(locationId: Long, navController: NavHostController) {
    var points by remember { mutableStateOf(0) }
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    if (userId == null) {
        // Prikaz poruke ako korisnik nije prijavljen
        Text("Please log in to take the quiz.")
        return
    }

    // Kreiramo ViewModel
    val viewModel = QuizViewModel()

    // Pozivamo funkciju za dobijanje kviza na osnovu ID-a lokacije
    // I koristimo remember kako bi sačuvali state unutar kompozicije
    val quiz = remember { mutableStateOf<Quiz?>(null) }

    // Nabavljamo trenutni kontekst za korišćenje u `Toast` funkciji
    val context = LocalContext.current

    // Učitavamo kviz za lokaciju unutar `LaunchedEffect`
    LaunchedEffect(locationId) {
        viewModel.getQuizForLocation(locationId) { loadedQuiz ->
            quiz.value = loadedQuiz
        }
    }
    // Učitaj bodove korisnika iz Firestore-a
    LaunchedEffect(userId) {
            viewModel.loadPointsFromFirestore(userId) { loadedPoints ->
                points = loadedPoints
            }
    }

    // Prikazujemo kviz ili poruku ako kviz nije pronađen
    if (quiz.value != null) {
        val currentQuiz = quiz.value!!
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            )
            Text("Vasi bodovi: $points")

            // Prikaz pitanja
            Text(text = "Kviz pitanje za lokaciju $locationId", modifier = Modifier.padding(bottom = 16.dp), textAlign = TextAlign.Center)

            // Iteriramo kroz pitanja i prikazujemo svako pitanje sa opcijama
            currentQuiz.questions.forEach { question ->
                Text(text = question.text, modifier = Modifier.padding(bottom = 8.dp))

                // Prikaz svake opcije za pitanje
                question.options.forEach { option ->
                    Button(
                        onClick = {
                            if (option == question.correctAnswer) {
                                // Logika za tačan odgovor
                                val newPoints = points + 10  // Na primer, dodaj 10 poena
                                points = newPoints
                                viewModel.updatePointsInFirestore(userId, newPoints)  // Ažuriraj bodove u Firestore-u
                                Toast.makeText(context, "Tačan odgovor!", Toast.LENGTH_SHORT).show()
                            } else {
                                // Logika za netačan odgovor
                                Toast.makeText(context, "Netačan odgovor!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    ) {
                        Text(text = option)
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Nazad")
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Kviz nije pronađen za ovu lokaciju.")
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = { navController.popBackStack() }
            ) {
                Text(text = "Nazad")
            }
        }
    }
}
