part of '../location2_platform_interface.dart';

///
class MethodChannelLocationPermission extends LocationPermissionPlatform {
  ///
  factory MethodChannelLocationPermission() {
    if (_instance == null) {
      const eventChannel =
          EventChannel('xunreal75/location2_permission_stream');
      _instance = MethodChannelLocationPermission.private(eventChannel);
    }
    return _instance!;
  }

  /// This constructor is only used for testing and shouldn't be accessed by
  /// users of the plugin. It may break or change at any time.
  @visibleForTesting
  MethodChannelLocationPermission.private(this._permissionEventChannel);

  static MethodChannelLocationPermission? _instance;

  final _permissionApi = LocationPermissionsHostApi();

  late final EventChannel _permissionEventChannel;

  /// Current opened stream of locationPermission

  Stream<LocationPermissionData>? _onLocationPermissionChanged;

  @override
  Future<LocationPermissionData?> getLocationPermissionStatus() async {
    final res = await _permissionApi.getLocationPermissionStatus();
    return LocationPermissionData.fromPigeon(res);
  }

  @override
  Future<LocationPermissionData?> requestLocationPermission(
    LocationPermission locationPermission,
  ) async {
    final val = await _permissionApi
        .requestLocationPermission(locationPermission.toPigeon());
    return LocationPermissionData.fromPigeon(val);
  }

  @override
  Stream<LocationPermissionData?> onLocationPermissionChanged() {
    return _onLocationPermissionChanged ??= _permissionEventChannel
        .receiveBroadcastStream()
        .map<LocationPermissionData>(
          (dynamic event) => LocationPermissionData.fromPigeon(
            PigeonLocationPermissionData.decode(event as Object),
          ),
        );
  }
}
