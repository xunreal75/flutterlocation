package app.huth.location.location.providers.permissionprovider

import android.os.Handler
import android.util.Log
import androidx.lifecycle.LiveData
import app.huth.location.location.listener.PermissionListener
import app.huth.location.location.providers.locationprovider.LocationProvider
import io.flutter.plugin.common.EventChannel

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.location.GnssStatus
import android.location.LocationManager
import android.os.Looper
import android.os.Build

import androidx.annotation.RequiresApi

import androidx.lifecycle.Observer
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.EventChannel.StreamHandler

@RequiresApi(Build.VERSION_CODES.P)
class LocationPermissionProvider(context: Context) :FlutterPlugin, StreamHandler, LiveData<GnssStatus>(),
        PermissionListener,ActivityAware  {
    private var context: Context? = null
    private lateinit var sensorManager:SensorManager
    private lateinit var pressureSensor: Sensor
    private lateinit var motionSensor:Sensor

    private lateinit var locationManager:LocationManager
    private var locationEnabled = false

    fun initialize(context: Context?) {
        this.context = context
        if (context == null) return
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager ;
        pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        motionSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MOTION_DETECT);
        locationEnabled = locationManager.isLocationEnabled
    }
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
            println("locperm chgd")

        }

        override fun onActive() = handlePermissionCheck()
        @RequiresApi(Build.VERSION_CODES.P)
        private fun handlePermissionCheck() {
             var locMan = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
             val isPermissionGranted = locMan.isLocationEnabled
            println("locperm chgd")
             if (isPermissionGranted)
               ///postValue(GnssStatus.CONSTELLATION_GPS)//PermissionStatus.Granted())
            else{
              //postValue(PermissionStatus.Denied())
            }
        }

        override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
            permissionEventSink = events
        }

        override fun onCancel(arguments: Any?) {
            permissionEventSink = null
        }

        override fun onPermissionsGranted() {
        }

        override fun onPermissionsDenied() {
        }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        context =binding.activity
    }

    override fun onDetachedFromActivityForConfigChanges() {

    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        TODO("Not yet implemented")
    }

    override fun onDetachedFromActivity() {
        context = null
    }

    override fun onAttachedToEngine( flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        context = flutterPluginBinding.applicationContext
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {

    }

}