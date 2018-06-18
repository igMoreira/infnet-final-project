package edu.infnet.tcc.codapp.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;

public class Utils {
    public static BluetoothAdapter getBluetoothAdapter(Context ctx) {
        return ((BluetoothManager) ctx.getSystemService(ctx.BLUETOOTH_SERVICE)).getAdapter();
    }
}
