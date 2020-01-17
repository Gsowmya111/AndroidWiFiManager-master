package kong.qingwei.kqwwifimanagerdemo.adapter;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kongqw.wifilibrary.WiFiManager;

import java.util.ArrayList;
import java.util.List;

import kong.qingwei.kqwwifimanagerdemo.R;

import static com.kongqw.wifilibrary.WiFiManager.gateway_ip1;
import static com.kongqw.wifilibrary.WiFiManager.netwmask1;


public class WifiListAdapter extends BaseAdapter {

    private static final String TAG = "WifiListAdapter";
    private List<ScanResult> scanResults;
    private Context mContext;
    DhcpInfo d;
    public WifiListAdapter(Context context) {
        mContext = context.getApplicationContext();
        this.scanResults = new ArrayList<>();
    }

    public void refreshData(List<ScanResult> scanResults) {
        if (null != scanResults) {
            Log.i(TAG, "refreshData 1 : " + scanResults.size());

            scanResults = WiFiManager.excludeRepetition(scanResults);
            Log.i(TAG, "refreshData 2 : " + scanResults.size());

            this.scanResults.clear();

            this.scanResults.addAll(scanResults);
        }

        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return scanResults.size();
    }

    @Override
    public Object getItem(int position) {
        return scanResults.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {



            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.item_wifi, null);
            holder = new ViewHolder();
            holder.ssid = (TextView) (convertView).findViewById(R.id.ssid);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ScanResult scanResult = scanResults.get(position);
        holder.ssid.setText("ssid：" + scanResult.SSID + "\n：" + WifiManager.calculateSignalLevel(scanResult.level, 5) + "/5\ntype：" + scanResult.capabilities
                +"\n gateway IP:"+gateway_ip1+"\n subnet mask"+netwmask1);
        return convertView;
    }

    private class ViewHolder {
        private TextView ssid;
    }
}
