 package utc.assos.payutcapp;

import static utc.assos.payutcapp.CommonUtilities.TAG;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

public class LoginActivity extends Activity{
	
	Context context;
    GcmManager gcm_manager = new GcmManager(this);
    String regid;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		context = getApplicationContext();
		// Check device for Play Services APK. If check succeeds, proceed with
        //  GCM registration.
        if (gcm_manager.checkPlayServices()) {
            regid = gcm_manager.getRegistrationId(context);
            if (regid.isEmpty()) {
            	gcm_manager.registerInBackground();
            } else {
            	Log.i(TAG, "Device alreadly registed. Regid = " + regid);
            }
        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }
	}
	
	@Override
	protected void onResume() {
	    super.onResume();
	    gcm_manager.checkPlayServices();
	}
}
