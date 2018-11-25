package com.example.fares.bleproj;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class ScanActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    BluetoothAdapter mBluetoothAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    DeviceListAdapter deviceListAdapter;
    ListView lv;
    private ArrayList<BluetoothDevice> items = new ArrayList<>();

    private final BroadcastReceiver mBroadcastReceiver3 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                items.add(device);
                Log.i("BT", device.getName() + "\n" + device.getAddress());
                lv.setAdapter(deviceListAdapter);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        swipeRefreshLayout = findViewById(R.id.swiperefresh);
        lv = (ListView) findViewById(R.id.listView);
        deviceListAdapter = new DeviceListAdapter(getApplicationContext(),R.layout.activity_scan, items);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter.startDiscovery();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mBroadcastReceiver3, filter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Log.d(TAG, "onItemClick: You Clicked on a device.");
                String deviceName = items.get(position).getName();
                String deviceAddress = items.get(position).getAddress();

                Log.d(TAG, "onItemClick: deviceName = " + deviceName);
                Log.d(TAG, "onItemClick: deviceAddress = " + deviceAddress);

                //create the bond.
                //NOTE: Requires API 17+? I think this is JellyBean
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    Log.d(TAG, "Trying to pair with " + deviceName);
                    items.get(position).createBond();
                    Toast.makeText(getApplicationContext(), deviceName + " est Associer", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent();
                    intent.putExtra( "DEVICE",items.get(position));
                    setResult(2,intent);
                    finish();//finishing activity
                }
            }
        });

    }

}
