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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    BluetoothAdapter mBluetoothAdapter;
    ListView lv;
    private ArrayList<BluetoothDevice> items = new ArrayList<>();
    private DeviceListAdapter mDeviceListAdapter;
    //
    private ListView pairedListView;
    private ArrayList<BluetoothDevice> paireditems = new ArrayList<>();


    //----------------------------------------receiver---------
    private final BroadcastReceiver mBroadcastReceiver3 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                items.add(device);
                Log.i("BT", device.getName() + "\n" + device.getAddress());
                lv.setAdapter(new DeviceListAdapter(context, R.layout.device_adapter_view, items));
            }
        }
    };
    private final BroadcastReceiver mPairedReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
                for(BluetoothDevice bt : pairedDevices)
                {paireditems.add(bt);
                    Toast.makeText(getApplicationContext(),bt.getName(),Toast.LENGTH_SHORT).show();}
                Log.i("BT", device.getName() + "\n" + device.getAddress());
                pairedListView.setAdapter(new DeviceListAdapter (context,
                        R.layout.device_adapter_view, paireditems));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lv = (ListView) findViewById(R.id.listView);
        Button btAdd=(Button)findViewById(R.id.btAdd);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputBox();
            }
        });
        pairedListView =  findViewById(R.id.pairedevice);
        mBluetoothAdapter.startDiscovery();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mPairedReceiver, filter);


    }
    public void showInputBox(){

        final Dialog dialog=new Dialog(MainActivity.this);
        dialog.setTitle("Scan");
        dialog.setContentView(R.layout.input_box);
        lv = (ListView )dialog.findViewById(R.id.listView);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter.startDiscovery();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mBroadcastReceiver3, filter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mBluetoothAdapter.cancelDiscovery();

                Log.d(TAG, "onItemClick: You Clicked on a device.");
                String deviceName = items.get(position).getName();
                String deviceAddress = items.get(position).getAddress();

                Log.d(TAG, "onItemClick: deviceName = " + deviceName);
                Log.d(TAG, "onItemClick: deviceAddress = " + deviceAddress);

                //create the bond.
                //NOTE: Requires API 17+? I think this is JellyBean
                if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2){
                    Log.d(TAG, "Trying to pair with " + deviceName);
                    items.get(position).createBond();

                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(),deviceName +" est Associer",Toast.LENGTH_SHORT).show();
                }
            }
        });
/*        lv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                adapter.notifyDataSetChanged();
                dialog.dismiss();
                Toast.makeText(getApplicationContext(),"Modification Valider",Toast.LENGTH_SHORT).show();
            }
        });*/
        dialog.show();
    }

}
