package sk.sandeep.method_channel_test.repository

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import sk.sandeep.method_channel_test.db.DatabaseHelper
import sk.sandeep.method_channel_test.locationWorker.LocationUpdateWorker
import sk.sandeep.method_channel_test.model.LocationModel
import sk.sandeep.method_channel_test.utils.hasBackgroundLocationPermission
import sk.sandeep.method_channel_test.utils.hasLocationPermission
import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.TimeUnit

class LocationRepository(private val db: DatabaseHelper) {

    fun insert(location: LocationModel) = db.insertLocation(location)

    fun getAllLocations() = db.getAllLocationData()

    @SuppressLint("MissingPermission")
    fun getLocation(
        fusedLocationClient: FusedLocationProviderClient,
        appContext: Context
    ): LocationModel {
        if (!checkPermissions(appContext)) {
            requestPermissions(appContext)
        }
        var locationData = LocationModel(
            0,
            0.0,
            0.0,
            ""
        )

        val resultLocation = fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_BALANCED_POWER_ACCURACY,
            CancellationTokenSource().token
        )
        resultLocation.addOnCompleteListener { location ->
            val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
            val currentDate = sdf.format(Date())
            locationData = LocationModel(
                0,
                location.result.latitude,
                location.result.longitude,
                currentDate
            )
        }
        return locationData;
    }

    fun checkPermissions(appContext: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (appContext.hasLocationPermission()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    if (appContext.hasBackgroundLocationPermission()) {
                        return true
                    }
                    return false
                }
                return true
            }
            return false
        } else {
            return true
        }
    }

    fun requestPermissions(appContext: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ActivityCompat.requestPermissions(
                appContext as Activity,
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                12
            )
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(
                appContext as Activity,
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                ),
                12
            )
        }
    }

    fun setLocationWorker(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val periodicWorkRequest =
            PeriodicWorkRequestBuilder<LocationUpdateWorker>(10, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "LocationUpdateWork",
            ExistingPeriodicWorkPolicy.KEEP,
            periodicWorkRequest
        )
    }
}