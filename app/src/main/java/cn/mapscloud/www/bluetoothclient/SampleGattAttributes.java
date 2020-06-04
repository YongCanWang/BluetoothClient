package cn.mapscloud.www.bluetoothclient;

import java.util.HashMap;

/**
 * Created by wangyongcan on 2017/10/17.
 */

public class SampleGattAttributes {

    public static String BATTERY_LEVEL = "00002a19-0000-1000-8000-00805f9b34fb";
    public static String BATTERY_SERVICE = "0000180f-0000-1000-8000-00805f9b34fb";
    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
    public static String HEART_RATE_MEASUREMENT = "00002a1c-0000-1000-8000-00805f9b34fb";
    public static String MYCJ_BLE = "00001c00-d102-11e1-9b23-00025b00a5a5";
    public static String MYCJ_BLE_READ_WRITE = "00001c01-d103-11e1-9b23-00025b00a5a5";
    private static HashMap<String, String> attributes = new HashMap();

    static {
        attributes.put("0000180f-0000-1000-8000-00805f9b34fb", "Heart Rate Service");
        attributes.put("0000180a-0000-1000-8000-00805f9b34fb", "Device Information Service");
        attributes.put(MYCJ_BLE, "MYCJ_BLE");
        attributes.put(MYCJ_BLE_READ_WRITE, "MYCJ_BLE_READ_WRITE");
        attributes.put(HEART_RATE_MEASUREMENT, "Heart Rate Measurement");
        attributes.put("00002a29-0000-1000-8000-00805f9b34fb", "Manufacturer Name String");
    }

    public SampleGattAttributes() {
    }

    public static String lookup(String var0, String var1) {
        String var2 = (String)attributes.get(var0);
        return var2 == null?var1:var2;
    }
}
