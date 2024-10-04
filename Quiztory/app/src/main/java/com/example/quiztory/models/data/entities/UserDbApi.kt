package com.example.quiztory.models.data.entities

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UserDbApi() {

    private val userRoot = Firebase.firestore.collection("users")

    /**
     * Adds user with the given uid as document id
     *
     *  @param uid if null  auto-generates uid
     *  @return true if write was completed successfully
     *  @author Mihajlo
     */
    suspend fun add(uid: String?, user: User) : Boolean {

        var success : Boolean = false
        if (uid == null)
            userRoot.add(user)
                .addOnSuccessListener {
                    Log.d("USER_DB_ADD", "User document created successfully!")
                    success = true
                }
                .addOnFailureListener { e -> Log.w("USER_DB_ADD", "Error creating user document!", e) }
        else{
            userRoot.document(uid).set(user)
                .addOnSuccessListener {
                    Log.d("USER_DB_ADD", "User document: $uid created successfully!")
                    success = true
                }
                .addOnFailureListener { e -> Log.w("USER_DB_ADD", "Error creating user document: $uid!", e) }
        }
        return success
    }

    /**
     * Returns user with the given uid
     *
     *  @param uid
     *  @return null if user doesn't exists or there was an query failure
     *  @author Mihajlo
     */
    suspend fun get(uid: String) : User? {
        var user: User? = null
        val ud = userRoot.document(uid).get()
            .addOnSuccessListener { document ->

                if (document != null) {
                    Log.d("USER_DB_GET", "DocumentSnapshot data: ${document.data}")

                } else {
                    Log.d("USER_DB_GET", "No such document!")
                }

                return@addOnSuccessListener
            }
            .addOnFailureListener { exception ->
                Log.w("USER_DB_GET", "Get user failed with ==> ", exception)
            }.await()
        user = ud.toObject<User>()
        return user
    }

    suspend fun getUserWithUsername2(username:String):List<User> {
        val querySnapshot = userRoot
            .whereEqualTo("username", username)
            .get()
            .await()
        return querySnapshot.documents.mapNotNull { it.toObject<User>() }

    }

}
