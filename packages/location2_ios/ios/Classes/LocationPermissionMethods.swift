//
//  LocationPermissionMethods.swift
//  integration_test
//
//  Created by Lars Huth on 15.07.23.
//

import Flutter
import UIKit
import CoreLocation
import Foundation
import SwiftLocation

class LocationPermissionMethods: NSObject, LocationPermissionsHostApi,CLLocationManagerDelegate {
    private var locationManager: CLLocationManager
    private var authorizationStatus:CLAuthorizationStatus
    var permissionStreamHandler: PermissionStreamHandler?
    
    let TAG = "LocationPermissionsHostApi"
    
    override init() {
        self.locationManager = CLLocationManager()
        self.authorizationStatus = .notDetermined
        super.init()
        self.locationManager.delegate = self
        
    }
    
    public func requestLocationPermissionPermission(_ permission: PigeonLocationPermission, completion: @escaping (PigeonLocationPermissionData?, FlutterError?) -> Void) {
        self.authorizationStatus = SwiftLocation.authorizationStatus
        
        if self.authorizationStatus != .authorizedAlways ||
            self.authorizationStatus !=  .authorizedWhenInUse {
            self.locationManager.requestWhenInUseAuthorization();
            completion(LocationPermissionMethods.locationPermissionToData(authorizationStatus), nil)
        }
        else {
            completion(LocationPermissionMethods.locationPermissionToData(.notDetermined), FlutterError(code: "LOCATION2_INVALID_PARAMETER",message: "You can only request authorizedAlways,authorizedWhenInUse or authorized", details: nil))
            return
            
        }
      
    }
    
    public func getLocationPermissionStatusWithError(_ error: AutoreleasingUnsafeMutablePointer<FlutterError?>) -> PigeonLocationPermissionData? {
        let permission = SwiftLocation.authorizationStatus
        return LocationPermissionMethods.locationPermissionToData(permission)
        
    }
    
    @available(iOS, introduced: 4.2, deprecated: 14.0)
    public func locationManager(_ manager: CLLocationManager, didChangeAuthorization status: CLAuthorizationStatus) {
        checkLocationServicesStatusChange()
    }
    
    @available(iOS 14.0, *)
    public func locationManagerDidChangeAuthorization(_ manager: CLLocationManager) {
        checkLocationServicesStatusChange()
    }
    
    func checkLocationServicesStatusChange() {
        self.authorizationStatus = SwiftLocation.authorizationStatus
    }
    
    static public func locationPermissionToData(_ permission: CLAuthorizationStatus) -> PigeonLocationPermissionData{
        switch permission {
            /*
             PigeonLocationPermissionNotDetermined = 0,
             PigeonLocationPermissionRestricted = 1,
             PigeonLocationPermissionDenied = 2,
             PigeonLocationPermissionAuthorizedAlways = 3,
             PigeonLocationPermissionAuthorizedWhenInUse = 4,
             PigeonLocationPermissionAuthorized = 3,
             PigeonLocationPermissionUnknown = 6,
             */
        case .notDetermined:
            return PigeonLocationPermissionData.make(withPigeonLocationPermission:NSNumber(value:0 ))
        case .restricted:
            return PigeonLocationPermissionData.make(withPigeonLocationPermission:NSNumber(value:1 ))
        case .denied:
            return PigeonLocationPermissionData.make(withPigeonLocationPermission:NSNumber(value:2 ))
        case .authorizedAlways:
            return PigeonLocationPermissionData.make(withPigeonLocationPermission:NSNumber(value:3 ))
        case .authorizedWhenInUse:
            return PigeonLocationPermissionData.make(withPigeonLocationPermission:NSNumber(value:4 ))
        case .authorized:
            return PigeonLocationPermissionData.make(withPigeonLocationPermission:NSNumber(value:3 ))
        @unknown default:
            return PigeonLocationPermissionData.make(withPigeonLocationPermission:NSNumber(value:6 ))
        }
    }
}
