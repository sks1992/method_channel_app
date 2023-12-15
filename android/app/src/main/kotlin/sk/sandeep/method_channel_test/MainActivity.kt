package sk.sandeep.method_channel_test

import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import sk.sandeep.method_channel_test.db.DatabaseHelper
import sk.sandeep.method_channel_test.repository.LocationRepository

class MainActivity : FlutterActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val channelName = "Location"
    private lateinit var locationRepository: LocationRepository


    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        locationRepository = LocationRepository(DatabaseHelper(this))
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val channel = MethodChannel(flutterEngine.dartExecutor.binaryMessenger, channelName)
        channel.setMethodCallHandler { call, result ->
            when (call.method) {
                "startWorkManger" -> {
                    locationRepository.setLocationWorker(this@MainActivity)
                    result.success(true)
                }

                "getLastLocation" -> {
                    val allLocationListFromDb = locationRepository.getAllLocations()
                    if (allLocationListFromDb.isEmpty()) {
                        locationRepository.getLocation(
                            fusedLocationClient, this
                        )
                        val allLocationListFromDatabase = locationRepository.getAllLocations()
                        if (allLocationListFromDatabase.isEmpty()) {
                            result.success(
                                "{\"id\":\"${""}\",\"latitude\":\"${""}\",\"longitude\":\"${""}\",\"time\":\"${""}\"}"
                            )
                        } else {
                            val lastLocationList = allLocationListFromDatabase.last()
                            Toast.makeText(
                                this,
                                "first location${lastLocationList.time}",
                                Toast.LENGTH_SHORT
                            ).show()
                            result.success(
                                "{\"id\":\"${lastLocationList.id}\",\"latitude\":\"${lastLocationList.latitude}\",\"longitude\":\"${lastLocationList.longitude}\",\"time\":\"${lastLocationList.time}\"}"
                            )
                        }
                    } else {
                        val locationList = allLocationListFromDb.last()
                        Toast.makeText(
                            this,
                            "last location${locationList.time}",
                            Toast.LENGTH_SHORT
                        ).show()
                        result.success(
                            "{\"id\":\"${locationList.id}\",\"latitude\":\"${locationList.latitude}\",\"longitude\":\"${locationList.longitude}\",\"time\":\"${locationList.time}\"}"
                        )
                    }

                }

                "getLocation" -> {
                    locationRepository.getAllLocations()
                    val allLocationListFromDatabase = locationRepository.getAllLocations()
                    if (allLocationListFromDatabase.isEmpty()) {
                        result.success(
                            "{\"id\":\"${""}\",\"latitude\":\"${""}\",\"longitude\":\"${""}\",\"time\":\"${""}\"}"
                        )
                    } else {
                        val lastLocationList = allLocationListFromDatabase.last()
                        Toast.makeText(
                            this,
                            "first location${lastLocationList.time}",
                            Toast.LENGTH_SHORT
                        ).show()
                        result.success(
                            "{\"id\":\"${lastLocationList.id}\",\"latitude\":\"${lastLocationList.latitude}\",\"longitude\":\"${lastLocationList.longitude}\",\"time\":\"${lastLocationList.time}\"}"
                        )
                    }
                }

                else -> result.notImplemented()
            }
        }
    }

}
