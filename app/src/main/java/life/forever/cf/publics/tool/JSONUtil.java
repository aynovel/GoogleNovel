package life.forever.cf.publics.tool;


import android.text.TextUtils;

import life.forever.cf.publics.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONUtil implements Constant {

    static final String TAG = "JSONUtil";

    private static boolean check(JSONObject jsonObject, String key) {
        if (jsonObject == null) {
            return false;
        }
        if (TextUtils.isEmpty(key)) {
            return false;
        }
        return jsonObject.has(key);
    }

    private static boolean check(JSONArray jsonArray, int index) {
        if (jsonArray == null) {
            return false;
        }
        if (index < 0) {
            return false;
        }
        return index < jsonArray.length();
    }

    public static JSONObject newJSONObject() {
        return new JSONObject();
    }

    public static JSONObject newJSONObject(String json) {
        try {
            return new JSONObject(json);
        } catch (JSONException e) {
            LOG.e(TAG, e.getMessage());
        }

        return null;
    }

    public static JSONArray newJSONArray() {
        return new JSONArray();
    }

    public static JSONArray newJSONArray(String json) {
        try {
            return new JSONArray(json);
        } catch (JSONException e) {
            LOG.e(TAG, e.getMessage());
        }

        return null;
    }

    public static int getInt(JSONObject jsonObject, String key) {
        if (check(jsonObject, key)) {
            try {
                return jsonObject.getInt(key);
            } catch (JSONException e) {
                LOG.e(TAG, e.getMessage());
            }
        }

        return ZERO;
    }

    public static double getDouble(JSONObject jsonObject, String key) {
        if (check(jsonObject, key)) {
            try {
                return jsonObject.getDouble(key);
            } catch (JSONException e) {
                LOG.e(TAG, e.getMessage());
            }
        }

        return ZERO;
    }

    public static String getString(JSONObject jsonObject, String key) {
        if (check(jsonObject, key)) {
            try {
                return jsonObject.getString(key);
            } catch (JSONException e) {
                LOG.e(TAG, e.getMessage());
            }
        }

        return BLANK;
    }

    public static boolean getBoolean(JSONObject jsonObject, String key) {
        if (check(jsonObject, key)) {
            try {
                return jsonObject.getBoolean(key);
            } catch (JSONException e) {
                LOG.e(TAG, e.getMessage());
            }
        }

        return false;
    }

    public static Object get(JSONObject jsonObject, String key) {
        if (check(jsonObject, key)) {
            try {
                return jsonObject.get(key);
            } catch (JSONException e) {
                LOG.e(TAG, e.getMessage());
            }
        }

        return null;
    }

    public static JSONObject getJSONObject(JSONObject jsonObject, String key) {
        if (check(jsonObject, key)) {
            try {
                return jsonObject.getJSONObject(key);
            } catch (JSONException e) {
                LOG.e(TAG, e.getMessage());
            }
        }

        return null;
    }

    public static JSONArray getJSONArray(JSONObject jsonObject, String key) {
        if (check(jsonObject, key)) {
            try {
                return jsonObject.getJSONArray(key);
            } catch (JSONException e) {
                LOG.e(TAG, e.getMessage());
            }
        }

        return null;
    }

    public static String getString(JSONArray jsonArray, int index) {
        if (check(jsonArray, index)) {
            try {
                return jsonArray.getString(index);
            } catch (JSONException e) {
                LOG.e(TAG, e.getMessage());
            }
        }

        return BLANK;
    }

    public static int getInt(JSONArray jsonArray, int index) {
        if (check(jsonArray, index)) {
            try {
                return jsonArray.getInt(index);
            } catch (JSONException e) {
                LOG.e(TAG, e.getMessage());
            }
        }

        return ZERO;
    }

    public static JSONObject getJSONObject(JSONArray jsonArray, int index) {
        if (check(jsonArray, index)) {
            try {
                return jsonArray.getJSONObject(index);
            } catch (JSONException e) {
                LOG.e(TAG, e.getMessage());
            }
        }

        return null;
    }

    public static void put(JSONObject jsonObject, String key, Object value) {
        try {
            if (jsonObject != null) {
                jsonObject.put(key, value);
            }
        } catch (JSONException e) {
            LOG.e(TAG, e.getMessage());
        }
    }

    public static void put(JSONArray jsonArray, Object value) {
        if (jsonArray != null) {
            jsonArray.put(value);
        }
    }

}
