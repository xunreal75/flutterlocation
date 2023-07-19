import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:location2/location2.dart';

class PermissionStatusWidget extends StatefulWidget {
  const PermissionStatusWidget({super.key});

  @override
  State<PermissionStatusWidget> createState() => _PermissionStatusWidgetState();
}

class _PermissionStatusWidgetState extends State<PermissionStatusWidget> {
  PermissionStatus _permissionGranted = PermissionStatus.notDetermined;
  LocationPermission _locationPermissionGranted =
      LocationPermission.notDetermined;

  @Deprecated('in favor of _checkLocationPermission')
  Future<void> _checkPermissions() async {
    final permissionGrantedResult = await getPermissionStatus();
    setState(() {
      _permissionGranted = permissionGrantedResult;
    });
  }

  @Deprecated('in favor of _requestLocationPermission')
  Future<void> _requestPermission() async {
    final permissionRequestedResult = await requestPermission();
    setState(() {
      _permissionGranted = permissionRequestedResult;
    });
  }

  Future<void> _requestLocationPermission(LocationPermission permission) async {
    final permissionRequestedResult = await requestLocationPermission(
      permission,
    ).catchError((dynamic error) {
      //throws on invalid Parameters and 2nd request for always permission
      if (kDebugMode) {
        print(error);
      }
      return LocationPermission.unknown;
    });
    setState(() {
      _locationPermissionGranted = permissionRequestedResult;
    });
    if (_locationPermissionGranted==LocationPermission.unknown){
      await _checkLocationPermission();
    }
  }

  Future<void> _checkLocationPermission() async {
    final permissionRequestedResult = await getLocationPermissionStatus();

    setState(() {
      if (permissionRequestedResult.locationPermissionId != null) {
        _locationPermissionGranted = LocationPermission
            .values[permissionRequestedResult.locationPermissionId!];
      } else {
        _locationPermissionGranted = LocationPermission.unknown;
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 8),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: <Widget>[
          Text(
            'Permission request result : $_locationPermissionGranted',
            style: Theme.of(context).textTheme.bodyLarge,
          ),
          Row(
            children: <Widget>[
              Container(
                margin: const EdgeInsets.only(right: 42),
                child: ElevatedButton(
                  onPressed: _checkLocationPermission,
                  child: const Text('Check Location Permission'),
                ),
              ),
            ],
          ),
          Row(
            children: <Widget>[
              Container(
                margin: const EdgeInsets.only(right: 42),
                child: ElevatedButton(
                  onPressed: () {
                    _requestLocationPermission(
                      LocationPermission.authorizedAlways,
                    );
                  },
                  child: const Text('Request always'),
                ),
              ),
            ],
          ),
          Row(
            children: <Widget>[
              Container(
                margin: const EdgeInsets.only(right: 42),
                child: ElevatedButton(
                  onPressed: () {
                    _requestLocationPermission(
                      LocationPermission.authorizedWhenInUse,
                    );
                  },
                  child: const Text('Request WhenInUse'),
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }
}
