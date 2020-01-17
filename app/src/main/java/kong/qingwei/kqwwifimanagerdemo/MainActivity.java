package kong.qingwei.kqwwifimanagerdemo;

import android.Manifest;
import android.content.DialogInterface;
import android.net.DhcpInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.kongqw.permissionslibrary.PermissionsManager;
import com.kongqw.wifilibrary.WiFiManager;
import com.kongqw.wifilibrary.listener.OnWifiConnectListener;
import com.kongqw.wifilibrary.listener.OnWifiEnabledListener;
import com.kongqw.wifilibrary.listener.OnWifiScanResultsListener;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import kong.qingwei.kqwwifimanagerdemo.adapter.WifiListAdapter;
import kong.qingwei.kqwwifimanagerdemo.view.ConnectWifiDialog;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, OnWifiScanResultsListener, OnWifiConnectListener, OnWifiEnabledListener {

    private static final String TAG = "MainActivity";

    private ListView mWifiList;
    private SwipeRefreshLayout mSwipeLayout;
    private PermissionsManager mPermissionsManager;


    static final String[] PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    private final int GET_WIFI_LIST_REQUEST_CODE = 0;
    private WiFiManager mWiFiManager;
    private WifiListAdapter mWifiListAdapter;
    private SwitchCompat switchCompat;
    private FrameLayout frameLayout;
    public static String password;
    public static String net_ssid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initView();

        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mWiFiManager.openWiFi();
                } else {
                    mWiFiManager.closeWiFi();
                }
            }
        });

        mSwipeLayout.setOnRefreshListener(this);

        mWifiList.setEmptyView(findViewById(R.id.empty_view));
        mWifiListAdapter = new WifiListAdapter(getApplicationContext());
        mWifiList.setAdapter(mWifiListAdapter);
        mWifiList.setOnItemClickListener(this);
        mWifiList.setOnItemLongClickListener(this);

        mWiFiManager = WiFiManager.getInstance(getApplicationContext());

        mPermissionsManager = new PermissionsManager(this) {
            @Override
            public void authorized(int requestCode) {
                // 6.0
                if (GET_WIFI_LIST_REQUEST_CODE == requestCode) {

                    List<ScanResult> scanResults = mWiFiManager.getScanResults();
                    refreshData(scanResults);
                }
            }

            @Override
            public void noAuthorization(int requestCode, String[] lacksPermissions) {
                // 6.0
            }

            @Override
            public void ignore() {
                // 6.0
                List<ScanResult> scanResults = mWiFiManager.getScanResults();
                refreshData(scanResults);
            }
        };
        mPermissionsManager.checkPermissions(GET_WIFI_LIST_REQUEST_CODE, PERMISSIONS);
    }


    private void initView() {

        switchCompat = (SwitchCompat) findViewById(R.id.switch_wifi);

        frameLayout = (FrameLayout) findViewById(R.id.fl_wifi);

        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);

        mWifiList = (ListView) findViewById(R.id.wifi_list);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWiFiManager.setOnWifiEnabledListener(this);
        mWiFiManager.setOnWifiScanResultsListener(this);
        mWiFiManager.setOnWifiConnectListener(this);
        switchCompat.setChecked(mWiFiManager.isWifiEnabled());
    }

    @Override
    protected void onPause() {
        super.onPause();

        mWiFiManager.removeOnWifiEnabledListener();
        mWiFiManager.removeOnWifiScanResultsListener();
        mWiFiManager.removeOnWifiConnectListener();
    }

    public void refreshData(List<ScanResult> scanResults) {
        mSwipeLayout.setRefreshing(false);

        mWifiListAdapter.refreshData(scanResults);

        Snackbar.make(mWifiList, "WIFI refreshed", Snackbar.LENGTH_SHORT).show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mPermissionsManager.recheckPermissions(requestCode, permissions, grantResults);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final ScanResult scanResult = (ScanResult) mWifiListAdapter.getItem(position);
        switch (mWiFiManager.getSecurityMode(scanResult)) {
            case WPA:
            case WPA2:
                ResourceLock lock = new ResourceLock();
                ThreadA a=new ThreadA(lock);
                ThreadB b=new ThreadB(lock);
                a.start();
                b.start();

              /*  Thread t1 = new Thread(new Runnable() {
                    public void run() {
                        scanResult.SSID="GW_NoAmp";
                        mWiFiManager.connectWPA2Network(scanResult.SSID, "namp@a20");
                       // scanResult.SSID="NoInt";
                        Log.d("TAG","timer...noamp loop...");
                    }
                });

                t1.start();*/

                /*if(scanResult.SSID.equals("NoInt")) {
                    mWiFiManager.connectWPA2Network(scanResult.SSID, "noint@a20");
                    scanResult.SSID = "GW_NoAmp";
                    Log.d("TAG", "starting ...noint ...");
                }
                    Timer _Request_Trip_Timer = new Timer();
                    _Request_Trip_Timer.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {



                        }
                    }, 100, 50000);
*/


                /*if(scanResult.SSID.equals("GW_NoAmp")){
                mWiFiManager.connectWPA2Network(scanResult.SSID, "namp@a20");
            }*/


//-----------before code commented by me start
               /* new ConnectWifiDialog(this) {

                    @Override
                    public void connect(String password) {


                        mWiFiManager.connectWPA2Network(scanResult.SSID, password);
                    }
                }.setSsid(scanResult.SSID).show();*/   //-----------end
                break;
            case WEP:
                new ConnectWifiDialog(this) {

                    @Override
                    public void connect(String password) {
                        mWiFiManager.connectWEPNetwork(scanResult.SSID, password);
                    }
                }.setSsid(scanResult.SSID).show();
                break;
            case OPEN:
                mWiFiManager.connectOpenNetwork(scanResult.SSID);
                break;
        }
    }


    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        ScanResult scanResult = (ScanResult) mWifiListAdapter.getItem(position);
        final String ssid = scanResult.SSID;
        new AlertDialog.Builder(this)
                .setTitle(ssid)
                .setItems(new String[]{""}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0: //
                                WifiInfo connectionInfo = mWiFiManager.getConnectionInfo();
                                Log.i(TAG, "onClick: connectionInfo :" + connectionInfo.getSSID());
                                if (mWiFiManager.addDoubleQuotation(ssid).equals(connectionInfo.getSSID())) {
                                    mWiFiManager.disconnectWifi(connectionInfo.getNetworkId());
                                } else {
                                    Toast.makeText(getApplicationContext(), " [ " + ssid + " ]", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            case 1: //
                                WifiConfiguration wifiConfiguration = mWiFiManager.getConfigFromConfiguredNetworksBySsid(ssid);
                                if (null != wifiConfiguration) {
                                    boolean isDelete = mWiFiManager.deleteConfig(wifiConfiguration.networkId);
                                    Toast.makeText(getApplicationContext(), isDelete ? "！" : "！", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "！", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            default:
                                break;
                        }
                    }
                })
                .show();
        return true;
    }



    @Override
    public void onRefresh() {

        mWiFiManager.startScan();
    }


    @Override
    public void onScanResults(List<ScanResult> scanResults) {
        refreshData(scanResults);
    }


    @Override
    public void onWiFiConnectLog(String log) {
        Log.i(TAG, "onWiFiConnectLog: " + log);
        Snackbar.make(mWifiList, "WIFI : " + log, Snackbar.LENGTH_SHORT).show();
    }


    @Override
    public void onWiFiConnectSuccess(String SSID) {
        Log.i(TAG, "onWiFiConnectSuccess:  [ " + SSID + " ] ");
        Toast.makeText(getApplicationContext(), SSID + "success to connect ", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onWiFiConnectFailure(String SSID) {
        Log.i(TAG, "onWiFiConnectFailure:  [ " + SSID + " ] ");
      //  mWiFiManager.connectWEPNetwork(SSID, password);
        Toast.makeText(getApplicationContext(), SSID + "  failed to connect！", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onWifiEnabled(boolean enabled) {
        switchCompat.setChecked(enabled);
        frameLayout.setVisibility(enabled ? View.VISIBLE : View.GONE);
    }
    public class ResourceLock{
        public volatile int flag = 1;
    }


    public class ThreadA extends Thread{

        ResourceLock lock;

        ThreadA(ResourceLock lock){
            this.lock = lock;
        }

        @Override
        public void run() {

            try{
                synchronized (lock) {

                    for (int i = 0; i < 100; i++) {

                        while(lock.flag!=1){
                            lock.wait();
                        }
                      password="noint@a20";
                        net_ssid="NoInt";
                        Log.d("TAG","Thread A..");
                     //   scanResult.SSID="NoInt";  password="noint@a20";
                        mWiFiManager.connectWPA2Network(net_ssid, password);

                        Thread.sleep(20000);
                        lock.flag = 2;
                        lock.notifyAll();
                    }

                }
            }catch (Exception e) {
                System.out.println("Exception 1 :"+e.getMessage());
            }

        }

    }


    public class ThreadB extends Thread{

        ResourceLock lock;

        ThreadB (ResourceLock lock){
            this.lock = lock;
        }

        @Override
        public void run() {

            try{
                synchronized (lock) {

                    for (int i = 0; i < 100; i++) {

                        while(lock.flag!=2){
                            lock.wait();
                        }
                        password="namp@a20";
                        net_ssid="GW_NoAmp";
                        Log.d("TAG","Thread B..");
                        //   scanResult.SSID="NoInt";  password="noint@a20";
                        mWiFiManager.connectWPA2Network(net_ssid, password);
                        Thread.sleep(20000);
                        lock.flag = 1;
                        lock.notifyAll();
                    }

                }
            }catch (Exception e) {
                System.out.println("Exception 3 :"+e.getMessage());
            }

        }
    }
}
