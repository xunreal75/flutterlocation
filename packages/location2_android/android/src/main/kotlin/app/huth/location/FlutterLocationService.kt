package app.huth.location

import android.Manifest
import android.app.Activity
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import app.huth.location.location.LocationManagerLoc2
import app.huth.location.location.configuration.DefaultProviderConfiguration
import app.huth.location.location.configuration.GooglePlayServicesConfiguration
import app.huth.location.location.configuration.LocationConfiguration
import app.huth.location.location.configuration.PermissionConfiguration
import io.flutter.plugin.common.PluginRegistry


class FlutterLocationService() : Service(),
    PluginRegistry.RequestPermissionsResultListener,
    PluginRegistry.ActivityResultListener {
    companion object {
        private const val TAG = "FlutterLocationService"

        private const val ONGOING_NOTIFICATION_ID = 75418
        private const val CHANNEL_ID = "flutter_location_channel_01"
    }

    // Binder given to clients
    private val binder = LocalBinder()

    // Service is foreground
    private var isForeground = false

    private var backgroundNotification: BackgroundNotification? = null

    var locationManagerLoc2: LocationManagerLoc2? = null
        private set

    // Store result until a permission check is resolved
    public var activity: Activity? = null


    inner class LocalBinder : Binder() {
        fun getService(): FlutterLocationService = this@FlutterLocationService
    }

    override fun onCreate() {
        super.onCreate()
        backgroundNotification = BackgroundNotification(
            applicationContext,
            CHANNEL_ID,
            ONGOING_NOTIFICATION_ID
        )
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.d(TAG, "Binding to location service.")
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(TAG, "Unbinding from location service.")
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        Log.d(TAG, "Destroying service.")

        locationManagerLoc2?.cancel()
        locationManagerLoc2 = null
        backgroundNotification = null

        super.onDestroy()
    }


    fun enableBackgroundMode() {
        if (isForeground) {
            Log.d(TAG, "Service already in foreground mode.")
        } else {
            Log.d(TAG, "Start service in foreground mode.")


            val locationConfiguration =
                LocationConfiguration.Builder()
                    .keepTracking(true)
                    .askForPermission(
                        PermissionConfiguration.Builder()
                            .requiredPermissions(
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                                    )
                                } else {
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                    )
                                }
                            )
                            .build()
                    )
                    .useGooglePlayServices(GooglePlayServicesConfiguration.Builder().build())
                    .useDefaultProviders(DefaultProviderConfiguration.Builder().build())
                    .build()

            locationManagerLoc2 = LocationManagerLoc2.Builder(applicationContext)
                .activity(activity) // Only required to ask permission and/or GoogleApi - SettingsApi
                .configuration(locationConfiguration)
                .build()

            locationManagerLoc2?.get()


            val notification = backgroundNotification!!.build()
            startForeground(ONGOING_NOTIFICATION_ID, notification)

            isForeground = true
        }
    }

    fun disableBackgroundMode() {
        Log.d(TAG, "Stop service in foreground.")
        stopForeground(true)

        locationManagerLoc2?.cancel()

        isForeground = false
    }

    fun changeNotificationOptions(options: NotificationOptions): Map<String, Any>? {
        backgroundNotification?.updateOptions(options, isForeground)

        return if (isForeground)
            mapOf("channelId" to CHANNEL_ID, "notificationId" to ONGOING_NOTIFICATION_ID)
        else
            null
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        Log.d("Location", "onActivityResult")
        locationManagerLoc2?.onActivityResult(requestCode, resultCode, data)
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ): Boolean {
        locationManagerLoc2?.onRequestPermissionsResult(requestCode, permissions, grantResults)
        return true
    }
}