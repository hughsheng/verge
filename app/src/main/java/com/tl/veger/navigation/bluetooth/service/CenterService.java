package com.tl.veger.navigation.bluetooth.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.tl.veger.base.app.AppApplication;
import com.tl.veger.busbean.BluetoothBusBean;
import com.tl.veger.utils.ConmmonUtil;
import com.tl.veger.utils.ConstanceValue;
import com.tl.veger.utils.DataProtocolUtil;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.tl.veger.utils.DataProtocolUtil.MSG_TYPE.MSG_WEATHER;

/**
 * created by tl on 2019/8/2
 * app作为中心设备接收蓝牙广播
 */
public class CenterService extends Service {

  public static final String TAG = "CenterService";
  private CenterServiceBinder binder;
  private BluetoothGatt bluetoothGatt;
  private boolean isConnected = false;
  private final List<BleDev> devList = new ArrayList<>();
  private Handler handler = new Handler();
  private boolean isScanning = false;
  private BluetoothLeScanner bluetoothLeScanner;
  private BluetoothAdapter bluetoothAdapter;
  private BluetoothGatt connectedGatt;
  private BluetoothGattService writeService;
  private BluetoothGattCharacteristic writeCharacteristic;
  private int count;

  @Override
  public void onCreate() {
    super.onCreate();
    binder = new CenterServiceBinder();
    bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
  }


  // 蓝牙扫描Callback
  private final ScanCallback mScanCallback = new ScanCallback() {
    @Override
    public void onScanResult(int callbackType, ScanResult result) {
      BleDev dev = new BleDev(result.getDevice(), result);
      String name = dev.dev.getName();
      if (name != null && name.contains("VERGE") && !devList.contains(dev)) {
        devList.add(dev);
        BluetoothBusBean bluetoothBusBean = new BluetoothBusBean();
        bluetoothBusBean.setNotice("有新设备");
        EventBus.getDefault().post(bluetoothBusBean);
        Log.i(TAG, "onScanResult: " + result); // result.getScanRecord() 获取BLE广播数据
      }
    }
  };


  // 与服务端连接的Callback
  public BluetoothGattCallback mBluetoothGattCallback = new BluetoothGattCallback() {
    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
      BluetoothDevice connectedDev = gatt.getDevice();
      Log.i(TAG, String.format("onConnectionStateChange:%s,%s,%s,%s", connectedDev.getName(),
          connectedDev.getAddress(), status, newState));
      if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED) {
        connectedGatt = gatt;
        isConnected = true;
        gatt.discoverServices(); //启动服务发现
      } else {
        isConnected = false;
        closeConn();
        connectedGatt = null;
        writeService = null;
        writeCharacteristic = null;
      }
      String result = String.format(status == 0 ? (newState == 2 ? "与[%s]连接成功" : "与[%s]连接断开") : (
          "与[%s" +
              "]连接出错,错误码:" + status), connectedDev);
      Log.i(TAG, result);

      BluetoothBusBean bluetoothBusBean = new BluetoothBusBean();
      bluetoothBusBean.setNotice(result);
      bluetoothBusBean.setConnectedDev(connectedDev);
      EventBus.getDefault().post(bluetoothBusBean);

    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
      Log.i(TAG, String.format("onServicesDiscovered:%s,%s,%s", gatt.getDevice().getName(),
          gatt.getDevice().getAddress(), status));
      if (status == BluetoothGatt.GATT_SUCCESS) { //BLE服务发现成功
        // 遍历获取BLE服务Services/Characteristics/Descriptors的全部UUID
        for (BluetoothGattService service : gatt.getServices()) {
          StringBuilder allUUIDs = new StringBuilder("UUIDs={\nS=" + service.getUuid().toString());
          for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
            allUUIDs.append(",\nC=").append(characteristic.getUuid());
            for (BluetoothGattDescriptor descriptor : characteristic.getDescriptors())
              allUUIDs.append(",\nD=").append(descriptor.getUuid());
          }
          allUUIDs.append("}");
          // Log.i(TAG, "onServicesDiscovered:" + allUUIDs.toString());
          //  sendEventBus("发现服务" + allUUIDs);
          enableNotification();
        }
      }
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt,
                                     BluetoothGattCharacteristic characteristic, int status) {
      String uuid = characteristic.getUuid().toString();
      String valueStr = new String(characteristic.getValue());
      Log.i(TAG, String.format("onCharacteristicRead:%s,%s,%s,%s,%s", gatt.getDevice().getName(),
          gatt.getDevice().getAddress(), uuid, valueStr, status));
      sendEventBus("读取Characteristic[" + uuid + "]:\n" + valueStr);
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt,
                                      BluetoothGattCharacteristic characteristic, int status) {
      String uuid = characteristic.getUuid().toString();
      if (uuid.equalsIgnoreCase(ConstanceValue.SEND_CHARACTERISTIC)) {
        Log.i(TAG, String.format("写入单数据触发" + "onCharacteristicChanged:", uuid));
        BluetoothBusBean bean = new BluetoothBusBean();
        bean.setNotice("sendFinish");
        EventBus.getDefault().post(bean);
      }
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt,
                                        BluetoothGattCharacteristic characteristic) {

      String uuid = characteristic.getUuid().toString();
      String valueStr = new String(characteristic.getValue());

//      if (uuid.equalsIgnoreCase(ConstanceValue.OBSERVE_CHARACTERISTIC)) {
//        count++;
//        if (count >= 5) {
//          Log.i(TAG, String.format("写入全数据触发" + "onCharacteristicChanged:", uuid));
//          sendEventBus("Characteristic发生改变[" + uuid + "]:\n" + valueStr);
//          count = 0;
//        }
//      }
    }

    @Override
    public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor,
                                 int status) {
      UUID uuid = descriptor.getUuid();
      String valueStr = Arrays.toString(descriptor.getValue());
      Log.i(TAG, String.format("onDescriptorRead:%s,%s,%s,%s,%s", gatt.getDevice().getName(),
          gatt.getDevice().getAddress(), uuid, valueStr, status));
      sendEventBus("读取Descriptor[" + uuid + "]:\n" + valueStr);
    }

    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor,
                                  int status) {
      UUID uuid = descriptor.getUuid();
      String valueStr = Arrays.toString(descriptor.getValue());
      Log.i(TAG, String.format("onDescriptorWrite:%s,%s,%s,%s,%s", gatt.getDevice().getName(),
          gatt.getDevice().getAddress(), uuid, valueStr, status));
      sendEventBus("写入Descriptor[" + uuid + "]:\n" + valueStr);
    }
  };


  public void bondDev(BluetoothDevice dev) {
    try {
      //如果想要取消已经配对的设备，只需要将creatBond改为removeBond
      Method method = BluetoothDevice.class.getMethod("createBond");
      Log.i(TAG, "开始配对");
      method.invoke(dev);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void connectDev(BluetoothDevice dev) {
    bluetoothGatt = dev.connectGatt(getApplicationContext(), false, mBluetoothGattCallback);
    // 连接蓝牙设备
    sendEventBus(String.format("与[%s]开始连接............", dev));
  }


  //更新特征值数据
  public void sendData(byte[] datas) {
    if (writeCharacteristic == null) {
      if (connectedGatt != null)
      {
        writeService = connectedGatt.getService(UUID.fromString(ConstanceValue.SERVICE_UUID));
        if (writeService != null) {
          writeCharacteristic =
              writeService.getCharacteristic(UUID.fromString(ConstanceValue.SEND_CHARACTERISTIC));
          updateData(datas);
        }
      }
    } else {
      updateData(datas);
    }
  }


  private void updateData(byte[] datas) {
    writeCharacteristic.setValue(datas);
    connectedGatt.writeCharacteristic(writeCharacteristic);
    Log.i(TAG, "发送的数据为:" + Arrays.toString(datas));
  }


  //断开连接
  public void disconnectDev() {
    if (bluetoothGatt != null) {
      bluetoothGatt.disconnect();
    }
  }

  // 扫描BLE蓝牙(不会扫描经典蓝牙)
  public void scanBle() {
    isScanning = true;
    devList.clear();
    // Android5.0新增的扫描API，扫描返回的结果更友好，比如BLE广播数据以前是byte[] scanRecord，而新API帮我们解析成ScanRecord类
    bluetoothLeScanner.startScan(mScanCallback);
    List<BluetoothDevice> vergeDeviceList = ConmmonUtil.getVergeDevice();
    if (vergeDeviceList != null&&vergeDeviceList.size()>0) {
      BleDev dev = new BleDev(vergeDeviceList.get(0), null);
      devList.add(dev);
      BluetoothBusBean bluetoothBusBean = new BluetoothBusBean();
      bluetoothBusBean.setNotice("有新设备");
      EventBus.getDefault().post(bluetoothBusBean);
    }

    sendEventBus("正在扫描...");
  }


  public void stopScanBle() {
    isScanning = false;
    bluetoothLeScanner.stopScan(mScanCallback); //停止扫描
  }


  public boolean isScanning() {
    return isScanning;
  }


  // 重新扫描
  public void reScan() {
    devList.clear();
    scanBle();
  }


  public List<BleDev> getDevList() {
    return devList;
  }


  public class BleDev {
    public BluetoothDevice dev;
    public ScanResult scanResult;

    BleDev(BluetoothDevice device, ScanResult result) {
      dev = device;
      scanResult = result;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      BleDev bleDev = (BleDev) o;
      return Objects.equals(dev, bleDev.dev);
    }

    @Override
    public int hashCode() {
      return Objects.hash(dev);
    }
  }


  @Override
  public IBinder onBind(Intent intent) {
    return binder;
  }


  public class CenterServiceBinder extends Binder {
    public CenterService getCenterService() {
      return CenterService.this;
    }
  }


  private void sendEventBus(String str) {
    BluetoothBusBean bluetoothBusBean = new BluetoothBusBean();
    bluetoothBusBean.setNotice(str);
    EventBus.getDefault().post(bluetoothBusBean);
  }

  // BLE中心设备连接外围设备的数量有限(大概2~7个)，在建立新连接之前必须释放旧连接资源，否则容易出现连接错误133
  private void closeConn() {
    if (bluetoothGatt != null) {
      bluetoothGatt.disconnect();
      bluetoothGatt.close();
    }
  }


  public boolean enableNotification() {
    UUID serviceUUID = UUID.fromString(ConstanceValue.SERVICE_UUID);
    UUID characteristicUUID = UUID.fromString(ConstanceValue.OBSERVE_CHARACTERISTIC);
    boolean success = false;
    BluetoothGattService service = connectedGatt.getService(serviceUUID);
    if (service != null) {
      Log.i(TAG, "service=" + service.getUuid());
      BluetoothGattCharacteristic characteristic = findNotifyCharacteristic(service,
          characteristicUUID);
      if (characteristic != null) {
        Log.i(TAG, "character=" + characteristic.getUuid());
        success = connectedGatt.setCharacteristicNotification(characteristic, true);
        if (success) {
          // 来源：http://stackoverflow.com/questions/38045294/oncharacteristicchanged-not-called
          // -with-ble
          for (BluetoothGattDescriptor dp : characteristic.getDescriptors()) {
            if (dp != null) {
              if ((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0) {
                dp.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
              } else if ((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_INDICATE) != 0) {
                dp.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
              }
              connectedGatt.writeDescriptor(dp);
            }
          }
        }
      }
    }
    Log.i(TAG, "isSuccess=" + success);
    return success;
  }

  private BluetoothGattCharacteristic findNotifyCharacteristic(BluetoothGattService service,
                                                               UUID characteristicUUID) {
    BluetoothGattCharacteristic characteristic = null;
    List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
    for (BluetoothGattCharacteristic c : characteristics) {
      if ((c.getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0
          && characteristicUUID.equals(c.getUuid())) {
        characteristic = c;
        break;
      }
    }
    if (characteristic != null)
      return characteristic;
    for (BluetoothGattCharacteristic c : characteristics) {
      if ((c.getProperties() & BluetoothGattCharacteristic.PROPERTY_INDICATE) != 0
          && characteristicUUID.equals(c.getUuid())) {
        characteristic = c;
        break;
      }
    }
    return characteristic;
  }


  @Override
  public boolean onUnbind(Intent intent) {
    closeConn();
    return super.onUnbind(intent);
  }
}
