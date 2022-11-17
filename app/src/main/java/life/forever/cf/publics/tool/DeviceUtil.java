package life.forever.cf.publics.tool;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

import life.forever.cf.activtiy.PlotRead;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;


public class DeviceUtil {

    private static final PlotRead application;

    static {
        application = PlotRead.getApplication();
    }



    //获得独一无二的Psuedo ID
    public static String getUniquePsuedoID() {
        String serial = null;
        String m_szDevIDShort = "35" +  Build.BOARD.length()%10+ Build.BRAND.length()%10 +
                Build.CPU_ABI.length()%10 + Build.DEVICE.length()%10 +
                Build.DISPLAY.length()%10 + Build.HOST.length()%10 +
                Build.ID.length()%10 + Build.MANUFACTURER.length()%10 +
                Build.MODEL.length()%10 + Build.PRODUCT.length()%10 +
                Build.TAGS.length()%10 + Build.TYPE.length()%10 +
                Build.USER.length()%10 ; //13 位
        try {
            serial = android.os.Build.class.getField("SERIAL").get(null).toString();
            //API>=9 使用serial号
            return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
        } catch (Exception exception) {
            serial = "";
            //serial需要一个初始化        serial = "serial"; // 随便一个初始化
        }
        // 使用硬件信息拼凑出来的15位号码
        return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
        // 最终会得到这样的一串ID：00000000-28ee-3eab-ffff-ffffe9374e72
    }

//    public static String getIMEI() {
//
////        android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager) application
////                .getSystemService(Context.TELEPHONY_SERVICE);
//        String device_id = null;
////        if (ActivityCompat.checkSelfPermission(application, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
//////            device_id = tm.getDeviceId();
////            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
////                device_id = tm.getImei();
////            } else {
////                device_id = tm.getDeviceId();
////            }
////        }
//
//        TelephonyManager tm = (TelephonyManager) application.getSystemService(Context.TELEPHONY_SERVICE);
//
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M){
//            // API available only >= 6.0
//            // Get first slot to avoid issue if we have multiple sim cards
//            device_id = tm.getDeviceId(0);
//        }
//        else
//        {
//            device_id = tm.getDeviceId();
//        }
//        if (device_id == null) {
//            device_id = Constant.BLANK;
//        }
//        return device_id;
//    }



    /**
     * 获取设备Mac地址
     *
     * @return
     */
    public static String getMacAddress() {
        String mac = getWifiMac(application);
        if (TextUtils.isEmpty(mac)) {
            mac = getMacFromIp();
            if (TextUtils.isEmpty(mac)) {
                mac = getMacFromNetwork();
            }
        }
        return mac;
    }

    /**
     * 获取Wifi管理器的mac
     *
     * @param context
     * @return
     */
    private static String getWifiMac(Context context) {
        WifiManager wm = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wm == null) {
            return null;
        }
        WifiInfo wi = wm.getConnectionInfo();
        if (wi == null || wi.getMacAddress() == null) {
            return null;
        }
        if ("02:00:00:00:00:00".equals(wi.getMacAddress().trim())) {
            return null;
        } else {
            return wi.getMacAddress().trim();
        }
    }

    /**
     * 通过ip地址获取mac
     *
     * @return
     */
    private static String getMacFromIp() {
        String mac = null;
        InetAddress ip = getIp();
        if (ip != null) {
            try {
                byte[] b = NetworkInterface.getByInetAddress(ip).getHardwareAddress();
                StringBuffer buffer = new StringBuffer();
                for (int i = 0; b != null && i < b.length; i++) {
                    if (i != 0) {
                        buffer.append(':');
                    }
                    String str = Integer.toHexString(b[i] & 0xFF);
                    buffer.append(str.length() == 1 ? 0 + str : str);
                }
                mac = buffer.toString().toUpperCase();
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }
        return mac;
    }

    /**
     * 获取Ip
     *
     * @return
     */
    private static InetAddress getIp() {
        InetAddress ip = null;
        try {
            Enumeration<NetworkInterface> en_netInterface = NetworkInterface.getNetworkInterfaces();
            while (en_netInterface != null && en_netInterface.hasMoreElements()) {
                NetworkInterface ni = en_netInterface.nextElement();
                Enumeration<InetAddress> en_ip = ni.getInetAddresses();
                while (en_ip.hasMoreElements()) {
                    ip = en_ip.nextElement();
                    if (!ip.isLoopbackAddress() && !ip.getHostAddress().contains(":")) {
                        break;
                    } else {
                        ip = null;
                    }
                }
                if (ip != null) {
                    break;
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return ip;
    }

    /**
     * 通过网络接口获取mac
     *
     * @return
     */
    private static String getMacFromNetwork() {
        String mac = "02:00:00:00:00:0";
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if ("wlan0".equalsIgnoreCase(nif.getName())) {
                    byte[] macBytes = nif.getHardwareAddress();
                    if (macBytes != null) {
                        StringBuilder strBuilder = new StringBuilder();
                        for (byte b : macBytes) {
                            strBuilder.append(String.format("%02X:", b));
                        }

                        if (strBuilder.length() > 0) {
                            strBuilder.deleteCharAt(strBuilder.length() - 1);
                        }
                        mac = strBuilder.toString();
                    }
                    break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return mac;
    }

    /**
     * 获取机型
     */
    public String getPhoneModel() {
        String brand = android.os.Build.BRAND;//手机品牌
        String model = android.os.Build.MODEL;//手机型号
        return brand + " " + model;
    }

    /**
     * 获取机型
     */
    public static String getAndroidID() {
        String android_id =  Settings.System.getString(application.getContentResolver(), Settings.System.ANDROID_ID);
        if (TextUtils.isEmpty(android_id)) {
            String imei = DeviceUtil.getUniquePsuedoID();
            String mac = DeviceUtil.getMacAddress();
            return imei + "***" + mac;
        }else {
            return  android_id;
        }
    }

}
