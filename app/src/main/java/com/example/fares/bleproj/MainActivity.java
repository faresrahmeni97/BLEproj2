package com.example.fares.bleproj;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    public static final String EXTRA_ADDRESS = "fares";
    public static final String EXTRA_NOM = "ayme";
    private static  final Integer RES = 1;
    BluetoothAdapter mBluetoothAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    DeviceListAdapter deviceListAdapter;
    private ListView pairedListView;
    private ArrayList<BluetoothDevice> paireditems = new ArrayList<>();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RES){

        deviceListAdapter.notifyDataSetChanged();
        }
    }

//----------------------------------------receiver---------

    private final BroadcastReceiver mPairedReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
                paireditems.clear();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
                for(BluetoothDevice bt : pairedDevices)
                paireditems.add(bt);
                Log.i("BT", device.getName() + "\n" + device.getAddress());
                pairedListView.setAdapter(deviceListAdapter);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        deviceListAdapter = new DeviceListAdapter(getApplicationContext(),
                R.layout.device_adapter_view, paireditems);
        loadPairedDevices();
        //-----------------Refresh--------------------------
        swipeRefreshLayout = findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadPairedDevices();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        //----------------------------------------------------
        Button btAdd=(Button)findViewById(R.id.btAdd);
        btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent i = new Intent(MainActivity.this,ScanActivity.class);
               // startActivityForResult(i,RES);
                Intent intentOpenBluetoothSettings = new Intent();
                intentOpenBluetoothSettings.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
                startActivityForResult(intentOpenBluetoothSettings,RES);

            }
        });
        pairedListView = findViewById(R.id.pairedevice);



    }


public void loadPairedDevices(){
    pairedListView =  findViewById(R.id.pairedevice);
    mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    mBluetoothAdapter.startDiscovery();

    IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
    registerReceiver(mPairedReceiver, filter);
    pairedListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String address = paireditems.get(position).getAddress();
            String name = paireditems.get(position).getName();
            Intent i = new Intent(MainActivity.this, DeviceActivity.class);
            i.putExtra(EXTRA_ADDRESS, address);
            i.putExtra(EXTRA_NOM,name);
            startActivity(i);
        }
    });
    pairedListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
        @Override
        public boolean onItemLongClick(AdapterView<?> av, View v, int pos, long id)
        {
            showInputBox(pos);

            return true;
        }
    });
}
    public void showInputBox( int pos){
        final Dialog dialog=new Dialog(MainActivity.this);
        dialog.setTitle(paireditems.get(pos).getName());
        dialog.setContentView(R.layout.input_box);
        Button btdel=(Button)dialog.findViewById(R.id.btdel);
            final BluetoothDevice device = paireditems.get(pos);
        btdel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Method m = device.getClass()
                            .getMethod("removeBond", (Class[]) null);
                    m.invoke(device, (Object[]) null);
                    loadPairedDevices();
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
                paireditems.remove(device);
                deviceListAdapter.notifyDataSetChanged();
                dialog.dismiss();

                Toast.makeText(getApplicationContext(),"Suppression Valider",Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }
}
