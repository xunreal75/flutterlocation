package app.huth.location.location.providers.permissionprovider

import android.content.Context
import android.location.GnssStatus
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import app.huth.location.GeneratedAndroidLocation
import app.huth.location.LocationPlugin
import app.huth.location.location.configuration.Defaults
import app.huth.location.location.listener.PermissionListener
import app.huth.location.location.view.ContextProcessor
import io.flutter.plugin.common.EventChannel

class LocationPermissionProviderHandler(context: Context) : EventChannel.StreamHandler,
    LiveData<GnssStatus>(),
    PermissionListener {


    init {
        if (context==null){
            Log.e("LocationPermissionProv", "error because context not attached")

        }else{
        handlePermissionCheck()
        }
    }

    private var context: Context? = null
    private var handler = Handler(Looper.getMainLooper())
    private var permissionEventSink: EventChannel.EventSink? = null

    private fun handleGpsStatus(status: GnssStatus) {
        Log.d("LocationPermission", "onLocationFailed $status")
    }


    private fun handlePermissionStatus(status: Any) {
        Log.d("LocationPermission", "handlePermissionStatus $status")


    }

    //override fun onActive() = handlePermissionCheck() throws error becaus activity not initialized
    private fun handlePermissionCheck() {

        // val isPermissionGranted = ActivityCompat.checkSelfPermission(context, permissionToListen) == PackageManager.PERMISSION_GRANTED
        Log.d("LocationPermission", "handlePermissionCheck: ")
        // if (isPermissionGranted)
        //   postValue(PermissionStatus.Granted())
        //else
        //  postValue(PermissionStatus.Denied())
    }

     /*fun getLocationPermissionStatus(): GeneratedAndroidLocation.PigeonLocationPermissionData {
        val permissionProvider = DefaultPermissionProvider(Defaults.LOCATION_PERMISSIONS, null)
        val contextProcessor = ContextProcessor(activity?.application)
        val pigeonLocationPermissionData = GeneratedAndroidLocation.PigeonLocationPermissionData()
        contextProcessor.activity = activity
        permissionProvider.setContextProcessor(contextProcessor)

        val permission = permissionProvider.checkLocationPermissions()

        pigeonLocationPermissionData.pigeonLocationPermission = permission.index.toLong()
        return pigeonLocationPermissionData

    }*/

    override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
        permissionEventSink = events
        handlePermissionCheck()
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
