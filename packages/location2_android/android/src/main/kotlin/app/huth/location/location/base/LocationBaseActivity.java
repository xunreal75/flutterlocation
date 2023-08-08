package app.huth.location.location.base;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import app.huth.location.location.LocationManagerLoc2;
import app.huth.location.location.configuration.LocationConfiguration;
import app.huth.location.location.constants.ProcessType;
import app.huth.location.location.listener.LocationListener;

public abstract class LocationBaseActivity extends AppCompatActivity implements LocationListener {

    private LocationManagerLoc2 locationManagerLoc2;

    public abstract LocationConfiguration getLocationConfiguration();

    protected LocationManagerLoc2 getLocationManager() {
        return locationManagerLoc2;
    }

    protected void getLocation() {
        if (locationManagerLoc2 != null) {
            locationManagerLoc2.get();
        } else {
            throw new IllegalStateException("locationManager is null. "
                  + "Make sure you call super.initialize before attempting to getLocation");
        }
    }

    @CallSuper
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationManagerLoc2 = new LocationManagerLoc2.Builder(getApplicationContext())
              .configuration(getLocationConfiguration())
              .activity(this)
              .notify(this)
              .build();
    }

    @CallSuper
    @Override
    protected void onDestroy() {
        locationManagerLoc2.onDestroy();
        super.onDestroy();
    }

    @CallSuper
    @Override
    protected void onPause() {
        locationManagerLoc2.onPause();
        super.onPause();
    }

    @CallSuper
    @Override
    protected void onResume() {
        super.onResume();
        locationManagerLoc2.onResume();
    }

    @CallSuper
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        locationManagerLoc2.onActivityResult(requestCode, resultCode, data);
    }

    @CallSuper
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        locationManagerLoc2.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
