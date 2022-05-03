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

import android.media.ToneGenerator;
import android.os.SystemClock;
import android.telecom.Call;

public class anysend extends CordovaPlugin {
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
	 
	// Register the receiver
	registerReceiver(receiver, new IntentFilter("com.handheldgroup.anysend.RESULT"));
	 
	// Start the service to make sure it's running
	Intent intent = new Intent(
			"com.handheldgroup.anysend.RfidService.START");intent.setPackage("com.handheldgroup.anysend");

	// This extra is optional and let's you set a message to be shown as a toast
	// after startup.
	// If set to a empty ("") string, no toast will be shown.
	intent.putExtra("startup","Ready to scan tags");

	startService(intent);

	public void enableRfid() {
		Intent intent = new Intent("com.handheldgroup.anysend.SET_STATE");
		intent.putExtra("device", true);
		sendBroadcast(intent);
	}

	public void disableRfid() {
		Intent intent = new Intent("com.handheldgroup.anysend.SET_STATE");
		intent.putExtra("device", false);
		sendBroadcast(intent);
	}
}
