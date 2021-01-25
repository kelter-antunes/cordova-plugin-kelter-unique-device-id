UniqueDeviceID
==============

PhoneGap / Cordova unique device id (UUID) plugin for Android, iOS and Windows Phone 8. Remains the same after app uninstall.

## Supported Platforms

- Android
- iOS
- Windows Phone 8

## Usage

    // Get UUID
    window.plugins.uniqueDeviceID.get(success, fail);

Success callback function:

    function success(uuid)
    {
        console.log(uuid);
    };
