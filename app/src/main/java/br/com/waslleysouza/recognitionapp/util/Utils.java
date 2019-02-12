package br.com.waslleysouza.recognitionapp.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

public class Utils {
    public static final String PREFS_NAME = "LoginPrefs";

    public static void setLoginSharedPreferences(final Context context, final String username,
                                                 final String password, final String serverURL,
                                                 final String identity) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("username", username);
        map.put("password", password);
        map.put("serverURL", serverURL);
        map.put("identity", identity);
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        for (String key : map.keySet()) {
            editor.putString(key, map.get(key));
        }
        editor.apply();
    }

    public static String getServerURL(final Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString("serverURL", "");
    }
}
