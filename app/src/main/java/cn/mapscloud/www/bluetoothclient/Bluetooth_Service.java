package cn.mapscloud.www.bluetoothclient;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by wangyongcan on 2017/10/17.
 */

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class Bluetooth_Service extends Service {

    //蓝牙端口配置程序
    private static HashMap<String, String> attributes = new HashMap();
    public static String HEART_RATE_MEASUREMENT = "00002a1c-0000-1000-8000-00805f9b34fb";
    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
    public static String MYCJ_BLE = "00001c00-d102-11e1-9b23-00025b00a5a5";
    public static String MYCJ_BLE_READ_WRITE = "00001c01-d103-11e1-9b23-00025b00a5a5";
//	public static String MYCJ_BLE_WRITE = "0000ff01-0000-1000-8000-00805f9b34fb";

    public static String BATTERY_SERVICE = "0000180f-0000-1000-8000-00805f9b34fb";
    public static String BATTERY_LEVEL = "00002a19-0000-1000-8000-00805f9b34fb";

    static {
        // Sample Services.
        attributes.put("0000180f-0000-1000-8000-00805f9b34fb",
                "Heart Rate Service");
        attributes.put("0000180a-0000-1000-8000-00805f9b34fb",
                "Device Information Service");
        // Sample Characteristics.
        attributes.put(MYCJ_BLE, "MYCJ_BLE");
        attributes.put(MYCJ_BLE_READ_WRITE, "MYCJ_BLE_READ_WRITE");
//		attributes.put(MYCJ_BLE_WRITE, "WRITER");
        attributes.put(HEART_RATE_MEASUREMENT, "Heart Rate Measurement");
        attributes.put("00002a29-0000-1000-8000-00805f9b34fb",
                "Manufacturer Name String");
    }


    private final static String TAG = Bluetooth_Service.class.getSimpleName();

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGattService myGattService;
    private BluetoothGattCharacteristic read_write,notification_characteristic,read_write2,notification2_characteristic;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;
    private int mConnectionState = STATE_DISCONNECTED;

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

//    private static final UUID service_uuid = UUID
//            .fromString(SampleGattAttributes.SERVICE);
//    private static final UUID characteristic1_uuid = UUID
//            .fromString(SampleGattAttributes.CHARACTERISTIC_1);
//    private static final UUID notification1_uuid = UUID.fromString(SampleGattAttributes.NOTIFICATION_1);
//
//    private static final UUID characteristic2_uuid = UUID
//            .fromString(SampleGattAttributes.CHARACTERISTIC_2);
//    private static final UUID notification2_uuid = UUID.fromString(SampleGattAttributes.NOTIFICATION_2);

    private static final UUID service_uuid = UUID
            .fromString(SampleGattAttributes.BATTERY_SERVICE);
    private static final UUID characteristic1_uuid = UUID
            .fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG);
    private static final UUID notification1_uuid = UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG);

    private static final UUID characteristic2_uuid = UUID
            .fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG);
    private static final UUID notification2_uuid = UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG);


    public final static String ACTION_GATT_CONNECTED =
            "com.kun.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.kun.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.kun.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.kun.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.kun.bluetooth.le.EXTRA_DATA";
    public final static String ERROR_CODE =
            "com.kun.bluetooth.le.ERROR_CODE";

    public static boolean Running = false;


    //蓝牙连接状态回调
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED;
                mConnectionState = STATE_CONNECTED;
//                broadcastUpdate(intentAction);
                mBluetoothGatt.getServices();
                mBluetoothGatt.discoverServices();
                Log.e("aaa", "onConnectionStateChange.");
                Running = true;
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED;
                mConnectionState = STATE_DISCONNECTED;
                Log.e(TAG, "Disconnected from GATT server.");
//                music.play(1, 0);
//                broadcastUpdate(intentAction);
            }

        }


        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.e("aaa","onServicesDiscovered");
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (mBluetoothGatt != null) {
                    myGattService = mBluetoothGatt.getService(service_uuid);
                }
                if (myGattService != null) {
                    String aaa = myGattService.getUuid().toString();
                    Log.e("aaa", aaa+"success");
                    read_write = myGattService
                            .getCharacteristic(characteristic1_uuid);
                    notification_characteristic = myGattService.getCharacteristic(notification1_uuid);

                    read_write2 = myGattService
                            .getCharacteristic(characteristic2_uuid);
                    notification2_characteristic = myGattService.getCharacteristic(notification2_uuid);
                }
                if (notification_characteristic != null) {
                    Log.e("aaa","notification_characteristic success");
                    setCharacteristicNotification(notification_characteristic, true);
                    setCharacteristicNotification(notification2_characteristic, true);
                }
//                if (read_write != null) {
//                    Log.i("aaa","read_write success");
//                    setCharacteristicNotification(read_write, true);
//                }
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {

            if (status == BluetoothGatt.GATT_SUCCESS) {
//                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {

//            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    //服务绑定
    @Override
    public boolean onUnbind(Intent intent) {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
        return super.onUnbind(intent);
    }

//    private final IBinder mBinder = new LocalBinder();

    /**
     * 初始化
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }
        return true;
    }


    //蓝牙数据写入
    public void writeStringToGatt(byte[] data,int num) {
        if(read_write == null||mBluetoothGatt == null){
            Running = false;
        }else if (data != null ) {
            if (num == 3){
                read_write2.setValue(data);
                mBluetoothGatt.writeCharacteristic(read_write2);
            }else {
                read_write.setValue(data);
                mBluetoothGatt.writeCharacteristic(read_write);
            }
            Log.i("aaa","write_to_gatt");
        }
    }

    /**
     *连接
     * @param address
     * @return
     */
    public boolean connect(final String address) {
        if (mBluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        // Previously connected device.  Try to reconnect.
        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                mConnectionState = STATE_CONNECTING;
                return true;
            } else {
                return false;
            }
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
        Log.d(TAG, "Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        mConnectionState = STATE_CONNECTING;
        return true;
    }

    /**
     * 断开
     */
    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
    }

    /**
     * 关闭
     */
    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }


    //读取蓝牙数据
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }



    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enabled) {

        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
        if (characteristic1_uuid.equals(characteristic.getUuid())) {
            BluetoothGattDescriptor descriptor = characteristic
                    .getDescriptor(UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            mBluetoothGatt.writeDescriptor(descriptor);
        }else if (notification1_uuid.equals(characteristic.getUuid())) {
            BluetoothGattDescriptor descriptor = characteristic
                    .getDescriptor(UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));

            descriptor
                    .setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            mBluetoothGatt.writeDescriptor(descriptor);
        }else if (characteristic2_uuid.equals(characteristic.getUuid())) {
            BluetoothGattDescriptor descriptor = characteristic
                    .getDescriptor(UUID
                            .fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
            descriptor
                    .setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            mBluetoothGatt.writeDescriptor(descriptor);
        }else if (notification2_uuid.equals(characteristic.getUuid())) {
            BluetoothGattDescriptor descriptor = characteristic
                    .getDescriptor(UUID
                            .fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
            descriptor
                    .setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            mBluetoothGatt.writeDescriptor(descriptor);
        }
    }
    public void getRssi() {
        if(mBluetoothGatt != null) {
            mBluetoothGatt.readRemoteRssi();
        }
    }


//    //蓝牙扫描程序
//    public void reScanLeDevice() {
//        if (deviceAddressList != null) {
//            deviceAddressList.clear();
//        }
//        if (myBluetoothGatt != null) {
//            myBluetoothGatt.close();
//            myBluetoothGatt = null;
//        }
//        if (myBluetoothAdapter != null) {
//            myBluetoothAdapter.stopLeScan(myReLeScanCallback);
//            myBluetoothAdapter.startLeScan(myReLeScanCallback);
//            myHandler.postDelayed(new Runnable() {
//
//                @Override
//                public void run() {
//                    // TODO Auto-generated method stub
//                    System.out.println("查找3秒后结束……");
////                    setView();
//                    if(scanning) {
//                        myBluetoothAdapter.stopLeScan(myReLeScanCallback);
//                    }
//                }
//            }, 3000);
//            Connect = false;
//            System.out.println("开始重新查找蓝牙设备");
//        }
//    }
//
//
//    //蓝牙连接
//    public boolean connect(String paramString) {
//        device = paramString;
//        if ((myBluetoothAdapter == null) || (paramString == null))
//            return false;
//        myBluetoothAdapter.stopLeScan(myReLeScanCallback);
//        if ((myBluetoothDeviceAddress != null)
//                && (paramString.equals(myBluetoothDeviceAddress))
//                && (myBluetoothGatt != null)) {
//            if (myBluetoothGatt.connect()) {
//                System.out.println("mBluetoothGatt不为空");
//                return true;
//            }
//            return false;
//        }
//        BluetoothDevice localBluetoothDevice = myBluetoothAdapter.getRemoteDevice(paramString);
//        if (localBluetoothDevice == null)
//            return false;
//        myBluetoothGatt = localBluetoothDevice.connectGatt(this, false,myGattCallback);
//        myBluetoothDeviceAddress = paramString;
//        return true;
//    }


}
