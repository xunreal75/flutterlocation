package app.huth.location

import android.Manifest
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.GnssStatus
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
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
import app.huth.location.location.listener.PermissionListener
import app.huth.location.location.providers.locationprovider.DefaultLocationProvider
import app.huth.location.location.providers.locationprovider.LocationProvider
import app.huth.location.location.providers.permissionprovider.DefaultPermissionProvider
import app.huth.location.location.providers.permissionprovider.LocationPermissionProvider
import app.huth.location.location.providers.permissionprovider.PermissionProvider
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
class LocationPlugin : FlutterPlugin, ActivityAware, LocationListener,
    PluginRegistry.RequestPermissionsResultListener,
    PluginRegistry.ActivityResultListener, GeneratedAndroidLocation.LocationHostApi,GeneratedAndroidLocation.LocationPermissionsHostApi, StreamHandler  {

    private var context: Context? = null
    private var activity: Activity? = null
    private var activityBinding: ActivityPluginBinding? = null

    private var globalLocationConfigurationBuilder: LocationConfiguration.Builder =
        defaultConfiguration("The location is needed", "The GPS is needed")
    private var locationManagerLoc2: LocationManagerLoc2? = null

    private var streamLocationManagerLoc2: LocationManagerLoc2? = null
    private var flutterLocationService: FlutterLocationService? = null


    @RequiresApi(Build.VERSION_CODES.P)
    private val locationPermissionHandler :LocationPermissionProvider? = null
    private var locationEventChannel: EventChannel? = null
    private var permissionEventChannel: EventChannel? = null
    private var locationEventSink: EventChannel.EventSink? = null
    private var permissionEventSink: EventChannel.EventSink? = null

    private var resultsNeedingLocation: MutableList<GeneratedAndroidLocation.Result<GeneratedAndroidLocation.PigeonLocationData>?> =
        mutableListOf()

    private var resultPermissionRequest: GeneratedAndroidLocation.Result<Long>? = null

    private var alreadyRequestedPermission = false

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        GeneratedAndroidLocation.LocationHostApi.setup(flutterPluginBinding.binaryMessenger, this)
        GeneratedAndroidLocation.LocationPermissionsHostApi.setup(flutterPluginBinding.binaryMessenger,this)
        context = flutterPluginBinding.applicationContext
        locationEventChannel = EventChannel(flutterPluginBinding.binaryMessenger, LOCATION_STREAM_CHANNEL_NAME)
        locationEventChannel?.setStreamHandler(this)

        permissionEventChannel = EventChannel(flutterPluginBinding.binaryMessenger, PERMISSION_STREAM_CHANNEL_NAME)
        permissionEventChannel?.setStreamHandler(LocationPermissionProviderHandler)
    }


    override fun onDetachedFromEngine( flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        GeneratedAndroidLocation.LocationHostApi.setup(flutterPluginBinding.binaryMessenger, null)
        GeneratedAndroidLocation.LocationPermissionsHostApi.setup(flutterPluginBinding.binaryMessenger,null)

        context = null
        locationEventChannel = null
        permissionEventChannel = null
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
        registerReceiver()
    }

    override fun onDetachedFromActivity() {
        activity = null
        activityBinding?.removeActivityResultListener(this)
        activityBinding?.removeRequestPermissionsResultListener(this)
        activityBinding?.activity?.unbindService(serviceConnection)
        activityBinding = null
        unregisterReceiver()
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

    private val gpsSwitchStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) = checkGpsAndReact()
    }

    private fun checkGpsAndReact() = if (isLocationEnabled()) {
        //postValue(GpsStatus.Enabled())
    } else {
       // postValue(GpsStatus.Disabled())
    }

    private fun isLocationEnabled(): Boolean {
        val locMan = context?.getSystemService( Context.LOCATION_SERVICE) as LocationManager
        return locMan.isProviderEnabled(LocationManager.GPS_PROVIDER) || locMan.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

   private fun registerReceiver() = context?.registerReceiver(gpsSwitchStateReceiver,
       IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
    )

    private fun unregisterReceiver() = context?.unregisterReceiver(gpsSwitchStateReceiver)

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

        locationEventSink?.success(pigeonLocationData.toList())

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

 object LocationPermissionProviderHandler:StreamHandler, LiveData<GnssStatus>(), PermissionListener  {

     init {
         handlePermissionCheck()
     }

     private var handler = Handler(Looper.getMainLooper())
     var permissionEventSink: EventChannel.EventSink? = null

     private val gpsObserver = Observer<GnssStatus> {
             status -> status?.let { handleGpsStatus(status) }
     }

     private fun handleGpsStatus(status: GnssStatus) {
             Log.d("LocationPermission", "onLocationFailed $status")
     }

     private val permissionObserver = Observer<LocationProvider> {
             status -> status?.let { handlePermissionStatus(status) }
     }

     private fun handlePermissionStatus(status: Any) {
         Log.d("LocationPermission", "handlePermissionStatus $status")


     }

     override fun onActive() = handlePermissionCheck()
     private fun handlePermissionCheck() {
        // val isPermissionGranted = ActivityCompat.checkSelfPermission(context, permissionToListen) == PackageManager.PERMISSION_GRANTED
         Log.d("LocationPermission", "handlePermissionCheck: ")
        // if (isPermissionGranted)
          //   postValue(PermissionStatus.Granted())
         //else
           //  postValue(PermissionStatus.Denied())
     }

     override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
        permissionEventSink = events
        //permissionProvider.requestPermissions()
     }

     override fun onCancel(arguments: Any?) {
         permissionEventSink = null
     }

     override fun onPermissionsGranted() {
         TODO("Not yet implemented")
     }

     override fun onPermissionsDenied() {
         TODO("Not yet implemented")
     }


 }

    override fun onPermissionGranted(alreadyHadPermission: Boolean, limitedPermission: Boolean) {
        Log.d("Location", "onPermissionGranted")
        val pigeonLocationPermissionData = GeneratedAndroidLocation.PigeonLocationPermissionData()
        pigeonLocationPermissionData.pigeonLocationPermission = if (limitedPermission) 1 else 4
        LocationPermissionProviderHandler.permissionEventSink?.success(pigeonLocationPermissionData.toList())
        //permissionEventChannel.LocationPermissionProviderHandler.permissionEventSink?.success(if (limitedPermission) 1 else 4)
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
        //LocationPermissionProviderHandler.permissionEventSink?.success(0)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ): Boolean {
        this.alreadyRequestedPermission = true
        Log.d("Location", "onRequestPermissionsResult $permissions")
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

    override fun getLocation(
        settings: GeneratedAndroidLocation.PigeonLocationSettings?,
        result: GeneratedAndroidLocation.Result<GeneratedAndroidLocation.PigeonLocationData>
    ) {

        resultsNeedingLocation.add(result)

        val isListening = streamLocationManagerLoc2 != null

        if (settings != null) {
            val locationConfiguration = getLocationConfigurationFromSettings(settings)
            locationManagerLoc2 = LocationManagerLoc2.Builder(context!!)
                .activity(activity) // Only required to ask permission and/or GoogleApi - SettingsApi
                .configuration(locationConfiguration.build())
                .notify(this)
                .build()

            locationManagerLoc2?.get()
        } else {
            if (!isListening) {
                locationManagerLoc2 = LocationManagerLoc2.Builder(context!!)
                    .activity(activity) // Only required to ask permission and/or GoogleApi - SettingsApi
                    .configuration(globalLocationConfigurationBuilder.build())
                    .notify(this)
                    .build()

                locationManagerLoc2?.get()
            }

        }

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

            val locationRequest = LocationRequest.Builder(getPriorityFromAccuracy(settings.accuracy),
                settings.interval.toLong())
                .setMinUpdateIntervalMillis(settings.fastestInterval.toLong())
                .setIntervalMillis (settings.interval.toLong())
                .setPriority(getPriorityFromAccuracy(settings.accuracy))
                .setMinUpdateDistanceMeters(settings.smallestDisplacement.toFloat())
                .setWaitForAccurateLocation(settings.waitForAccurateLocation)

            if (settings.expirationDuration != null) {
                locationRequest.setDurationMillis(settings.expirationDuration!!.toLong())
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

        globalLocationConfigurationBuilder = locationConfiguration

        if (streamLocationManagerLoc2 != null) {
            streamLocationManagerLoc2?.cancel()
            streamLocationManagerLoc2 = LocationManagerLoc2.Builder(context!!)
                .activity(activity) // Only required to ask permission and/or GoogleApi - SettingsApi
                .configuration(globalLocationConfigurationBuilder.keepTracking(true).build())
                .notify(this)
                .build()

            streamLocationManagerLoc2?.get()
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
                return 0
            }
        } else {
            return 4
        }


        if (alreadyRequestedPermission) {
            return 2
        }
        return 0
    }

    override fun requestPermission(result: GeneratedAndroidLocation.Result<Long>) {
        val permissionProvider = DefaultPermissionProvider(LOCATION_PERMISSIONS, null)
        val contextProcessor = ContextProcessor(activity?.application)
        contextProcessor.activity = activity
        permissionProvider.setContextProcessor(contextProcessor)
        val hasPermission = permissionProvider.requestPermissions()

        if (!hasPermission) {
            // Denied Forever
            result.success(2)
        } else {
            resultPermissionRequest = result
        }
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
        var status = locationPermissionStatus
        if (status.pigeonLocationPermission != permission.index.toLong()){
            var locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager;
            /*val locationPermissionRequest = registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { permissions ->
                when {
                    permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                        // Precise location access granted.
                    }
                    permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                        // Only approximate location access granted.
                    } else -> {
                    // No location access granted.
                }
                }
            } */           /*val result =
                Context.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION)
            val result1 = Context.checkSelfPermission(getApplicationContext(), CAMERA)*/
        }

        if (!hasPermission) {
            pigeonLocationPermissionData.pigeonLocationPermission = 0
            result.success(pigeonLocationPermissionData)
        } else {
             result.success(pigeonLocationPermissionData)
        //resultPermissionRequest = pigeonLocationPermissionData
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

    override fun openLocationSettings(result: GeneratedAndroidLocation.Result<Boolean>) {
        try {
            val settingsIntent = Intent()
            settingsIntent.action= Settings.ACTION_LOCATION_SOURCE_SETTINGS
            settingsIntent.addCategory(Intent.CATEGORY_DEFAULT)
            settingsIntent.data = Uri.parse("package:" + context!!.packageName)
            settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            settingsIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
            context!!.startActivity(settingsIntent)
            return
        } catch (ex: Exception) {
            return
        }
    }

    override fun openAppSettings(result: GeneratedAndroidLocation.Result<Boolean>) {
         try {
            val settingsIntent = Intent()
            settingsIntent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            settingsIntent.addCategory(Intent.CATEGORY_DEFAULT)
            settingsIntent.data = Uri.parse("package:" + context!!.packageName)
            settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            settingsIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
            context!!.startActivity(settingsIntent)

            result.success(true)
        } catch (ex: Exception) {
             result.success(false)
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
        locationEventSink = events
        streamLocationManagerLoc2 = LocationManagerLoc2.Builder(context!!)
            .activity(activity) // Only required to ask permission and/or GoogleApi - SettingsApi
            .configuration(globalLocationConfigurationBuilder.keepTracking(true).build())
            .notify(this)
            .build()

        streamLocationManagerLoc2?.get()
        if (arguments as Boolean) {
            flutterLocationService?.enableBackgroundMode()
        }
    }

    override fun onCancel(arguments: Any?) {
        flutterLocationService?.disableBackgroundMode()

        locationEventSink = null
        streamLocationManagerLoc2?.cancel()
        streamLocationManagerLoc2 = null
    }

    companion object {
        private const val LOCATION_STREAM_CHANNEL_NAME = "xunreal75/location2_stream"
        private const val PERMISSION_STREAM_CHANNEL_NAME = "xunreal75/location2_permission_stream"
    }
}