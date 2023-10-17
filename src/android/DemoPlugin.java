package demoplugin;

import org.apache.cordova.CordovaPlugin;

import java.math.BigDecimal;

import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import configuration.ConnectionConfig;
import configuration.DeviceService;

import events.IMessageSentInterface;

import terminals.NucleusEvents;

import abstractions.IDeviceInterface;
import abstractions.ISaleResponse;

import entities.enums.ConnectionModes;



/**
 * This class echoes a string called from JavaScript.
 */
public class DemoPlugin extends CordovaPlugin {

    IDeviceInterface _device;
    private String _rawMessage = "";
    private boolean _isConnected = false;

    private Long _requestId = 0L;
    private String _ecrId = "1";

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("coolMethod")) {
            String message = args.getString(0);
            this.coolMethod(message, callbackContext);
            return true;
        }
        if (action.equals("initializeConnection")) {
            String param = args.getString(0);
            JSONObject obj = new JSONObject(param);
            String ip = obj.getString("_sIp");
            String port = obj.getString("_sPort");

            this.initializeConnection(ip, port, callbackContext);
            return true;
        }
        if(action.equals("saleTransaction")) {
            String _baseAmount = args.getString(0);
            this.saleTransaction(_baseAmount, callbackContext);
            return true;
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
        try {
            ConnectionConfig config = new ConnectionConfig();
            config.setConnectionMode(ConnectionModes.TCP_IP);
            config.setPort(port);
            config.setIpAddress(ip);
            config.setTimeout(10);
            this._device = DeviceService.create(config);
            if(this._device == null) {
                this._isConnected = false;
                callbackContext.error("Failed connecting");
            }
            this._isConnected = true;
            callbackContext.success("Connected successfully");
        } catch(Exception e) {
            this._isConnected = false;
            callbackContext.error(e.getMessage());
        }
    }

    private void initializeEventListener() {
        NucleusEvents.setOnMessageSent(new IMessageSentInterface() {
            @Override
            public void messageSent(String s) {
                _rawMessage += formatString(s);
            }
        });
    }

    private void saleTransaction(String _baseAmount, CallbackContext callbackContext) {
        if(!this._isConnected) {
            callbackContext.error("Not connected");
            return;
        }
        try {
            ISaleResponse response = this._device.sale(new BigDecimal(_baseAmount)).withRequestId(this._requestId).withEcrId(this._ecrId).execute();
            this._requestId ++;
            callbackContext.success("Sent successfully");
        } catch (Exception e) {
            callbackContext.error(e.getMessage());
        }
    }

    private String formatString(String text) {
        StringBuilder json = new StringBuilder();
        String indentString = "";

        text = text.replace("<STX>", "\02").replace("<ETX>", "\03").replace("<LF>", "\r\n");

        for (int i = 0 ; i < text.length() ; i ++) {
            char letter = text.charAt(i);
            switch(letter) {
                case '{':
                case '[':
                    json.append("\n" + indentString + letter + "\n");
                    indentString = indentString + "\t";
                    json.append(indentString);
                    break;
                case '}':
                case ']':
                    indentString = indentString.replaceFirst("\t", "");
                    json.append("\n" + indentString + letter);
                case ',':
                    json.append(letter + "\n" + indentString);
                    break;
                default:
                    json.append(letter);
                    break;
            }
        }

        return json.toString();
    }
}
