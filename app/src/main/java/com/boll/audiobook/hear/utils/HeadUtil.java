package com.boll.audiobook.hear.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.Display;
import android.view.WindowManager;

import androidx.core.app.ActivityCompat;

import com.boll.audiobook.hear.BuildConfig;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;

/**
 * 请求头数据
 * created by zoro at 2023/5/8
 */
public class HeadUtil {
    /**
     * 未知
     */
    public static final int NETWORK_UNKNOW = -2;
    /**
     * 没有连接网络
     */
    private static final int NETWORK_NONE = -1;
    /**
     * 2G网络
     */
    public static final int NETWORK_2G = 0;
    /**
     * 3G网络
     */
    public static final int NETWORK_3G = 1;
    /**
     * 4G网络
     */
    public static final int NETWORK_4G = 2;
    /**
     * 无线网络
     */
    private static final int NETWORK_WIFI = 3;
    /**
     * 定义电话管理器对象
     */
    public static TelephonyManager mTelephonyManager;
    /**
     * 定义连接管理器对象
     */
    public static ConnectivityManager mConnectivityManager;
    /**
     * 定义网络信息对象
     */
    public static NetworkInfo mNetworkInfo;

    /**
     * 获取App版本
     *
     * @return
     */
    public static String getAppVersion() {
        return BuildConfig.VERSION_NAME;
    }

    /**
     * 获取安卓版本
     *
     * @return
     */
    public static String getAndroidVersion() {
        return "android" + android.os.Build.VERSION.RELEASE;
    }

    public static String getMacAddress() {
        try {
            List<NetworkInterface> networkInterfaceList = Collections.list(NetworkInterface.getNetworkInterfaces());
            String stringMac = "";
            for (NetworkInterface networkInterface : networkInterfaceList) {
                if (networkInterface.getName().equalsIgnoreCase("wlon0")) ;
                {
                    for (int i = 0; i < networkInterface.getHardwareAddress().length; i++) {
                        String stringMacByte = Integer.toHexString(networkInterface.getHardwareAddress()[i] & 0xFF);

                        if (stringMacByte.length() == 1) {
                            stringMacByte = "0" + stringMacByte;
                        }
                        stringMac = stringMac + stringMacByte.toUpperCase() + ":";
                    }
                    break;
                }
            }
            if (stringMac.endsWith(":")) {
                stringMac = stringMac.substring(0, stringMac.lastIndexOf(":"));
            }
            return stringMac;
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "0";
    }

    /**
     * 根据IP地址获取MAC地址
     *
     * @return
     */
    public static String getLocalMacAddressFromIp() {
        String strMacAddress = null;
        try {
            //获得Ip地址
            InetAddress ip = getLocalInetAddress();
            byte[] b = NetworkInterface.getByInetAddress(ip).getHardwareAddress();
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < b.length; i++) {
                if (i != 0) {
                    buffer.append(':');
                }
                String str = Integer.toHexString(b[i] & 0xFF);
                buffer.append(str.length() == 1 ? 0 + str : str);
            }
            strMacAddress = buffer.toString().toUpperCase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strMacAddress == null ? "mac" : strMacAddress;
    }

    /**
     * 获取移动设备本地IP
     *
     * @return
     */
    private static InetAddress getLocalInetAddress() {
        InetAddress ip = null;
        try {
            //列举
            Enumeration<NetworkInterface> en_netInterface = NetworkInterface.getNetworkInterfaces();
            while (en_netInterface.hasMoreElements()) {//是否还有元素
                NetworkInterface ni = en_netInterface.nextElement();//得到下一个元素
                Enumeration<InetAddress> en_ip = ni.getInetAddresses();//得到一个ip地址的列举
                while (en_ip.hasMoreElements()) {
                    ip = en_ip.nextElement();
                    if (!ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1)
                        break;
                    else
                        ip = null;
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
     * 获取屏幕宽度
     *
     * @param activity
     * @return
     */
    public static String getScreenWidth(Activity activity) {
        WindowManager windowManager = activity.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        int screenWidth = display.getWidth();
        return String.valueOf(screenWidth);
    }

    /**
     * 获取屏幕高度
     *
     * @param activity
     * @return
     */
    public static String getScreenHeight(Activity activity) {
        WindowManager windowManager = activity.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        int screenHeight = display.getHeight();
        return String.valueOf(screenHeight);
    }

    /**
     * 生成唯一UUID
     *
     * @return
     */
    public static String getUniqueUUID() {
        String serial = null;
        String m_szDevIDShort = "35" + Build.BOARD.length() % 10 + Build.BRAND.length() % 10
                + Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10
                + Build.DISPLAY.length() % 10 + Build.HOST.length() % 10
                + Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10
                + Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10
                + Build.TAGS.length() % 10 + Build.TYPE.length() % 10
                + Build.USER.length() % 10;
        try {
//            serial = android.os.Build.class.getField("SERIAL").get(null).toString(); //API>=9 使用serial号
            serial = HeadUtil.getSerialNumber();
            return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
        } catch (Exception exception) {
            //serial需要一个初始化
            serial = "serial";
        }
        return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
    }

    /**
     * 获取网络类型
     *
     * @param context
     * @return 网络类型
     */
    public static int getNetWorkType(Context context) {
        // 得到连接管理器对象
        mConnectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        if (mNetworkInfo != null && mNetworkInfo.isConnected()) {
            if (mNetworkInfo.getType() == (ConnectivityManager.TYPE_WIFI)) {
                return NETWORK_WIFI;
            } else if (mNetworkInfo.getType() == (ConnectivityManager.TYPE_MOBILE)) {
                return getMobileNetType(context);
            }
        }
        return NETWORK_NONE;
    }

    /**
     * 获取移动网络的类型
     *
     * @param context
     * @return 移动网络类型
     */
    public static final int getMobileNetType(Context context) {
        mTelephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            return 3;
        }
        int networkType = mTelephonyManager.getNetworkType();
        return getNetworkClass(networkType);
    }

    /**
     * 判断移动网络的类型
     *
     * @param networkType
     * @return 移动网络类型
     */
    private static final int getNetworkClass(int networkType) {
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return NETWORK_2G;
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return NETWORK_3G;
            case TelephonyManager.NETWORK_TYPE_LTE:
                return NETWORK_4G;
            default:
                return NETWORK_UNKNOW;
        }
    }

    /**
     * 获取IMEI或DeviceId
     *
     * @param context
     * @return
     */
    public static String getIMEIDeviceId(Context context) {
        String deviceId;
        //当APK运行在Android10（API>=29）及以上时，获取到的是AndroidID
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        } else {
            final TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            //当APK运行在Android6.0（API>=23）及以上时，需要check有无READ_PHONE_STATE权限。
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (context.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    return "";
                }
            }
            assert mTelephony != null;
            //如果TelephonyManager获取到的DeviceId不为null
            if (mTelephony.getDeviceId() != null) {
                //获取GSM手机的国际移动设备识别码（IMEI）或者 CDMA手机的移动设备识别码（MEID).
                deviceId = mTelephony.getDeviceId();
            } else {
                //如果DeviceId为null，我们的DID依然是AndroidID。
                deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            }
        }
        return deviceId;
    }

    /**
     * 如果getIMEIDeviceId获取不到则获取序列号
     *
     * @return
     */
    public static String getSerialNumber() {
        String serial = null;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            serial = (String) get.invoke(c, "ro.serialno");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return serial;
    }

}
