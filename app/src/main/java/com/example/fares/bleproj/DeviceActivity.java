package com.example.fares.bleproj;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.UUID;

public class DeviceActivity extends AppCompatActivity {
    Button btnOn, btnOff, btnDis;
    SeekBar brightness;
    TextView textViewnom ;
    TextView textViewAdresse;
    String address = null;
    String nom= null;
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
        Intent newint = getIntent();
        textViewnom = findViewById(R.id.nom);
        textViewAdresse= findViewById(R.id.ad);
        address = newint.getStringExtra(MainActivity.EXTRA_ADDRESS);
        nom = newint.getStringExtra(MainActivity.EXTRA_NOM);
        textViewnom.setText(nom);
        textViewAdresse.setText(address);

    }
}
