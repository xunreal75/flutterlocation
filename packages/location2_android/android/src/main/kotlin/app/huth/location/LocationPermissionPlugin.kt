package app.huth.location

import android.Manifest
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import app.huth.location.FlutterLocationService.LocalBinder
import app.huth.location.location.LocationManagerLoc2
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
import app.huth.location.location.providers.permissionprovider.DefaultPermissionProvider
import app.huth.location.location.view.ContextProcessor
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.Priority
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.EventChannel.StreamHandler
import io.flutter.plugin.common.PluginRegistry

//https://github.com/android/location-samples/tree/main/LocationUpdatesBackgroundKotlin
class LocationPermissionHandler: FlutterPlugin, ActivityAware, LocationListener,
    PluginRegistry.RequestPermissionsResultListener,
    PluginRegistry.ActivityResultListener, GeneratedAndroidLocation.LocationPermissionsHostApi, StreamHandler{

    private var context: Context? = null
    private var activity: Activity? = null
    private var activityBinding: ActivityPluginBinding? = null

    private var globalLocationConfigurationBuilder: LocationConfiguration.Builder =
        defaultConfiguration("The location is needed", "The GPS is needed")
    private var locationManagerLoc2: LocationManagerLoc2? = null
    private var streamLocationManagerLoc2: LocationManagerLoc2? = null
    private var flutterLocationService: FlutterLocationService? = null

    private var eventChannel: EventChannel? = null
    private var eventSink: EventChannel.EventSink? = null

    private var resultsNeedingLocation: MutableList<GeneratedAndroidLocation.Result<GeneratedAndroidLocation.PigeonLocationData>?> =
        mutableListOf()

    private var resultPermissionRequest: GeneratedAndroidLocation.Result<Long>? = null

    private var alreadyRequestedPermission = false

    override fun onAttachedToEngine( flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        GeneratedAndroidLocation.LocationPermissionsHostApi.setup(flutterPluginBinding.binaryMessenger, this)
        context = flutterPluginBinding.applicationContext
        eventChannel = EventChannel(flutterPluginBinding.binaryMessenger, PERMISSION_STREAM_CHANNEL_NAME)
        eventChannel?.setStreamHandler(this)
    }


    override fun onDetachedFromEngine( binding: FlutterPlugin.FlutterPluginBinding) {
        GeneratedAndroidLocation.LocationPermissionsHostApi.setup(binding.binaryMessenger,null)
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
                .setElapsedRealTimeNanos(location.elapsedRealtimeNanos.toDouble())

                //.setSatellites(location.extras.getInt("satellites").toLong())
                .setSpeed(location.speed.toDouble())


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            locationBuilder.setBearingAccuracyDegrees(location.bearingAccuracyDegrees.toDouble())
                .setSpeedAccuracy(location.speedAccuracyMetersPerSecond.toDouble())
                .setVerticalAccuracy(location.verticalAccuracyMeters.toDouble())
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            locationBuilder.setElapsedRealTimeUncertaintyNanos(location.elapsedRealtimeUncertaintyNanos)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            locationBuilder.setIsMock(location.isMock)
        }

        locationBuilder.setTime(System.currentTimeMillis())

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

    }

    override fun onProviderDisabled(provider: String?) {
        Log.d("Location", "onProviderDisabled")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ): Boolean {
        this.alreadyRequestedPermission = true
        Log.d("Location", "onRequestPermissionsResult")
        if (locationManagerLoc2 == null) {
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
        locationManagerLoc2?.onRequestPermissionsResult(requestCode, permissions, grantResults)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        Log.d("Location", "onActivityResult")
        locationManagerLoc2?.onActivityResult(requestCode, resultCode, data)
        return true
    }



    private fun getPriorityFromAccuracy(accuracy: GeneratedAndroidLocation.PigeonLocationAccuracy): Int {
        return when (accuracy) {
            GeneratedAndroidLocation.PigeonLocationAccuracy.POWER_SAVE -> Priority.PRIORITY_PASSIVE
            GeneratedAndroidLocation.PigeonLocationAccuracy.LOW -> Priority.PRIORITY_LOW_POWER
            GeneratedAndroidLocation.PigeonLocationAccuracy.BALANCED -> Priority.PRIORITY_BALANCED_POWER_ACCURACY
            GeneratedAndroidLocation.PigeonLocationAccuracy.HIGH -> Priority.PRIORITY_HIGH_ACCURACY
            GeneratedAndroidLocation.PigeonLocationAccuracy.NAVIGATION -> Priority.PRIORITY_HIGH_ACCURACY
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


//https://developers.google.com/android/reference/com/google/android/gms/location/LocationRequest
            val locationRequest = LocationRequest.Builder(getPriorityFromAccuracy(settings.accuracy),
                settings.interval.toLong())
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(settings.fastestInterval.toLong())
                .setMinUpdateDistanceMeters(settings.smallestDisplacement.toFloat())
                .setWaitForAccurateLocation(settings.waitForAccurateLocation)

            if (settings.expirationTime != null) {//setDurationMillis
                locationRequest.setDurationMillis( settings.expirationTime!!.toLong())
            }
            if (settings.maxWaitTime != null) {
                locationRequest.setMaxUpdateDelayMillis(settings.maxWaitTime!!.toLong())
            }
            if (settings.numUpdates != null) {
                locationRequest.setMaxUpdates(settings.numUpdates!!.toInt())
            }


            googlePlayServices.locationRequest(locationRequest.build())

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




    override fun getLocationPermissionStatus(): GeneratedAndroidLocation.PigeonLocationPermissionData {
        val permissionProvider = DefaultPermissionProvider(LOCATION_PERMISSIONS, null)
        val contextProcessor = ContextProcessor(activity?.application)
        val pigeonLocationPermissionData = GeneratedAndroidLocation.PigeonLocationPermissionData()
        contextProcessor.activity = activity
        permissionProvider.setContextProcessor(contextProcessor)

        if (permissionProvider.hasPermission()) {
            pigeonLocationPermissionData.pigeonLocationPermission = 4
            return pigeonLocationPermissionData
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity?.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) == true) {
                pigeonLocationPermissionData.pigeonLocationPermission = 0
                return pigeonLocationPermissionData
            }
        } else {
            pigeonLocationPermissionData.pigeonLocationPermission = 4
            return pigeonLocationPermissionData
        }


        if (alreadyRequestedPermission) {
            pigeonLocationPermissionData.pigeonLocationPermission = 2
            return pigeonLocationPermissionData
        }
        pigeonLocationPermissionData.pigeonLocationPermission = 0
        return pigeonLocationPermissionData
    }

    override fun requestLocationPermission(
        permission: GeneratedAndroidLocation.PigeonLocationPermission,
        result: GeneratedAndroidLocation.Result<GeneratedAndroidLocation.PigeonLocationPermissionData>
    ) {
        val pigeonLocationPermissionData = GeneratedAndroidLocation.PigeonLocationPermissionData()
        val permissionProvider = DefaultPermissionProvider(LOCATION_PERMISSIONS, null)
        val contextProcessor = ContextProcessor(activity?.application)
        contextProcessor.activity = activity
        permissionProvider.setContextProcessor(contextProcessor)
        val hasPermission = permissionProvider.requestPermissions()

        if (!hasPermission) {
            pigeonLocationPermissionData.pigeonLocationPermission = 0
            result.success(pigeonLocationPermissionData)
        } else {
            pigeonLocationPermissionData.pigeonLocationPermission = permission.index.toLong()
            result.success(pigeonLocationPermissionData)
            //resultPermissionRequest = pigeonLocationPermissionData
        }
    }




    override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
        eventSink = events
        streamLocationManagerLoc2 = LocationManagerLoc2.Builder(context!!)
            .activity(activity) // Only required to ask permission and/or GoogleApi - SettingsApi
            .configuration(globalLocationConfigurationBuilder.keepTracking(true).build())
            .notify(this)
            .build()

        streamLocationManagerLoc2?.get()
    }

    override fun onCancel(arguments: Any?) {
        flutterLocationService?.disableBackgroundMode()

        eventSink = null
        streamLocationManagerLoc2?.cancel()
        streamLocationManagerLoc2 = null
    }

    companion object {
        private const val PERMISSION_STREAM_CHANNEL_NAME = "xunreal75/location2_permission_stream"
    }


}