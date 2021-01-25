package hu.dpal.phonegap.plugins;

import android.content.Context;
import android.content.SharedPreferences;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import android.os.Build;
import android.provider.Settings.Secure;
import java.util.UUID;

import android.media.MediaDrm;
import java.util.*;


public class UniqueDeviceID extends CordovaPlugin {

    private CallbackContext callbackContext;
    private static final String PREF_UNIQUE_ID = "PREF_OSAPP_UNIQUE_ID";

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        this.callbackContext = callbackContext;

        try {
            if (action.equals("get")) {
                getDeviceId();
            } else {
                this.callbackContext.error("Invalid action");
                return false;
            }
        } catch (Exception e) {
            this.callbackContext.error("Exception occurred: ".concat(e.getMessage()));
            return false;
        }

        return true;
    }

    protected void getDeviceId() {
       
        try {

            String uuid;
            
            /* try DrmID
            ** for more details about DRM follow the link DRM_Android (https://source.android.com/devices/drm).
            */
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                UUID wideVineUuid = new UUID(-0x121074568629b532L, -0x5c37d8232ae2de13L);
                try {
                  MediaDrm wvDrm = new MediaDrm(wideVineUuid);
                  byte[] wideVineId = wvDrm.getPropertyByteArray(MediaDrm.PROPERTY_DEVICE_UNIQUE_ID);
                  uuid = Base64.encodeToString(WideVineId , Base64.Default) 

                } catch (Exception e) {
                  // Inspect exception
                  uuid = "";
                }

            }

            /*Still no uuid?*/
            if( uuid == "" ) {
                /*Try to get the ANDROID_ID
                ** It will give same android Id until Device get reset.
                */
                Context context = cordova.getActivity().getApplicationContext();
                uuid = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID); 
                if (uuid == null || uuid.equals("9774d56d682e549c") || uuid.length() < 15 ) {
                    /* if ANDROID_ID is null, 
                    ** or it's equals to the GalaxyTab generic ANDROID_ID 
                    ** or bad, 
                    ** generates a new one
                    ** 
                    ** Random UUID
                    ** It will generate different id every time for that you have to store in 
                    ** preference or database for use as unique id in the app.
                    */
                    SharedPreferences sharedPrefs = context.getSharedPreferences(PREF_UNIQUE_ID, Context.MODE_PRIVATE);
                    uuid = sharedPrefs.getString(PREF_UNIQUE_ID, null);

                    if (uuid == null) {
                        uuid = UUID.randomUUID().toString();
                        SharedPreferences.Editor editor = sharedPrefs.edit();
                        editor.putString(PREF_UNIQUE_ID, uuid);
                        editor.commit();
                    }

                }
            }

            uuid = hashUUID(uuid);

            this.callbackContext.success(uuid);

        } catch (Exception e) {
            this.callbackContext.error("Exception occurred: ".concat(e.getMessage()));
        }
    }

    private string hashUUID(String uuid){
        try {
                    MessageDigest messageDigest = MessageDigest.getInstance("MD5");
                    messageDigest.update(uuid.getBytes(), 0, uuid.length());

                    // get md5 bytes
                    byte md5Bytes[] = messageDigest.digest();

                    // creating a hex string
                    String identifier = "";

                    for (byte md5Byte : md5Bytes) {
                        int b = (0xFF & md5Byte);

                        // if it is a single digit, make sure it have 0 in front (proper padding)
                        if (b <= 0xF) {
                            identifier += "0";
                        }

                        // add number to string
                        identifier += Integer.toHexString(b);
                    }

                    // hex string to uppercase
                    identifier = identifier.toUpperCase();
                    return identifier;
                } catch (Exception e) {
     
                }

    }
}