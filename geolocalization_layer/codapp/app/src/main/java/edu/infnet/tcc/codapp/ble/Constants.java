package edu.infnet.tcc.codapp.ble;

import java.util.HashMap;

public class Constants {

    public static final int SCAN_PERIOD = 15 * 1000;//10000;

    public static final int REQUEST_BLUETOOTH_ENABLE_CODE = 101;
    public static final int REQUEST_LOCATION_ENABLE_CODE = 101;

    public static class GattAttributes {
//        public static final String CO_DETECTION_SERVICE_UUID = "0000180f-0000-1000-8000-00805f9b34fb";
//        public static final String CO_LEVEL_UUID = "00002a19-0000-1000-8000-00805f9b34fb";
        public static final String CO_DETECTION_SERVICE_UUID = "6e400001-b5a3-f393-e0a9-e50e24dcca9e";
        public static final String CO_LEVEL_UUID = "6e400003-b5a3-f393-e0a9-e50e24dcca9e";
        public static final String NOTIFY_DESCRIPTOR_UUID = "00002902-0000-1000-8000-00805f9b34fb";

        private static HashMap<String, String> attributes;

        static {
            attributes = new HashMap(){};
            attributes.put(CO_DETECTION_SERVICE_UUID.toLowerCase(), "CO Detection Service");
            attributes.put(CO_LEVEL_UUID.toLowerCase(), "CO Level");
        }

        public static String lookup(String uuid) {
            return attributes.get(uuid.toLowerCase());
        }
    }
}
