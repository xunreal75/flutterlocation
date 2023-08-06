//
//  StreamHandler.swift
//  location2_ios
//
//  Created by Guillaume Bernos on 10/06/2022.
//  Edited by Lhuth on 03/08/2023 because Streamhandler issue with 2nd package

import Foundation
import SwiftLocation
import CoreLocation

class StreamHandlerLoc2: NSObject, FlutterStreamHandler,CLLocationManagerDelegate {
    var locationRequest: GPSLocationRequest?
    var authorizationStatus:CLAuthorizationStatus = .notDetermined
    var locationSettings: PigeonLocationSettings?
    var events: FlutterEventSink?
    var lastKnownLocation: CLLocation?
    private let locationManager: CLLocationManager = CLLocationManager()
    var getLocationPermissionCompletion:((CLAuthorizationStatus)->Void)?
    var startTrackingAfterPermissionCb:((PigeonLocationPermissionData?, FlutterError?)->Void)?
    
    override init() {
        super.init()
        self.locationManager.delegate = self
    }
    
 
    func locationManager(_: CLLocationManager, didFailWithError error : Error){
        if self.events != nil{
            let err = CLError.Code(rawValue: (error as NSError).code)!
            self.events!(FlutterError(code: "LOCATION2_LOCATION_ERROR",
                                      message: "locationManager did fail with error: \(err)",
                                      details: nil))
        }
        
    }
    
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        
        guard let location = locations.last else{return}
            print("New location: \(location) received")
            lastKnownLocation = location
            LocationMethods.setLastKnownPoint(location)
            if self.events != nil{
                self.events!(LocationMethods.locationToDataObject(location))
            }
    }
    
    @available(iOS, introduced: 4.2, deprecated: 14.0)
    public func locationManager(_ manager: CLLocationManager, didChangeAuthorization status: CLAuthorizationStatus) {
        self.authorizationStatus = status
        getLocationPermissionCompletion?(status)
    }
    
    @available(iOS 14.0, *)
    public func locationManagerDidChangeAuthorization(_ manager: CLLocationManager) {
        self.authorizationStatus=manager.authorizationStatus
        getLocationPermissionCompletion?(manager.authorizationStatus)
        debugPrint("Locatiomanager authorisation is set to: \(self.authorizationStatus.description)")
    }
    
    
    public func onListen(withArguments arguments: Any?, eventSink events: @escaping FlutterEventSink) -> FlutterError? {
        if (self.authorizationStatus == .denied) {
            //UIApplication.shared.open(URL(string:UIApplication.openSettingsURLString)!)
            events( FlutterError(code: "LOCATION2_SERVICE_DISABLED",
                                 message: "The user have deactivated the location service, the settings page has been opened",
                                 details: nil))
        }
        
        if (self.authorizationStatus == .notDetermined) {
            
            
        }
        
        if (self.authorizationStatus == .denied)
        {
            //UIApplication.shared.open(URL(string:UIApplication.openSettingsURLString)!)
            return FlutterError(code: "LOCATION2_SERVICE",
                                message: "The user have deactivated the location service, the settings page has been opened",
                                details: nil)
        }
        let activated = arguments as! Bool? ?? false
        if #available(iOS 11.0, *) {
            // Running iOS 11 OR NEWER
            locationManager.showsBackgroundLocationIndicator = activated
        }
        
        self.events = events
        startListening(inBackground: activated)
        
        return nil
    }
    
    
    
    public func onCancel(withArguments arguments: Any?) -> FlutterError? {
        locationRequest?.cancelRequest()
        locationManager.stopUpdatingLocation()
        LocationMethods.locationManagerIsContinousListening(false)
        events = nil
        return nil
    }
    
    public func setPigeonLocationSettings(_ settings: PigeonLocationSettings) {
        self.locationSettings = settings
        locationRequest?.cancelRequest()
        startListening(inBackground: false)
    }
    
    private func startListening(inBackground:Bool) {
        if (events == nil) {
            return
        }
        
        let options = LocationMethods.locationSettingsToGPSLocationOptions(locationSettings)
        options?.subscription = .continous
        
        var avoidRequestPermissions = false;
        
        locationRequest = options != nil ? SwiftLocation.gpsLocationWith (options!) :  SwiftLocation.gpsLocationWith {
            $0.subscription = .continous
            $0.accuracy = .house
            $0.minTimeInterval = 2
            $0.activityType = .automotiveNavigation
        }
        
        if (options != nil){
            locationManager.desiredAccuracy = options!.accuracy.value
            locationManager.allowsBackgroundLocationUpdates = inBackground
            locationManager.activityType = options!.activityType
            locationManager.pausesLocationUpdatesAutomatically = false
            avoidRequestPermissions = options!.avoidRequestAuthorization
        }
        else if (LocationMethods.globalPigeonLocationSettings != nil ){
            locationManager.desiredAccuracy = LocationMethods.getCLLocationAccuracy(pAccuracy: LocationMethods.globalPigeonLocationSettings!.accuracy)
            locationManager.allowsBackgroundLocationUpdates = inBackground
            locationManager.activityType = .automotiveNavigation
            locationManager.pausesLocationUpdatesAutomatically = false
            avoidRequestPermissions = !(LocationMethods.globalPigeonLocationSettings!.askForPermission as! Bool)
        }
        else  {
            locationManager.allowsBackgroundLocationUpdates = inBackground
            locationManager.activityType = .automotiveNavigation
            locationManager.pausesLocationUpdatesAutomatically = false
            locationManager.desiredAccuracy = kCLLocationAccuracyNearestTenMeters
        }
        
        
        if (self.authorizationStatus == .notDetermined){
            if (!avoidRequestPermissions){
                
                requestLocationPermission(.authorizedAlways, completion:
                {  status  in
                    debugPrint(status)
                    if (status != .denied && status != .notDetermined){
                        self.locationManager.startUpdatingLocation()
                        LocationMethods.locationManagerIsContinousListening(true)
                        
                    }
                    else{
                        self.locationManager.stopUpdatingLocation()
                        self.events = nil
                        LocationMethods.locationManagerIsContinousListening(true)
                    }
                })
               
            }
            else{
                events!( FlutterError(code: "LOCATION2_SERVICE_PERMISSION_NOT_REQUESTED",
                                      message: "You should request locationPermissions beforehand, or set askForPermissionOption for permission to automatic get permission",
                                      details: nil))
                return
                
            }
            return
        }
        locationManager.startUpdatingLocation()
        LocationMethods.locationManagerIsContinousListening(true)
        
    }
    
    public func requestLocationPermission(_ requestedStatus:CLAuthorizationStatus,completion: @escaping (CLAuthorizationStatus) -> Void) {
        getLocationPermissionCompletion =
         {  status  in
             debugPrint(status)
             completion(status)
         }
        if (requestedStatus == .authorizedWhenInUse){
            locationManager.requestWhenInUseAuthorization()
        }
        else{
            locationManager.requestAlwaysAuthorization()
        }
    }
}
