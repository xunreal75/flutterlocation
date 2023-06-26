// Autogenerated from Pigeon (v3.1.5), do not edit directly.
// See also: https://pub.dev/packages/pigeon
#import "messages.g.h"
#import <FlutterMacOS/FlutterMacOS.h>

#if !__has_feature(objc_arc)
#error File requires ARC to be enabled.
#endif

static NSDictionary<NSString *, id> *wrapResult(id result, FlutterError *error) {
  NSDictionary *errorDict = (NSDictionary *)[NSNull null];
  if (error) {
    errorDict = @{
        @"code": (error.code ?: [NSNull null]),
        @"message": (error.message ?: [NSNull null]),
        @"details": (error.details ?: [NSNull null]),
        };
  }
  return @{
      @"result": (result ?: [NSNull null]),
      @"error": errorDict,
      };
}
static id GetNullableObject(NSDictionary* dict, id key) {
  id result = dict[key];
  return (result == [NSNull null]) ? nil : result;
}
static id GetNullableObjectAtIndex(NSArray* array, NSInteger key) {
  id result = array[key];
  return (result == [NSNull null]) ? nil : result;
}


@interface PigeonLocationData ()
+ (PigeonLocationData *)fromMap:(NSDictionary *)dict;
+ (nullable PigeonLocationData *)nullableFromMap:(NSDictionary *)dict;
- (NSDictionary *)toMap;
@end
@interface PigeonLocationSettings ()
+ (PigeonLocationSettings *)fromMap:(NSDictionary *)dict;
+ (nullable PigeonLocationSettings *)nullableFromMap:(NSDictionary *)dict;
- (NSDictionary *)toMap;
@end

@implementation PigeonLocationData
+ (instancetype)makeWithLatitude:(nullable NSNumber *)latitude
    longitude:(nullable NSNumber *)longitude
    accuracy:(nullable NSNumber *)accuracy
    altitude:(nullable NSNumber *)altitude
    bearing:(nullable NSNumber *)bearing
    bearingAccuracyDegrees:(nullable NSNumber *)bearingAccuracyDegrees
    elaspedRealTimeNanos:(nullable NSNumber *)elaspedRealTimeNanos
    elaspedRealTimeUncertaintyNanos:(nullable NSNumber *)elaspedRealTimeUncertaintyNanos
    satellites:(nullable NSNumber *)satellites
    speed:(nullable NSNumber *)speed
    speedAccuracy:(nullable NSNumber *)speedAccuracy
    time:(nullable NSNumber *)time
    verticalAccuracy:(nullable NSNumber *)verticalAccuracy
    isMock:(nullable NSNumber *)isMock {
  PigeonLocationData* pigeonResult = [[PigeonLocationData alloc] init];
  pigeonResult.latitude = latitude;
  pigeonResult.longitude = longitude;
  pigeonResult.accuracy = accuracy;
  pigeonResult.altitude = altitude;
  pigeonResult.bearing = bearing;
  pigeonResult.bearingAccuracyDegrees = bearingAccuracyDegrees;
  pigeonResult.elaspedRealTimeNanos = elaspedRealTimeNanos;
  pigeonResult.elaspedRealTimeUncertaintyNanos = elaspedRealTimeUncertaintyNanos;
  pigeonResult.satellites = satellites;
  pigeonResult.speed = speed;
  pigeonResult.speedAccuracy = speedAccuracy;
  pigeonResult.time = time;
  pigeonResult.verticalAccuracy = verticalAccuracy;
  pigeonResult.isMock = isMock;
  return pigeonResult;
}
+ (PigeonLocationData *)fromMap:(NSDictionary *)dict {
  PigeonLocationData *pigeonResult = [[PigeonLocationData alloc] init];
  pigeonResult.latitude = GetNullableObject(dict, @"latitude");
  pigeonResult.longitude = GetNullableObject(dict, @"longitude");
  pigeonResult.accuracy = GetNullableObject(dict, @"accuracy");
  pigeonResult.altitude = GetNullableObject(dict, @"altitude");
  pigeonResult.bearing = GetNullableObject(dict, @"bearing");
  pigeonResult.bearingAccuracyDegrees = GetNullableObject(dict, @"bearingAccuracyDegrees");
  pigeonResult.elaspedRealTimeNanos = GetNullableObject(dict, @"elaspedRealTimeNanos");
  pigeonResult.elaspedRealTimeUncertaintyNanos = GetNullableObject(dict, @"elaspedRealTimeUncertaintyNanos");
  pigeonResult.satellites = GetNullableObject(dict, @"satellites");
  pigeonResult.speed = GetNullableObject(dict, @"speed");
  pigeonResult.speedAccuracy = GetNullableObject(dict, @"speedAccuracy");
  pigeonResult.time = GetNullableObject(dict, @"time");
  pigeonResult.verticalAccuracy = GetNullableObject(dict, @"verticalAccuracy");
  pigeonResult.isMock = GetNullableObject(dict, @"isMock");
  return pigeonResult;
}
+ (nullable PigeonLocationData *)nullableFromMap:(NSDictionary *)dict { return (dict) ? [PigeonLocationData fromMap:dict] : nil; }
- (NSDictionary *)toMap {
  return @{
    @"latitude" : (self.latitude ?: [NSNull null]),
    @"longitude" : (self.longitude ?: [NSNull null]),
    @"accuracy" : (self.accuracy ?: [NSNull null]),
    @"altitude" : (self.altitude ?: [NSNull null]),
    @"bearing" : (self.bearing ?: [NSNull null]),
    @"bearingAccuracyDegrees" : (self.bearingAccuracyDegrees ?: [NSNull null]),
    @"elaspedRealTimeNanos" : (self.elaspedRealTimeNanos ?: [NSNull null]),
    @"elaspedRealTimeUncertaintyNanos" : (self.elaspedRealTimeUncertaintyNanos ?: [NSNull null]),
    @"satellites" : (self.satellites ?: [NSNull null]),
    @"speed" : (self.speed ?: [NSNull null]),
    @"speedAccuracy" : (self.speedAccuracy ?: [NSNull null]),
    @"time" : (self.time ?: [NSNull null]),
    @"verticalAccuracy" : (self.verticalAccuracy ?: [NSNull null]),
    @"isMock" : (self.isMock ?: [NSNull null]),
  };
}
@end

@implementation PigeonLocationSettings
+ (instancetype)makeWithAskForPermission:(NSNumber *)askForPermission
    rationaleMessageForPermissionRequest:(NSString *)rationaleMessageForPermissionRequest
    rationaleMessageForGPSRequest:(NSString *)rationaleMessageForGPSRequest
    useGooglePlayServices:(NSNumber *)useGooglePlayServices
    askForGooglePlayServices:(NSNumber *)askForGooglePlayServices
    askForGPS:(NSNumber *)askForGPS
    fallbackToGPS:(NSNumber *)fallbackToGPS
    ignoreLastKnownPosition:(NSNumber *)ignoreLastKnownPosition
    expirationDuration:(nullable NSNumber *)expirationDuration
    expirationTime:(nullable NSNumber *)expirationTime
    fastestInterval:(NSNumber *)fastestInterval
    interval:(NSNumber *)interval
    maxWaitTime:(nullable NSNumber *)maxWaitTime
    numUpdates:(nullable NSNumber *)numUpdates
    accuracy:(PigeonLocationAccuracy)accuracy
    smallestDisplacement:(NSNumber *)smallestDisplacement
    waitForAccurateLocation:(NSNumber *)waitForAccurateLocation
    acceptableAccuracy:(nullable NSNumber *)acceptableAccuracy {
  PigeonLocationSettings* pigeonResult = [[PigeonLocationSettings alloc] init];
  pigeonResult.askForPermission = askForPermission;
  pigeonResult.rationaleMessageForPermissionRequest = rationaleMessageForPermissionRequest;
  pigeonResult.rationaleMessageForGPSRequest = rationaleMessageForGPSRequest;
  pigeonResult.useGooglePlayServices = useGooglePlayServices;
  pigeonResult.askForGooglePlayServices = askForGooglePlayServices;
  pigeonResult.askForGPS = askForGPS;
  pigeonResult.fallbackToGPS = fallbackToGPS;
  pigeonResult.ignoreLastKnownPosition = ignoreLastKnownPosition;
  pigeonResult.expirationDuration = expirationDuration;
  pigeonResult.expirationTime = expirationTime;
  pigeonResult.fastestInterval = fastestInterval;
  pigeonResult.interval = interval;
  pigeonResult.maxWaitTime = maxWaitTime;
  pigeonResult.numUpdates = numUpdates;
  pigeonResult.accuracy = accuracy;
  pigeonResult.smallestDisplacement = smallestDisplacement;
  pigeonResult.waitForAccurateLocation = waitForAccurateLocation;
  pigeonResult.acceptableAccuracy = acceptableAccuracy;
  return pigeonResult;
}
+ (PigeonLocationSettings *)fromMap:(NSDictionary *)dict {
  PigeonLocationSettings *pigeonResult = [[PigeonLocationSettings alloc] init];
  pigeonResult.askForPermission = GetNullableObject(dict, @"askForPermission");
  NSAssert(pigeonResult.askForPermission != nil, @"");
  pigeonResult.rationaleMessageForPermissionRequest = GetNullableObject(dict, @"rationaleMessageForPermissionRequest");
  NSAssert(pigeonResult.rationaleMessageForPermissionRequest != nil, @"");
  pigeonResult.rationaleMessageForGPSRequest = GetNullableObject(dict, @"rationaleMessageForGPSRequest");
  NSAssert(pigeonResult.rationaleMessageForGPSRequest != nil, @"");
  pigeonResult.useGooglePlayServices = GetNullableObject(dict, @"useGooglePlayServices");
  NSAssert(pigeonResult.useGooglePlayServices != nil, @"");
  pigeonResult.askForGooglePlayServices = GetNullableObject(dict, @"askForGooglePlayServices");
  NSAssert(pigeonResult.askForGooglePlayServices != nil, @"");
  pigeonResult.askForGPS = GetNullableObject(dict, @"askForGPS");
  NSAssert(pigeonResult.askForGPS != nil, @"");
  pigeonResult.fallbackToGPS = GetNullableObject(dict, @"fallbackToGPS");
  NSAssert(pigeonResult.fallbackToGPS != nil, @"");
  pigeonResult.ignoreLastKnownPosition = GetNullableObject(dict, @"ignoreLastKnownPosition");
  NSAssert(pigeonResult.ignoreLastKnownPosition != nil, @"");
  pigeonResult.expirationDuration = GetNullableObject(dict, @"expirationDuration");
  pigeonResult.expirationTime = GetNullableObject(dict, @"expirationTime");
  pigeonResult.fastestInterval = GetNullableObject(dict, @"fastestInterval");
  NSAssert(pigeonResult.fastestInterval != nil, @"");
  pigeonResult.interval = GetNullableObject(dict, @"interval");
  NSAssert(pigeonResult.interval != nil, @"");
  pigeonResult.maxWaitTime = GetNullableObject(dict, @"maxWaitTime");
  pigeonResult.numUpdates = GetNullableObject(dict, @"numUpdates");
  pigeonResult.accuracy = [GetNullableObject(dict, @"accuracy") integerValue];
  pigeonResult.smallestDisplacement = GetNullableObject(dict, @"smallestDisplacement");
  NSAssert(pigeonResult.smallestDisplacement != nil, @"");
  pigeonResult.waitForAccurateLocation = GetNullableObject(dict, @"waitForAccurateLocation");
  NSAssert(pigeonResult.waitForAccurateLocation != nil, @"");
  pigeonResult.acceptableAccuracy = GetNullableObject(dict, @"acceptableAccuracy");
  return pigeonResult;
}
+ (nullable PigeonLocationSettings *)nullableFromMap:(NSDictionary *)dict { return (dict) ? [PigeonLocationSettings fromMap:dict] : nil; }
- (NSDictionary *)toMap {
  return @{
    @"askForPermission" : (self.askForPermission ?: [NSNull null]),
    @"rationaleMessageForPermissionRequest" : (self.rationaleMessageForPermissionRequest ?: [NSNull null]),
    @"rationaleMessageForGPSRequest" : (self.rationaleMessageForGPSRequest ?: [NSNull null]),
    @"useGooglePlayServices" : (self.useGooglePlayServices ?: [NSNull null]),
    @"askForGooglePlayServices" : (self.askForGooglePlayServices ?: [NSNull null]),
    @"askForGPS" : (self.askForGPS ?: [NSNull null]),
    @"fallbackToGPS" : (self.fallbackToGPS ?: [NSNull null]),
    @"ignoreLastKnownPosition" : (self.ignoreLastKnownPosition ?: [NSNull null]),
    @"expirationDuration" : (self.expirationDuration ?: [NSNull null]),
    @"expirationTime" : (self.expirationTime ?: [NSNull null]),
    @"fastestInterval" : (self.fastestInterval ?: [NSNull null]),
    @"interval" : (self.interval ?: [NSNull null]),
    @"maxWaitTime" : (self.maxWaitTime ?: [NSNull null]),
    @"numUpdates" : (self.numUpdates ?: [NSNull null]),
    @"accuracy" : @(self.accuracy),
    @"smallestDisplacement" : (self.smallestDisplacement ?: [NSNull null]),
    @"waitForAccurateLocation" : (self.waitForAccurateLocation ?: [NSNull null]),
    @"acceptableAccuracy" : (self.acceptableAccuracy ?: [NSNull null]),
  };
}
@end

@interface LocationHostApiCodecReader : FlutterStandardReader
@end
@implementation LocationHostApiCodecReader
- (nullable id)readValueOfType:(UInt8)type 
{
  switch (type) {
    case 128:     
      return [PigeonLocationData fromMap:[self readValue]];
    
    case 129:     
      return [PigeonLocationSettings fromMap:[self readValue]];
    
    case 130:     
      return [PigeonLocationSettings fromMap:[self readValue]];
    
    default:    
      return [super readValueOfType:type];
    
  }
}
@end

@interface LocationHostApiCodecWriter : FlutterStandardWriter
@end
@implementation LocationHostApiCodecWriter
- (void)writeValue:(id)value 
{
  if ([value isKindOfClass:[PigeonLocationData class]]) {
    [self writeByte:128];
    [self writeValue:[value toMap]];
  } else 
  if ([value isKindOfClass:[PigeonLocationSettings class]]) {
    [self writeByte:129];
    [self writeValue:[value toMap]];
  } else 
  if ([value isKindOfClass:[PigeonLocationSettings class]]) {
    [self writeByte:130];
    [self writeValue:[value toMap]];
  } else 
{
    [super writeValue:value];
  }
}
@end

@interface LocationHostApiCodecReaderWriter : FlutterStandardReaderWriter
@end
@implementation LocationHostApiCodecReaderWriter
- (FlutterStandardWriter *)writerWithData:(NSMutableData *)data {
  return [[LocationHostApiCodecWriter alloc] initWithData:data];
}
- (FlutterStandardReader *)readerWithData:(NSData *)data {
  return [[LocationHostApiCodecReader alloc] initWithData:data];
}
@end

NSObject<FlutterMessageCodec> *LocationHostApiGetCodec() {
  static dispatch_once_t sPred = 0;
  static FlutterStandardMessageCodec *sSharedObject = nil;
  dispatch_once(&sPred, ^{
    LocationHostApiCodecReaderWriter *readerWriter = [[LocationHostApiCodecReaderWriter alloc] init];
    sSharedObject = [FlutterStandardMessageCodec codecWithReaderWriter:readerWriter];
  });
  return sSharedObject;
}


void LocationHostApiSetup(id<FlutterBinaryMessenger> binaryMessenger, NSObject<LocationHostApi> *api) {
  {
    FlutterBasicMessageChannel *channel =
      [[FlutterBasicMessageChannel alloc]
        initWithName:@"dev.flutter.pigeon.LocationHostApi.getLocation"
        binaryMessenger:binaryMessenger
        codec:LocationHostApiGetCodec()        ];
    if (api) {
      NSCAssert([api respondsToSelector:@selector(getLocationSettings:completion:)], @"LocationHostApi api (%@) doesn't respond to @selector(getLocationSettings:completion:)", api);
      [channel setMessageHandler:^(id _Nullable message, FlutterReply callback) {
        NSArray *args = message;
        PigeonLocationSettings *arg_settings = GetNullableObjectAtIndex(args, 0);
        [api getLocationSettings:arg_settings completion:^(PigeonLocationData *_Nullable output, FlutterError *_Nullable error) {
          callback(wrapResult(output, error));
        }];
      }];
    }
    else {
      [channel setMessageHandler:nil];
    }
  }
  {
    FlutterBasicMessageChannel *channel =
      [[FlutterBasicMessageChannel alloc]
        initWithName:@"dev.flutter.pigeon.LocationHostApi.setLocationSettings"
        binaryMessenger:binaryMessenger
        codec:LocationHostApiGetCodec()        ];
    if (api) {
      NSCAssert([api respondsToSelector:@selector(setLocationSettingsSettings:error:)], @"LocationHostApi api (%@) doesn't respond to @selector(setLocationSettingsSettings:error:)", api);
      [channel setMessageHandler:^(id _Nullable message, FlutterReply callback) {
        NSArray *args = message;
        PigeonLocationSettings *arg_settings = GetNullableObjectAtIndex(args, 0);
        FlutterError *error;
        NSNumber *output = [api setLocationSettingsSettings:arg_settings error:&error];
        callback(wrapResult(output, error));
      }];
    }
    else {
      [channel setMessageHandler:nil];
    }
  }
  {
    FlutterBasicMessageChannel *channel =
      [[FlutterBasicMessageChannel alloc]
        initWithName:@"dev.flutter.pigeon.LocationHostApi.getPermissionStatus"
        binaryMessenger:binaryMessenger
        codec:LocationHostApiGetCodec()        ];
    if (api) {
      NSCAssert([api respondsToSelector:@selector(getPermissionStatusWithError:)], @"LocationHostApi api (%@) doesn't respond to @selector(getPermissionStatusWithError:)", api);
      [channel setMessageHandler:^(id _Nullable message, FlutterReply callback) {
        FlutterError *error;
        NSNumber *output = [api getPermissionStatusWithError:&error];
        callback(wrapResult(output, error));
      }];
    }
    else {
      [channel setMessageHandler:nil];
    }
  }
  {
    FlutterBasicMessageChannel *channel =
      [[FlutterBasicMessageChannel alloc]
        initWithName:@"dev.flutter.pigeon.LocationHostApi.requestPermission"
        binaryMessenger:binaryMessenger
        codec:LocationHostApiGetCodec()        ];
    if (api) {
      NSCAssert([api respondsToSelector:@selector(requestPermissionWithCompletion:)], @"LocationHostApi api (%@) doesn't respond to @selector(requestPermissionWithCompletion:)", api);
      [channel setMessageHandler:^(id _Nullable message, FlutterReply callback) {
        [api requestPermissionWithCompletion:^(NSNumber *_Nullable output, FlutterError *_Nullable error) {
          callback(wrapResult(output, error));
        }];
      }];
    }
    else {
      [channel setMessageHandler:nil];
    }
  }
  {
    FlutterBasicMessageChannel *channel =
      [[FlutterBasicMessageChannel alloc]
        initWithName:@"dev.flutter.pigeon.LocationHostApi.isGPSEnabled"
        binaryMessenger:binaryMessenger
        codec:LocationHostApiGetCodec()        ];
    if (api) {
      NSCAssert([api respondsToSelector:@selector(isGPSEnabledWithError:)], @"LocationHostApi api (%@) doesn't respond to @selector(isGPSEnabledWithError:)", api);
      [channel setMessageHandler:^(id _Nullable message, FlutterReply callback) {
        FlutterError *error;
        NSNumber *output = [api isGPSEnabledWithError:&error];
        callback(wrapResult(output, error));
      }];
    }
    else {
      [channel setMessageHandler:nil];
    }
  }
  {
    FlutterBasicMessageChannel *channel =
      [[FlutterBasicMessageChannel alloc]
        initWithName:@"dev.flutter.pigeon.LocationHostApi.isNetworkEnabled"
        binaryMessenger:binaryMessenger
        codec:LocationHostApiGetCodec()        ];
    if (api) {
      NSCAssert([api respondsToSelector:@selector(isNetworkEnabledWithError:)], @"LocationHostApi api (%@) doesn't respond to @selector(isNetworkEnabledWithError:)", api);
      [channel setMessageHandler:^(id _Nullable message, FlutterReply callback) {
        FlutterError *error;
        NSNumber *output = [api isNetworkEnabledWithError:&error];
        callback(wrapResult(output, error));
      }];
    }
    else {
      [channel setMessageHandler:nil];
    }
  }
}
