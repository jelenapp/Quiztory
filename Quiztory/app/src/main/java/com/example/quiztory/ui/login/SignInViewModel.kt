package com.example.quiztory.ui.login;

import android.content.Context
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import android.widget.Toast.LENGTH_SHORT
import android.widget.Toast.makeText
import com.example.quiztory.services.implementations.AccountService
import com.example.quiztory.ui.QuiztoryAppViewModel
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await


class SignInViewModel private constructor(
    private val accountService: AccountService,
    context:Context
) : QuiztoryAppViewModel() {
    val appContext = context

    companion object {

        private var INSTANCE: SignInViewModel? = null

        fun getInstance(context: Context): SignInViewModel {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SignInViewModel(
                    accountService = AccountService(),
                    context
                ).also { INSTANCE = it }
            }
        }
    }


    val username = MutableStateFlow("")
    val password = MutableStateFlow("")
    val email = MutableStateFlow("")

    fun updateEmail(newEmail: String) {
        email.value = newEmail
    }

    fun updateUsername(newUsername: String) {
        username.value = newUsername
    }

    fun updatePassword(newPassword: String) {
        password.value = newPassword
    }

    fun onSignInClick(openAndPopUp: () -> Unit) {
        launchCatching {
            if(accountService.signIn(email.value, password.value)) {
                if (Firebase.auth.currentUser!!.isEmailVerified) {
                makeText(appContext, "Logged in!", LENGTH_SHORT).show()
                openAndPopUp()
            } else makeText(
                appContext,
                "Potvrdite Vas email klikom na link poslat na isti!",
                LENGTH_SHORT
            ).show()
        }
        else {
            makeText(appContext, "Wrong credentials!", LENGTH_SHORT).show()
        }
    }
}
    fun onSignUpClick(openAndPopUp: () -> Unit) {
        openAndPopUp()
    }

}
