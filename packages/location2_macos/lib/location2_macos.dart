import 'package:location2_platform_interface/location2_platform_interface.dart';

/// The MacOS implementation of [LocationPlatform].
class LocationMacOS {
  /// Registers this class as the default instance of [LocationPlatform]
  static void registerWith() {
    LocationPlatform.instance = MethodChannelLocation();
  }
}
