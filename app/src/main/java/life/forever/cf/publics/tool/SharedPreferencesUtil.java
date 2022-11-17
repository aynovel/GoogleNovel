package life.forever.cf.publics.tool;

import android.content.Context;
import android.content.SharedPreferences;

import life.forever.cf.activtiy.PlotRead;
import life.forever.cf.publics.Constant;

import java.util.Map;


public class SharedPreferencesUtil implements Constant {


    public static SharedPreferences getSharedPreferences(String name) {
        return PlotRead.getApplication().getSharedPreferences(name, Context.MODE_PRIVATE);
    }


    public static int getInt(String name, String key) {
        return getInt(getSharedPreferences(name), key);
    }

    public static int getInt(SharedPreferences sp, String key) {
        return sp.getInt(key, ZERO);
    }


    public static boolean getBoolean(String name, String key) {
        return getBoolean(getSharedPreferences(name), key);
    }


    public static boolean getBoolean(SharedPreferences sp, String key) {
        return sp.getBoolean(key, false);
    }


    public static String getString(String name, String key) {
        return getString(getSharedPreferences(name), key);
    }


    public static String getString(SharedPreferences sp, String key) {
        return sp.getString(key, BLANK);
    }



    public static float getFloat(SharedPreferences sp, String key) {
        return sp.getFloat(key, ZERO);
    }


    public static long getLong(String name, String key) {
        return getLong(getSharedPreferences(name), key);
    }

    public static long getLong(SharedPreferences sp, String key) {
        return sp.getLong(key, ZERO);
    }


    public static void clear(String name) {
        clear(getSharedPreferences(name));
    }


    public static void clear(SharedPreferences sp) {
        sp.edit().clear().apply();
    }


    public static void remove(String name, String key) {
        remove(getSharedPreferences(name), key);
    }


    public static void remove(SharedPreferences sp, String key) {
        sp.edit().remove(key).apply();
    }

    public static void putInt(String name, String key, int value) {
        putInt(getSharedPreferences(name), key, value);
    }


    public static void putInt(SharedPreferences sp, String key, int value) {
        sp.edit().putInt(key, value).apply();
    }

    public static void putString(String name, String key, String value) {
        putString(getSharedPreferences(name), key, value);
    }


    public static void putString(SharedPreferences sp, String key, String value) {
        sp.edit().putString(key, value).apply();
    }


    public static void putBoolean(String name, String key, boolean value) {
        if (name != null && key != null){
            getSharedPreferences(name).edit().putBoolean(key, value).apply();
        }

    }


    public static void putBoolean(SharedPreferences sp, String key, boolean value) {
        sp.edit().putBoolean(key, value).apply();
    }


    public static void putFloat(SharedPreferences sp, String key, float value) {
        sp.edit().putFloat(key, value).apply();
    }


    public static void putLong(String name, String key, long value) {
        putLong(getSharedPreferences(name), key, value);
    }


    public static void putLong(SharedPreferences sp, String key, long value) {
        sp.edit().putLong(key, value).apply();
    }


    public static void clone(SharedPreferences old, SharedPreferences clone) {
        Map<String, ?> all = old.getAll();
        for (String key : all.keySet()) {
            Object value = all.get(key);
            if (value instanceof Integer) {
                putInt(clone, key, (int) value);
                continue;
            }
            if (value instanceof Boolean) {
                putBoolean(clone, key, (boolean) value);
                continue;
            }
            if (value instanceof String) {
                putString(clone, key, (String) value);
                continue;
            }
            if (value instanceof Float) {
                putFloat(clone, key, (float) value);
                continue;
            }
            if (value instanceof Long) {
                putLong(clone, key, (long) value);
            }
        }
    }

}
