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

    /**
     * Updates property of user with the given id to database
     *
     *  @param uid if null  auto-generates uid
     *  @param field field to update
     *  @param newValue new value of the field
     *  @return true if the update was performed successfully
     *  @author Mihajlo
     */
    enum class UserFields{
        id,
        username,
        namesurname,
        phonenumber,
        email,
        profilePicture,
    }
    suspend fun update(uid: String, field: UserFields, newValue: Any) : Boolean{
        var success : Boolean = false
        userRoot.document(uid).update(field.name, newValue)
            .addOnSuccessListener {
                Log.d("USER_DB_UPDATE", "User document: $uid updated successfully!")
                success = true
            }
            .addOnFailureListener { e -> Log.w("USER_DB_UPDATE", "Error creating user document: $uid!", e) }
        return success
    }

    /**
     * Deletes user with the given uid
     *
     *  @param uid document id
     *  @return true if the deletion was completed successfully
     *  @author Mihajlo
     */
    suspend fun delete(uid: String) : Boolean{
        var success:Boolean = false
        userRoot.document(uid).delete()
            .addOnSuccessListener {
                Log.d("USER_DB_DELETE", "User document: $uid deleted successfully!")
                success = true
            }
            .addOnFailureListener { e -> Log.w("USER_DB_DELETE", "Error deleting user document: $uid!", e) }
        return success
    }

    /**
     * Gets user with the given username
     *
     *  @param username username to search user by
     *  @return User if there is user with such username else null
     *  @author Filip, Mihajlo
     */
    // POPRAVITI
    suspend fun getUserWithUsername(username:String):User? {
        val querySnapshot = userRoot
            .whereEqualTo(UserFields.username.name, username)
            .get()
            .await()

        return querySnapshot.documents.first()?.toObject<User>()
        //return querySnapshot.documents.mapNotNull { it.toObject<User>() }
    }
//    fun getEventsForOtherUser(id: String) {
//        val db = Firebase.firestore
//
//        GlobalScope.launch(Dispatchers.IO)
//        {
//            val querySnapshot = db.collection("events")
//                .whereEqualTo("publisherId", id)
//                .get()
//
//                .addOnSuccessListener {qs ->
//
//                    OtherProfileViewModel.getInstance().setEvents(qs.documents.mapNotNull { it.toObject(Event::class.java) })
//                }.addOnFailureListener {
//                    Log.e("PROFILE_PIC_DOWNLOAD", it.message ?: "Nema poruke o gresci")
//                }
//        }
//    }

//    fun getEventsForUser() {
//        val db = Firebase.firestore
//
//        GlobalScope.launch(Dispatchers.IO)
//        {
//            val querySnapshot = db.collection("events")
//                .whereEqualTo("publisherId", Firebase.auth.currentUser?.uid)
//                .get()
//
//                .addOnSuccessListener {qs ->
//                    MyPostsPageViewModel.getInstance().setEvents(qs.documents.mapNotNull { it.toObject(Event::class.java) })
//
//                }.addOnFailureListener {
//                    Log.e("PROFILE_PIC_DOWNLOAD", it.message ?: "Nema poruke o gresci")
//                }
//        }
//    }

    //    fun update(user: User){
//
//    val db = Firebase.firestore
//
//    db.runBatch() { batch ->
//
//
//
//        batch.update(user.id, "population", newPopulation)
//
//
//        null
//    }.addOnSuccessListener { Log.d(TAG, "Transaction success!") }
//    .addOnFailureListener { e -> Log.w(TAG, "Transaction failure.", e) }
//    }
    suspend fun getUserWithUsername2(username:String):List<User> {
        val querySnapshot = userRoot
            .whereEqualTo("username", username)
            .get()
            .await()
        return querySnapshot.documents.mapNotNull { it.toObject<User>() }

    }

}
