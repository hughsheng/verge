package com.tl.veger;

import android.app.Notification;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.tl.veger.base.BaseActivity;
import com.tl.veger.busbean.BluetoothBusBean;
import com.tl.veger.busbean.NotificationPostedBusBean;
import com.tl.veger.receiver.BatteryReceiver;
import com.tl.veger.utils.ConmmonUtil;
import com.tl.veger.utils.ConstanceValue;
import com.tl.veger.utils.DataProtocolUtil;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.Toolbar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import butterknife.BindView;

public class HomeActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

  public static final int QUIET = 0x2001;
  public static final int DRAWER = 0x2002;
  public static final int NONE = 0x2003;
  private NavController navController;
  private BatteryReceiver batteryReceiver;

  @BindView(R.id.toolbar)
  Toolbar toolbar;
  @BindView(R.id.drawer_layout)
  DrawerLayout drawer;
  @BindView(R.id.nav_view)
  NavigationView navigationView;
  @BindView(R.id.toolbar_menu_iv)
  ImageView toolbar_menu_iv;
  @BindView(R.id.nav_host_fragment)
  FrameLayout nav_host_fragment;
  @BindView(R.id.toolbar_quiet)
  ImageView toolbar_quiet;


  @Override
  protected int getLayoutResId() {
    return R.layout.activity_main;
  }

  @Override
  protected void initFragment() {
    EventBus.getDefault().register(this);
    toolbar_menu_iv.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
          drawer.closeDrawer(GravityCompat.START);
        } else {
          drawer.openDrawer(GravityCompat.START);
        }
      }
    });
    batteryReceiver=new BatteryReceiver();
    registerReceiver(batteryReceiver,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    initNavigation();
  }


  private void initNavigation() {
    drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    navController = Navigation.findNavController(this, R.id.nav_host_fragment);
    navigationView.setNavigationItemSelectedListener(this);
    //  NavigationUI.setupWithNavController(navigationView, navController);
    ImageView close = navigationView.getHeaderView(0).findViewById(R.id.close_iv);
    close.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        drawer.closeDrawer(GravityCompat.START);
      }
    });
    //  initNavigationMenu();
  }


  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onEvent(NotificationPostedBusBean bean) {
    Notification notification=bean.getNotification();
    Bundle extra=notification.extras;
    String title=extra.getString(Notification.EXTRA_TITLE, "");
    String content = extra.getString(Notification.EXTRA_TEXT, "");
    Log.i("mylog","title="+title+"content="+content);
  }


  //是否显示退出图标
  public void showQuiet(int state) {
    switch (state) {
      case QUIET:
        toolbar_quiet.setVisibility(View.VISIBLE);
        toolbar_menu_iv.setVisibility(View.INVISIBLE);
        break;

      case DRAWER:
        toolbar_quiet.setVisibility(View.INVISIBLE);
        toolbar_menu_iv.setVisibility(View.VISIBLE);
        break;

      case NONE:
        toolbar_quiet.setVisibility(View.INVISIBLE);
        toolbar_menu_iv.setVisibility(View.INVISIBLE);
        break;
    }

  }


  //侧边栏是否支持手势滑动
  public void canJesture(boolean isJesture) {
    if (isJesture) {
      drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    } else {
      drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }
  }


  public ImageView getQuietView() {
    return toolbar_quiet;
  }


  public void jumpToBluetooth() {
    String label = "";
    NavDestination currentDestination = navController.getCurrentDestination();
    if (currentDestination != null) {
      label = currentDestination.getLabel() + "";
    }
    if (!label.equals(getString(R.string.menu_bluetooth))) {
      navController.navigate(R.id.nav_bluetooth);
    }
  }



  @Override
  public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
    String label = "";
    NavDestination currentDestination = navController.getCurrentDestination();
    if (currentDestination != null) {
      label = currentDestination.getLabel() + "";
    }
    switch (menuItem.getItemId()) {
      case R.id.nav_controlPanel:
        if (!label.equals(getString(R.string.menu_control_panel))) {
          navController.navigate(R.id.nav_controlPanel);
        }
        drawer.closeDrawer(GravityCompat.START);
        break;

      case R.id.nav_chargingStatus:
        if (!label.equals(getString(R.string.menu_charging_status))) {
          navController.navigate(R.id.nav_chargingStatus);
        }
        drawer.closeDrawer(GravityCompat.START);
        break;

      case R.id.nav_softwareUpdate:
//        navController.navigate(R.id.nav_softwareUpdate);
//        drawer.closeDrawer(GravityCompat.START);
        break;
    }
    return false;
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    unregisterReceiver(batteryReceiver);
    EventBus.getDefault().unregister(this);
  }
}
