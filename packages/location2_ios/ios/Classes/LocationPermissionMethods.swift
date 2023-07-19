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
    private var askForAlways:Bool
    var  requestLocationPermissionFunc : ((PigeonLocationPermissionData?, FlutterError?) -> Void)?
    var permissionStreamHandler: PermissionStreamHandler?
    
    let TAG = "LocationPermissionsHostApi"
    
    override init() {
        self.locationManager = CLLocationManager()
        self.authorizationStatus = .notDetermined
        self.askForAlways = false
        requestLocationPermissionFunc = nil
        super.init()
        self.locationManager.delegate = self
    }
    
    public func requestLocationPermissionPermission(_ permission: PigeonLocationPermission, completion: @escaping (PigeonLocationPermissionData?, FlutterError?) -> Void) {
        if self.authorizationStatus == .denied {
            //completion(LocationPermissionMethods.locationPermissionToData(.denied), nil)
            //return
        }
        
        if(permission == PigeonLocationPermission.authorizedAlways || permission == PigeonLocationPermission.authorizedWhenInUse){
            
            if self.authorizationStatus == .authorizedAlways {
                //do nothing ist permitted
                completion(LocationPermissionMethods.locationPermissionToData(authorizationStatus), nil)
                return
            }
            
            if  (permission == PigeonLocationPermission.authorizedAlways && self.authorizationStatus == .authorizedWhenInUse) {
                let showedAuthorizedAlways = UserDefaults.standard.string(forKey: "showedAuthorizedAlways")
                if showedAuthorizedAlways != nil  {
                            print ("authorizedAlways was already requested" )
                            completion(LocationPermissionMethods.locationPermissionToData(authorizationStatus), FlutterError(code: "LOCATION2_ALWAYS_ALREADY_REQUESTED",message: "You can only request authorizedAlways once because limitations of iOS. Open App Settings manual", details: nil))
                            return
                        }
                UserDefaults.standard.setValue(true, forKey: "showedAuthorizedAlways")
                        // ios only shows always request once a time. you must show an individual self coded alert
                self.locationManager.requestAlwaysAuthorization();
                self.requestLocationPermissionFunc = completion
                self.askForAlways = true
                return
            }
            
            if  (permission == PigeonLocationPermission.authorizedAlways && self.authorizationStatus == .notDetermined) {
                self.locationManager.requestWhenInUseAuthorization();
                self.requestLocationPermissionFunc = completion
                self.askForAlways = true
                return
            }
            
            if  (permission == PigeonLocationPermission.authorizedWhenInUse && self.authorizationStatus == .authorizedWhenInUse) {
                //no changes
                completion(LocationPermissionMethods.locationPermissionToData(authorizationStatus), nil)
                self.askForAlways = true
                return
            }
            
            if self.authorizationStatus != .authorizedWhenInUse {
                self.locationManager.requestWhenInUseAuthorization();
                self.requestLocationPermissionFunc = completion
                return
            }
        }else{
            completion(LocationPermissionMethods.locationPermissionToData(.notDetermined), FlutterError(code: "LOCATION2_INVALID_PARAMETER",message: "You can only request authorizedAlways or authorizedWhenInUse", details: nil))
            return
        }
    }
    
    func completePermissionRequest() {
        //double step permissionrequest
        if (askForAlways == true && self.authorizationStatus == .authorizedWhenInUse){
            askForAlways = false;
            self.locationManager.requestAlwaysAuthorization();
            return;
        }
        askForAlways = false;
        requestLocationPermissionFunc?(LocationPermissionMethods.locationPermissionToData(authorizationStatus), nil)
    }
    
    public func getLocationPermissionStatusWithError(_ error: AutoreleasingUnsafeMutablePointer<FlutterError?>) -> PigeonLocationPermissionData? {
        let permission = self.authorizationStatus
        return LocationPermissionMethods.locationPermissionToData(permission)
        
    }
    
    @available(iOS, introduced: 4.2, deprecated: 14.0)
    public func locationManager(_ manager: CLLocationManager, didChangeAuthorization status: CLAuthorizationStatus) {
        checkLocationServicesStatusChange(status)
    }
    
    @available(iOS 14.0, *)
    public func locationManagerDidChangeAuthorization(_ manager: CLLocationManager) {
        checkLocationServicesStatusChange(manager.authorizationStatus)
       print( manager.accuracyAuthorization)
    }
    
    func checkLocationServicesStatusChange(_ status:CLAuthorizationStatus) {
        self.authorizationStatus = status
        completePermissionRequest()
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
