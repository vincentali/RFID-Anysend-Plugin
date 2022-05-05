/*
       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
*/
package de.mindsquare.rfid;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;

import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaInterface;
import org.json.JSONArray;
import org.json.JSONException;

import com.handheldgroup.serialport.SerialPort;
import com.handheldgroup.anysend.*;

import android.media.ToneGenerator;
import android.os.SystemClock;
import android.telecom.Call;
import anysendRFID.BroadcastReceiver;
import anysendRFID.Context;
import anysendRFID.IntentFilter;
import android.content.Intent;

public class RFID extends CordovaPlugin {
	SearchTagActivity sta = null;

	/**
	 * Constructor.
	 */
	public RFID() {
	}

	/**
	 * Sets the context of the Command. This can then be used to do things like get
	 * file paths associated with the Activity.
	 *
	 * @param cordova The context of the main Activity.
	 * @param webView The CordovaWebView Cordova is running in.
	 */
	public void initialize(CordovaInterface cordova, CordovaWebView webView) {
		super.initialize(cordova, webView);
	}

	/**
	 * Executes the request and returns PluginResult.
	 *
	 * @param action          The action to execute.
	 * @param args            JSONArry of arguments for the plugin.
	 * @param callbackContext The callback id used when calling back into
	 *                        JavaScript.
	 * @return True if the action was valid, false if not.
	 */
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
		if ("readRFID".equals(action)) {
			this.read(callbackContext);
		} else if ("cancelRFID".equals(action)) {
			this.cancel(callbackContext);
		} else {
			return false;
		}
		return true;
	}

	public void read(CallbackContext cbCtx) {
		this.sta = new SearchTagActivity(cbCtx);
	}

	public void cancel(CallbackContext cbCtx) {
		// is the cordova plugin implementatio singleton?
		if (this.sta != null) {
			// this.sta.mSendingThread.interrupt();
			this.sta.onDestroy();
			this.sta = null;
		}
	}

	public void enableRfid() {
	    Intent intent = new Intent("com.handheldgroup.anysend.SET_STATE");
	    intent.putExtra("device", true);
	    sendBroadcast(intent);
	    startService(intent);
	}
	 
	public void disableRfid() {
	    Intent intent = new Intent("com.handheldgroup.anysend.SET_STATE");
	    intent.putExtra("device", false);
	    sendBroadcast(intent);
	    stopService(intent);
	}
	
	public void broadcastReciever() {
		private BroadcastReceiver receiver = new BroadcastReceiver() {
		    @Override
		    public void onReceive(Context context, Intent intent) {
		        if(intent.getAction().equals("com.handheldgroup.anysend.RESULT") == false) return;
		 
		        // The device which sent this data. RFID = 2
		        int device = intent.getIntExtra("device", -1);
		        
		        // Numeric representation of the tag type
		        int type = intent.getIntExtra("type", -1);
		        
		        // String representation of the tag type
		        String typeName = intent.getStringExtra("type_name");
		        
		        // Data as byte array
		        byte[] dataArray = intent.getByteArrayExtra("data");
		        
		        // Data as String
		        String dataString = intent.getStringExtra("string");
		 
		        Log.i("anysend-result", "Device #" + device + " returned " + dataString + " for type " + typeName);
		    }
		};
		registerReceiver(receiver, new IntentFilter("com.handheldgroup.anysend.RESULT"));
		Intent intent = new Intent("com.handheldgroup.anysend.RfidService.START");
		intent.setPackage("com.handheldgroup.anysend");
		startService(intent);
	}
}
