package com.tl.veger.welcome;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;
import com.tl.veger.HomeActivity;
import com.tl.veger.R;
import com.tl.veger.base.BaseActivity;
import com.tl.veger.gmail.GoogleLoginActivity;
import com.tl.veger.utils.AnimationUtils;
import com.tl.veger.utils.ConmmonUtil;
import com.tl.veger.utils.ConstanceValue;
import butterknife.BindView;


/**
 * created by tl on 2019/8/30
 */
public class WelcomeActivity extends BaseActivity {

    @BindView(R.id.welcome_iv)
    ImageView welcome_iv;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_welcome;
    }

    @Override
    protected void initFragment() {
        checkPermissions();
    }


    private void toHome() {
        ObjectAnimator objectAnimator = AnimationUtils.alpha(welcome_iv);
        objectAnimator.setDuration(5000);
        objectAnimator.start();
        objectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                startActivity(new Intent(WelcomeActivity.this, HomeActivity.class));
                finish();
            }
        });
    }

    /**
     * 检查该设备是否打开蓝牙
     */
    private void checkBleSwitch() {
        boolean isOpen = false;
        BluetoothManager bluetoothManager = (BluetoothManager) WelcomeActivity.this.getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager != null) {
            BluetoothAdapter blueToothAdapter = bluetoothManager.getAdapter();
            if (blueToothAdapter == null || !blueToothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                WelcomeActivity.this.startActivity(enableBtIntent);
                Toast.makeText(WelcomeActivity.this, "Please allow Bluetooth to be turned on, otherwise the app cannot be used normally", Toast.LENGTH_SHORT).show();
            } else {
                isOpen = true;
            }
        } else {
            Toast.makeText(WelcomeActivity.this, "The app can't be used normally without Bluetooth", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 蓝牙和位置权限
     */
    private void requestBleAndLocation() {

        PermissionsUtil.requestPermission(this, new PermissionListener() {
            @Override
            public void permissionGranted(@NonNull String[] permissions) {
                // 跳转蓝牙主页
                toHome();

                // 检测蓝牙开关
                checkBleSwitch();

                // 获取经纬度
                initLocationManager();


            }

            @Override
            public void permissionDenied(@NonNull String[] permissions) {
                Log.e("Fuck", "用户拒绝了-蓝牙权限");
            }
        }, new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
        }, false, null);
    }

    /**
     * 短信权限
     */
    private void requestSms() {

        PermissionsUtil.requestPermission(this, new PermissionListener() {
            @Override
            public void permissionGranted(@NonNull String[] permissions) {
                // 读取短信
                ConmmonUtil.getUnreadSmsCount();
            }

            @Override
            public void permissionDenied(@NonNull String[] permissions) {
                Log.e("Fuck", "用户拒绝了-消息权限");
            }
        }, new String[]{
                Manifest.permission.READ_SMS,
                Manifest.permission.RECEIVE_SMS
        }, false, null);
    }

    /**
     * 电话权限
     */
    private void requestCall() {

        PermissionsUtil.requestPermission(this, new PermissionListener() {
            @Override
            public void permissionGranted(@NonNull String[] permissions) {
                // 读取未接电话
                ConmmonUtil.getMissCallCount();
            }

            @Override
            public void permissionDenied(@NonNull String[] permissions) {
                Log.e("Fuck", "用户拒绝了-电话权限");
            }
        }, new String[]{
                Manifest.permission.READ_CALL_LOG,
                Manifest.permission.WRITE_CALL_LOG
        }, false, null);
    }

    /**
     * 申请app所需要的权限
     */
    private void checkPermissions() {
        requestCall();
        requestSms();
        requestBleAndLocation();
    }

    public void initLocationManager() {
        // Get user location
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Keep track of user location.
        // Use callback/listener since requesting immediately may return null location.
        // IMPORTANT: TO GET GPS TO WORK, MAKE SURE THE LOCATION SERVICES ON YOUR PHONE ARE ON.
        // FOR ME, THIS WAS LOCATED IN SETTINGS > LOCATION.
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 10, new Listener());
        // Have another for GPS provider just in case.
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, new Listener());
        // Try to request the location immediately
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (location == null) {
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        if (location != null) {
            handleLatLng(location.getLatitude(), location.getLongitude());
        }
    }

    /**
     * Handle lat lng.
     */
    private void handleLatLng(double latitude, double longitude) {
        Log.i("handleLatLng", "-----------(" + latitude + "," + longitude + ")");
        ConmmonUtil.getWeather(latitude, longitude);
    }

    /**
     * Listener for changing gps coords.
     */
    private class Listener implements LocationListener {
        public void onLocationChanged(Location location) {
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            handleLatLng(latitude, longitude);
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }
}
