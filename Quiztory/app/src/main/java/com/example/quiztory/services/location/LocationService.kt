package com.example.quiztory.services.location


import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.quiztory.MainActivity
import com.example.quiztory.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.json.JSONObject

class LocationService : Service() {

    private lateinit var locationManager: LocationManager
    private val serviceScope = CoroutineScope(Dispatchers.IO)  // Kreiramo CoroutineScope za servis

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            Log.d("LocationService", "Lokacija promenjena: ${location.latitude}, ${location.longitude}")

            sendLocationToServer(location)
            Log.d("LocationService", "Lokacija poslata serveru") // Dodaj log ovde

            // Koristimo CoroutineScope za asinhronu proveru objekata u blizini
            serviceScope.launch {
                Log.d("LocationService", "Pokrenuta provera za objekte u blizini")

                checkAndNotifyNearbyObjects(location)
            }
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }
    private val TAG = "LocationService"

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "LocationService started")

        createNotificationChannel()
        try {
            startLocationUpdates()
        } catch (e: Exception) {
            Log.e("LocationService", "Error in onStartCommand", e)
        }
        return START_STICKY
    }
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "location_channel_id", // ID kanala
                "Lokacija", // Naziv kanala
                NotificationManager.IMPORTANCE_HIGH // Prioritet kanala
            )
            channel.description = "Kanal za notifikacije o lokaciji"

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
            Log.d("LocationService", "Notification channel created: ${channel.id}")

        }
    }
    private fun startLocationUpdates() {
        try {
            locationManager = getSystemService(Context.LOCATION_SERVICE) as? LocationManager
                ?: throw IllegalStateException("LocationManager not available")

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
            ) {
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    10000L, 10f, locationListener, Looper.getMainLooper()
                )
                Log.d(TAG, "Location updates started")  // Log kada su ažuriranja lokacije pokrenuta

            }
        }catch (e:Exception){
            Log.e("LocationService", "Error starting location updates", e)

        }
    }

    private fun sendLocationToServer(location: Location) {
        val firestore = FirebaseFirestore.getInstance()

        // Kreiraj mapu sa koordinatama
        val locationData = hashMapOf(
            "latitude" to location.latitude,
            "longitude" to location.longitude,
            "timestamp" to System.currentTimeMillis() // možeš dodati i vreme
        )

        // Dodaj podatke u Firestore u kolekciju "locations"
        firestore.collection("locations")
            .add(locationData)
            .addOnSuccessListener {
                Log.d("LocationService", "Lokacija uspešno sačuvana u Firestore")
            }
            .addOnFailureListener { e ->
                Log.e("LocationService", "Greška prilikom čuvanja lokacije", e)
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        locationManager.removeUpdates(locationListener)
        Log.d(TAG, "LocationService stopped")  // Log kada se servis zaustavi
        serviceScope.cancel()  // Otkazujemo sve korutine kada se servis uništi,  I obavezno je da se korutina otkaže (serviceScope.cancel()) kada se servis zaustavi kako bi se izbegli curenja memorije (memory leaks).

    }
    // Funkcija koja proverava bliske objekte i prikazuje notifikacije
    private suspend fun checkAndNotifyNearbyObjects(location: Location) {
        try {
            val nearbyObjects = checkNearbyObjects(location.latitude, location.longitude)
            if (nearbyObjects.isNotEmpty()) {
                Log.d("LocationService", "Pronađeni objekti u blizini: ${nearbyObjects.size}")
                for (nearbyObject in nearbyObjects) {
                    Log.d("LocationService", "Prikazivanje notifikacije za objekat: ${nearbyObject.title}")
                    showNotification(nearbyObject)
                }
            }else {
                Log.d("LocationService", "Nema objekata u blizini.")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error while checking nearby objects", e)
        }
    }
    // Funkcija koja se zove za periodičnu proveru objekata u blizini
    private suspend fun checkNearbyObjects(lat: Double, lon: Double): List<NearbyObject> {
        val firestore = FirebaseFirestore.getInstance()

        return withContext(Dispatchers.IO) {
            val nearbyObjects = mutableListOf<NearbyObject>()

            try {
                val querySnapshot = firestore.collection("historical_locations")
                    .get()
                    .await()
                Log.d("LocationService", "Broj pronađenih dokumenata: ${querySnapshot.size()}")

                for (document in querySnapshot.documents) {
                        val nearbyObjects = mutableListOf<NearbyObject>()
                        for (document in querySnapshot.documents) {
                            val nearbyObject = document.toObject(NearbyObject::class.java)
                            if (nearbyObject != null) {
                                Log.d("LocationService", "Objekat: ${nearbyObject.title}, Koordinate: (${nearbyObject.latitude}, ${nearbyObject.longitude})")
                                // Proveravamo da li su koordinate validne
                                if (nearbyObject.latitude == 0.0 && nearbyObject.longitude == 0.0) {
                                    Log.w("LocationService", "Objekat ${nearbyObject.title} ima nevalidne koordinate (0.0, 0.0)")
                                    continue  // Preskoči ovaj objekat
                                }
                                val distance = calculateDistance(
                                    lat, lon,
                                    nearbyObject.latitude, nearbyObject.longitude
                                )
                                Log.d("LocationService", "Trenutna lokacija: ($lat, $lon)")
                                Log.d("LocationService", "Provera objekta: ${nearbyObject.title}, Koordinate: (${nearbyObject.latitude}, ${nearbyObject.longitude}), Udaljenost: $distance metara")
                                if (distance <= 1000) { // 1000 metara
                                    nearbyObjects.add(nearbyObject)
                                }
                            } else {
                                Log.w("LocationService", "Nevalidan objekat u Firestore-u: ${document.id}")
                            }
                        }
                    }
            } catch (e: Exception) {
                Log.e("LocationService", "Error while fetching nearby objects", e)
            }

            return@withContext nearbyObjects
        }
    }
    private fun calculateDistance(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Float {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0]  // Vraća udaljenost u metrima
    }
//fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
//    val earthRadius = 6371e3 // Zemljin poluprečnik u metrima
//    val dLat = Math.toRadians(lat2 - lat1)
//    val dLon = Math.toRadians(lon2 - lon1)
//
//    val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
//            Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
//            Math.sin(dLon / 2) * Math.sin(dLon / 2)
//    val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
//
//    return earthRadius * c // Udaljenost u metrima
//}

    // Funkcija za prikazivanje notifikacije
    private fun showNotification(nearbyObject: NearbyObject) {
        Log.d("LocationService", "Prikazivanje notifikacije za objekat: ${nearbyObject.title}")

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("objectId", nearbyObject.id)  // Možeš proslediti ID objekta ili korisnika
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(this, "location_channel_id")
            .setContentTitle("Objekat u blizini: ${nearbyObject.title}")
            .setContentText("Kliknite da otvorite aplikaciju")
            .setSmallIcon(R.mipmap.quiztorylogo)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(nearbyObject.id.toInt(), notification)
    }
}


