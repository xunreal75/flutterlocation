package app.huth.location.location

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import app.huth.location.location.configuration.LocationConfiguration
import app.huth.location.location.constants.FailType
import app.huth.location.location.constants.ProcessType
import app.huth.location.location.helper.LogUtils
import app.huth.location.location.helper.logging.Logger
import app.huth.location.location.listener.LocationListener
import app.huth.location.location.listener.PermissionListener
import app.huth.location.location.providers.locationprovider.DispatcherLocationProvider
import app.huth.location.location.providers.locationprovider.LocationProvider
import app.huth.location.location.providers.permissionprovider.PermissionProvider
import app.huth.location.location.view.ContextProcessor

class PermissionManager private constructor(builder: Builder) :
    PermissionListener {
    private val listener: LocationListener?

    /**
     * Returns configuration object which is defined to this manager
     */
    val configuration: LocationConfiguration?
    private val activeProvider: LocationProvider?
    private val permissionProvider: PermissionProvider

    /**
     * To create an instance of this manager you MUST specify a LocationConfiguration
     */
    init {
        listener = builder.listener
        configuration = builder.configuration
        activeProvider = builder.activeProvider
        permissionProvider = configuration!!.permissionConfiguration().permissionProvider()
        permissionProvider.setContextProcessor(builder.contextProcessor)
        permissionProvider.permissionListener = this
    }

    class Builder {
        val contextProcessor: ContextProcessor?
        var listener: LocationListener? = null
        var configuration: LocationConfiguration? = null
        var activeProvider: LocationProvider? = null

        /**
         * Builder object to create LocationManager
         *
         * @param contextProcessor holds the address of the context,which this manager will run on
         */
        constructor(contextProcessor: ContextProcessor) {
            this.contextProcessor = contextProcessor
        }

        /**
         * Builder object to create LocationManager
         *
         * @param context MUST be an application context
         */
        constructor(context: Context) {
            contextProcessor = ContextProcessor(context)
        }

        /**
         * Activity is required in order to ask for permission, GPS enable dialog, Rationale dialog,
         * GoogleApiClient and SettingsApi.
         *
         * @param activity will be kept as weakReference
         */
        fun activity(activity: Activity?): Builder {
            contextProcessor!!.activity = activity
            return this
        }

        /**
         * Fragment is required in order to ask for permission, GPS enable dialog, Rationale dialog,
         * GoogleApiClient and SettingsApi.
         *
         * @param fragment will be kept as weakReference
         */
        fun fragment(fragment: Fragment?): Builder {
            contextProcessor!!.fragment = fragment
            return this
        }

        /**
         * Configuration object in order to take decisions accordingly while trying to retrieve location
         */
        fun configuration(locationConfiguration: LocationConfiguration): Builder {
            configuration = locationConfiguration
            return this
        }

        /**
         * Instead of using [DispatcherLocationProvider] you can create your own,
         * and set it to manager so it will use given one.
         */
        fun locationProvider(provider: LocationProvider): Builder {
            activeProvider = provider
            return this
        }

        /**
         * Specify a LocationListener to receive location when it is available,
         * or get knowledge of any other steps in process
         */
        fun notify(listener: LocationListener?): Builder {
            this.listener = listener
            return this
        }

        fun build(): PermissionManager {
            checkNotNull(contextProcessor) { "You must set a context to PermissionManager." }
            checkNotNull(configuration) { "You must set a configuration object." }
            if (activeProvider == null) {
                locationProvider(DispatcherLocationProvider())
            }
            activeProvider!!.configure(contextProcessor, configuration, listener)
            return PermissionManager(this)
        }
    }



    /**
     * Release whatever you need to when onDestroy is called
     */
    fun onDestroy() {
        activeProvider!!.onDestroy()
    }

    /**
     * This is required to check when user handles with Google Play Services error, or enables GPS...
     */
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        activeProvider!!.onActivityResult(requestCode, resultCode, data)
    }

    /**
     * Provide requestPermissionResult to manager so the it can handle RuntimePermission
     */
    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        permissionProvider.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    /**
     * To determine whether LocationManager is currently waiting for location or it did already receive one!
     */
    val isWaitingForLocation: Boolean
        get() = activeProvider!!.isWaiting

    /**
     * To determine whether the manager is currently displaying any dialog or not
     */
    val isAnyDialogShowing: Boolean
        get() = activeProvider!!.isDialogShowing

    /**
     * Abort the mission and cancel all location update requests
     */
    fun cancel() {
        activeProvider!!.cancel()
    }

    /**
     * The only method you need to call to trigger getting location process
     */
    fun get() {
        askForPermission()
    }

    /**
     * Only For Test Purposes
     */
    fun activeProvider(): LocationProvider? {
        return activeProvider
    }

    fun askForPermission() {
        if (permissionProvider.hasPermission()) {
            permissionGranted(true)
        } else {
            listener?.onProcessTypeChanged(ProcessType.ASKING_PERMISSIONS)
            if (permissionProvider.requestPermissions()) {
                LogUtils.logI("Waiting until we receive any callback from PermissionProvider...")
            } else {
                LogUtils.logI("Couldn't get permission, Abort!")
                failed(FailType.PERMISSION_DENIED)
            }
        }
    }

    private fun permissionGranted(alreadyHadPermission: Boolean) {
        LogUtils.logI("We got permission!")
        listener?.onPermissionGranted(alreadyHadPermission, false)
        activeProvider!!.get()
    }

    private fun failed(@FailType type: Int) {
        listener?.onLocationFailed(type)
    }

    override fun onPermissionsGranted() {
        permissionGranted(false)
    }

    override fun onPermissionsDenied() {
        failed(FailType.PERMISSION_DENIED)
    }

    companion object {
        /**
         * Library tries to log as much as possible in order to make it transparent to see what is actually going on
         * under the hood. You can enable it for debug purposes, but do not forget to disable on production.
         *
         * Log is disabled as default.
         */
        fun enableLog(enable: Boolean) {
            LogUtils.enable(enable)
        }

        /**
         * The Logger specifies how this Library is logging debug information. By default [DefaultLogger]
         * is used and it can be replaced by your own custom implementation of [Logger].
         */
        fun setLogger(logger: Logger) {
            LogUtils.setLogger(logger)
        }
    }
}