import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:location2/location2.dart';

class GetLocationWidget extends StatefulWidget {
  const GetLocationWidget({super.key});

  @override
  State<GetLocationWidget> createState() => _GetLocationWidgetState();
}

class _GetLocationWidgetState extends State<GetLocationWidget> {
  bool _loading = false;

  LocationData? _location;
  String? _error;

  Future<void> _getLocation() async {
    setState(() {
      _error = null;
      _loading = true;
    });
    try {
      final locationResult = await getLocation(
        settings: LocationSettings(
          ignoreLastKnownPosition: true,
        ),
      );

      setState(() {
        _location = locationResult;
        _loading = false;
      });
    } on PlatformException catch (err) {
      setState(() {
        _error = err.code;
        _loading = false;
      });
    }
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
                'Location: ${_location?.latitude}, ${_location?.longitude}',
            style: Theme.of(context).textTheme.bodyLarge,
          ),
          Row(
            children: <Widget>[
              ElevatedButton(
                onPressed: _getLocation,
                child: _loading
                    ? const CircularProgressIndicator(
                        color: Colors.white,
                      )
                    : const Text('Get'),
              )
            ],
          ),
        ],
      ),
    );
  }
}
