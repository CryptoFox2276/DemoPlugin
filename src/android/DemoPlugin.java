package demoplugin;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import configuration.ConnectionConfig;
import configuration.DeviceSevice;


/**
 * This class echoes a string called from JavaScript.
 */
public class DemoPlugin extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("coolMethod")) {
            String message = args.getString(0);
            this.coolMethod(message, callbackContext);
            return true;
        }
        if (action.equals("initializeConnection")) {
            String ip = args.getString(0);
            String port = args.getString(1);
            this.initializeConnection(ip, port, callbackContext);
        }
        return false;
    }

    private void coolMethod(String message, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
            callbackContext.success(message);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }

    private void initializeConnection(String ip, String port, CallbackContext callbackContext) {
        callbackContext.success("InitialzeConnection");
    }
}
