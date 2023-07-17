//
//  PermissionStreamHandler.swift
//  location2_ios
//
//  Created by Lars Huth on 14.07.23.
//


import Foundation
import SwiftLocation
import CoreLocation

class PermissionStreamHandler: NSObject, FlutterStreamHandler,CLLocationManagerDelegate {
    var locationRequest: GPSLocationRequest?
    var locationSettings: PigeonLocationSettings?
    var events: FlutterEventSink?
    var isListening:Bool
    private let locationManager = CLLocationManager()
    private var authorizationStatus:CLAuthorizationStatus = .notDetermined

    
    override init() {
        isListening = false
        super.init()
        self.locationManager.delegate = self
    }
    
    @available(iOS, introduced: 4.2, deprecated: 14.0)
    public func locationManager(_ manager: CLLocationManager, didChangeAuthorization status: CLAuthorizationStatus) {
        checkLocationServicesStatusChange()
    }
    
    @available(iOS 14.0, *)
    public func locationManagerDidChangeAuthorization(_ manager: CLLocationManager) {
        checkLocationServicesStatusChange()
    }
    
    func locationManager(_: CLLocationManager, didFailWithError error : Error){
        if self.events != nil{
            let err = CLError.Code(rawValue: (error as NSError).code)!
            self.events!(FlutterError(code: "LOCATION2_PERMISSION_ERROR",
                                      message: "locationManager did fail with error: \(err)",
                                      details: nil))
        }
         
    }
    
    
    func checkLocationServicesStatusChange() {
        self.authorizationStatus = SwiftLocation.authorizationStatus
        if ( self.events != nil && isListening == true){
            
        }
    }
    
    public func onListen(withArguments arguments: Any?, eventSink events: @escaping FlutterEventSink) -> FlutterError? {
        
        self.events = events
        startListening()
        
        return nil
    }
    
    
    
    public func onCancel(withArguments arguments: Any?) -> FlutterError? {
        locationRequest?.cancelRequest()
        events = nil
        isListening = false
        return nil
    }
    
    private func startListening() {
        if (events == nil) {
            return
        }
        
        isListening = true;
        
      
        if self.events != nil{
            let newLocPermission = LocationPermissionMethods.locationPermissionToData(authorizationStatus)
            //Workaround for missing to List Method implementation from Pigeon
            let newLocList  =
            [
                newLocPermission.pigeonLocationPermission,
               
            ]
            self.events!(newLocList)
        }
        else{
            //onCancel sets event to nil - Called if new loc in min interval
            //avoid exception and app crash on nil events (FlutterSink?)
        }
        
    }
}



