package br.edu.infnet.al.bleclientsample;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_ENABLE_BT = 1;
    private static final String ESP32_ADDRESS = "30:AE:A4:27:48:1E";
    private static final int PERMISSION_REQUEST_LOCATION = 200;
    private static final long SCAN_PERIOD = 10000;

    private static final UUID ESP32_SERVICE_UUID = UUID.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E");
    private static final UUID ESP32_GAS_MEASUREMENT_CHAR_UUID = UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e");
    private static final UUID NOTIFY_DESCRIPTOR_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    private BluetoothManager bluetManager;
    private BluetoothAdapter bluetAdp;
    private BluetoothDevice myDevice;

    private Handler mHandler;
    private BluetoothAdapter.LeScanCallback scanCallback;
    private BluetoothGatt gatt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bluetManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetAdp = bluetManager.getAdapter();
        mHandler = new Handler();

        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        if (bluetAdp == null || !bluetAdp.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_LOCATION);
        }


        BluetoothGattCallback gattCallback = new BluetoothGattCallback() {

            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    Log.i(TAG, "Connected to GATT server.");
                    Log.i(TAG, "Attempting to start service discovery:" +
                            gatt.discoverServices());

                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    Log.i(TAG, "Disconnected from GATT server.");
                }
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                BluetoothGattCharacteristic characteristic =
                        gatt.getService(ESP32_SERVICE_UUID)
                                .getCharacteristic(ESP32_GAS_MEASUREMENT_CHAR_UUID);
                gatt.setCharacteristicNotification(characteristic, true);

                BluetoothGattDescriptor descriptor =
                        characteristic.getDescriptor(NOTIFY_DESCRIPTOR_UUID);

                descriptor.setValue(
                        BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                gatt.writeDescriptor(descriptor);
            }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                Charset utf8 = Charset.forName("UTF-8");
                CharBuffer charBuffer = utf8.decode(ByteBuffer.wrap(characteristic.getValue()));
                Log.d(TAG, "Incoming Value : ".concat(charBuffer.toString()));
            }
        };

        scanCallback = (device, i, bytes) -> {
            if (device != null && device.getAddress().equalsIgnoreCase(ESP32_ADDRESS) && myDevice == null) {
                Log.d(TAG, "FOUND ESP32!!!");
                myDevice = device;
                bluetAdp.stopLeScan(scanCallback);
                gatt = myDevice.connectGatt(this, true, gattCallback);
            }
        };
        mHandler.postDelayed(() -> bluetAdp.stopLeScan(scanCallback), SCAN_PERIOD);
        bluetAdp.startLeScan(scanCallback);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Toast.makeText(this, "COULD NOT ACQUIRE NECESSARY PERMISSIONS", Toast.LENGTH_LONG);
    }
}
