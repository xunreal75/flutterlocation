//
//  LocationApi.swift
//  location2_ios
//
//  Created by Lars Huth on 15.07.23.
//
import Flutter
import UIKit
import CoreLocation
import Foundation
import SwiftLocation

class LocationMethods: NSObject, LocationHostApi,CLLocationManagerDelegate {
    private let locationManager: CLLocationManager = CLLocationManager()
    var authorizationStatus:CLAuthorizationStatus = .notDetermined
    var getLocationCompletion:  ((PigeonLocationData?, FlutterError?)->Void)?
    var getLocationPermissionCompletion:((CLAuthorizationStatus)->Void)?
    private static var locationManagerIsListening = false
    static var globalPigeonLocationSettings: PigeonLocationSettings?
    private static var lastKnownGPSLocation:CLLocation?

    let TAG = "LocationHostApi"
    
    override init() {
        super.init()
        self.locationManager.delegate = self
    }
    
    public static func locationManagerIsContinousListening( _ isListen:Bool){
        LocationMethods.locationManagerIsListening = isListen
    }
    
    
    public static func setLastKnownPoint( _ lastKnownPoint:CLLocation){
        LocationMethods.lastKnownGPSLocation = lastKnownPoint
    }
    
    
    public func openLocationSettings(completion: @escaping (NSNumber?, FlutterError?) -> Void) {
        if let url = URL(string: UIApplication.openSettingsURLString) {
            UIApplication.shared.open(url)
            completion(NSNumber(true),nil)
        }
        completion(NSNumber(true),nil)
    }
    
    public func openAppSettings(completion: @escaping (NSNumber?, FlutterError?) -> Void) {
        if let url = URL(string: UIApplication.openSettingsURLString) {
            UIApplication.shared.open(url)
            completion(NSNumber(true),nil)
        }
        completion(NSNumber(false),nil)
    }
    
    
    
     public func openLocationSettingsWithError(_ error: AutoreleasingUnsafeMutablePointer<FlutterError?>) -> NSNumber? {
        if let url = URL(string: UIApplication.openSettingsURLString) {
            UIApplication.shared.open(url)
            return NSNumber(true)
        }
        return NSNumber(false)
    }
    
    public func openAppSettingsWithError(_ error: AutoreleasingUnsafeMutablePointer<FlutterError?>) -> NSNumber? {
        if let url = URL(string: UIApplication.openSettingsURLString) {
            UIApplication.shared.open(url)
            return NSNumber(true)
        }
        return NSNumber(false)
    }
    
    /// get location with settings --> func name passed by pigeon
    public func getLocationSettings(_ settings: PigeonLocationSettings?, completion: @escaping (PigeonLocationData?, FlutterError?) -> Void) {
        let currentSettings = LocationMethods.locationSettingsToGPSLocationOptions(settings ?? LocationMethods.globalPigeonLocationSettings)
       
        var ignoreLastKnownPosition = false
        if (currentSettings != nil){
            ignoreLastKnownPosition = ((LocationMethods.globalPigeonLocationSettings?.ignoreLastKnownPosition) != nil)
        }
        else{
            locationManager.distanceFilter = .leastNormalMagnitude
           // locationManager.accuracyAuthorization = .fullAccuracy
        }
        
        if ((LocationMethods.locationManagerIsListening == true || ignoreLastKnownPosition == false) &&
            LocationMethods.lastKnownGPSLocation != nil) {
            print("Get Location - is listening and has lastKnownPoint")
                completion(LocationMethods.locationToData(LocationMethods.lastKnownGPSLocation!), nil)
                return;
        }
        
        getLocationCompletion = completion
        let stat = self.authorizationStatus
        let req = currentSettings?.avoidRequestAuthorization
        
        if (stat == .notDetermined && req == false){
        
               getLocationPermissionCompletion =
                {  status  in
                    debugPrint(status)
                    if (status != .denied && status != .notDetermined){
                        self.locationManager.startUpdatingLocation()}
                    else{
                        completion(nil, FlutterError(code: "LOCATION2_SERVICE_PERMISSION",
                                              message: "Location permisssion not ok. Result is: \(status)",
                                              details: nil))
                    }
                }
                locationManager.requestWhenInUseAuthorization()
            return;
               
            }
        else if (stat == .denied){
                completion(nil, FlutterError(code: "LOCATION2_SERVICE_DENIED",
                                      message: "Location service denied",
                                      details: nil))
                return
                
        }
        //workaround for long waiting on first request
        locationManager.startUpdatingLocation()
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
    
    static public func locationToDataObject(_ location: CLLocation)->[NSObject]{
        let newLoc = LocationMethods.locationToData(location)
        let newLocList =
        [
            newLoc.latitude ?? NSNull(),
            newLoc.longitude ?? NSNull(),
            newLoc.accuracy ?? NSNull(),
            newLoc.altitude ?? NSNull(),
            newLoc.bearing ?? NSNull(),
            newLoc.bearingAccuracyDegrees ?? NSNull(),
            newLoc.elapsedRealTimeNanos ?? NSNull(),
            newLoc.elapsedRealTimeUncertaintyNanos ?? NSNull(),
            newLoc.satellites ?? NSNull(),
            newLoc.speed ?? NSNull(),
            newLoc.speedAccuracy ?? NSNull(),
            newLoc.time ?? NSNull(),
            newLoc.verticalAccuracy ?? NSNull() ,
            newLoc.isMock ?? NSNull()
        ]
        return newLocList
    }
    
    static public func locationToData(_ location: CLLocation) -> PigeonLocationData {
        if #available(iOS 13.4, *) {
            return PigeonLocationData.make(
                withLatitude: NSNumber(value: location.coordinate.latitude),
                longitude: NSNumber(value: location.coordinate.longitude),
                accuracy: NSNumber(value: location.horizontalAccuracy),
                altitude: NSNumber(value: location.altitude),
                bearing: NSNumber(value: location.course),
                bearingAccuracyDegrees: NSNumber(value: location.courseAccuracy),
                elapsedRealTimeNanos: nil,
                elapsedRealTimeUncertaintyNanos: nil,
                satellites: nil,
                speed: NSNumber(value: location.speed),
                speedAccuracy: NSNumber(value: location.speedAccuracy),
                time: NSNumber(value: Int64(location.timestamp.timeIntervalSince1970*1000)),
                verticalAccuracy: NSNumber(value: location.verticalAccuracy),
                isMock: false)
        }
        return PigeonLocationData.make(
            withLatitude: NSNumber(value: location.coordinate.latitude),
            longitude: NSNumber(value: location.coordinate.longitude),
            accuracy: NSNumber(value: location.horizontalAccuracy),
            altitude: NSNumber(value: location.altitude),
            bearing: NSNumber(value: location.course),
            bearingAccuracyDegrees: nil,
            elapsedRealTimeNanos: nil,
            elapsedRealTimeUncertaintyNanos: nil,
            satellites: nil,
            speed: NSNumber(value: location.speed),
            speedAccuracy: NSNumber(value: location.speedAccuracy),
            time: NSNumber(value: Int64(location.timestamp.timeIntervalSince1970*1000)), //to ms
            verticalAccuracy: NSNumber(value: location.verticalAccuracy),
            isMock: false)
    }
    
    
    public func getDefaultGPSLocationOptions() -> GPSLocationOptions {
        let defaultOption = GPSLocationOptions()
        defaultOption.minTimeInterval = 2
        defaultOption.subscription = .single
        
        return defaultOption
    }
    
    static private func mapAccuracy (_ accuracy: PigeonLocationAccuracy) -> GPSLocationOptions.Accuracy {
        switch (accuracy) {
            
        case .powerSave:
            return GPSLocationOptions.Accuracy.city
        case .low:
            return GPSLocationOptions.Accuracy.block
        case .balanced:
            return GPSLocationOptions.Accuracy.house
        case .high:
            return GPSLocationOptions.Accuracy.room
        case .navigation:
            return GPSLocationOptions.Accuracy.room
        @unknown default:
            return GPSLocationOptions.Accuracy.room
        }
    }
    
    
   
    static public func locationSettingsToGPSLocationOptions(_ settings: PigeonLocationSettings?) -> GPSLocationOptions? {
        if (settings == nil) {
            return nil
        }
        let options = GPSLocationOptions()
        
        let minTimeInterval = settings?.interval
        let accuracy = settings?.accuracy
        let askForPermission = settings?.askForPermission
        let minDistance = settings?.smallestDisplacement
        
        
        options.activityType = .automotiveNavigation
        options.subscription = .single
        
        if (minTimeInterval != nil) {
            options.minTimeInterval = Double(Int(truncating: minTimeInterval!) / 1000)
        }
        
        
        if (accuracy != nil) {
            options.accuracy = mapAccuracy(accuracy!)
        }
        
        if (askForPermission == false) {
            options.avoidRequestAuthorization = true
        }
        if (minDistance != nil) {
            options.minDistance = Double(truncating: minDistance!)
        }
        
        
        return options
    }
    
    
    
    public func setLocationSettingsSettings(_ settings: PigeonLocationSettings, error: AutoreleasingUnsafeMutablePointer<FlutterError?>) -> NSNumber? {
        LocationMethods.globalPigeonLocationSettings = settings;
        
        return NSNumber(true)
    }
    
    static public func getPermissionNumber() -> NSNumber {
        let currentStatus = SwiftLocation.authorizationStatus
        
        switch currentStatus {
        case .notDetermined:
            return 0
        case .restricted:
            return 1
        case .denied:
            return 2
        case .authorizedAlways:
            return 3
        case .authorizedWhenInUse:
            return 4
        case .authorized:
            return 3
        @unknown default:
            return 5
        }
    }
    
    static public func getCLLocationAccuracy(pAccuracy:PigeonLocationAccuracy ) -> CLLocationAccuracy {
        switch pAccuracy {
        case .navigation:
            return kCLLocationAccuracyBestForNavigation
        case .powerSave:
            return kCLLocationAccuracyHundredMeters
        case .low:
            return kCLLocationAccuracyHundredMeters
        case .balanced:
           return kCLLocationAccuracyBest
        case .high:
            return kCLLocationAccuracyBest
        @unknown default:
            return kCLLocationAccuracyBestForNavigation
        }
    }
    
    public func getPermissionStatusWithError(_ error: AutoreleasingUnsafeMutablePointer<FlutterError?>) -> NSNumber? {
        return LocationMethods.getPermissionNumber()
    }
    
    public func requestPermission(completion: @escaping (NSNumber?, FlutterError?) -> Void) {
        SwiftLocation.requestAuthorization(.onlyInUse) { newStatus in
            switch newStatus {
            case .notDetermined:
                completion(0, nil)
            case .restricted:
                completion(1, nil)
            case .denied:
                completion(2, nil)
            case .authorizedAlways:
                completion(3, nil)
            case .authorizedWhenInUse:
                completion(4, nil)
            case .authorized:
                completion(3, nil)
            @unknown default:
                completion(5, nil)
            }
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
    
    
    public func isGPSEnabledWithError(_ error: AutoreleasingUnsafeMutablePointer<FlutterError?>) -> NSNumber? {
        if CLLocationManager.locationServicesEnabled() {
            return NSNumber(true)
        }
        return NSNumber(false)
    }
    
    public func isNetworkEnabledWithError(_ error: AutoreleasingUnsafeMutablePointer<FlutterError?>) -> NSNumber? {
        if CLLocationManager.locationServicesEnabled() {
            return NSNumber(true)
        }
        return NSNumber(false)
    }
    
    // Not applicable to iOS
    public func changeNotificationSettingsSettings(_ settings: PigeonNotificationSettings, error: AutoreleasingUnsafeMutablePointer<FlutterError?>) -> NSNumber? {
        return NSNumber(true)
    }
    
    public func setBackgroundActivatedActivated(_ activated: NSNumber, error: AutoreleasingUnsafeMutablePointer<FlutterError?>) -> NSNumber? {
        SwiftLocation.allowsBackgroundLocationUpdates = activated.boolValue
        return NSNumber(true)
    }
    
    func locationManager(_: CLLocationManager, didFailWithError error : Error){
        let err = CLError.Code(rawValue: (error as NSError).code)!
        if (getLocationCompletion != nil){
            getLocationCompletion!(nil, FlutterError(code: "LOCATION2_LOCATION_ERROR",
                                      message: "locationManager did fail with error: \(err)",
                                      details: nil))
            
        }
        getLocationCompletion = nil
    }
    
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        if (getLocationCompletion != nil){
            if let location = locations.last {
                self.locationManager.stopUpdatingLocation()
                LocationMethods.lastKnownGPSLocation = location
                getLocationCompletion!(LocationMethods.locationToData(location),nil)
                
            }
            getLocationCompletion = nil
        }
    }
}
