package com.example.quiztory.services.implementations


import com.example.quiztory.models.data.entities.User
import com.example.quiztory.models.data.entities.UserDbApi
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

class AccountService constructor() {

    val currentUserId: String
        get() = Firebase.auth.currentUser?.uid.orEmpty()



    fun hasUser(): Boolean {
        return Firebase.auth.currentUser != null
    }

    fun isVerified(): Boolean {
        val current = Firebase.auth.currentUser
        if(current != null)
            return current.isEmailVerified
        return false
    }

    suspend fun signIn(username: String, password: String):Boolean {
        try {
            Firebase.auth.signInWithEmailAndPassword(username, password).await()
            return true
        }
        catch (error: Exception){
            return false
        }
    }

    suspend fun signUp(username: String,
                       password: String,
                       namesurname: String,
                       phonenumber: String,
                       email: String): Task<AuthResult> {
        val userCredential = Firebase.auth.createUserWithEmailAndPassword(email, password).await()
        signIn(email, password)
        val user = userCredential.user
        user?.sendEmailVerification()?.await()
        addUser(
            User(
                id = Firebase.auth.currentUser!!.uid,
                username = username,
                namesurname = namesurname,
                phonenumber = phonenumber,
                email = email
            )
        )
        signOut()
        return FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)

    }
    private val users: UserDbApi = UserDbApi()

    fun addUser(user: User) {

        runBlocking {
            users.add(user.id, user)
        }
    }

    suspend fun signOut() {
        Firebase.auth.signOut()
    }

}