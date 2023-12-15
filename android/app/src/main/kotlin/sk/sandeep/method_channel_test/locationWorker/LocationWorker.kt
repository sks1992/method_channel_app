package sk.sandeep.method_channel_test.locationWorker

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import android.widget.Toast
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import sk.sandeep.method_channel_test.db.DatabaseHelper
import sk.sandeep.method_channel_test.repository.LocationRepository

class LocationUpdateWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {
    private val locationRepository = LocationRepository(DatabaseHelper(appContext))
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(appContext)

    @SuppressLint("MissingPermission", "SimpleDateFormat")
    override suspend fun doWork(): Result {
        try {
            if (!locationRepository.checkPermissions(applicationContext)) {
                locationRepository.requestPermissions(applicationContext)
                Toast.makeText(
                    applicationContext,
                    "\"Missing location permission",
                    Toast.LENGTH_SHORT
                ).show()
            }
            val locationManager =
                applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val isNetworkEnabled =
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            if (!isGpsEnabled && !isNetworkEnabled) {
                Toast.makeText(applicationContext, "\"GPS is disabled", Toast.LENGTH_SHORT).show()
            } else {
                val locationModel = locationRepository.getLocation(fusedLocationClient,applicationContext)
                CoroutineScope(Dispatchers.IO).launch {
                    locationRepository.insert(locationModel)
                }
            }
            return Result.success()
        } catch (e: Exception) {
            return Result.failure()
        }
    }
}