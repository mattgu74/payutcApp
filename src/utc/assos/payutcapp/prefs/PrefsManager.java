package utc.assos.payutcapp.prefs;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefsManager{
	public static String LOGIN = "login";
    public static String PASSWORD = "password";
    

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences("myprefs", 0);
    }

//get the login from the prefs
    public static String getLogin(Context context) {
        return getPrefs(context).getString(LOGIN, null);
    }

//set the login in the prefs
    public static void setLogin(Context context, String value) {
        getPrefs(context).edit().putString(LOGIN, value).commit();
    }
    
  //get the password from the prefs
    public static String getPassword(Context context) {
        return getPrefs(context).getString(PASSWORD, null);
    }

//set the password in the prefs
    public static void setPassword(Context context, String value) {
        getPrefs(context).edit().putString(PASSWORD, value).commit();
    }
}