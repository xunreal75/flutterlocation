//
//  StreamHandler.swift
//  location2_ios
//
//  Created by Guillaume Bernos on 10/06/2022.
//

import Foundation
import SwiftLocation
import CoreLocation

class StreamHandler: NSObject, FlutterStreamHandler,CLLocationManagerDelegate {
    var locationRequest: GPSLocationRequest?
    var locationSettings: PigeonLocationSettings?
    var events: FlutterEventSink?
    private let locationManager: CLLocationManager = CLLocationManager()
    private var authorizationStatus:CLAuthorizationStatus
    
    override init() {
        self.authorizationStatus = .notDetermined
        super.init()
        self.locationManager.delegate = self
    }
    
    @available(iOS, introduced: 4.2, deprecated: 14.0)
    public func locationManager(_ manager: CLLocationManager, didChangeAuthorization status: CLAuthorizationStatus) {
        authorizationStatus = status
    }
    
    @available(iOS 14.0, *)
    public func locationManagerDidChangeAuthorization(_ manager: CLLocationManager) {
        authorizationStatus=manager.authorizationStatus
        print("StreamHandler Authorisation is set to: \(self.authorizationStatus.description)")
    }
    
    func locationManager(_: CLLocationManager, didFailWithError error : Error){
        if self.events != nil{
            let err = CLError.Code(rawValue: (error as NSError).code)!
            self.events!(FlutterError(code: "LOCATION2_LOCATION_ERROR",
                                      message: "locationManager did fail with error: \(err)",
                                      details: nil))
        }
         
    }
    
    public func onListen(withArguments arguments: Any?, eventSink events: @escaping FlutterEventSink) -> FlutterError? {
        if authorizationStatus == .denied {
            //UIApplication.shared.open(URL(string:UIApplication.openSettingsURLString)!)
            return FlutterError(code: "LOCATION2_SERVICE_DISABLED",
                                message: "The user have deactivated the location service, the settings page has been opened",
                                details: nil)
        }
        
         if authorizationStatus != .authorizedAlways ||
         authorizationStatus != .authorizedWhenInUse
         {
         //UIApplication.shared.open(URL(string:UIApplication.openSettingsURLString)!)
         return FlutterError(code: "LOCATION2_SERVICE",
         message: "The user have deactivated the location service, the settings page has been opened",
         details: nil)
         }
        let activated = arguments as! Bool? ?? false
        SwiftLocation.allowsBackgroundLocationUpdates = activated
        if #available(iOS 11.0, *) {
            // Running iOS 11 OR NEWER
            locationManager.showsBackgroundLocationIndicator = activated
        }
        self.events = events
        startListening()
        
        return nil
    }
    
    
    
    public func onCancel(withArguments arguments: Any?) -> FlutterError? {
        locationRequest?.cancelRequest()
        events = nil
        return nil
    }
    
    public func setPigeonLocationSettings(_ settings: PigeonLocationSettings) {
        self.locationSettings = settings
        locationRequest?.cancelRequest()
        startListening()
    }
    
    private func startListening() {
        if (events == nil) {
            return
        }
        
        let options = LocationMethods.locationSettingsToGPSLocationOptions(locationSettings)
        options?.subscription = .continous
        
        
        locationRequest = options != nil ? SwiftLocation.gpsLocationWith (options!) :  SwiftLocation.gpsLocationWith {
            $0.subscription = .continous
            $0.accuracy = .house
            $0.minTimeInterval = 2
            $0.activityType = .automotiveNavigation
        }
        
        
        locationRequest?.then { result in
            switch result {
            case .success(let newData):
                print("New location: \(newData) received")
                let newLoc = LocationMethods.locationToData(newData)
                //Workaround for missing to List Method implementation from Pigeon
                let newLocList  =
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
                if self.events != nil{
                    self.events!(newLocList)
                }
                else{
                    //onCancel sets event to nil - Called if new loc in min interval
                    //avoid exception and app crash on nil events (FlutterSink?)
                }
                
            case .failure(let error):
                print("An error has occurred: \(error.localizedDescription)")
                if self.events != nil{
                    self.events!(FlutterError(code: "LOCATION2_ERROR",
                                              message: error.localizedDescription,
                                              details: error.recoverySuggestion))
                }
                
            }
        }
        
    }
}
