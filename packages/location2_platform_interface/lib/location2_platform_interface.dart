library location2_platform_interface;

import 'dart:io' show Platform;

import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:location2_platform_interface/src/messages.pigeon.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

part 'src/mapper.dart';

part 'src/method_channel_location.dart';

part 'src/method_channel_location_permission.dart';

part 'src/types.dart';

/// The interface that implementations of location must implement.
///
/// Platform implementations should extend this class
/// rather than implement it as `Location`.
/// Extending this class (using `extends`) ensures that the subclass will get
/// the default implementation, while platform implementations that `implements`
///  this interface will be broken by newly added [LocationPlatform] methods.
abstract class LocationPlatform extends PlatformInterface {
  /// Constructs a LocationPlatform.
  LocationPlatform() : super(token: _token);

  static final Object _token = Object();

  static LocationPlatform _instance = MethodChannelLocation();

  /// The default instance of [LocationPlatform] to use.
  ///
  /// Defaults to [MethodChannelLocation].
  static LocationPlatform get instance => _instance;

  /// Platform-specific plugins should set this with their own platform-specific
  /// class that extends [LocationPlatform] when they  themselves.
  static set instance(LocationPlatform instance) {
    PlatformInterface.verify(instance, _token);
    _instance = instance;
  }

  /// Return the current location.
  Future<LocationData?> getLocation({LocationSettings? settings});

  /// Return a stream of the user's location.
  Stream<LocationData?> onLocationChanged({bool inBackground = false});

  /// Set new global location settings for the app
  Future<bool?> setLocationSettings(LocationSettings settings);

  /// Get the permission status of the app
  Future<PermissionStatus?> getPermissionStatus();

  /// Request location permission for the app
  Future<PermissionStatus?> requestPermission();

  /// Return true if GPS is enabled on the device
  Future<bool?> isGPSEnabled();

  /// Return true if Network is enabled on the device
  Future<bool?> isNetworkEnabled();

  /// Return true if the notification was properly updated
  Future<bool?> updateBackgroundNotification({
    String? channelName,
    String? title,
    String? iconName,
    String? subtitle,
    String? description,
    Color? color,
    bool? onTapBringToFront,
  });

  /// Will open the App settings page.
  /// Returns true if the location settings page could be opened, otherwise
  /// or false if not.
  Future<bool> openAppSettings();

  /// Will open the location settings page.
  /// Returns true if the location settings page could be opened, otherwise
  /// or false if not.
  Future<bool> openLocationSettings();
}

/// The interface that implementations of location_permission must implement.
///
/// Platform implementations should extend this class
/// rather than implement it as `Location`.
/// Extending this class (using `extends`) ensures that the subclass will get
/// the default implementation, while platform implementations that `implements`
///  this interface will be broken by newly added [LocationPermissionPlatform] methods.
abstract class LocationPermissionPlatform extends PlatformInterface {
  /// Constructs a LocationPlatform.
  LocationPermissionPlatform() : super(token: _token);

  static final Object _token = Object();

  static LocationPermissionPlatform _instance =
      MethodChannelLocationPermission();

  /// Platform-specific plugins should set this with their own platform-specific
  /// class that extends [LocationPermissionPlatform] when they  themselves.
  static set instance(LocationPermissionPlatform instance) {
    PlatformInterface.verify(instance, _token);
    _instance = instance;
  }

  /// The default instance of [LocationPlatform] to use.
  ///
  /// Defaults to [MethodChannelLocation].
  static LocationPermissionPlatform get instance => _instance;

  /// Return a stream of the user's locationPermission.
  Stream<LocationPermissionData?> onLocationPermissionChanged();

  /// Get the permission status of the app
  Future<LocationPermissionData?> getLocationPermissionStatus();

  /// Request location permission for the app
  /// [LocationPermissionData]
  Future<LocationPermissionData?> requestLocationPermission(
    LocationPermission locationPermission,
  );
}
