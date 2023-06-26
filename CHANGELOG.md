# Change Log

All notable changes to this project will be documented in this file.
See [Conventional Commits](https://conventionalcommits.org) for commit guidelines.

## 2023-06-26

### Changes

---

Packages with breaking changes:

 - There are no breaking changes in this release.

Packages with other changes:

 - [`location2_android` - `v6.0.2`](#location2_android---v602)
 - [`location2` - `v6.0.2`](#location2---v602)

Packages with dependency updates only:

> Packages listed below depend on other packages in this workspace that have had changes. Their versions have been incremented to bump the minimum dependency versions of the packages they depend upon in this project.

 - `location2` - `v6.0.2`

---

#### `location2_android` - `v6.0.2`

 - added timestamp on Android


## 2023-06-18

### Changes

---

Packages with breaking changes:

 - There are no breaking changes in this release.

Packages with other changes:

 - [`location_web` - `v6.0.1`](#location_web---v601)
 - [`location` - `v6.0.2`](#location---v602)

Packages with dependency updates only:

> Packages listed below depend on other packages in this workspace that have had changes. Their versions have been incremented to bump the minimum dependency versions of the packages they depend upon in this project.

 - `location` - `v6.0.2`

---

#### `location_web` - `v6.0.1`

 - error on creation


## 2023-06-17

### Changes

---

Packages with breaking changes:

 - There are no breaking changes in this release.

Packages with other changes:

 - [`location_platform_interface` - `v6.0.1`](#location_platform_interface---v601)
 - [`location_macos` - `v6.0.1`](#location_macos---v601)
 - [`location_web` - `v6.0.1`](#location_web---v601)
 - [`location_ios` - `v6.0.1`](#location_ios---v601)
 - [`location_android` - `v6.0.1`](#location_android---v601)
 - [`location` - `v6.0.1`](#location---v601)

Packages with dependency updates only:

> Packages listed below depend on other packages in this workspace that have had changes. Their versions have been incremented to bump the minimum dependency versions of the packages they depend upon in this project.

 - `location_macos` - `v6.0.1`
 - `location_web` - `v6.0.1`
 - `location_ios` - `v6.0.1`
 - `location_android` - `v6.0.1`
 - `location` - `v6.0.1`

---

#### `location_platform_interface` - `v6.0.1`

 - reinit und upgrade


## 2023-06-17

### Changes

---

Packages with breaking changes:

 - [`location_web` - `v6.0.0`](#location_web---v500-dev4)

Packages with other changes:

 - [`location` - `v6.0.0`](#location---v600)
 - [`location_android` - `v6.0.0`](#location_android---v500-dev5)
 - [`location_ios` - `v6.0.0`](#location_ios---v500-dev5)
 - [`location_macos` - `v6.0.0`](#location_macos---v500-dev4)
 - [`location_platform_interface` - `v6.0.0`](#location_platform_interface---v500-dev4)

---

#### `location_web` - `v6.0.0`

 - **FIX**: remove unused android and ios project in location_web.
 - **FEAT**: improve PermissionStatus.
 - **FEAT**(android): background mode for android.
 - **FEAT**: unify into one pigeon file.
 - **FEAT**: remove useless tests thanks to Pigeon.
 - **FEAT**(web): web support.
 - **FEAT**(web): activate web.
 - **FEAT**: first version 5 commit.
 - **FEAT**(android): add several information to resolve #552.
 - **FEAT**: add option to reopen app from notification.
 - **FEAT**: allow for customizing Android notification text, subtext and color.
 - **FEAT**: allow for customizing Android background notification from dart.
 - **FEAT**: Update to null safety.
 - **DOCS**: writing docs for version 5.
 - **DOCS**: writing docs for version 5.
 - **DOCS**: update documentation.
 - **DOCS**: add documentation to code.
 - **DOCS**: update readme web.
 - **BREAKING** **REFACTOR**: update the location_web flutter dependency to >=1.20.0 in order to remove placeholder folders.

#### `location` - `v6.0.0`

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

#### `location_android` - `v6.0.0`

 - **FEAT**: update to run on Flutter 3.7.
 - **FEAT**: improve PermissionStatus.
 - **FEAT**(android): background mode for android.
 - **FEAT**: unify into one pigeon file.
 - **FEAT**: remove useless tests thanks to Pigeon.
 - **FEAT**(ios): using SwiftLocation.
 - **FEAT**(ios): initialize the ios package.
 - **FEAT**(android): readying docs.
 - **FEAT**(android): dialog with AppCompat.
 - **FEAT**(android): support for coarse location.
 - **FEAT**(android): get permission status.
 - **FEAT**(android): set settings for individual location settings.
 - **FEAT**(android): change settings of onLocationChanged.
 - **FEAT**(android): change settings.
 - **FEAT**(android): implement setLocationSettings.
 - **FEAT**(android): implement onLocationChanged.
 - **FEAT**: first version 5 commit.
 - **DOCS**: writing docs for version 5.
 - **DOCS**: writing docs for version 5.
 - **DOCS**: update documentation.
 - **DOCS**: add documentation to code.

#### `location_ios` - `v6.0.0`

 - **FEAT**: improve PermissionStatus.
 - **FEAT**(ios): background mode for ios.
 - **FEAT**(android): background mode for android.
 - **FEAT**(macos): ready version macos.
 - **FEAT**: unify into one pigeon file.
 - **FEAT**: remove useless tests thanks to Pigeon.
 - **FEAT**(ios): settings mapping.
 - **FEAT**(ios): using SwiftLocation.
 - **FEAT**(ios): ready with CLocationManager.
 - **FEAT**(ios): initialize the ios package.
 - **FEAT**: first version 5 commit.
 - **DOCS**: writing docs for version 5.
 - **DOCS**: writing docs for version 5.
 - **DOCS**: update documentation.
 - **DOCS**: add documentation to code.

#### `location_macos` - `v6.0.0`

 - **FEAT**: improve PermissionStatus.
 - **FEAT**(macos): ready version macos.
 - **FEAT**: remove useless tests thanks to Pigeon.
 - **FEAT**: first version 5 commit.
 - **DOCS**: writing docs for version 5.
 - **DOCS**: writing docs for version 5.
 - **DOCS**: update documentation.
 - **DOCS**: add documentation to code.

#### `location_platform_interface` - `v6.0.0`

 - **FIX**(tests): add platform tests.
 - **FIX**(tests): fix tests.
 - **FIX**(android): fix the depreciation warning on android #550.
 - **FEAT**: improve PermissionStatus.
 - **FEAT**(ios): background mode for ios.
 - **FEAT**(android): background mode for android.
 - **FEAT**: unify into one pigeon file.
 - **FEAT**: remove useless tests thanks to Pigeon.
 - **FEAT**(ios): initialize the ios package.
 - **FEAT**(android): dialog with AppCompat.
 - **FEAT**(android): support for coarse location.
 - **FEAT**(android): get permission status.
 - **FEAT**(android): set settings for individual location settings.
 - **FEAT**(android): change settings.
 - **FEAT**(android): implement setLocationSettings.
 - **FEAT**(android): implement onLocationChanged.
 - **FEAT**: first version 5 commit.
 - **FEAT**(tests): improve coverage.
 - **FEAT**(android): add several information to resolve #552.
 - **FEAT**(docs): improve LocationData doc.
 - **FEAT**: improve example app.
 - **FEAT**(android): add isMock information on LocationData.
 - **FEAT**(android): add fallback for LocationAccuracy.reduced on Android.
 - **FEAT**: add option to reopen app from notification.
 - **FEAT**: allow for customizing Android notification text, subtext and color.
 - **FEAT**: allow for customizing Android background notification from dart.
 - **FEAT**: Update to null safety.
 - **DOCS**: writing docs for version 5.
 - **DOCS**: writing docs for version 5.
 - **DOCS**: update documentation.
 - **DOCS**: add documentation to code.
 - **DOCS**: update readme web.


## 2023-03-02

### Changes

---

Packages with breaking changes:

 - There are no breaking changes in this release.

Packages with other changes:

 - [`location` - `v5.0.0-dev.9`](#location---v500-dev9)
 - [`location_android` - `v6.0.0`](#location_android---v500-dev4)

---

#### `location` - `v5.0.0-dev.9`

 - **FEAT**: update to run on Flutter 3.7.

#### `location_android` - `v6.0.0`

 - **FEAT**: update to run on Flutter 3.7.

