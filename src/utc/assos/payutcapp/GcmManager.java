package utc.assos.payutcapp;

import static utc.assos.payutcapp.CommonUtilities.SENDER_ID;
import static utc.assos.payutcapp.CommonUtilities.TAG;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmManager {
	private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    
    TextView mDisplay;
    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    SharedPreferences prefs;
    Context context;

    String regid;
    LoginActivity activity;
    
	public GcmManager(LoginActivity loginActivity) {
		this.activity = loginActivity;
	}
	
	

	 /**
	  * Check the device to make sure it has the Google Play Services APK. If
	  * it doesn't, display a dialog that allows users to download the APK from
	  * the Google Play Store or enable it in the device's system settings.
	  */
	 public boolean checkPlayServices() {
	     int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
	     if (resultCode != ConnectionResult.SUCCESS) {
	         if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
	             GooglePlayServicesUtil.getErrorDialog(resultCode, activity,
	                     PLAY_SERVICES_RESOLUTION_REQUEST).show();
	         } else {
	             Log.i(TAG, "This device is not supported.");
	             activity.finish();
	         }
	         return false;
	     }
	     return true;
	 }
	 
	 /**
	  * Gets the current registration ID for application on GCM service.
	  * <p>
	  * If result is empty, the app needs to register.
	  *
	  * @return registration ID, or empty string if there is no existing
	  *         registration ID.
	  */
	 public String getRegistrationId(Context context) {
	     final SharedPreferences prefs = getGCMPreferences(context);
	     String registrationId = prefs.getString(PROPERTY_REG_ID, "");
	     if (registrationId.isEmpty()) {
	         Log.i(TAG, "Registration not found.");
	         return "";
	     }
	     // Check if app was updated; if so, it must clear the registration ID
	     // since the existing regID is not guaranteed to work with the new
	     // app version.
	     int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
	     int currentVersion = getAppVersion(context);
	     if (registeredVersion != currentVersion) {
	         Log.i(TAG, "App version changed.");
	         return "";
	     }
	     return registrationId;
	 }
	 
	 /**
	  * @return Application's {@code SharedPreferences}.
	  */
	 private SharedPreferences getGCMPreferences(Context context) {
	     // This sample app persists the registration ID in shared preferences, but
	     // how you store the regID in your app is up to you.
	     return activity.getSharedPreferences(MainActivity.class.getSimpleName(),
	             Context.MODE_PRIVATE);
	 }
	 
	 /**
	  * @return Application's version code from the {@code PackageManager}.
	  */
	 private static int getAppVersion(Context context) {
	     try {
	         PackageInfo packageInfo = context.getPackageManager()
	                 .getPackageInfo(context.getPackageName(), 0);
	         return packageInfo.versionCode;
	     } catch (NameNotFoundException e) {
	         // should never happen
	         throw new RuntimeException("Could not get package name: " + e);
	     }
	 }
	 
	 /**
	  * Registers the application with GCM servers asynchronously.
	  * <p>
	  * Stores the registration ID and app versionCode in the application's
	  * shared preferences.
	  */
	 public void registerInBackground() {
	     AsyncTask<Void, Void, String> register_task = new AsyncTask<Void, Void, String>() {
	    	 @Override
	    	 protected String doInBackground(Void... params) {
	             String msg = "";
                Log.i(TAG, "getting gcm");
	             try {
	                 if (gcm == null) {
	                     gcm = GoogleCloudMessaging.getInstance(context);
	                 }
	                 Log.i(TAG, "gcm got");
	                 regid = gcm.register(SENDER_ID);
	                 msg = "Device registered, registration ID=" + regid;
	                 Log.i(TAG, msg);

	                 // You should send the registration ID to your server over HTTP,
	                 // so it can use GCM/HTTP or CCS to send messages to your app.
	                 // The request to your server should be authenticated if your app
	                 // is using accounts.
	                 sendRegistrationIdToBackend();

	                 // For this demo: we don't need to send it because the device
	                 // will send upstream messages to a server that echo back the
	                 // message using the 'from' address in the message.

	                 // Persist the regID - no need to register again.
	                 storeRegistrationId(context, regid);
	             } catch (IOException ex) {
	                 msg = "Error :" + ex.getMessage();
	                 // If there is an error, don't just keep trying to register.
	                 // Require the user to click a button again, or perform
	                 // exponential back-off.
	             }
	             return msg;
	         }
			
	        protected void onPostExecute(String msg) {
	            mDisplay.append(msg + "\n");
	        }
		};
		register_task.execute(null, null, null);
	 }
	 
	 /**
	  * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP
	  * or CCS to send messages to your app. Not needed for this demo since the
	  * device sends upstream messages to a server that echoes back the message
	  * using the 'from' address in the message.
	  */
	 private void sendRegistrationIdToBackend() {
	     // Send regid to payutc server
		 
	 }
	 
	 /**
	  * Stores the registration ID and app versionCode in the application's
	  * {@code SharedPreferences}.
	  *
	  * @param context application's context.
	  * @param regId registration ID
	  */
	 private void storeRegistrationId(Context context, String regId) {
	     final SharedPreferences prefs = getGCMPreferences(context);
	     int appVersion = getAppVersion(context);
	     Log.i(TAG, "Saving regId on app version " + appVersion);
	     SharedPreferences.Editor editor = prefs.edit();
	     editor.putString(PROPERTY_REG_ID, regId);
	     editor.putInt(PROPERTY_APP_VERSION, appVersion);
	     editor.commit();
	 }

}
