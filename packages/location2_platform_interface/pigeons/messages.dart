// ignore_for_file: avoid_positional_boolean_parameters

import 'package:pigeon/pigeon.dart';

@ConfigurePigeon(
  PigeonOptions(
    dartOut: 'lib/src/messages.pigeon.dart',
    dartTestOut: 'test/test.pigeon.dart',
    javaOut:
        '../location2_android/android/src/main/java/app/huth/location/GeneratedAndroidLocation.java',
    javaOptions: JavaOptions(
      package: 'app.huth.location',
      className: 'GeneratedAndroidLocation',
    ),
    objcHeaderOut: '../location2_ios/ios/Classes/messages.g.h',
    objcSourceOut: '../location2_ios/ios/Classes/messages.g.m',
  ),
)
class PigeonLocationData {
  double? latitude;
  double? longitude;
  double? accuracy;
  double? altitude;
  double? bearing;
  double? bearingAccuracyDegrees;
  double? elapsedRealTimeNanos;
  double? elapsedRealTimeUncertaintyNanos;
  int? satellites;
  double? speed;
  double? speedAccuracy;
  int? time;
  double? verticalAccuracy;
  bool? isMock;
}

class PigeonLocationPermissionData {
  int? pigeonLocationPermission;
}

enum PigeonLocationPermission {
  notDetermined,
  restricted,
  denied,
  authorizedAlways,
  authorizedWhenInUse,
  authorized,
  unknown,
}

class PigeonNotificationSettings {
  String? channelName;
  String? title;
  String? iconName;
  String? subtitle;
  String? description;
  String? color;
  bool? onTapBringToFront;
}

enum PigeonLocationAccuracy { powerSave, low, balanced, high, navigation }

class PigeonLocationSettings {
  PigeonLocationSettings({
    this.askForPermission = true,
    this.rationaleMessageForPermissionRequest =
        'The app needs to access your location',
    this.rationaleMessageForGPSRequest =
        'The app needs to access your GPS location',
    this.useGooglePlayServices = true,
    this.askForGooglePlayServices = false,
    this.askForGPS = true,
    this.fallbackToGPS = true,
    this.ignoreLastKnownPosition = false,
    this.expirationDuration,
    this.expirationTime,
    this.fastestInterval = 500,
    this.interval = 1000,
    this.maxWaitTime,
    this.numUpdates,
    this.acceptableAccuracy,
    this.accuracy = PigeonLocationAccuracy.high,
    this.smallestDisplacement = 0,
    this.waitForAccurateLocation = true,
  });

  bool askForPermission;
  String rationaleMessageForPermissionRequest;
  String rationaleMessageForGPSRequest;
  bool useGooglePlayServices;
  bool askForGooglePlayServices;
  bool askForGPS;
  bool fallbackToGPS;
  bool ignoreLastKnownPosition;
  double? expirationDuration;
  double? expirationTime;
  double fastestInterval;
  double interval;
  double? maxWaitTime;
  int? numUpdates;
  PigeonLocationAccuracy accuracy;
  double smallestDisplacement;
  bool waitForAccurateLocation;
  double? acceptableAccuracy;
}

@HostApi()
abstract class LocationHostApi {
  @async
  PigeonLocationData getLocation(PigeonLocationSettings? settings);

  bool setLocationSettings(PigeonLocationSettings settings);

  int getPermissionStatus();

  @async
  int requestPermission();

  bool isGPSEnabled();

  bool isNetworkEnabled();

  bool changeNotificationSettings(PigeonNotificationSettings settings);

  bool setBackgroundActivated(bool activated);

  @async
  bool openLocationSettings();

  @async
  bool openAppSettings();
}

@HostApi()
abstract class LocationPermissionsHostApi {

  PigeonLocationPermissionData getLocationPermissionStatus();

  @async
  PigeonLocationPermissionData requestLocationPermission(
      PigeonLocationPermission permission,
      );
}
