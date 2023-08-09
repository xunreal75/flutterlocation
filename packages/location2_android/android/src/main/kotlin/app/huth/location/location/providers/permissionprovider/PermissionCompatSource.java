package app.huth.location.location.providers.permissionprovider;

import android.app.Activity;

import androidx.core.app.ActivityCompat;

class PermissionCompatSource {
    boolean shouldShowRequestPermissionRationale(Activity activity, String permission) {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
    }

    //Background -> Always request for
    // android.permission.ACCESS_FINE_LOCATION and
    // android.permission.ACCESS_BACKGROUND_LOCATION
    void requestPermissions(Activity activity, String[] requiredPermissions) {
        ActivityCompat.requestPermissions(activity, requiredPermissions, app.huth.location.location.constants.RequestCode.RUNTIME_PERMISSION);
    }

}
