import 'package:location2_platform_interface/location2_platform_interface.dart';

/// The iOS implementation of [LocationPlatform].
class LocationIOS {
  /// Registers this class as the default instance of [LocationPlatform]
  static void registerWith() {
    LocationPlatform.instance = MethodChannelLocation();
  }
}
