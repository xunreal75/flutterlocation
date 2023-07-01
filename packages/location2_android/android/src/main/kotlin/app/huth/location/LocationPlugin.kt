package app.huth.location

import android.Manifest
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import androidx.annotation.NonNull
import app.huth.location.FlutterLocationService.LocalBinder
import app.huth.location.location.LocationManager
import app.huth.location.location.configuration.Configurations.defaultConfiguration
import app.huth.location.location.configuration.DefaultProviderConfiguration
import app.huth.location.location.configuration.Defaults.LOCATION_PERMISSIONS
import app.huth.location.location.configuration.GooglePlayServicesConfiguration
import app.huth.location.location.configuration.LocationConfiguration
import app.huth.location.location.configuration.PermissionConfiguration
import app.huth.location.location.constants.FailType
import app.huth.location.location.constants.ProcessType
import app.huth.location.location.constants.RequestCode
import app.huth.location.location.helper.LogUtils
import app.huth.location.location.listener.LocationListener
import app.huth.location.location.providers.locationprovider.DefaultLocationProvider
import app.huth.location.location.providers.permissionprovider.DefaultPermissionProvider
import app.huth.location.location.view.ContextProcessor
import com.google.android.gms.location.LocationRequest
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.EventChannel.StreamHandler
import io.flutter.plugin.common.PluginRegistry


class LocationPlugin : FlutterPlugin, ActivityAware, LocationListener,
    PluginRegistry.RequestPermissionsResultListener,
    PluginRegistry.ActivityResultListener, GeneratedAndroidLocation.LocationHostApi, StreamHandler {
    private var context: Context? = null
    private var activity: Activity? = null
    private var activityBinding: ActivityPluginBinding? = null

    private var globalLocationConfigurationBuilder: LocationConfiguration.Builder =
        defaultConfiguration("The location is needed", "The GPS is needed")
    private var locationManager: LocationManager? = null
    private var streamLocationManager: LocationManager? = null
    private var flutterLocationService: FlutterLocationService? = null

    private var eventChannel: EventChannel? = null
    private var eventSink: EventChannel.EventSink? = null

    private var resultsNeedingLocation: MutableList<GeneratedAndroidLocation.Result<GeneratedAndroidLocation.PigeonLocationData>?> =
        mutableListOf()

    private var resultPermissionRequest: GeneratedAndroidLocation.Result<Long>? = null

    private var alreadyRequestedPermission = false

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        GeneratedAndroidLocation.LocationHostApi.setup(flutterPluginBinding.binaryMessenger, this)
        context = flutterPluginBinding.applicationContext
        eventChannel = EventChannel(flutterPluginBinding.binaryMessenger, STREAM_CHANNEL_NAME)
        eventChannel?.setStreamHandler(this)
    }


    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        GeneratedAndroidLocation.LocationHostApi.setup(binding.binaryMessenger, null)
        context = null
        eventChannel = null
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        activity = binding.activity
        activityBinding = binding
        activityBinding?.addActivityResultListener(this)
        activityBinding?.addRequestPermissionsResultListener(this)

        binding.activity.bindService(
            Intent(
                binding.activity,
                FlutterLocationService::class.java
            ), serviceConnection, Context.BIND_AUTO_CREATE
        )
    }

    override fun onDetachedFromActivity() {
        activity = null
        activityBinding?.removeActivityResultListener(this)
        activityBinding?.removeRequestPermissionsResultListener(this)
        activityBinding?.activity?.unbindService(serviceConnection)
        activityBinding = null
    }


    override fun onDetachedFromActivityForConfigChanges() {
        this.onDetachedFromActivity()
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        this.onAttachedToActivity(binding)
    }


    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            Log.d("LOCATION", "Service connected: $name")
            flutterLocationService = (service as LocalBinder).getService()
            flutterLocationService!!.activity = activity
        }

        override fun onServiceDisconnected(name: ComponentName) {
            Log.d("LOCATION", "Service disconnected:$name")
            flutterLocationService = null
        }
    }

    override fun onProcessTypeChanged(processType: Int) {
        Log.d("Location", "onProcessTypeChanged")
        when (processType) {
            ProcessType.ASKING_PERMISSIONS -> {
                Log.d("Location", "ASKING_PERMISSIONS")
            }

            ProcessType.GETTING_LOCATION_FROM_CUSTOM_PROVIDER -> {
                Log.d("Location", "GETTING_LOCATION_FROM_CUSTOM_PROVIDER")
            }

            ProcessType.GETTING_LOCATION_FROM_GOOGLE_PLAY_SERVICES -> {
                Log.d("Location", "GETTING_LOCATION_FROM_GOOGLE_PLAY_SERVICES")
            }

            ProcessType.GETTING_LOCATION_FROM_GPS_PROVIDER -> {
                Log.d("Location", "GETTING_LOCATION_FROM_GPS_PROVIDER")
            }

            ProcessType.GETTING_LOCATION_FROM_NETWORK_PROVIDER -> {
                Log.d("Location", "GETTING_LOCATION_FROM_NETWORK_PROVIDER")
            }
        }
    }

    override fun onLocationChanged(location: Location?) {
        Log.d("LOCATION", location?.latitude.toString() + " " + location?.longitude.toString())

        val locationBuilder =
            GeneratedAndroidLocation.PigeonLocationData.Builder().setLatitude(location!!.latitude)
                .setLongitude(location.longitude)
                .setAccuracy(location.accuracy.toDouble())
                .setAltitude(location.altitude)
                .setBearing(location.bearing.toDouble())
                .setElaspedRealTimeNanos(location.elapsedRealtimeNanos.toDouble())
                .setIsMock(location.isFromMockProvider)
                //.setSatellites(location.extras.getInt("satellites").toLong())
                .setSpeed(location.speed.toDouble())


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            locationBuilder.setBearingAccuracyDegrees(location.bearingAccuracyDegrees.toDouble())
                .setSpeedAccuracy(location.speedAccuracyMetersPerSecond.toDouble())
                .setVerticalAccuracy(location.verticalAccuracyMeters.toDouble())
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            locationBuilder.setElaspedRealTimeUncertaintyNanos(location.elapsedRealtimeUncertaintyNanos)
        }

        locationBuilder.setTime(System.currentTimeMillis().toDouble())

        val pigeonLocationData = locationBuilder.build()

        for (result in resultsNeedingLocation) {
            if (result == null) {
                return
            }
            result.success(
                pigeonLocationData
            )
        }

        eventSink?.success(pigeonLocationData.toList())

        resultsNeedingLocation = mutableListOf()
    }

    override fun onLocationFailed(type: Int) {
        Log.d("Location", "onLocationFailed")
        when (type) {
            FailType.PERMISSION_DENIED -> {
                resultPermissionRequest?.success(2)
                resultPermissionRequest = null
            }

            FailType.GOOGLE_PLAY_SERVICES_NOT_AVAILABLE -> {
            }

            FailType.GOOGLE_PLAY_SERVICES_SETTINGS_DENIED -> {
            }

            FailType.GOOGLE_PLAY_SERVICES_SETTINGS_DIALOG -> {
            }

            FailType.NETWORK_NOT_AVAILABLE -> {
            }

            FailType.TIMEOUT -> {
            }

            FailType.UNKNOWN -> {
            }

            FailType.VIEW_DETACHED -> {
            }

            FailType.VIEW_NOT_REQUIRED_TYPE -> {
            }
        }
    }

    override fun onPermissionGranted(alreadyHadPermission: Boolean, limitedPermission: Boolean) {
        Log.d("Location", "onPermissionGranted")
        resultPermissionRequest?.success(if (limitedPermission) 1 else 4)
        resultPermissionRequest = null
    }


    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        Log.d("Location", "onStatusChanged")

    }

    override fun onProviderEnabled(provider: String?) {
        Log.d("Location", "onProviderEnabled")
        ///TODO add listener
    }

    override fun onProviderDisabled(provider: String?) {
        Log.d("Location", "onProviderDisabled")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ): Boolean {
        this.alreadyRequestedPermission = true;
        Log.d("Location", "onRequestPermissionsResult")
        if (locationManager == null) {
            if (requestCode == RequestCode.RUNTIME_PERMISSION) {
                // Check if any of required permissions are denied.
                var isDenied = 0
                var i = 0
                val size = permissions.size
                while (i < size) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        isDenied++
                    }
                    i++
                }
                if (isDenied == size) {
                    LogUtils.logI("User denied all of required permissions, task will be aborted!")
                    this.onLocationFailed(FailType.PERMISSION_DENIED)
                } else {
                    LogUtils.logI("We got all required permission!")
                    this.onPermissionGranted(false, isDenied > 0)
                }
            }
        }
        locationManager?.onRequestPermissionsResult(requestCode, permissions, grantResults)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        Log.d("Location", "onActivityResult")
        locationManager?.onActivityResult(requestCode, resultCode, data)
        return true
    }

    override fun getLocation(
        settings: GeneratedAndroidLocation.PigeonLocationSettings?,
        result: GeneratedAndroidLocation.Result<GeneratedAndroidLocation.PigeonLocationData>
    ) {

        resultsNeedingLocation.add(result)

        val isListening = streamLocationManager != null

        if (settings != null) {
            val locationConfiguration = getLocationConfigurationFromSettings(settings)
            locationManager = LocationManager.Builder(context!!)
                .activity(activity) // Only required to ask permission and/or GoogleApi - SettingsApi
                .configuration(locationConfiguration.build())
                .notify(this)
                .build()

            locationManager?.get()
        } else {
            if (!isListening) {
                locationManager = LocationManager.Builder(context!!)
                    .activity(activity) // Only required to ask permission and/or GoogleApi - SettingsApi
                    .configuration(globalLocationConfigurationBuilder.build())
                    .notify(this)
                    .build()

                locationManager?.get()
            }

        }

    }

    private fun getPriorityFromAccuracy(accuracy: GeneratedAndroidLocation.PigeonLocationAccuracy): Int {
        return when (accuracy) {
            GeneratedAndroidLocation.PigeonLocationAccuracy.POWER_SAVE -> LocationRequest.PRIORITY_NO_POWER
            GeneratedAndroidLocation.PigeonLocationAccuracy.LOW -> LocationRequest.PRIORITY_LOW_POWER
            GeneratedAndroidLocation.PigeonLocationAccuracy.BALANCED -> LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
            GeneratedAndroidLocation.PigeonLocationAccuracy.HIGH -> LocationRequest.PRIORITY_HIGH_ACCURACY
            GeneratedAndroidLocation.PigeonLocationAccuracy.NAVIGATION -> LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }


    private fun getLocationConfigurationFromSettings(settings: GeneratedAndroidLocation.PigeonLocationSettings): LocationConfiguration.Builder {
        val locationConfiguration = LocationConfiguration.Builder()

        if (settings.askForPermission) {
            val permissionConfiguration = PermissionConfiguration.Builder()
                .rationaleMessage(settings.rationaleMessageForPermissionRequest)

            locationConfiguration.askForPermission(permissionConfiguration.build())
        }

        if (settings.useGooglePlayServices) {
            val googlePlayServices = GooglePlayServicesConfiguration.Builder()
            googlePlayServices.askForGooglePlayServices(settings.askForGooglePlayServices)
                .askForSettingsApi(settings.askForGPS)
                .fallbackToDefault(settings.fallbackToGPS)
                .ignoreLastKnowLocation(settings.ignoreLastKnownPosition)


            val locationRequest = LocationRequest.create()

            if (settings.expirationDuration != null) {
                locationRequest.setExpirationDuration(settings.expirationDuration!!.toLong())
            }
            if (settings.expirationTime != null) {
                locationRequest.expirationTime = settings.expirationTime!!.toLong()
            }
            locationRequest.fastestInterval = (settings.fastestInterval.toLong())
            locationRequest.interval = settings.interval.toLong()
            locationRequest.priority = getPriorityFromAccuracy(settings.accuracy)

            if (settings.maxWaitTime != null) {
                locationRequest.maxWaitTime = settings.maxWaitTime!!.toLong()
            }
            if (settings.numUpdates != null) {
                locationRequest.numUpdates = settings.numUpdates!!.toInt()
            }
            locationRequest.smallestDisplacement = settings.smallestDisplacement.toFloat()
            locationRequest.isWaitForAccurateLocation = settings.waitForAccurateLocation

            googlePlayServices.locationRequest(locationRequest)

            locationConfiguration.useGooglePlayServices(googlePlayServices.build())
        }

        if (settings.fallbackToGPS) {
            val defaultProvider = DefaultProviderConfiguration.Builder()

            defaultProvider.gpsMessage(settings.rationaleMessageForGPSRequest)


            defaultProvider.requiredTimeInterval(settings.interval.toLong())
            if (settings.acceptableAccuracy != null) {
                defaultProvider.acceptableAccuracy(settings.acceptableAccuracy!!.toFloat())
            }

            locationConfiguration.useDefaultProviders(defaultProvider.build())
        }

        return locationConfiguration
    }


    override fun setLocationSettings(settings: GeneratedAndroidLocation.PigeonLocationSettings): Boolean {
        val locationConfiguration = getLocationConfigurationFromSettings(settings)

        if (settings.askForPermission) {
            val permissionConfiguration = PermissionConfiguration.Builder()
                .rationaleMessage(settings.rationaleMessageForPermissionRequest)

            locationConfiguration.askForPermission(permissionConfiguration.build())
        }

        if (settings.useGooglePlayServices) {
            val googlePlayServices = GooglePlayServicesConfiguration.Builder()
            googlePlayServices.askForGooglePlayServices(settings.askForGooglePlayServices)
                .askForSettingsApi(settings.askForGPS)
                .fallbackToDefault(settings.fallbackToGPS)
                .ignoreLastKnowLocation(settings.ignoreLastKnownPosition)


            val locationRequest = LocationRequest.create()

            if (settings.expirationDuration != null) {
                locationRequest.setExpirationDuration(settings.expirationDuration!!.toLong())
            }
            if (settings.expirationTime != null) {
                locationRequest.expirationTime = settings.expirationTime!!.toLong()
            }
            locationRequest.fastestInterval = (settings.fastestInterval.toLong())
            locationRequest.interval = settings.interval.toLong()
            locationRequest.priority = getPriorityFromAccuracy(settings.accuracy)

            if (settings.maxWaitTime != null) {
                locationRequest.maxWaitTime = settings.maxWaitTime!!.toLong()
            }
            if (settings.numUpdates != null) {
                locationRequest.numUpdates = settings.numUpdates!!.toInt()
            }
            locationRequest.smallestDisplacement = settings.smallestDisplacement.toFloat()
            locationRequest.isWaitForAccurateLocation = settings.waitForAccurateLocation

            googlePlayServices.locationRequest(locationRequest)

            locationConfiguration.useGooglePlayServices(googlePlayServices.build())
        }

        if (settings.fallbackToGPS) {
            val defaultProvider = DefaultProviderConfiguration.Builder()

            defaultProvider.gpsMessage(settings.rationaleMessageForGPSRequest)

            defaultProvider.requiredTimeInterval(settings.interval.toLong())
            if (settings.acceptableAccuracy != null) {
                defaultProvider.acceptableAccuracy(settings.acceptableAccuracy!!.toFloat())
            }

            locationConfiguration.useDefaultProviders(defaultProvider.build())
        }

        globalLocationConfigurationBuilder = locationConfiguration

        if (streamLocationManager != null) {
            streamLocationManager?.cancel()
            streamLocationManager = LocationManager.Builder(context!!)
                .activity(activity) // Only required to ask permission and/or GoogleApi - SettingsApi
                .configuration(globalLocationConfigurationBuilder.keepTracking(true).build())
                .notify(this)
                .build()

            streamLocationManager?.get()
        }

        return true
    }

    override fun getPermissionStatus(): Long {
        val permissionProvider = DefaultPermissionProvider(LOCATION_PERMISSIONS, null)
        val contextProcessor = ContextProcessor(activity?.application)
        contextProcessor.activity = activity
        permissionProvider.setContextProcessor(contextProcessor)

        if (permissionProvider.hasPermission()) {
            return 4
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity?.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) == true) {
                return 0;
            }
        } else {
            return 4
        };


        if (alreadyRequestedPermission) {
            return 2
        }
        return 0;
    }

    override fun requestPermission(result: GeneratedAndroidLocation.Result<Long>) {
        val permissionProvider = DefaultPermissionProvider(LOCATION_PERMISSIONS, null)
        val contextProcessor = ContextProcessor(activity?.application)
        contextProcessor.activity = activity
        permissionProvider.setContextProcessor(contextProcessor)
        val hasPermission = permissionProvider.requestPermissions()

        if (!hasPermission) {
            // Denied Forever
            result?.success(2)
        } else {
            resultPermissionRequest = result
        }
    }

    override fun isGPSEnabled(): Boolean {
        val locationProvider = DefaultLocationProvider()
        val contextProcessor = ContextProcessor(activity?.application)
        contextProcessor.activity = activity

        locationProvider.configure(
            contextProcessor, globalLocationConfigurationBuilder.build(),
            this
        )
        return locationProvider.isGPSProviderEnabled
    }

    override fun isNetworkEnabled(): Boolean {
        val locationProvider = DefaultLocationProvider()
        val contextProcessor = ContextProcessor(activity?.application)
        contextProcessor.activity = activity

        locationProvider.configure(
            contextProcessor, globalLocationConfigurationBuilder.build(),
            this
        )
        return locationProvider.isNetworkProviderEnabled
    }

    override fun changeNotificationSettings(settings: GeneratedAndroidLocation.PigeonNotificationSettings): Boolean {
        flutterLocationService?.changeNotificationOptions(
            NotificationOptions(
                title = if (settings.title != null) settings.title!! else kDefaultNotificationTitle,
                iconName = if (settings.iconName != null) settings.iconName!! else kDefaultNotificationIconName,
                subtitle = settings.subtitle,
                description = settings.subtitle,
                color = if (settings.color != null) Color.parseColor(settings.color) else null,
                onTapBringToFront = if (settings.onTapBringToFront != null) settings.onTapBringToFront!! else false,
            )
        )

        return true
    }

    override fun openLocationSettings(): Boolean {

        try {
            val settingsIntent = Intent();
            settingsIntent.action= Settings.ACTION_LOCATION_SOURCE_SETTINGS;
            settingsIntent.addCategory(Intent.CATEGORY_DEFAULT);
            settingsIntent.data = Uri.parse("package:" + context!!.packageName)
            settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            settingsIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            context!!.startActivity(settingsIntent);
            return true;
        } catch (ex: Exception) {
            return false;
        }
    }

    override fun openAppSettings(): Boolean {
        return try {
            val settingsIntent = Intent()
            settingsIntent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            settingsIntent.addCategory(Intent.CATEGORY_DEFAULT)
            settingsIntent.data = Uri.parse("package:" + context!!.packageName)
            settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            settingsIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
            context!!.startActivity(settingsIntent)
            true
        } catch (ex: Exception) {
            false
        }
    }

    override fun setBackgroundActivated(activated: Boolean): Boolean {
        if (activated) {
            flutterLocationService?.enableBackgroundMode()
        } else {
            flutterLocationService?.disableBackgroundMode()
        }
        return true
    }


    override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
        val inBackground = arguments as Boolean
        eventSink = events
        streamLocationManager = LocationManager.Builder(context!!)
            .activity(activity) // Only required to ask permission and/or GoogleApi - SettingsApi
            .configuration(globalLocationConfigurationBuilder.keepTracking(true).build())
            .notify(this)
            .build()

        streamLocationManager?.get()
        if (inBackground) {
            flutterLocationService?.enableBackgroundMode()
        }
    }

    override fun onCancel(arguments: Any?) {
        flutterLocationService?.disableBackgroundMode()

        eventSink = null
        streamLocationManager?.cancel()
        streamLocationManager = null
    }

    companion object {
        private const val STREAM_CHANNEL_NAME = "xunreal75/location2_stream"
    }


}