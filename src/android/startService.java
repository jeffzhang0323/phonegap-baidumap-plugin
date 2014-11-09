package com.isoft.baidu.location;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;

public class startService extends CordovaPlugin {
	public startService(){}
	 /**
     * Executes the request and returns PluginResult.
     *
     * @param action            The action to execute.
     * @param args              JSONArray of arguments for the plugin.
     * @param callbackContext   The callback context used when calling back into JavaScript.
     * @return                  True when the action was valid, false otherwise.
     */
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

       //this.start(args, callbackContext);
    	
    	 //这里面开始调用后台服务了类了
    	try{
    		final Activity act = cordova.getActivity();
    		Intent i = new Intent(act,BDService.class);
    		i.putExtra("ServerIP", args.getString(0));
    		i.putExtra("ServerPort", args.getString(1));
    		act.startService(i);//启动后台服务
    		callbackContext.success();
    	}
    	catch(Exception e)
    	{
    		callbackContext.error(e.toString());
    	}
		return true;
    }
}
