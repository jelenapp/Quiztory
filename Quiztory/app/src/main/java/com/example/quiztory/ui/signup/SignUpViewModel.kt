package com.example.quiztory.ui.signup


import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.quiztory.models.data.entities.User
import com.example.quiztory.models.data.entities.UserDbApi
import com.example.quiztory.services.implementations.AccountService
import com.example.quiztory.ui.QuiztoryAppViewModel
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import com.google.firebase.storage.storageMetadata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.util.UUID
import java.util.concurrent.TimeUnit


class SignUpViewModel private constructor(
    private val accountService: AccountService,
    context: Context
) : QuiztoryAppViewModel() {
    val username = MutableStateFlow("")
    val password = MutableStateFlow("")
    val confirmPassword = MutableStateFlow("")
    val namesurname = MutableStateFlow("")
    val phonenumber = MutableStateFlow("")
    val email = MutableStateFlow("")

    val appContext = context

    companion object {

        private var INSTANCE: SignUpViewModel? = null

        fun getInstance(context: Context): SignUpViewModel {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SignUpViewModel(
                    accountService = AccountService(),
                    context
                ).also { INSTANCE = it }
            }
        }
    }

    fun updateEmail(newEmail: String) {
        email.value = newEmail
    }

    fun updateUsername(newUsername: String) {
        username.value = newUsername
    }

    fun updatePassword(newPassword: String) {
        password.value = newPassword
    }

    fun updateConfirmPassword(newConfirmPassword: String) {
        confirmPassword.value = newConfirmPassword
    }

    fun updateNameSurname(newName: String) {
        namesurname.value = newName
    }

    fun updatePhonenumber(newPhone: String) {
        phonenumber.value = newPhone
    }


    fun onSignUpClick(openAndPopUp: () -> Unit) {

        launchCatching {
            if (username.value == "" || password.value == "" || confirmPassword.value == "" ||
                namesurname.value == "" || phonenumber.value == "" || email.value == ""
            )
                Toast.makeText(appContext, "Popunite sva polja!", Toast.LENGTH_SHORT).show()
            else if (password.value.length < 6) {
                Toast.makeText(
                    appContext,
                    "Šifra mora biti duza od 6 karaktera!",
                    Toast.LENGTH_LONG
                ).show()
                //throw Exception(R.string.kratka_sifra.toString()
            } else if (password.value != confirmPassword.value) {
                Toast.makeText(appContext, "Šifre se ne podudaraju!", Toast.LENGTH_SHORT).show()
                //throw Exception(R.string.password_dont_match.toString())
            } else if (getUserWithUsername(username.value))
                Toast.makeText(
                    appContext,
                    "Postoji korisnik sa istim username-om!",
                    Toast.LENGTH_SHORT
                ).show()
            else {
                accountService.signUp(
                    username.value,
                    password.value,
                    namesurname.value,
                    phonenumber.value,
                    email.value,
                )
                Toast.makeText(
                    appContext,
                    "Poslat je linl za verifikaciju na vas email!",
                    Toast.LENGTH_SHORT
                ).show()
                openAndPopUp()
            }
        }
    }

    private val users: UserDbApi = UserDbApi()

    private val userRoot = Firebase.firestore.collection("users")

    fun getUserWithUsername(username: String): Boolean {
        var u: List<User>? = null
        runBlocking {
            u = async { users.getUserWithUsername2(username) }.await()
        }
        return u!!.isNotEmpty()
    }

    suspend fun getUserWithUsername2(username: String): List<User> {
        val querySnapshot = userRoot
            .whereEqualTo("username", username)
            .get()
            .await()
        return querySnapshot.documents.mapNotNull { it.toObject<User>() }

    }


    var profilna by mutableStateOf<Uri?>(null)

    fun setProfilePicture(uri: Uri?) {
        this.profilna = uri
    }

    private val storageRef = com.google.firebase.Firebase.storage.reference
    private val profPic = "/profilePicture.jpg"
    private val usersi = "users/"

    fun downloadProfilePicture(
        uid: String,
    ) {
        //val profileViewModel: ProfileViewModel = ProfileViewModel.getInstance()

        val upr = storageRef.child(usersi + uid + profPic)

        upr.downloadUrl
            .addOnSuccessListener {

                setProfilePicture(it)
            }.addOnFailureListener {
                Log.e("PROFILE_PIC_DOWNLOAD", it.message ?: "Nema poruke o gresci")
            }
    }

    private val userAuth: FirebaseAuth = com.google.firebase.Firebase.auth

    fun getSignedUserId(): String? {

        var id: String?
        runBlocking {
            id = userAuth.currentUser?.uid
        }
        return id
    }

    val uid = getSignedUserId()
    fun uploadProfileImage(
        uri: Uri,
        uid: String,
        onSuccess: (String) -> Unit
    ) {

        val upr = storageRef.child(usersi + uid + profPic)

        val metadata = storageMetadata {
            contentType = "image/jpeg"
        }

        GlobalScope.launch(Dispatchers.IO) {

            // Upload file and metadata to the path upr
            val uploadTask = upr.putFile(uri, metadata)

            uploadTask
                .addOnProgressListener {

                    val progress = (100.0 * it.bytesTransferred) / it.totalByteCount
                    Log.d("PICTURE_UPLOAD", "Upload is $progress% done")

                }.addOnPausedListener {
                    Log.d("PICTURE_UPLOAD", "Upload is paused")

                }.addOnFailureListener {
                    Log.e("PICTURE_UPLOAD", "Neuspešno uploadovanje slike: ${it.message}")

                }.addOnSuccessListener {
                    upr.downloadUrl.addOnSuccessListener { uri ->
                        onSuccess(uri.toString()) // Prosledi URL slike
                    }
                }
        }
    }
}