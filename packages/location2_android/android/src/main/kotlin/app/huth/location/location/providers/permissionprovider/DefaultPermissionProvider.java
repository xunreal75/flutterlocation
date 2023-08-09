package app.huth.location.location.providers.permissionprovider;

import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import app.huth.location.GeneratedAndroidLocation;
import app.huth.location.location.constants.RequestCode;
import app.huth.location.location.helper.LogUtils;
import app.huth.location.location.listener.DialogListener;
import app.huth.location.location.providers.dialogprovider.DialogProvider;

public class DefaultPermissionProvider extends PermissionProvider implements DialogListener {

    private app.huth.location.location.providers.permissionprovider.PermissionCompatSource permissionCompatSource;

    public DefaultPermissionProvider(String[] requiredPermissions, @Nullable DialogProvider dialogProvider) {
        super(requiredPermissions, dialogProvider);
    }

    public GeneratedAndroidLocation.PigeonLocationPermission checkLocationPermissions(){
        if (getActivity() == null) {
            LogUtils.logI("Cannot ask for permissions, "
                    + "because DefaultPermissionProvider doesn't contain an Activity instance.");
            return GeneratedAndroidLocation.PigeonLocationPermission.NOT_DETERMINED;
        }
        Activity activity = getActivity();
        boolean hasForegroundLocationPermission = false;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            hasForegroundLocationPermission = activity.checkSelfPermission(
                    android.Manifest.permission.ACCESS_COARSE_LOCATION.toString()) == PackageManager.PERMISSION_GRANTED;
        }
        if (hasForegroundLocationPermission) {
            boolean hasBackgroundLocationPermission = false;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                hasBackgroundLocationPermission = activity.checkSelfPermission(
                        android.Manifest.permission.ACCESS_BACKGROUND_LOCATION.toString()) == PackageManager.PERMISSION_GRANTED;
                if (hasBackgroundLocationPermission) return GeneratedAndroidLocation.PigeonLocationPermission.AUTHORIZED_ALWAYS;
               return GeneratedAndroidLocation.PigeonLocationPermission.AUTHORIZED_WHEN_IN_USE;
             }
        }
        else {
            return GeneratedAndroidLocation.PigeonLocationPermission.DENIED;
        }
        return GeneratedAndroidLocation.PigeonLocationPermission.NOT_DETERMINED;
    }

    @Override
    public boolean requestPermissions() {
        if (getActivity() == null) {
            LogUtils.logI("Cannot ask for permissions, "
                  + "because DefaultPermissionProvider doesn't contain an Activity instance.");
            return false;
        }

        if (shouldShowRequestPermissionRationale()) {
            getDialogProvider().setDialogListener(this);
            getDialogProvider().getDialog(getActivity()).show();
        } else {
            executePermissionsRequest();
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == RequestCode.RUNTIME_PERMISSION) {

            // Check if any of required permissions are denied.
            int isDenied = 0;
            for (int i = 0, size = permissions.length; i < size; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    isDenied ++;
                }
            }

            if (isDenied == permissions.length) {
                LogUtils.logI("User denied all of required permissions, task will be aborted!");
                if (getPermissionListener() != null) getPermissionListener().onPermissionsDenied();
            } else {
                LogUtils.logI("We got all required permission!");
                if (getPermissionListener() != null) getPermissionListener().onPermissionsGranted();
            }
        }
    }

    @Override
    public void onPositiveButtonClick() {
        executePermissionsRequest();
    }

    @Override
    public void onNegativeButtonClick() {
        LogUtils.logI("User didn't even let us to ask for permission!");
        if (getPermissionListener() != null) getPermissionListener().onPermissionsDenied();
    }

    public boolean shouldShowRequestPermissionRationale() {
        boolean shouldShowRationale = false;
        for (String permission : getRequiredPermissions()) {
            shouldShowRationale = shouldShowRationale || checkRationaleForPermission(permission);
        }

        LogUtils.logI("Should show rationale dialog for required permissions: " + shouldShowRationale);

        return shouldShowRationale && getActivity() != null && getDialogProvider() != null;
    }

    public boolean checkRationaleForPermission(String permission) {
        if (getActivity() != null) {
            return getPermissionCompatSource().shouldShowRequestPermissionRationale(getActivity(), permission);
        } else {
            return false;
        }
    }

    void executePermissionsRequest() {
        LogUtils.logI("Asking for Runtime Permissions...");
        if (getFragment() != null) {
            getPermissionCompatSource().requestPermissions(getFragment(),
                  getRequiredPermissions());
        } else if (getActivity() != null) {
            getPermissionCompatSource().requestPermissions(getActivity(),
                  getRequiredPermissions());
        } else {
            LogUtils.logE("Something went wrong requesting for permissions.");
            if (getPermissionListener() != null) getPermissionListener().onPermissionsDenied();
        }
    }

    // For test purposes
    void setPermissionCompatSource(app.huth.location.location.providers.permissionprovider.PermissionCompatSource permissionCompatSource) {
        this.permissionCompatSource = permissionCompatSource;
    }

    protected app.huth.location.location.providers.permissionprovider.PermissionCompatSource getPermissionCompatSource() {
        if (permissionCompatSource == null) {
            permissionCompatSource = new app.huth.location.location.providers.permissionprovider.PermissionCompatSource();
        }
        return permissionCompatSource;
    }

}
