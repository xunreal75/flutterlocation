import Flutter
import UIKit
import SwiftLocation
import CoreLocation
import Foundation

@UIApplicationMain
public class SwiftLocationPlugin: NSObject, FlutterPlugin, UIApplicationDelegate {
    var globalPigeonLocationSettings: PigeonLocationSettings?
    var streamHandler: StreamHandler?
    var permissionStreamHandler: PermissionStreamHandler?
    
    static let locationApi: LocationMethods = LocationMethods()
    static let locatinPermApi: LocationPermissionMethods = LocationPermissionMethods()
    
    init(_ messenger: FlutterBinaryMessenger, _ registrar: FlutterPluginRegistrar) {
        super.init()
        
        let locationPermissionEventChannel = FlutterEventChannel(name: "xunreal75/location2_permission_stream", binaryMessenger: messenger)
       
        self.permissionStreamHandler = PermissionStreamHandler()
        locationPermissionEventChannel.setStreamHandler(self.permissionStreamHandler)
            
        let eventChannel = FlutterEventChannel(name: "xunreal75/location2_stream", binaryMessenger: messenger)
        self.streamHandler = StreamHandler()
        eventChannel.setStreamHandler(self.streamHandler)
        
        registrar.addApplicationDelegate(self)
    }
    
    
    public static func register(with registrar: FlutterPluginRegistrar) {
    
        LocationHostApiSetup(registrar.messenger(), SwiftLocationPlugin.locationApi)
        LocationPermissionsHostApiSetup(registrar.messenger(), SwiftLocationPlugin.locatinPermApi)
        
        _ = SwiftLocationPlugin.init(registrar.messenger(), registrar)
              
        
    }
    
    
    @nonobjc public func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [AnyHashable : Any] = [:]) -> Bool {
        return true
    }
    
}
