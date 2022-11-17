package life.forever.cf.internet;

import android.os.Build;
import android.text.TextUtils;

import life.forever.cf.activtiy.PlotRead;
import life.forever.cf.interfaces.InterFace;
import life.forever.cf.publics.Constant;
import life.forever.cf.publics.tool.AndroidManifestUtil;
import life.forever.cf.publics.tool.ComYou;
import life.forever.cf.publics.tool.DeviceUtil;
import life.forever.cf.publics.tool.SharedPreferencesUtil;
import life.forever.cf.activtiy.Bus;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReaderApiParamsHelper {

    private static Hashtable<Integer, int[]> hash = new Hashtable<>();

    static {
        hash.put(0, new int[]{0, 5, 9, 15, 22, 28});
        hash.put(1, new int[]{2, 8, 19, 25, 30, 31});
        hash.put(2, new int[]{20, 25, 31, 3, 4, 8});
        hash.put(3, new int[]{25, 31, 0, 9, 13, 17});
        hash.put(4, new int[]{29, 2, 11, 17, 21, 26});
        hash.put(5, new int[]{10, 15, 18, 29, 2, 3});
        hash.put(6, new int[]{5, 10, 15, 17, 18, 22});
        hash.put(7, new int[]{8, 20, 22, 27, 19, 21});
    }

    public static String hashToken(String token) {
        StringBuilder result = new StringBuilder();
        if (token.length() >= 9) {
            String mToken = token.charAt(2) + "" + token.charAt(5) + "" + token.charAt(8);
            int mInt = Integer.parseInt(mToken, 16);
            int index = mInt % 8;
            int[] array = hash.get(index);
            if (array == null) {
                result.append(token);
            } else {
                for (int i : array) {
                    result.append(token.charAt(i));
                }
            }
        } else {
            if (TextUtils.isEmpty(token)) {
                token = "bycw2018";
            }
            result.append(token);
        }
        return result.toString();
    }


    static String getTockenStr(String tokenStr){
        return hashToken(tokenStr);
    }


    public static String MD5(String source) {
        String rst = source;
        try {
            byte[] result = MessageDigest.getInstance("MD5").digest(source.getBytes());
            StringBuilder hex = new StringBuilder();
            for (byte b : result) {
                hex.append(String.format("%02X", b));
            }
            rst = hex.toString().toLowerCase();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return rst;
    }






    static HashMap<String, String> getParamsMap(String path, JSONObject paramObject, boolean userFlag)
    {

        String paramsStr = "";
        if(paramObject != null)
        {
            paramsStr = paramObject.toString();

        }
        paramsStr = Bus.Base64Encode(paramsStr);

        Pattern p = Pattern.compile("\\s*|\t|\r|\n");
        Matcher m = p.matcher(paramsStr);
        paramsStr = m.replaceAll("");


        HashMap<String, String> map = new LinkedHashMap<>();
        String apiVersion = AndroidManifestUtil.getApiVersion();
        String versionName = AndroidManifestUtil.getVersionName();
        String sdkVersion = Build.VERSION.RELEASE;
        String channel = AndroidManifestUtil.getChannel();


        String token = hashToken(PlotRead.getAppUser().token);
        int tokenTime = PlotRead.getAppUser().tokenTime;

        // 获取时间戳
        int time = ComYou.currentTimeSeconds();
        // 获取版本号
        String version = apiVersion + "_" + 3 + "_" + versionName + "_" + sdkVersion + "_"
                + tokenTime + "_" + channel + "_" + Constant.APP_CODE;
        // 获取用户id
        int uid = PlotRead.getAppUser().uid;
        // 获取设备号
        String deviceId = DeviceUtil.getAndroidID();
        // 获取签名
        String signature = path + time + uid + paramsStr + token;
        signature = MD5(signature);
        // 获取用户性别
        int sex = SharedPreferencesUtil.getInt(PlotRead.getConfig(), Constant.SEX);

        map.put("time", String.valueOf(time));
        map.put("version", version);
        map.put("uid", String.valueOf(uid));
        map.put("deviceid", deviceId);
        // TODO:1.8.1 2021/9/30 1.8.1测试服验证
        if(path.equals(InterFace.TEST_APP_SIGN))
        {
            map.put("param", paramsStr);
        }else{
            map.put("param", paramsStr);
        }

        map.put("signature", signature);
        map.put("sex", String.valueOf(sex));
        map.put("umid", MD5(deviceId));

        return map;

    }

}
