//
//  StreamHandler.swift
//  location2_ios
//
//  Created by Guillaume Bernos on 10/06/2022.
//

import Foundation
import SwiftLocation
import CoreLocation

class StreamHandler: NSObject, FlutterStreamHandler {
    var locationRequest: GPSLocationRequest?
    var locationSettings: PigeonLocationSettings?
    var events: FlutterEventSink?
    
    public func onListen(withArguments arguments: Any?, eventSink events: @escaping FlutterEventSink) -> FlutterError? {
        if !CLLocationManager.locationServicesEnabled() {
            UIApplication.shared.open(URL(string:UIApplication.openSettingsURLString)!)
            return FlutterError(code: "LOCATION2_SERVICE_DISABLED",
                                message: "The user have deactivated the location service, the settings page has been opened",
                                details: nil)
        }
        
        let activated = arguments as! Bool? ?? false
        SwiftLocation.allowsBackgroundLocationUpdates = activated

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

        let options = SwiftLocationPlugin.locationSettingsToGPSLocationOptions(locationSettings)
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
                print("New location: \(newData)")
                let newLoc = SwiftLocationPlugin.locationToData(newData)
                //Workaround for missing to List Method implementation from Pigeon
                let newLocList  =
                [
                    newLoc.latitude ?? NSNull(),
                    newLoc.longitude ?? NSNull(),
                    newLoc.accuracy ?? NSNull(),
                    newLoc.altitude ?? NSNull(),
                    newLoc.bearing ?? NSNull(),
                    newLoc.bearingAccuracyDegrees ?? NSNull(),
                    newLoc.elaspedRealTimeNanos ?? NSNull(),
                    newLoc.elaspedRealTimeUncertaintyNanos ?? NSNull(),
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
