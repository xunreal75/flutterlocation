part of '../location2_platform_interface.dart';

/// Allow to map the return of the permission request
/// Will be deleted once Pigeon supports returning Enum
/// https://github.com/flutter/flutter/issues/87307
PermissionStatus permissionStatusFromInt(int permission) {
  switch (permission) {
    case 0:
      return PermissionStatus.notDetermined;
    case 1:
      return PermissionStatus.restricted;
    case 2:
      return PermissionStatus.denied;
    case 3:
      return PermissionStatus.authorizedAlways;
    case 4:
      return PermissionStatus.authorizedWhenInUse;
    default:
      throw Exception('Unknown permission status: $permission');
  }
}

/// Allow to map the return of the [LocationPermission] request
/// Will be deleted once Pigeon supports returning Enum
/// https://github.com/flutter/flutter/issues/87307
LocationPermission locationPermissionStatusFromInt(int permission) {
  switch (permission) {
    case 0:
      return LocationPermission.notDetermined;
    case 1:
      return LocationPermission.restricted;
    case 2:
      return LocationPermission.denied;
    case 3:
      return LocationPermission.authorizedAlways;
    case 4:
      return LocationPermission.authorizedWhenInUse;
    case 5:
      return LocationPermission.authorizedAlways;
    default:
      return LocationPermission.unknown;
  }


}