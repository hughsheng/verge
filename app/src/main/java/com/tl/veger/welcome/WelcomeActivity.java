package com.tl.veger.welcome;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
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

import com.tl.veger.HomeActivity;
import com.tl.veger.R;
import com.tl.veger.base.BaseActivity;
import com.tl.veger.gmail.GoogleLoginActivity;
import com.tl.veger.utils.AnimationUtils;
import com.tl.veger.utils.ConmmonUtil;
import com.tl.veger.utils.ConstanceValue;
import com.tl.veger.utils.PermissionUtils;

import butterknife.BindView;

/**
 * created by tl on 2019/8/30
 */
public class WelcomeActivity extends BaseActivity implements PermissionUtils.PermissionListener {

    @BindView(R.id.welcome_iv)
    ImageView welcome_iv;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_welcome;
    }

    @Override
    protected void initFragment() {
        checkPermissions();

        initLocationManager();
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

    private void checkPermissions() {
        PermissionUtils permissionUtils = PermissionUtils.getInstance(this, this);
        permissionUtils.checkPermission(PermissionUtils.REQUEST_SMS);
        permissionUtils.checkPermission(PermissionUtils.REQUEST_CALL);
        permissionUtils.checkPermission(PermissionUtils.REQUEST_LOCATION);
        permissionUtils.checkPermission(PermissionUtils.REQUEST_BLUETOOTH);

        ConmmonUtil.getUnreadSmsCount();
        ConmmonUtil.getMissCallCount();

        //permissionUtils.checkPermission(PermissionUtils.REQUEST_NOTIFICATION);
    }

    @Override
    public void onPermissionResult(boolean hasPermission, int permissionCode) {
        switch (permissionCode) {
            case PermissionUtils.REQUEST_BLUETOOTH:
                toHome();
                break;
        }
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
