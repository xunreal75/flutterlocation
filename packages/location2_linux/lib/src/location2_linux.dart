import 'dart:ui';

import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:location2_platform_interface/location2_platform_interface.dart';

/// The Linux implementation of [LocationPlatform].
class LocationLinux extends LocationPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('location2_linux');

  /// Registers this class as the default instance of [LocationPlatform]
  static void registerWith() {
    LocationPlatform.instance = LocationLinux();
  }

  @override
  Future<LocationData?> getLocation({LocationSettings? settings}) {
    // TODO: implement getLocation
    throw UnimplementedError();
  }

  @override
  Future<PermissionStatus?> getPermissionStatus() {
    // TODO: implement getPermissionStatus
    throw UnimplementedError();
  }

  @override
  Future<bool?> isGPSEnabled() {
    // TODO: implement isGPSEnabled
    throw UnimplementedError();
  }

  @override
  Future<bool?> isNetworkEnabled() {
    // TODO: implement isNetworkEnabled
    throw UnimplementedError();
  }

  @override
  Future<PermissionStatus?> requestPermission() {
    // TODO: implement requestPermission
    throw UnimplementedError();
  }

  @override
  Future<bool?> setLocationSettings(LocationSettings settings) {
    // TODO: implement setLocationSettings
    throw UnimplementedError();
  }

  @override
  Future<bool> updateBackgroundNotification(
      {String? channelName,
      String? title,
      String? iconName,
      String? subtitle,
      String? description,
      Color? color,
      bool? onTapBringToFront}) {
    // TODO: implement updateBackgroundNotification
    throw UnimplementedError();
  }

  @override
  Stream<LocationData?> onLocationChanged({bool inBackground = false}) {
    // TODO: implement onLocationChanged
    throw UnimplementedError();
  }

  @override
  Stream<PermissionStatus?> onProviderChanged() {
    // TODO: implement onProviderChanged
    throw UnimplementedError();
  }

  @override
  Future<bool> openAppSettings() {
    // TODO: implement openAppSettings
    throw UnimplementedError();
  }

  @override
  Future<bool> openLocationSettings() {
    // TODO: implement openLocationSettings
    throw UnimplementedError();
  }

  @override
  Future<LocationPermissionData?> getLocationPermissionStatus() {
    // TODO: implement getLocationPermissionStatus
    throw UnimplementedError();
  }

  @override
  Stream<LocationPermissionData?> onLocationPermissionChanged() {
    // TODO: implement onLocationPermissionChanged
    throw UnimplementedError();
  }

  @override
  Future<LocationPermissionData?> requestLocationPermission(
      LocationPermission locationPermission) {
    // TODO: implement requestLocationPermission
    throw UnimplementedError();
  }
}
