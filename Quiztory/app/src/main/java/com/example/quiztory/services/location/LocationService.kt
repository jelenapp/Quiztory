package com.example.quiztory.services.location


import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
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
import org.json.JSONObject

class LocationService : Service() {

    private lateinit var locationManager: LocationManager
    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            sendLocationToServer(location)
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

        override fun onProviderEnabled(provider: String) {}

        override fun onProviderDisabled(provider: String) {}
    }
    private val TAG = "LocationService"  // Dodajemo TAG za logovanje

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "LocationService started")  // Log kada se servis pokrene

        try {
            startLocationUpdates()
        } catch (e: Exception) {
            Log.e("LocationService", "Error in onStartCommand", e)
        }
        return START_STICKY
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
//        // HTTP request ka serveru sa trenutnom lokacijom
//        val url = "https://yourserver.com/location"
//        val requestBody = JSONObject().apply {
//            put("latitude", location.latitude)
//            put("longitude", location.longitude)
//        }
//
//        val request = JsonObjectRequest(
//            Request.Method.POST, url, requestBody,
//            Response.Listener { response ->
//                if (response.getBoolean("object_nearby")) {
//                    showNotification("Objekat u blizini!")
//                }
//            },
//            Response.ErrorListener { error ->
//                // Obradi grešku
//            }
//        )
//
//        Volley.newRequestQueue(this).add(request)
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

    private fun showNotification(message: String) {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, "location_channel_id")
            .setContentTitle("Bliski objekat ili korisnik")
            .setContentText(message)
            .setSmallIcon(R.mipmap.quiztorylogo)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Kreiraj NotificationChannel za API 26+
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "location_channel_id", "Lokacija", NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(1, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        locationManager.removeUpdates(locationListener)
        Log.d(TAG, "LocationService stopped")  // Log kada se servis zaustavi

    }


}
