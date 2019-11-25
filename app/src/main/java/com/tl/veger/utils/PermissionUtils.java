package com.tl.veger.utils;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

public class PermissionUtils {


  public static final int REQUEST_EXTERNAL_STORAGE = 0x0001;
  public static final int REQUEST_BLUETOOTH = 0x0002;
  public static final int REQUEST_LOCATION = 0x003;
  public static final int REQUEST_NOTIFICATION = 0x004;
  public static final int REQUEST_SMS=0x005;
  public static final int REQUEST_CALL=0x006;

  private Activity activity;
  private PermissionListener listener;

  //读写权限
  private final String[] PERMISSIONS_STORAGE = {
      Manifest.permission.READ_EXTERNAL_STORAGE,
      Manifest.permission.WRITE_EXTERNAL_STORAGE};

  //定位权限
  private final String[] PERMISSIONS_LOCATION = {
      Manifest.permission.ACCESS_FINE_LOCATION,
      Manifest.permission.ACCESS_COARSE_LOCATION
  };

  //蓝牙权限
  private final String[] PERMISSION_BLUETOOTH = {
      Manifest.permission.BLUETOOTH,
      Manifest.permission.BLUETOOTH_ADMIN
  };

  //短信读取权限
  private final String[] PERMISSION_SMS={
      Manifest.permission.READ_SMS,
      Manifest.permission.RECEIVE_SMS
  };


  private final String[] PERMISSION_CALL={
      Manifest.permission.READ_CALL_LOG,
      Manifest.permission.WRITE_CALL_LOG
  };

  private PermissionUtils(Activity activity, PermissionListener listener) {
    this.activity = activity;
    this.listener = listener;
  }

  public static PermissionUtils getInstance(Activity activity, PermissionListener listener) {
    return new PermissionUtils(activity, listener);
  }


  public void checkPermission(int permissionCode) {
    switch (permissionCode) {
      case REQUEST_EXTERNAL_STORAGE:
        if (ActivityCompat.checkSelfPermission(activity,
            Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
          ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
              REQUEST_EXTERNAL_STORAGE);
        }
        break;

      case REQUEST_BLUETOOTH:
        if (ActivityCompat.checkSelfPermission(activity,
            Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
          ActivityCompat.requestPermissions(activity, PERMISSION_BLUETOOTH,
              REQUEST_BLUETOOTH);
        }
        checkBluetooth();
        break;

      case REQUEST_LOCATION:
        if (ActivityCompat.checkSelfPermission(activity,
            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
          ActivityCompat.requestPermissions(activity, PERMISSIONS_LOCATION,
              REQUEST_LOCATION);
        }
        break;

      case REQUEST_NOTIFICATION:
        checkNotify();
        break;

      case REQUEST_SMS:
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_SMS)
            != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(activity, Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED) {
          ActivityCompat.requestPermissions(activity, PERMISSION_SMS,REQUEST_SMS);
        }
        break;

      case REQUEST_CALL:
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_CALL_LOG)
            != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_CALL_LOG)
                != PackageManager.PERMISSION_GRANTED) {
          ActivityCompat.requestPermissions(activity, PERMISSION_CALL,REQUEST_CALL);
        }
        break;
    }
  }


  //检查是否有通知权限
  private boolean checkNotify() {
    boolean enable = false;
    String packageName = activity.getPackageName();
    String flat = Settings.Secure.getString(activity.getContentResolver(),
        "enabled_notification_listeners");
    if (flat != null) {
      enable = flat.contains(packageName);
    }
    if (!enable) {
      gotoNotificationAccessSetting();
    }
    return enable;
  }


  //跳转到通知权限界面
  private void gotoNotificationAccessSetting() {
    try {
      Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      Toast.makeText(activity, "Please open the notification permission, otherwise the app may " +
          "not work properly", Toast.LENGTH_SHORT).show();
      activity.startActivity(intent);
    } catch (ActivityNotFoundException e) {
      try {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ComponentName cn = new ComponentName("com.android.settings", "com.android.settings" +
            ".Settings$NotificationAccessSettingsActivity");
        intent.setComponent(cn);
        intent.putExtra(":settings:show_fragment", "NotificationAccessSettings");
        Toast.makeText(activity, "Please open the notification permission, otherwise the app may " +
            "not work properly", Toast.LENGTH_SHORT).show();
        activity.startActivity(intent);
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
  }


  /**
   * 检查该设备是否打开蓝牙
   */
  private void checkBluetooth() {
    boolean isOpen = false;
    BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context
        .BLUETOOTH_SERVICE);
    if (bluetoothManager != null) {
      BluetoothAdapter blueToothAdapter = bluetoothManager.getAdapter();
      if (blueToothAdapter == null || !blueToothAdapter.isEnabled()) {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivity(enableBtIntent);
        Toast.makeText(activity, "请允许打开蓝牙，否则无法正常使用该app", Toast.LENGTH_SHORT).show();
      } else {
        isOpen = true;
      }
    } else {
      Toast.makeText(activity, "该设备没有蓝牙无法正常使用该app", Toast.LENGTH_SHORT).show();
    }
    listener.onPermissionResult(isOpen, REQUEST_BLUETOOTH);
  }


  public interface PermissionListener {
    void onPermissionResult(boolean hasPermission, int permissionCode);
  }

}
