package hu.dpal.phonegap.plugins;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import java.util.UUID;

import java.lang.reflect.Method;

public class UniqueDeviceID extends CordovaPlugin {

    public static final String TAG = "UniqueDeviceID";
    public CallbackContext callbackContext;
    public static final int REQUEST_READ_PHONE_STATE = 0;

    protected final static String permission = Manifest.permission.READ_PHONE_STATE;
    private static final String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";

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
        }catch(Exception e ) {
            this.callbackContext.error("Exception occurred: ".concat(e.getMessage()));
            return false;
        }
        return true;

    }

    protected void getDeviceId() {
        try {
            Context context = cordova.getActivity().getApplicationContext();

            SharedPreferences sharedPrefs = context.getSharedPreferences(PREF_UNIQUE_ID, Context.MODE_PRIVATE);
            String uniqueID = sharedPrefs.getString(PREF_UNIQUE_ID, null);

            if (uniqueID == null) {
                uniqueID = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
                Editor editor = sharedPrefs.edit();
                editor.putString(PREF_UNIQUE_ID, uniqueID);
                editor.commit();
            }

            this.callbackContext.success(uniqueID);
        } catch(Exception e ) {
            this.callbackContext.error("Exception occurred: ".concat(e.getMessage()));
        }
    }

    private boolean hasPermission(String permission) throws Exception{
        boolean hasPermission = true;
        Method method = null;
        try {
            method = cordova.getClass().getMethod("hasPermission", permission.getClass());
            Boolean bool = (Boolean) method.invoke(cordova, permission);
            hasPermission = bool.booleanValue();
        } catch (NoSuchMethodException e) {
            Log.w(TAG, "Cordova v" + CordovaWebView.CORDOVA_VERSION + " does not support API 23 runtime permissions so defaulting to GRANTED for " + permission);
        }
        return hasPermission;
    }

    private void requestPermission(CordovaPlugin plugin, int requestCode, String permission) throws Exception{
        try {
            java.lang.reflect.Method method = cordova.getClass().getMethod("requestPermission", org.apache.cordova.CordovaPlugin.class ,int.class, java.lang.String.class);
            method.invoke(cordova, plugin, requestCode, permission);
        } catch (NoSuchMethodException e) {
            throw new Exception("requestPermission() method not found in CordovaInterface implementation of Cordova v" + CordovaWebView.CORDOVA_VERSION);
        }
    }
}