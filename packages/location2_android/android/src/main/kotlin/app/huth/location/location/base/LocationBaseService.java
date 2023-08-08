package app.huth.location.location.base;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.CallSuper;

import app.huth.location.location.LocationManagerLoc2;
import app.huth.location.location.configuration.LocationConfiguration;
import app.huth.location.location.constants.ProcessType;
import app.huth.location.location.listener.LocationListener;

public abstract class LocationBaseService extends Service implements LocationListener {

    private LocationManagerLoc2 locationManagerLoc2;

    public abstract LocationConfiguration getLocationConfiguration();

    @CallSuper
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        locationManagerLoc2 = new LocationManagerLoc2.Builder(getApplicationContext())
              .configuration(getLocationConfiguration())
              .notify(this)
              .build();
        return super.onStartCommand(intent, flags, startId);
    }

    protected LocationManagerLoc2 getLocationManager() {
        return locationManagerLoc2;
    }

    protected void getLocation() {
        if (locationManagerLoc2 != null) {
            locationManagerLoc2.get();
        } else {
            throw new IllegalStateException("locationManager is null. "
                  + "Make sure you call super.onStartCommand before attempting to getLocation");
        }
    }

    @Override
    public void onProcessTypeChanged(@ProcessType int processType) {
        // override if needed
    }

    @Override
    public void onPermissionGranted(boolean alreadyHadPermission, boolean limitedPermission) {
        // override if needed
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // override if needed
    }

    @Override
    public void onProviderEnabled(String provider) {
        // override if needed
    }

    @Override
    public void onProviderDisabled(String provider) {
        // override if needed
    }
}
