package edu.infnet.tcc.codapp;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.ParcelUuid;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.infnet.tcc.codapp.ble.BLEService;
import edu.infnet.tcc.codapp.ble.Constants;
import edu.infnet.tcc.codapp.ble.Utils;
import edu.infnet.tcc.codapp.model.CarbonMonoxideData;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = MainActivity.class.getName();

    Button button;
    Button connectDevice;
    TextView deviceStatus;
    TextView batteryLevel;
    TextView deviceAddress;
    TextView deviceName;
    TextView serviceName;
    ProgressBar progressBar;
    Button connectService;

    BluetoothDevice bluetoothDevice;
    private boolean mScanning;
    private BluetoothLeScanner bluetoothLeScanner;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGattCharacteristic mNotifyCharacteristic;
    private boolean mConnected = false;
    private BLEService mBLEService;

    private LocationManager locationManager;
    public static Location currentLocation;

    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBLEService = ((BLEService.LocalBinder) service).getService();
            if (!mBLEService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            mBLEService.connect(bluetoothDevice.getAddress());
            mBLEService.connect(bluetoothDevice.getAddress());
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBLEService = null;
        }
    };

    private void BindView() {
        setContentView(R.layout.activity_main);
//        ButterKnife.bind(this);

        button = findViewById(R.id.startScan);
        connectDevice = findViewById(R.id.connectDevice);
        deviceStatus = findViewById(R.id.deviceState);
        batteryLevel = findViewById(R.id.batteryLevel);
        deviceAddress = findViewById(R.id.deviceAddress);
        deviceName = findViewById(R.id.deviceName);
        serviceName = findViewById(R.id.serviceName);
        progressBar = findViewById(R.id.progressBar);
        connectService = findViewById(R.id.connectService);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BindView();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        mBluetoothAdapter = Utils.getBluetoothAdapter(MainActivity.this);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                startScanning(true);
            }
        });

        connectDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bluetoothDevice != null) {
                    progressBar.setVisibility(View.VISIBLE);
                    Intent gattServiceIntent = new Intent(MainActivity.this, BLEService.class);
                    bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
                }
            }
        });

        connectService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mNotifyCharacteristic != null) {
                    final int characteristicProperties = mNotifyCharacteristic.getProperties();
                    if ((characteristicProperties | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                        mBLEService.readCharacteristic(mNotifyCharacteristic);
                    }
                    if ((characteristicProperties | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                        mBLEService.setCharacteristicNotification(mNotifyCharacteristic, true);
                    }
                }
            }
        });
    }

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BLEService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                updateConnectionState("connected");
                invalidateOptionsMenu();
            } else if (BLEService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                updateConnectionState("disconnected");
                //clearUI();
            } else if (BLEService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                Log.d(TAG, "ACTION_GATT_SERVICES_DISCOVERED Broadcast received...");
                displayGattServices(mBLEService.getSupportedGattServices());
            } else if (BLEService.ACTION_DATA_AVAILABLE.equals(action)) {
                String gasConcentration = intent.getStringExtra(BLEService.EXTRA_DATA);
                displayData(gasConcentration);
                CarbonMonoxideData coData = new CarbonMonoxideData(gasConcentration, currentLocation);
                Log.i(TAG, coData.toString());
                //TODO: send to data analytics service
            }
        }
    };

    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            if (result != null) {
//                Log.d(TAG, "Device: " + result.toString());
                if (result.getDevice() != null && result.getDevice().getName() != null) {
                    BluetoothDevice d = result.getDevice();
                    Log.d(TAG, "Name: " + (d.getName() != null && !d.getName().isEmpty() ? d.getName() : "<no name>"));
                    Log.d(TAG, "Address: " + (d.getAddress() != null && !d.getAddress().isEmpty() ? d.getAddress() : "<no address>"));
                    Log.d(TAG, "Service UUID: " + (d.getUuids() != null && d.getUuids().length > 0 ? d.getUuids()[0] : "<no services>"));
                    Log.d(TAG, "Result: " + result.toString());
                    Log.d(TAG, "Device: " + d.toString());
                }
            }
            if (bluetoothDevice == null && result != null && result.getDevice() != null && result.getDevice().getName() != null && !result.getDevice().getName().isEmpty()) {
                bluetoothDevice = result.getDevice();
                deviceAddress.setText(bluetoothDevice.getAddress());
                deviceName.setText(bluetoothDevice.getName());
                progressBar.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.d(TAG, "Scanning Failed " + errorCode);
            progressBar.setVisibility(View.INVISIBLE);
        }
    };

    private static IntentFilter GattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BLEService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BLEService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BLEService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BLEService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    Constants.REQUEST_LOCATION_ENABLE_CODE);
        }
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.BLUETOOTH},
                    Constants.REQUEST_BLUETOOTH_ENABLE_CODE);
        }
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.BLUETOOTH_ADMIN},
                    Constants.REQUEST_BLUETOOTH_ENABLE_CODE);
        }

        startGpsListener();

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "Your devices that don't support BLE", Toast.LENGTH_LONG).show();
            finish();
        }
        if (!mBluetoothAdapter.enable()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, Constants.REQUEST_BLUETOOTH_ENABLE_CODE);
        }
        registerReceiver(mGattUpdateReceiver, GattUpdateIntentFilter());
        if (mBLEService != null) {
            final boolean result = mBLEService.connect(bluetoothDevice.getAddress());
            Log.d(TAG, "Connect request result=" + result);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
        locationManager.removeUpdates(locationListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBLEService = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_BLUETOOTH_ENABLE_CODE && resultCode == RESULT_CANCELED) {
            finish();
        }
    }


    private void startScanning(final boolean enable) {
        bluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        Handler mHandler = new Handler();
        if (enable) {
            List<ScanFilter> scanFilters = new ArrayList<>();
            final ScanSettings settings = new ScanSettings
                    .Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                    .build();
            Log.d(TAG, "Filtering devices: " + Constants.GattAttributes.CO_DETECTION_SERVICE_UUID);
            ScanFilter scanFilter = new ScanFilter.Builder().setServiceUuid(ParcelUuid.fromString(Constants.GattAttributes.CO_DETECTION_SERVICE_UUID)).build();
            scanFilters.add(scanFilter);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    progressBar.setVisibility(View.INVISIBLE);
                    bluetoothLeScanner.stopScan(scanCallback);
                }
            }, Constants.SCAN_PERIOD);
            mScanning = true;
            bluetoothLeScanner.startScan(scanFilters, settings, scanCallback);
        } else {
            mScanning = false;
            bluetoothLeScanner.stopScan(scanCallback);
        }
    }


    private void updateConnectionState(final String status) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                deviceStatus.setText(status);
            }
        });
    }

    private void displayData(String data) {
        if (data != null) {
            batteryLevel.setText(data);
        }
    }

    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) {
            Log.d(TAG, "No GATT services was found.");
            return;
        }
        String uuid = null;
        String serviceString = "unknown service";
        String charaString = "unknown characteristic";

        for (BluetoothGattService gattService : gattServices) {

            uuid = gattService.getUuid().toString();
            Log.d(TAG, "GATT Service UUID: " + uuid);

            serviceString = Constants.GattAttributes.lookup(uuid);

            if (serviceString != null) {
                List<BluetoothGattCharacteristic> gattCharacteristics =
                        gattService.getCharacteristics();

                for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                    HashMap<String, String> currentCharaData = new HashMap<String, String>();
                    uuid = gattCharacteristic.getUuid().toString();
                    charaString = Constants.GattAttributes.lookup(uuid);
                    if (charaString != null) {
                        serviceName.setText(charaString);
                    }
                    mNotifyCharacteristic = gattCharacteristic;

                    return;
                }
            }
        }
    }


    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.d(TAG, "onLocationChanged: LOCATION HAS CHANGED!!!!!!!!!!!!!!!!!!!!!");
            currentLocation = location;
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }
    };


    private void startGpsListener() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    Constants.REQUEST_GPS_ENABLE_CODE);
        }
        Log.d(TAG, "startGpsListener: I was here!!!");
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
    }
}
