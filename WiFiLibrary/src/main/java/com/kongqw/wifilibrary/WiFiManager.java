package com.kongqw.wifilibrary;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.format.Formatter;
import android.util.Log;

import com.kongqw.wifilibrary.listener.OnWifiConnectListener;
import com.kongqw.wifilibrary.listener.OnWifiEnabledListener;
import com.kongqw.wifilibrary.listener.OnWifiScanResultsListener;

import java.util.List;


/**
 * Created by kqw on 2016/8/2.
 * WifiManager : https://developer.android.com/reference/android/net/wifi/WifiManager.html
 * WifiConfiguration : https://developer.android.com/reference/android/net/wifi/WifiConfiguration.html
 * ScanResult : https://developer.android.com/reference/android/net/wifi/ScanResult.html
 * WifiInfo : https://developer.android.com/reference/android/net/wifi/WifiInfo.html
 * Wifi管理
 */
public class WiFiManager extends BaseWiFiManager {

    private static final String TAG = "WiFiManager";
    private static WiFiManager mWiFiManager;
    private static CallBackHandler mCallBackHandler = new CallBackHandler();
    private static final int WIFI_STATE_ENABLED = 0;
    private static final int WIFI_STATE_DISABLED = 1;
    private static final int SCAN_RESULTS_UPDATED = 3;
    private static final int WIFI_CONNECT_LOG = 4;
    private static final int WIFI_CONNECT_SUCCESS = 5;
    private static final int WIFI_CONNECT_FAILURE = 6;
   public static int netwmask; public static int gateway_ip;
    public static String netwmask1; public static String gateway_ip1;
    private WiFiManager(Context context) {
        super(context);
    }

    public static WiFiManager getInstance(Context context) {
        if (null == mWiFiManager) {
            synchronized (WiFiManager.class) {
                if (null == mWiFiManager) {
                    mWiFiManager = new WiFiManager(context);
                }
            }
        }
        return mWiFiManager;
    }


    public void openWiFi() {
        if (!isWifiEnabled() && null != mWifiManager) {
            mWifiManager.setWifiEnabled(true);
        }
    }


    public void closeWiFi() {
        if (isWifiEnabled() && null != mWifiManager) {
            mWifiManager.setWifiEnabled(false);
        }
    }


    public boolean connectOpenNetwork(@NonNull String ssid) {

        int networkId = setOpenNetwork(ssid);
        if (-1 != networkId) {

            boolean isSave = saveConfiguration();

            boolean isEnable = enableNetwork(networkId);

            return isSave && isEnable;
        }
        return false;
    }

    public boolean connectWEPNetwork(@NonNull String ssid, @NonNull String password) {

        int networkId = setWEPNetwork(ssid, password);
        if (-1 != networkId) {

            boolean isSave = saveConfiguration();

            boolean isEnable = enableNetwork(networkId);

            return isSave && isEnable;
        }
        return false;
    }


    public boolean connectWPA2Network(@NonNull String ssid, @NonNull String password) {

        int networkId = setWPA2Network(ssid, password);
       // if (-1 != networkId) {

            boolean isSave = saveConfiguration();

            boolean isEnable = enableNetwork(networkId);

            return isSave && isEnable;
       // }
       /// return false;
    }

    /* *******************************************************************************************/



    public static class NetworkBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            DhcpInfo d = wifiManager.getDhcpInfo();


         netwmask=d.netmask;
            netwmask1 = Formatter.formatIpAddress(netwmask);
         gateway_ip = d.gateway;
            gateway_ip1 = Formatter.formatIpAddress(gateway_ip);
            switch (intent.getAction()) {
                case WifiManager.WIFI_STATE_CHANGED_ACTION:
                    switch (intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN)) {
                        case WifiManager.WIFI_STATE_ENABLING:
                            Log.i(TAG, "onReceive: ...");
                            break;
                        case WifiManager.WIFI_STATE_ENABLED:
                            Log.i(TAG, "onReceive: WIFI ");
                            mCallBackHandler.sendEmptyMessage(WIFI_STATE_ENABLED);
                            break;
                        case WifiManager.WIFI_STATE_DISABLING:
                            Log.i(TAG, "onReceive: ");
                            break;
                        case WifiManager.WIFI_STATE_DISABLED:
                            Log.i(TAG, "onReceive:");
                            mCallBackHandler.sendEmptyMessage(WIFI_STATE_DISABLED);
                            break;
                        case WifiManager.WIFI_STATE_UNKNOWN:
                        default:
                            Log.i(TAG, "onReceive: WIFI !");
                            break;
                    }
                    break;
                case WifiManager.SCAN_RESULTS_AVAILABLE_ACTION: // WIFI扫描完成
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        boolean isUpdated = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false);
                        Log.i(TAG, "onReceive:   " + (isUpdated ? "" : ""));
                    } else {
                        Log.i(TAG, "onReceive: ");
                    }

                    Message scanResultsMessage = Message.obtain();
                    scanResultsMessage.what = SCAN_RESULTS_UPDATED;
                    scanResultsMessage.obj = wifiManager.getScanResults();
                    mCallBackHandler.sendMessage(scanResultsMessage);
                    break;
                case WifiManager.NETWORK_STATE_CHANGED_ACTION: // WIFI连接状态发生改变
//                    Log.i(TAG, "onReceive: WIFI连接状态发生改变");
//                    NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
//                    if (null != networkInfo && ConnectivityManager.TYPE_WIFI == networkInfo.getType()) {
//                    }
                    WifiInfo wifiInfo = intent.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);
                    if (null != wifiInfo && wifiInfo.getSupplicantState() == SupplicantState.COMPLETED) {
                        String ssid = wifiInfo.getSSID();
                        Log.i(TAG, "onReceive:  ssid = " + ssid);
                    }
                    break;
                case WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION:
                    boolean isConnected = intent.getBooleanExtra(WifiManager.EXTRA_SUPPLICANT_CONNECTED, false);
                    Log.i(TAG, "onReceive: SUPPLICANT_CONNECTION_CHANGE_ACTION  isConnected = " + isConnected);
                    break;
                case WifiManager.SUPPLICANT_STATE_CHANGED_ACTION:

                    SupplicantState supplicantState = intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);

                    Message logMessage = Message.obtain();
                    logMessage.what = WIFI_CONNECT_LOG;
                    logMessage.obj = supplicantState.toString();
                    logMessage.obj = supplicantState.toString();
                    mCallBackHandler.sendMessage(logMessage);

                    switch (supplicantState) {
                        case INTERFACE_DISABLED:
                            Log.i(TAG, "onReceive: INTERFACE_DISABLED ");
                            break;
                        case DISCONNECTED:
//                            Log.i(TAG, "onReceive: DISCONNECTED:// 断开连接");
//                            break;
                        case INACTIVE:
                            WifiInfo connectFailureInfo = wifiManager.getConnectionInfo();
                            Log.i(TAG, "onReceive: INACTIVE   connectFailureInfo = " + connectFailureInfo);
                            if (null != connectFailureInfo) {
                                Message wifiConnectFailureMessage = Message.obtain();
                                wifiConnectFailureMessage.what = WIFI_CONNECT_FAILURE;
                                wifiConnectFailureMessage.obj = connectFailureInfo.getSSID();
                                mCallBackHandler.sendMessage(wifiConnectFailureMessage);

                                int networkId = connectFailureInfo.getNetworkId();
                                boolean isDisable = wifiManager.disableNetwork(networkId);
                                boolean isDisconnect = wifiManager.disconnect();
                                Log.i(TAG, "onReceive:   =  " + (isDisable && isDisconnect));
                            }
                            break;
                        case SCANNING:
                            Log.i(TAG, "onReceive: SCANNING ");
                            break;
                        case AUTHENTICATING:
                            Log.i(TAG, "onReceive: AUTHENTICATING: ");
                            break;
                        case ASSOCIATING:
                            Log.i(TAG, "onReceive: ASSOCIATING: ");
                            break;
                        case ASSOCIATED:
                            Log.i(TAG, "onReceive: ASSOCIATED: ");
                            break;
                        case FOUR_WAY_HANDSHAKE:
                            Log.i(TAG, "onReceive: FOUR_WAY_HANDSHAKE:");
                            break;
                        case GROUP_HANDSHAKE:
                            Log.i(TAG, "onReceive: GROUP_HANDSHAKE:");
                            break;
                        case COMPLETED:
                            Log.i(TAG, "onReceive: WIFI_CONNECT_SUCCESS: ");
                            WifiInfo connectSuccessInfo = wifiManager.getConnectionInfo();
                            if (null != connectSuccessInfo) {
                                Message wifiConnectSuccessMessage = Message.obtain();
                                wifiConnectSuccessMessage.what = WIFI_CONNECT_SUCCESS;
                                wifiConnectSuccessMessage.obj = connectSuccessInfo.getSSID();
                                mCallBackHandler.sendMessage(wifiConnectSuccessMessage);
                            }
                            break;
                        case DORMANT:
                            Log.i(TAG, "onReceive: DORMANT:");
                            break;
                        case UNINITIALIZED:
                            Log.i(TAG, "onReceive: UNINITIALIZED: ");
                            break;
                        case INVALID:
                            Log.i(TAG, "onReceive: INVALID:");
                            break;
                        default:
                            break;
                    }
                    break;
                default:
                    break;
            }
        }
    }


    private static class CallBackHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case WIFI_STATE_ENABLED:
                    if (null != mOnWifiEnabledListener) {
                        mOnWifiEnabledListener.onWifiEnabled(true);
                    }
                    break;
                case WIFI_STATE_DISABLED:
                    if (null != mOnWifiEnabledListener) {
                        mOnWifiEnabledListener.onWifiEnabled(false);
                    }
                    break;
                case SCAN_RESULTS_UPDATED:
                    if (null != mOnWifiScanResultsListener) {
                        @SuppressWarnings("unchecked")
                        List<ScanResult> scanResults = (List<ScanResult>) msg.obj;
                        mOnWifiScanResultsListener.onScanResults(scanResults);
                    }
                    break;
                case WIFI_CONNECT_LOG:
                    if (null != mOnWifiConnectListener) {
                        String log = (String) msg.obj;
                        mOnWifiConnectListener.onWiFiConnectLog(log);
                    }
                    break;
                case WIFI_CONNECT_SUCCESS:
                    if (null != mOnWifiConnectListener) {
                        String ssid = (String) msg.obj;
                        Log.d("TAG","WIFI CONNECT SUCCESS ");
                        mOnWifiConnectListener.onWiFiConnectSuccess(ssid);
                    }
                    break;
                case WIFI_CONNECT_FAILURE:
                    if (null != mOnWifiConnectListener) {
                        Log.d("TAG","WIFI CONNECT FAILURE ");
                        String ssid = (String) msg.obj;
                        mOnWifiConnectListener.onWiFiConnectFailure(ssid);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private static OnWifiEnabledListener mOnWifiEnabledListener;

    private static OnWifiScanResultsListener mOnWifiScanResultsListener;

    private static OnWifiConnectListener mOnWifiConnectListener;

    public void setOnWifiEnabledListener(OnWifiEnabledListener listener) {
        mOnWifiEnabledListener = listener;
    }

    public void removeOnWifiEnabledListener() {
        mOnWifiEnabledListener = null;
    }

    public void setOnWifiScanResultsListener(OnWifiScanResultsListener listener) {
        mOnWifiScanResultsListener = listener;
    }

    public void removeOnWifiScanResultsListener() {
        mOnWifiScanResultsListener = null;
    }

    public void setOnWifiConnectListener(OnWifiConnectListener listener) {
        mOnWifiConnectListener = listener;
    }

    public void removeOnWifiConnectListener() {
        mOnWifiConnectListener = null;
    }
}
