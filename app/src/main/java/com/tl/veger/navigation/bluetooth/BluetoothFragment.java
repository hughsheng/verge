package com.tl.veger.navigation.bluetooth;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yann.waveapplication.WaveView;
import com.tl.veger.HomeActivity;
import com.tl.veger.R;
import com.tl.veger.base.BaseFragment;
import com.tl.veger.base.app.AppApplication;
import com.tl.veger.gmail.ApiAsyncTask;
import com.tl.veger.navigation.bluetooth.adapter.BlueToothAdapter;
import com.tl.veger.busbean.BluetoothBusBean;
import com.tl.veger.navigation.bluetooth.adapter.SpaceItemDecoration;
import com.tl.veger.navigation.bluetooth.service.CenterService;
import com.tl.veger.utils.ConmmonUtil;
import com.tl.veger.utils.ConstanceValue;
import com.tl.veger.utils.DataProtocolUtil;
import com.tl.veger.utils.PictureUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.Map;

import butterknife.BindView;

import static com.tl.veger.utils.ConstanceValue.currentIs24HourFormat;

public class BluetoothFragment extends BaseFragment implements View.OnClickListener {

  @BindView(R.id.search_bluetooth_icon)
  ImageView search_bluetooth_icon;
  @BindView(R.id.search_rv)
  RecyclerView search_rv;
  @BindView(R.id.search_bottom_tip_iv)
  ImageView search_bottom_tip_iv;
  @BindView(R.id.search_state_tv)
  TextView search_state_tv;
  @BindView(R.id.bluetooth_bg_iv)
  ImageView bluetooth_bg_iv;
  @BindView(R.id.bluetooth_connect_iv)
  ImageView bluetooth_connect_iv;
  @BindView(R.id.waveView)
  WaveView waveView;

  public static final String TAG = "BluetoothFragment";
  private ImageView quiet_iv;
  private CenterService centerService;
  private HomeActivity homeActivity;
  private BlueToothAdapter adapter;

  @Override
  protected int getLayoutResId() {
    return R.layout.fragment_bluetooth;
  }


  public static BluetoothFragment newInstance() {

    Bundle args = new Bundle();

    BluetoothFragment fragment = new BluetoothFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onAttach(@NonNull Context context) {
    super.onAttach(context);
    EventBus.getDefault().register(this);
    homeActivity = (HomeActivity) context;
    quiet_iv = homeActivity.getQuietView();
    homeActivity.showQuiet(HomeActivity.QUIET);
    openService();
  }

  @Override
  protected void init() {
    waveView.setWaveStart(true);
    setListView();
    setListener();
    startsendThread();
  }


  private void setListView() {
    adapter = new BlueToothAdapter(getContext(), R.layout.item_bluetooth);
    adapter.setListener(new BlueToothAdapter.BlueToothAdapterListener() {
      @Override
      public void connect(CenterService.BleDev bleDev) {
        if (centerService != null) {
          centerService.connectDev(bleDev.dev);
        }
      }
    });

    GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
    search_rv.addItemDecoration(new SpaceItemDecoration());
    search_rv.setLayoutManager(layoutManager);
    search_rv.setAdapter(adapter);
  }


  private void setListener() {
    quiet_iv.setOnClickListener(this);
    search_bluetooth_icon.setOnClickListener(this);
    search_bluetooth_icon.setClickable(false);
    bluetooth_connect_iv.setOnClickListener(this);
//    Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.texgyreadventor);
//    search_state_tv.setTypeface(typeface);
  }


  ServiceConnection connection = new ServiceConnection() {
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
      if (service instanceof CenterService.CenterServiceBinder) {
        CenterService.CenterServiceBinder binder = (CenterService.CenterServiceBinder) service;
        centerService = binder.getCenterService();
        centerService.scanBle();
        //  Toast.makeText(getContext(), "服务启动成功", Toast.LENGTH_SHORT).show();
      }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
      //  Toast.makeText(getContext(), "服务启动失败", Toast.LENGTH_SHORT).show();
    }
  };


  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onEvent(BluetoothBusBean bean) {
    String notice = bean.getNotice();
    //  Toast.makeText(getContext(), notice, Toast.LENGTH_SHORT).show();
    if (notice.contains("连接断开") || notice.contains("连接出错")) {
      connectFailedState();
      homeActivity.jumpToBluetooth();
      ConstanceValue.bluetoothQueue.clear();

      //非主动断开自动重连
      if (!ConstanceValue.isActiveDisconnect) {
        centerService.connectDev(bean.getConnectedDev());
      }
    }

    if (notice.contains("连接成功")) {
      connectedState();
    }

    if (notice.contains("开始连接")) {
      connectingState();
    }

    if (notice.contains("扫描结束")) {

    }

    if (notice.contains("有新设备")) {
      adapter.setData(centerService.getDevList());
    }
    if (notice.contains("开关断开蓝牙")) {
      ConstanceValue.isActiveDisconnect = true;
      centerService.disconnectDev();
    }

    if (notice.contains("Characteristic发生改变")) {
      //  sendUpdateData();
    }

    if (notice.contains("sendFinish")) {
      ConstanceValue.sendFinish = true;
    }

    //发送具体数据
    switch (notice) {
      case ConstanceValue.SWITCH_LIGHT:
        sendSwitch();
        sendlight();
        break;

      case ConstanceValue.SWITCH_JESTURE:
        sendSwitch();
        break;

      case ConstanceValue.SWITCH_TIME:
        sendSwitch();
        sendTime();
        break;

      case ConstanceValue.SWITCH_WEATHER:
        sendSwitch();
        sendWeather();
        break;
      case ConstanceValue.SWITCH_BATTERY:
        sendSwitch();
        sendBattery();
        break;
      case ConstanceValue.SWITCH_MESSAGE:
        sendSwitch();
        sendMsg();
        break;

      case ConstanceValue.SWITCH_CALL:
        sendSwitch();
        sendCall();
        break;

      case ConstanceValue.SWITCH_EMAIL:
        sendSwitch();
        sendGmail();
        break;
    }
  }

  //搜索状态
  private void searchingState() {
    waveView.setWaveStart(true);
    adapter.clearData();
    centerService.disconnectDev();
    centerService.scanBle();
    homeActivity.canJesture(false);
    homeActivity.showQuiet(HomeActivity.QUIET);
    search_bluetooth_icon.setClickable(false);
    bluetooth_connect_iv.setVisibility(View.INVISIBLE);
    search_bluetooth_icon.setVisibility(View.VISIBLE);
    search_bluetooth_icon.setImageResource(R.mipmap.bluetooth_no_circle);
    search_rv.setVisibility(View.VISIBLE);
    search_state_tv.setText(R.string.state_device_nearby);
    search_bottom_tip_iv.setImageResource(R.mipmap.searching_tip);
    bluetooth_bg_iv.setVisibility(View.INVISIBLE);
  }


  //正在连接状态
  private void connectingState() {
    centerService.stopScanBle();
    homeActivity.canJesture(false);
    homeActivity.showQuiet(HomeActivity.NONE);
    search_rv.setVisibility(View.INVISIBLE);
    search_bluetooth_icon.setClickable(false);
    search_bluetooth_icon.setImageResource(R.mipmap.bluetooth_no_circle);
    search_state_tv.setText(R.string.state_bluetooth_pairing);
    search_bottom_tip_iv.setImageResource(R.mipmap.connecting_tip);
    bluetooth_bg_iv.setVisibility(View.VISIBLE);
    bluetooth_bg_iv.setImageResource(R.mipmap.veger_bg);
  }


  //已连接状态
  private void connectedState() {
    waveView.setWaveStart(false);
    ConstanceValue.isActiveDisconnect = false;
    homeActivity.canJesture(true);
    homeActivity.showQuiet(HomeActivity.DRAWER);
    search_state_tv.setText(R.string.state_bluetooth_paired);
    search_bottom_tip_iv.setImageResource(R.mipmap.connected_tip);
    bluetooth_connect_iv.setVisibility(View.VISIBLE);
    search_bluetooth_icon.setVisibility(View.INVISIBLE);
    bluetooth_bg_iv.setVisibility(View.VISIBLE);
    bluetooth_bg_iv.setImageResource(R.mipmap.veger_bg);

    Map map = AppApplication.getInstance().getMapData(ConstanceValue.SWITCH);
    if (map.size() > 1) {
      ConstanceValue.current_light =
          Integer.parseInt((String) map.get(ConstanceValue.SWITCH_LIGHT));
      ConstanceValue.current_bluetooth =
          Integer.parseInt((String) map.get(ConstanceValue.SWITCH_BLUETOOTH));
      ConstanceValue.current_jesture =
          Integer.parseInt((String) map.get(ConstanceValue.SWITCH_JESTURE));
      ConstanceValue.current_time = Integer.parseInt((String) map.get(ConstanceValue.SWITCH_TIME));
      ConstanceValue.current_weather =
          Integer.parseInt((String) map.get(ConstanceValue.SWITCH_WEATHER));
      ConstanceValue.current_battery =
          Integer.parseInt((String) map.get(ConstanceValue.SWITCH_BATTERY));
      ConstanceValue.current_message =
          Integer.parseInt((String) map.get(ConstanceValue.SWITCH_MESSAGE));
      ConstanceValue.current_call = Integer.parseInt((String) map.get(ConstanceValue.SWITCH_CALL));
      ConstanceValue.current_email =
          Integer.parseInt((String) map.get(ConstanceValue.SWITCH_EMAIL));
    }

    //
    new Handler().postDelayed(new Runnable() {
      @Override
      public void run() {
        ConstanceValue.sendFinish = true;
        sendAllData();
        startUpdateDataThread();
      }
    }, 1000);
  }


  //连接失败状态
  private void connectFailedState() {
    waveView.setWaveStart(false);
    homeActivity.canJesture(false);
    homeActivity.showQuiet(HomeActivity.NONE);
    search_bluetooth_icon.setClickable(true);
    search_rv.setVisibility(View.INVISIBLE);
    search_state_tv.setText(R.string.state_pairing_failed);
    search_bottom_tip_iv.setImageResource(R.mipmap.connect_fali_tip);
    bluetooth_connect_iv.setVisibility(View.INVISIBLE);
    search_bluetooth_icon.setVisibility(View.VISIBLE);
    search_bluetooth_icon.setImageResource(R.mipmap.bluetooth);
    bluetooth_bg_iv.setVisibility(View.VISIBLE);
    bluetooth_bg_iv.setImageResource(R.mipmap.veger_bg_mohu);
  }


  //开启蓝牙连接服务
  private void openService() {
    Intent serviceIntent = new Intent(getContext(), CenterService.class);
    homeActivity.bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);
  }


  //循环发送蓝牙队列里的数据
  private void startsendThread() {
    new Thread() {
      @Override
      public void run() {
        while (true) {
          try {
            Thread.sleep(100);
            synchronized (ConstanceValue.bluetoothQueue) {
              if (ConstanceValue.sendFinish && ConstanceValue.bluetoothQueue != null) {
                byte[] data = ConstanceValue.bluetoothQueue.poll();
                if (data != null && centerService != null) {
                  ConstanceValue.sendFinish = false;
                  centerService.sendData(data);
                }

              }
            }
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }
    }.start();
  }


  //更新数据线程
  private void startUpdateDataThread() {
    new Thread() {
      @Override
      public void run() {
        while (true) {
          try {
            Thread.sleep(1000);

            if (ConmmonUtil.getUnreadSmsCount()) {
              sendMsg();
            }

            if (ConmmonUtil.getMissCallCount()) {
              sendCall();
            }
            if (ConmmonUtil.getDataUnreadNum(getActivity())>0) {
              sendGmail();
            }

            if (!currentIs24HourFormat == DateFormat.is24HourFormat(AppApplication.getInstance())) {
              currentIs24HourFormat = DateFormat.is24HourFormat(AppApplication.getInstance());
              sendTime();
            }

          } catch (Exception e) {
            e.printStackTrace();
          }
        }

      }
    }.start();
  }


  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.toolbar_quiet://到失败状态
        centerService.stopScanBle();
        connectFailedState();
        break;

      case R.id.search_bluetooth_icon://连接失败重新搜索
        searchingState();
        break;

      case R.id.bluetooth_connect_iv://断开
        ConstanceValue.isActiveDisconnect = true;
        centerService.disconnectDev();
        break;
    }
  }


  private void sendAllData() {
    sendWeather();
    sendTime();
    sendBattery();
    sendSwitch();
    sendlight();
    sendCall();
    sendMsg();
    sendGmail();
  }


  //发送天气
  private void sendWeather() {
    addData(DataProtocolUtil.getData(DataProtocolUtil.MSG_TYPE.MSG_WEATHER,
				ConstanceValue.WEATHER));
//    Toast.makeText(getContext(),
//				"温度：" + ConstanceValue.WEATHER[0] + "---------" + "icon=" + ConstanceValue.WEATHER[1] +
//						"---------" + "温度零上还是零下=" + ConstanceValue.WEATHER[2], Toast.LENGTH_SHORT).show();
  }

  //发送时间
  private void sendTime() {
    addData(DataProtocolUtil.getData(DataProtocolUtil.MSG_TYPE.MSG_TIME,
        ConmmonUtil.getCurrentTimeForByte()));
  }

  //发送开关
  private void sendSwitch() {
    addData(DataProtocolUtil.getData(DataProtocolUtil.MSG_TYPE.MSG_OPTIONSWITCH,
        ConmmonUtil.getSwitchSate()));
  }

  //发送未接来电
  private void sendCall() {
    byte[] call = new byte[1];
    if (ConstanceValue.MISS_CALL > 0) {
      call[0] = 1;
    }
    addData(DataProtocolUtil.getData(DataProtocolUtil.MSG_TYPE.MSG_CALL, call));
  }

  //发送电量
  private void sendBattery() {
    byte[] battery = new byte[3];
    battery[0] = (byte) ConstanceValue.CURRENT_BATTERY_PERCENT;
    int state = ConstanceValue.IS_CHARGING ? 1 : 0;
    battery[1] = (byte) state;
    double tmp = (100 - ConstanceValue.CURRENT_BATTERY_PERCENT) / 100.0;
    battery[2] = (byte) (tmp * (3.5 * 60));

    addData(DataProtocolUtil.getData(DataProtocolUtil.MSG_TYPE.MSG_BATTERY,
        battery));
//		Toast.makeText(getContext(), "发送电量状态：" + state+"tmp="+tmp+"电池电量为："+ ConstanceValue
//		.CURRENT_BATTERY_PERCENT, Toast.LENGTH_LONG).show();
  }


  private void sendMsg() {
    byte[] message = new byte[1];
    if (ConstanceValue.UNREAD_SMS > 0) {
      message[0] = 1;
    }

    addData(DataProtocolUtil.getData(DataProtocolUtil.MSG_TYPE.MSG_MESSAGE,
        message));
  }


  //发送亮度
  private void sendlight() {
    byte[] light = new byte[1];
    int ll = ConstanceValue.current_light;
    light[0] = (byte) ll;

    addData(DataProtocolUtil.getData(DataProtocolUtil.MSG_TYPE.MSG_BRIGHTNESS,
        light));
  }

  //发送gmail未读邮件
  private void sendGmail() {
    byte[] gmial = new byte[1];
    gmial[0] = (byte) ConstanceValue.UNREAD_MAIL;
    addData(DataProtocolUtil.getData(DataProtocolUtil.MSG_TYPE.MSG_EMAIL, gmial));
  }


  private void addData(byte[] data) {
    synchronized (ConstanceValue.bluetoothQueue) {
      ConstanceValue.bluetoothQueue.offer(data);
    }
  }


  @Override
  public void onDestroy() {
    super.onDestroy();
    EventBus.getDefault().unregister(this);
    if (centerService != null) {
      centerService.disconnectDev();
      centerService.unbindService(connection);
    }
  }

}