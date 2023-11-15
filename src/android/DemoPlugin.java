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

import abstractions.IAuthCompletionResponse;
import abstractions.IDeletePreAuthResponse;
import abstractions.IMailOrderResponse;
import abstractions.IPreAuthResponse;
import abstractions.IDeviceInterface;
import abstractions.IRefundResponse;
import abstractions.IDeviceResponse;
import abstractions.ISaleResponse;
import abstractions.IVoidResponse;

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
            String param = args.getString(0);
            JSONObject obj = new JSONObject(param);
            String _baseAmount = obj.getString("_sBaseAmount");
            String _tipAmount = obj.getString("_sTipAmount");
            this.saleTransaction(_baseAmount, _tipAmount, callbackContext);
            return true;
        }
        if(action.equals("saleTransactionWithTip")) {
            String param = args.getString(0);
            JSONObject obj = new JSONObject(param);
            String _baseAmount = obj.getString("_sBaseAmount");
            String _tipAmount = obj.getString("_sTipAmount");
            this.saleTransactionWithTip(_baseAmount, _tipAmount, callbackContext);
            return true;
        }
        if(action.equals("refundTransaction")) {
            String param = args.getString(0);
            JSONObject obj = new JSONObject(param);
            String _baseAmount = obj.getString("_sBaseAmount");
            this.refundTransaction(_baseAmount, callbackContext);
            return true;
        }
        if(action.equals("voidTransaction")) {
            String param = args.getString(0);
            JSONObject obj = new JSONObject(param);
            String _transactionID = obj.getString("_sTransactionID");
            this.voidTransaction(_transactionID, callbackContext);
            return true;
        }
        if(action.equals("preAuthTransaction")) {
            String param = args.getString(0);
            JSONObject obj = new JSONObject(param);
            String _amount = obj.getString("_sBaseAmount");
            this.preAuthTransaction(_amount, callbackContext);
            return true;
        }
        if(action.equals("deletePreAuthTransaction")) {
            String param = args.getString(0);
            JSONObject obj = new JSONObject(param);
            String _referenceNumber = obj.getString("_sReferenceNumber");
            this.deletePreAuthTransaction(_referenceNumber, callbackContext);
            return true;
        }
        if(action.equals("authCompletionTransaction")) {
            String param = args.getString(0);
            JSONObject obj = new JSONObject(param);
            String _amount = obj.getString("_sBaseAmount");
            String _tip = obj.getString("_sTipAmount");
            String _referenceNumber = obj.getString("_sReferenceNumber");
            this.authCompletionTransaction(_amount, _tip, _referenceNumber, callbackContext);
            return true;
        }
        if(action.equals("mailOrderTransaction")) {
          String param = args.getString(0);
          JSONObject obj = new JSONObject(param);
          String _amount = obj.getString("_sBaseAmount");
          this.mailOrderTransaction(_amount, callbackContext);
          return true;
        }
        if(action.equals("batchCloseTransaction")) {
            this.batchCloseTransaction(callbackContext);
            return true;            
        }
        if(action.equals("cancelTransaction")) {
            this.cancelTransaction(callbackContext);
            return true;
        }
        if(action.equals("restartTransaction")) {
            this.restartTransaction(callbackContext);
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
            if(this._device != null) {
              callbackContext.success("Connected already");
              return;
            }
            ConnectionConfig config = new ConnectionConfig();
            config.setConnectionMode(ConnectionModes.TCP_IP);
            config.setPort(port);
            config.setIpAddress(ip);
            config.setTimeout(10);
            this._device = DeviceService.create(config);
            if(this._device == null) {
                this._isConnected = false;
                callbackContext.error("Failed connecting");
            } else {
                this._isConnected = true;
                callbackContext.success("Connected successfully");
            }
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

    private void saleTransaction(String _baseAmount, String _tipAmount, CallbackContext callbackContext) {
        if(!this._isConnected) {
            callbackContext.error("Not connected");
            return;
        }
        try {
            ISaleResponse response = this._device.sale(new BigDecimal(_baseAmount)).withRequestId(this._requestId).withEcrId(this._ecrId).withTipAmount(new BigDecimal(_tipAmount)).execute();
            this._requestId ++;
            if(response.getErrorCode() != null) {
              callbackContext.error(response.getErrorMessage());
              return;
            }
            JSONObject r = new JSONObject();
            r.put("tranNo", response.getHost().getTransactionNumber());
            r.put("referenceNumber", response.getHost().getReferenceNumber());
            callbackContext.success(r);
        } catch (Exception e) {
            callbackContext.error(e.getMessage());
        }
    }

    private void saleTransactionWithTip(String _baseAmount, String _tipAmount, CallbackContext callbackContext) {
        if(!this._isConnected) {
            callbackContext.error("Not connected");
            return;
        }
        try {
            ISaleResponse response = this._device.sale(new BigDecimal(_baseAmount)).withRequestId(this._requestId).withEcrId(this._ecrId).withTipAmount(new BigDecimal(_tipAmount)).execute();
            this._requestId ++;
            if(response.getErrorCode() != null) {
              callbackContext.error(response.getErrorMessage());
              return;
            }
            JSONObject r = new JSONObject();
            r.put("tranNo", response.getHost().getTransactionNumber());
            r.put("referenceNumber", response.getHost().getReferenceNumber());
            callbackContext.success(r);
        } catch (Exception e) {
            callbackContext.error(e.getMessage());
        }
    }

    private void refundTransaction(String _baseAmount, CallbackContext callbackContext) {
        if(!this._isConnected) {
            callbackContext.error("Not connected");
            return;
        }
        try {
            IRefundResponse response = _device.refund(new BigDecimal(_baseAmount)).withRequestId(this._requestId).withEcrId(this._ecrId).execute();
            this._requestId ++;
            if(response.getErrorCode() != null) {
              callbackContext.error(response.getErrorMessage());
              return;
            }
            JSONObject r = new JSONObject();
            r.put("tranNo", response.getHost().getTransactionNumber());
            r.put("referenceNumber", response.getHost().getReferenceNumber());
            callbackContext.success(r);
        } catch (Exception e) {
            callbackContext.error(e.getMessage());
        }
    }

    private void voidTransaction(String _transactionID, CallbackContext callbackContext) {
        if(!this._isConnected) {
            callbackContext.error("Not connected");
            return;
        }
        try {
            IVoidResponse response = _device.void_(_transactionID).withRequestId(this._requestId).withEcrId(this._ecrId).execute();
            this._requestId ++;
            if(response.getErrorCode() != null) {
              callbackContext.error(response.getErrorMessage());
              return;
            }
            JSONObject r = new JSONObject();
            r.put("tranNo", response.getHost().getTransactionNumber());
            r.put("referenceNumber", response.getHost().getReferenceNumber());
            callbackContext.success(r);
        } catch (Exception e) {
            callbackContext.error(e.getMessage());
        }
    }

    private void preAuthTransaction(String _amount, CallbackContext callbackContext) {
        if(!this._isConnected) {
            callbackContext.error("Not connected");
            return;
        }
        try {
            IPreAuthResponse response = _device.preAuth(new BigDecimal(_amount)).withRequestId(this._requestId).withEcrId(this._ecrId).execute();
            this._requestId ++;
            if(response.getErrorCode() != null) {
              callbackContext.error(response.getErrorMessage());
              return;
            }
            JSONObject r = new JSONObject();
            r.put("tranNo", response.getHost().getTransactionNumber());
            r.put("referenceNumber", response.getHost().getReferenceNumber());
            callbackContext.success(r);
        } catch (Exception e) {
            callbackContext.error(e.getMessage());
        }
    }

    private void deletePreAuthTransaction(String _referenceNumber,  CallbackContext callbackContext) {
        if(!this._isConnected) {
            callbackContext.error("Not connected");
            return;
        }
        try {
            IDeletePreAuthResponse response = _device.deletePreAuth(_referenceNumber).withRequestId(this._requestId).withEcrId(this._ecrId).execute();
            this._requestId ++;
            if(response.getErrorCode() != null) {
              callbackContext.error(response.getErrorMessage());
              return;
            }
            JSONObject r = new JSONObject();
            r.put("tranNo", response.getHost().getTransactionNumber());
            r.put("referenceNumber", response.getHost().getReferenceNumber());
            callbackContext.success(r);
        } catch (Exception e) {
            callbackContext.error(e.getMessage());
        }
    }

    private void authCompletionTransaction(String _baseAmount, String _tipAmount, String _referenceNumber, CallbackContext callbackContext) {
        if(!this._isConnected) {
            callbackContext.error("Not connected");
            return;
        }
        try {
            IAuthCompletionResponse response = _device.authCompletion(new BigDecimal(_baseAmount)).withRequestId(this._requestId).withEcrId(this._ecrId).withTipAmount(new BigDecimal(_tipAmount)).withReferenceNumber(_referenceNumber).execute();
            this._requestId ++;
            if(response.getErrorCode() != null) {
              callbackContext.error(response.getErrorMessage());
              return;
            }
            JSONObject r = new JSONObject();
            r.put("tranNo", response.getHost().getTransactionNumber());
            r.put("referenceNumber", response.getHost().getReferenceNumber());
            callbackContext.success(r);
        } catch (Exception e) {
            callbackContext.error(e.getMessage());
        }
    }

    private void mailOrderTransaction(String _amount, CallbackContext callbackContext) {
      if(!this._isConnected) {
        callbackContext.error("Not connected");
        return;
      }
      try {
        IMailOrderResponse response = _device.mailOrder(new BigDecimal(_amount)).withRequestId(this._requestId).withEcrId(this._ecrId).execute();
        this._requestId ++;
        if(response.getErrorCode() != null) {
          callbackContext.error(response.getErrorMessage());
          return;
        }
        JSONObject r = new JSONObject();
        r.put("tranNo", response.getHost().getTransactionNumber());
        r.put("referenceNumber", response.getHost().getReferenceNumber());
        callbackContext.success(r);
      } catch (Exception e) {
        callbackContext.error(e.getMessage());
      }
    }

    private void cancelTransaction(CallbackContext callbackContext) {
        if(!this._isConnected) {
            callbackContext.error("Not connected");
            return;
        }
        try {
            IDeviceResponse response = _device.cancel().withRequestId(this._requestId).withEcrId(this._ecrId).execute();
            this._requestId ++;
            callbackContext.success("Canceled");
        } catch (Exception e) {
            callbackContext.error(e.getMessage());
        }
    }

    private void restartTransaction(CallbackContext callbackContext) {
        if(!this._isConnected) {
            callbackContext.error("Not connected");
            return;
        }
        try {
            IDeviceResponse response = _device.restart().withRequestId(this._requestId).withEcrId(this._ecrId).execute();
            this._requestId ++;
            callbackContext.success("Restarted");
        } catch (Exception e) {
            callbackContext.error(e.getMessage());
        }
    }

    private void batchCloseTransaction(CallbackContext callbackContext) {
        if(!this._isConnected) {
            callbackContext.error("Not connected");
            return;
        }
        try {
            IEODResponse response = _device.eodProcessing().withRequestId(this._requestId).withEcrId(this._ecrId).execute();
            this._requestId ++;
            callbackContext.success("eodProcessed");
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
