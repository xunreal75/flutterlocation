import 'dart:async';

import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:location2/location2.dart';

class ListenLocationPermissionWidget extends StatefulWidget {
  const ListenLocationPermissionWidget({super.key});

  @override
  State<ListenLocationPermissionWidget> createState() =>
      _ListenLocationPermissionWidgetState();
}

class _ListenLocationPermissionWidgetState
    extends State<ListenLocationPermissionWidget> {
  LocationPermission? _locationPermission;
  StreamSubscription<LocationPermissionData>? _locationPermissionSubscription;
  String? _error;

  Future<void> _stopListenPermissionChanges() async {
    await _locationPermissionSubscription?.cancel();
    setState(() {
      _locationPermissionSubscription = null;
    });
  }

  Future<void> _listenLocationPermission() async {
    _locationPermissionSubscription =
        onLocationPermissionChanged().handleError((dynamic err) {
      if (err is PlatformException) {
        setState(() {
          _error = err.code;
        });
      }
      _locationPermissionSubscription?.cancel();
      setState(() {
        _locationPermissionSubscription = null;
      });
    }).listen((LocationPermissionData currentLocationPermission) async {
      setState(() {
        _error = null;

        if (kDebugMode) {
          print(currentLocationPermission.locationPermissionId);
        }

        _locationPermission = LocationPermission
            .values[currentLocationPermission.locationPermissionId ?? 0];
      });
    });
    setState(() {});
  }

  @override
  void dispose() {
    _locationPermissionSubscription?.cancel();
    setState(() {
      _locationPermissionSubscription = null;
    });
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 8),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: <Widget>[
          Text(
            _error ??
                '''
                Listen locationPermission: $_locationPermission}
                ''',
            style: Theme.of(context).textTheme.bodyLarge,
          ),
          Row(
            children: <Widget>[
              Container(
                margin: const EdgeInsets.only(right: 16),
                child: ElevatedButton(
                  onPressed: _locationPermissionSubscription == null
                      ? _listenLocationPermission
                      : null,
                  child: const Text('Listen Permission'),
                ),
              ),
              ElevatedButton(
                onPressed: _locationPermissionSubscription != null
                    ? _stopListenPermissionChanges
                    : null,
                child: const Text('Stop'),
              )
            ],
          ),
        ],
      ),
    );
  }
}
