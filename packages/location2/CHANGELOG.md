## 6.0.1

 - Update a dependency to the latest release.

## 6.0.0

 - forked from v5 from lyokone

 - **REFACTOR**: extract background notification logic to separate class.
 - **REFACTOR**: remove Android strings.
 - **FIX**(android): improve changeSettings to be applied immediatly.
 - **FIX**(android): fix the depreciation warning on android #550.
 - **FIX**(tests): fix location package test.
 - **FIX**(android): fix android crash on Android API 27, #579.
 - **FIX**(android): add compatibility version to target Java 8.
 - **FIX**(ios): wait 2 location updates to make sure that the last knwown position isn't returned instantly #549.
 - **FEAT**(web): activate web.
 - **FEAT**(ios): background mode for ios.
 - **FEAT**: return notification and channel id when changing options.
 - **FEAT**(macos): ready version macos.
 - **FEAT**: update readme.
 - **FEAT**: allow for customizing Android notification text, subtext and color.
 - **FEAT**: add option to reopen app from notification.
 - **FEAT**(android): add fallback for LocationAccuracy.reduced on Android.
 - **FEAT**(android): add isMock information on LocationData.
 - **FEAT**(android): separate result variables to prevent result override.
 - **FEAT**: unify into one pigeon file.
 - **FEAT**: remove useless tests thanks to Pigeon.
 - **FEAT**: update to run on Flutter 3.7.
 - **FEAT**(ios): settings mapping.
 - **FEAT**(ios): ready with CLocationManager.
 - **FEAT**(ios): initialize the ios package.
 - **FEAT**(android): readying docs.
 - **FEAT**(android): dialog with AppCompat.
 - **FEAT**(android): support for coarse location.
 - **FEAT**(android): get permission status.
 - **FEAT**(android): change settings.
 - **FEAT**(android): implement setLocationSettings.
 - **FEAT**(android): implement onLocationChanged.
 - **FEAT**: first version 5 commit.
 - **FEAT**: ready for flutter 3.
 - **FEAT**(android): support android 12 in example app.
 - **FEAT**(tests): improve coverage.
 - **FEAT**: add the ability to use a mock instance.
 - **FEAT**: handle notification changes in Android MethodCallHandler.
 - **FEAT**(demo): add web demo.
 - **FEAT**: allow for customizing Android background notification from dart.
 - **FEAT**: update example app to showcase Android notification options.
 - **FEAT**: Update to null safety.
 - **FEAT**(ios): add several information to resolve #552.
 - **FEAT**(android): add several information to resolve #552.
 - **FEAT**: improve PermissionStatus.
 - **FEAT**(example): better listen example to prevent infinite location request.
 - **FEAT**(android): fix typos.
 - **FEAT**(ios): add ios requirements.
 - **FEAT**: improve example app.
 - **FEAT**(android): background mode for android.
 - **DOCS**: update documentation.
 - **DOCS**: writing docs for version 5.
 - **DOCS**: writing docs for version 5.
 - **DOCS**: writing docs for version 5.
 - **DOCS**: update readme.
 - **DOCS**: update readme.
 - **DOCS**: update readme.
 - **DOCS**: update docs.
 - **DOCS**: add next previous.
 - **DOCS**: add links.
 - **DOCS**: update readme web.
 - **DOCS**: add documentation to code.

## 5.0.0-dev.9

 - **FEAT**: update to run on Flutter 3.7.

## 5.0.0-dev.8

 - **FEAT**: improve PermissionStatus.

## 5.0.0-dev.7

 - **DOCS**: add links.
 - **DOCS**: add next previous.

## 5.0.0-dev.6

 - **FEAT**: add the ability to use a mock instance.

## 6.0.0

 - **DOCS**: update docs.

## 6.0.0

 - **DOCS**: update readme.
 - **DOCS**: update readme.

## 5.0.0-dev.3

 - **FEAT**: background mode for ios.
 - **FEAT**: background mode for android.
 - **DOCS**: update readme.

## 5.0.0-dev.2

 - **FEAT**: ready version macos.
 - **FEAT**: update readme.

## 5.0.0-dev.1

 - **REFACTOR**: remove Android strings.
 - **REFACTOR**: extract background notification logic to separate class.
 - **FIX**: fix android crash on Android API 27, #579.
 - **FIX**: fix location package test.
 - **FIX**: add compatibility version to target Java 8.
 - **FIX**: wait 2 location updates to make sure that the last knwown position isn't returned instantly #549.
 - **FIX**: fix the depreciation warning on android #550.
 - **FIX**: improve changeSettings to be applied immediatly.
 - **FEAT**: change settings.
 - **FEAT**: ready with CLocationManager.
 - **FEAT**: initialize the ios package.
 - **FEAT**: readying docs.
 - **FEAT**: dialog with AppCompat.
 - **FEAT**: support for coarse location.
 - **FEAT**: get permission status.
 - **FEAT**: update example app to showcase Android notification options.
 - **FEAT**: implement setLocationSettings.
 - **FEAT**: implement onLocationChanged.
 - **FEAT**: first version 5 commit.
 - **FEAT**: ready for flutter 3.
 - **FEAT**: support android 12 in example app.
 - **FEAT**: improve coverage.
 - **FEAT**: handle notification changes in Android MethodCallHandler.
 - **FEAT**: settings mapping.
 - **FEAT**: add web demo.
 - **FEAT**: allow for customizing Android background notification from dart.
 - **FEAT**: unify into one pigeon file.
 - **FEAT**: remove useless tests thanks to Pigeon.
 - **FEAT**: add several information to resolve #552.
 - **FEAT**: add several information to resolve #552.
 - **FEAT**: activate web.
 - **FEAT**: Update to null safety.
 - **FEAT**: fix typos.
 - **FEAT**: add ios requirements.
 - **FEAT**: improve example app.
 - **FEAT**: separate result variables to prevent result override.
 - **FEAT**: add isMock information on LocationData.
 - **FEAT**: add fallback for LocationAccuracy.reduced on Android.
 - **FEAT**: add option to reopen app from notification.
 - **FEAT**: allow for customizing Android notification text, subtext and color.
 - **FEAT**: return notification and channel id when changing options.
 - **FEAT**: better listen example to prevent infinite location request.
 - **DOCS**: add documentation to code.
 - **DOCS**: update documentation.
 - **DOCS**: writing docs for version 5.
 - **DOCS**: writing docs for version 5.
 - **DOCS**: update readme web.
 - **DOCS**: writing docs for version 5.

## 5.0.0-dev.0

- Update all the location packages to version 5
- Separation of the Android and iOS implementation
- Using pigeon for the interface with native
- Support for Google Play Services
- Support for multiples simultaneous requests
- Upgrade underlying implementation

## 4.4.0

- **FEAT**: support android 12 in example app.
- **FIX**: Fix for Flutter 3

## 4.3.0

- **FIX**: fix location package test.
- **FEAT**: improve coverage.

## 4.2.3

- **FIX**: fix macos build, fixing #544.

## 4.2.2

- **FIX**: fix android crash on Android API 27, #579.

## 4.2.1

- **FIX**: add compatibility version to target Java 8.

## 4.2.0

- **REFACTOR**: remove Android strings.
- **REFACTOR**: extract background notification logic to separate class.
- **FIX**: wait 2 location updates to make sure that the last knwown position isn't returned instantly #549.
- **FIX**: fix the depreciation warning on android #550.
- **FIX**: improve changeSettings to be applied immediatly.
- **FEAT**: add several information to resolve #552.
- **FEAT**: add several information to resolve #552.
- **FEAT**: better listen example to prevent infinite location request.
- **FEAT**: fix typos.
- **FEAT**: add ios requirements.
- **FEAT**: improve example app.
- **FEAT**: separate result variables to prevent result override.
- **FEAT**: add isMock information on LocationData.
- **FEAT**: add fallback for LocationAccuracy.reduced on Android.
- **FEAT**: add option to reopen app from notification.
- **FEAT**: allow for customizing Android notification text, subtext and color.
- **FEAT**: update example app to showcase Android notification options.
- **FEAT**: allow for customizing Android background notification from dart.
- **FEAT**: handle notification changes in Android MethodCallHandler.
- **FEAT**: return notification and channel id when changing options.
- **DOCS**: update readme web.
- **CHORE**: publish packages.
- **CHORE**: publish packages.
- **CHORE**: publish packages.
- **CHORE**: publish packages.
- **CHORE**: publish packages.

## 4.1.1

- **FIX**: fix crash on build.

## 4.1.0

- **REFACTOR**: remove Android strings.
- **REFACTOR**: extract background notification logic to separate class.
- **FEAT**: add option to reopen app from notification.
- **FEAT**: allow for customizing Android notification text, subtext and color.
- **FEAT**: update example app to showcase Android notification options.
- **FEAT**: allow for customizing Android background notification from dart.
- **FEAT**: handle notification changes in Android MethodCallHandler.
- **FEAT**: return notification and channel id when changing options.
- **DOCS**: update readme web.
- **CHORE**: publish packages.
- **CHORE**: publish packages.
- **CHORE**: publish packages.
- **CHORE**: publish packages.

## 4.0.2

- Bump "location" to `4.0.2`.

## 4.0.1

- **DOCS**: update readme web.
- **CHORE**: publish packages.

## 4.0.0

- **FEAT**: Update to null safety.
- Update to null safety and Melos

## [3.2.4] 19th January 2021

- Fix crash on Android

## [3.2.3] 19th January 2021

- Fix crash during close of the app
- Remove mandatory Android permission if not using the background location

## [3.2.1] 23rd December 2020

- Fix crash during build

## [3.2.0] 23rd December 2020

- Add the ability to launch location notifications when application is in background
  - on iOS implemented with native background location support by adding required permissions and permission checks
  - on Android by providing a custom service that wraps existing native location API calls in a foreground service
- Update Android SDK to Android 10/Q (API level 29)
- Updated sample application to include the background mode
- Various bug fixing

## [3.1.0] 09 October 2020

- Do not throw errors from methods that do not need an activity.
- [BREAKING] The error thrown is now ActivityNotFoundException which changes the error code
  returned when activity is not found. It used to be NO_ACTIVITY, now it is just error. We
  anticipate this error to be rarely experienced in the wild.

## [3.0.3] 25th August 2020

- Add capability to return reduced accuracy permission on iOS 14.0

## [3.0.2] 23rd April 2020

- Fix crashes on v1 apps.

## [3.0.1] 27th March 2020

- Fix a crash happening during iOS build

## [3.0.0] 26th March 2020

- Add Web and macOS as new supported platforms (huge thanks to long1eu)
- [BREAKING] Enums are now following Dart guidelines.
- [BREAKING] _onLocationChanged_ is now a getter to follow Dart guidelines.

## [2.5.4] 11st March 2020

- Update documentation
- Fix: Airplane mode was preventing location from being requested
- Fix: Not crashing when activity is not set on Android

## [2.5.3] 26th February 2020

- Improve code coverage
- Update documentation

## [2.5.2] 25th February 2020

- Fix crash on pre-1.12 projects
- Align PermissionStatus on iOS with Android

## [2.5.1] 23rd February 2020

- Fix SDK version

## [2.5.0] 23rd February 2020

- [BREAKING] The `requestPermission` and `hasPermission` are now returning PermissionStatus enum.
- Upgrade to Android Embedding V2 (follow https://github.com/flutter/flutter/wiki/Upgrading-pre-1.12-Android-projects if the plugin isn't working after upgrade)
- Resolve getLocation when service is disabled thanks to nicowernli
- Update example app
- Fix bugs leading to non returning code
- `getLocation` now throws properly
- `pub.dev` now states that the plugin is not compatible with Flutter Web (yet)

## [2.4.0] 14th February 2020

- Align timestamp in Android and iOS, previously the iOS timestamp was in seconds instead of milliseconds. Thanks to 781flyingdutchman.

## [2.3.7] 08th January 2020

- Fix bug where requestPermission is called after the user has already denied the system location dialog, then this method call would never return.

## [2.3.6] 07th January 2020

- Fix ClassCastException errors on some Android phones when requesting Location status.

## [2.3.5] 10th April 2019

- Fix incompatibily with headless plugins thanks to ehhc
- Fix error with iOS when permission already given
- Add Google maps example

## [2.3.4] 8th April 2019

- Fix error on Android 21 API thanks to noordawod
- Update Google API version

## [2.3.3] 31th March 2019

- Align altitude on Sea Level when available on Android (matching iOS altitude).

## [2.3.2] 27th March 2019

- Remove GPS limitation on Android

## [2.3.1] 25th March 2019

- Fixes README
- Fixes requestPermission not responding the correct result on iOS

## [2.3.0] 22nd March 2019

- Update example App with proper cancel
- Add possibility to set accuracy, interval and minimum notification ditance of the requests.
- Add LocationAccuracy object

## [2.2.0] 19th March 2019

- Actually updating locatino when using getLocation (not only relying on LastLocation)
- Add timestamp to LocationData
- Add serviceEnabled method to check whether Location Service is enabled.
- Add requestService method to ask the user to activate the location service.
- Fix continuous callback heading

## [2.1.0] 16th Match 2019

- iOS permission should be closer to Android permission behaviour thanks to PerrchicK
- Adding requestPermission(), to manually request permission
- Several feature fixed for less crash when using the plugin
- Code Cleanup
- Update Readme and add a warning for the location bug in iOS simulator

## [2.0.0] 25th January 2019

- Code cleanup
- BREAKING CHANGE: Change Dart API to return structured data rather than a map.

## [1.4.0] 21st August 2018

- Add lazy permission request thanks to yathit
- Add hasPermission() thanks to vagrantrobbie
- Bug correction thanks to jalpedersen
- Add more examples

## [1.3.4] 4th June 2018

- Fix crash for Android API pre 27 thanks to matthewtsmith.

## [1.3.3] 30th May 2018

- Correct implementation of iOS plugin to match Android behaviour. No need to call getLocation
  to get permissions for location callbacks.

## [1.3.2] 30th May 2018

- Change implementation to api in build.gradle in order to solve incompatibilities between
  GMS versions thanks to luccascorrea

## [1.3.1] 29th May 2018

- Added speed and speed_accuracy (only Android truly discover speed accuracy, so its always 0 for now on iOS)
- Solved a crash

## [1.3.0] 27th May 2018

- Make it compatible with Firebase thanks to quangIO
- Resolve runtime error exception thanks to jharrison902
- Update gitignore thanks to bcko

## [1.2.0] 5th April 2018

- Permissions denied on Android handled thanks to g123k
- Dart 2 update thanks to efortuna

## [1.1.6] - 19th Octobre 2017.

- iOS code from Swift to Objective-C thanks to fluff

## [1.1.1] - 20th July 2017.

- Fixes for iOS result's format.

## [1.1.0] - 17th July 2017.

- Added permission check for Android 6+ (thanks netdur). Still no callback when permissions granted
  so aiming SDK 21 is safer.

## [1.0.0] - 7th July 2017.

- Initial Release.
