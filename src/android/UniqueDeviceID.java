package hu.dpal.phonegap.plugins;

import android.content.Context;
import android.content.SharedPreferences;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import android.provider.Settings.Secure;
import java.util.UUID;

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
            Context context = cordova.getActivity().getApplicationContext();
            SharedPreferences sharedPrefs = context.getSharedPreferences(PREF_UNIQUE_ID, Context.MODE_PRIVATE);
            String uuid = sharedPrefs.getString(PREF_UNIQUE_ID, null);

            if (uuid == null) {
                uuid = generateDeviceIdentifier(context);
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putString(PREF_UNIQUE_ID, uuid);
                editor.commit();
            }

            this.callbackContext.success(uuid);
        } catch (Exception e) {
            this.callbackContext.error("Exception occurred: ".concat(e.getMessage()));
        }
    }

    /*
     * Generate a new UDID
     */
    private String generateUDID(Context context) {
        
        //Try to get the ANDROID_ID
        String UDID = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID); 
        if (UDID == null || UDID.equals("9774d56d682e549c") || UDID.length() < 15 ) {
            //if ANDROID_ID is null, or it's equals to the GalaxyTab generic ANDROID_ID or bad, generates a new one
            UDID = UUID.randomUUID().toString();
        }


    }

    public static String generateDeviceIdentifier(Context context) {

            String pseudoId = "35" +
                    Build.BOARD.length() % 10 +
                    Build.BRAND.length() % 10 +
                    Build.CPU_ABI.length() % 10 +
                    Build.DEVICE.length() % 10 +
                    Build.DISPLAY.length() % 10 +
                    Build.HOST.length() % 10 +
                    Build.ID.length() % 10 +
                    Build.MANUFACTURER.length() % 10 +
                    Build.MODEL.length() % 10 +
                    Build.PRODUCT.length() % 10 +
                    Build.TAGS.length() % 10 +
                    Build.TYPE.length() % 10 +
                    Build.USER.length() % 10;

            //Try to get the ANDROID_ID
            String androidId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID); 
            if (androidId == null || androidId.equals("9774d56d682e549c") || androidId.length() < 15 ) {
                //if ANDROID_ID is null, or it's equals to the GalaxyTab generic ANDROID_ID or bad, generates a new one
                androidId = UUID.randomUUID().toString();
            }

            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            String btId = "";

            if (bluetoothAdapter != null) {
                btId = bluetoothAdapter.getAddress();
            }

            String longId = pseudoId + androidId + btId;

            try {
                MessageDigest messageDigest = MessageDigest.getInstance("MD5");
                messageDigest.update(longId.getBytes(), 0, longId.length());

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
                Log.e("TAG", e.toString());
            }
            return "";
    }

}