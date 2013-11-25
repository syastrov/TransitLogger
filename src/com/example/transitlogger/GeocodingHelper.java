package com.example.transitlogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.location.Address;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class GeocodingHelper {
	public static void getFromLocation(final double lat, final double lng, int maxResult, final Handler handler){
		new Thread(new Runnable() {
			public void run() {
				String url = String.format(Locale.getDefault(), "http://maps.googleapis.com/maps/api/geocode/json?latlng=%1$f,%2$f&sensor=true&language="+Locale.getDefault().getCountry(), lat, lng);
				HttpGet httpGet = new HttpGet(url);
				HttpClient client = new DefaultHttpClient();
				HttpResponse response;
				StringBuilder stringBuilder = new StringBuilder();
		
				List<Address> retList = null;
				
				try {
					response = client.execute(httpGet);
					
					// Get the response
					BufferedReader rd = new BufferedReader
					  (new InputStreamReader(response.getEntity().getContent()));
					    
					String line = "";
					while ((line = rd.readLine()) != null) {
					  stringBuilder.append(line);
					} 
					
					JSONObject jsonObject = new JSONObject();
					jsonObject = new JSONObject(stringBuilder.toString());
					
					
					retList = new ArrayList<Address>();
					
					
					if("OK".equalsIgnoreCase(jsonObject.getString("status"))){
						JSONArray results = jsonObject.getJSONArray("results");
						for (int i=0;i<results.length();i++ ) {
							JSONObject result = results.getJSONObject(i);
							String indiStr = result.getString("formatted_address");
							Address addr = new Address(Locale.getDefault());
							addr.setAddressLine(0, indiStr);
							retList.add(addr);
						}
					}
					
					
				} catch (ClientProtocolException e) {
					Log.e(GeocodingHelper.class.getName(), "Error calling Google geocode webservice.", e);
				} catch (IOException e) {
					Log.e(GeocodingHelper.class.getName(), "Error calling Google geocode webservice.", e);
				} catch (JSONException e) {
					Log.e(GeocodingHelper.class.getName(), "Error parsing Google geocode webservice response.", e);
				}
				
				String result = null;
                if (retList != null && retList.size() > 0) {
                    Address address = retList.get(0);
                    // sending back first address line
                    result = address.getAddressLine(0);
                }
                
                Message msg = Message.obtain();
                msg.setTarget(handler);
                if (result != null) {
                    msg.what = 1;
                    Bundle bundle = new Bundle();
                    bundle.putString("address", result);
                    msg.setData(bundle);
                } else 
                    msg.what = 0;
                msg.sendToTarget();
			}
		}).start();
	}
}
