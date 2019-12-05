package com.tl.veger.navigation.bluetooth;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
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

import com.tl.veger.HomeActivity;
import com.tl.veger.R;
import com.tl.veger.base.BaseFragment;
import com.tl.veger.base.app.AppApplication;
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

import java.lang.ref.WeakReference;
import java.util.Map;

import butterknife.BindView;

public class BluetoothFragment extends BaseFragment implements View.OnClickListener {

    @BindView(R.id.search_bluetooth_icon)
    ImageView search_bluetooth_icon;
    @BindView(R.id.search_wave1)
    ImageView search_wave1;
    @BindView(R.id.search_wave2)
    ImageView search_wave2;
    @BindView(R.id.search_wave3)
    ImageView search_wave3;
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


    private final float ORIGINAL = 1.0f;
    private final float BORDER = 0.7f;
    private final float STARTX = 0.5f;
    private final float STARTY = 0.5f;
    private static final int STARTTWO = 2;
    private static final int STARTTHREE = 3;
    private AnimationSet animationSet1, animationSet2, animationSet3;
    private AnimiHandler handler;
    private static final int SHOW_SPACING_TIME = 700;
    private boolean isSearching;
    private ImageView quiet_iv;
    private CenterService centerService;
    private HomeActivity homeActivity;
    private BlueToothAdapter adapter;
    private Bitmap dimPic;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_bluetooth;
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
        initAnimation();
        setListView();
        setListener();
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

    private void initAnimation() {
        animationSet1 = initAnimationSet();
        animationSet2 = initAnimationSet();
        animationSet3 = initAnimationSet();
        handler = new AnimiHandler(this);
        startWaveAnimation();
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
            centerService.disconnectDev();
        }

        if (notice.contains("Characteristic发生改变")) {
            sendAllData(DataProtocolUtil.MSG_TYPE.MSG_WEATHER);
        }

        if (notice.contains("发送数据")) {
            sendAllData(DataProtocolUtil.MSG_TYPE.MSG_WEATHER);
        }

        if (notice.contains("sendFinish")) {
            sendAllData(ConstanceValue.curentType);
        }

    }

    //搜索状态
    private void searchingState() {
        adapter.clearData();
        centerService.disconnectDev();
        centerService.scanBle();
        homeActivity.canJesture(false);
        homeActivity.showQuiet(HomeActivity.QUIET);
        startWaveAnimation();
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
        stopWaveAnimation();
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
            ConstanceValue.current_light=Integer.parseInt((String) map.get(ConstanceValue.SWITCH_LIGHT));
            ConstanceValue.current_bluetooth = Integer.parseInt((String) map.get(ConstanceValue.SWITCH_BLUETOOTH));
            ConstanceValue.current_jesture = Integer.parseInt((String) map.get(ConstanceValue.SWITCH_JESTURE));
            ConstanceValue.current_time = Integer.parseInt((String) map.get(ConstanceValue.SWITCH_TIME));
            ConstanceValue.current_weather = Integer.parseInt((String) map.get(ConstanceValue.SWITCH_WEATHER));
            ConstanceValue.current_battery = Integer.parseInt((String) map.get(ConstanceValue.SWITCH_BATTERY));
            ConstanceValue.current_message = Integer.parseInt((String) map.get(ConstanceValue.SWITCH_MESSAGE));
            ConstanceValue.current_call = Integer.parseInt((String) map.get(ConstanceValue.SWITCH_CALL));
            ConstanceValue.current_email = Integer.parseInt((String) map.get(ConstanceValue.SWITCH_EMAIL));
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                sendAllData(DataProtocolUtil.MSG_TYPE.MSG_WEATHER);
            }
        },1000);
    }


    //连接失败状态
    private void connectFailedState() {
        stopWaveAnimation();
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


    public void stopWaveAnimation() {
        isSearching = false;
        search_wave1.clearAnimation();
        search_wave2.clearAnimation();
        search_wave3.clearAnimation();
    }

    public void startWaveAnimation() {
        isSearching = true;
        search_wave1.startAnimation(animationSet1);
        handler.sendEmptyMessageDelayed(STARTTWO, SHOW_SPACING_TIME);
        handler.sendEmptyMessageDelayed(STARTTHREE, SHOW_SPACING_TIME * 2);
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
                centerService.disconnectDev();
                break;
        }
    }


    private static class AnimiHandler extends Handler {
        private WeakReference<BluetoothFragment> fragmentWeakReference;

        public AnimiHandler(BluetoothFragment fragment) {
            this.fragmentWeakReference = new WeakReference<BluetoothFragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            BluetoothFragment bluetoothFragment = fragmentWeakReference.get();
            if (bluetoothFragment != null) {
                switch (msg.what) {
                    case STARTTWO:
                        bluetoothFragment.search_wave2.startAnimation(bluetoothFragment
                                .animationSet2);
                        break;

                    case STARTTHREE:
                        bluetoothFragment.search_wave3.startAnimation(bluetoothFragment
                                .animationSet3);
                }
            }
        }
    }

    /**
     * 初始化动画集
     *
     * @return
     */
    private AnimationSet initAnimationSet() {
        AnimationSet as = new AnimationSet(true);
        //缩放度：变大两倍
        ScaleAnimation sa = new ScaleAnimation(ORIGINAL, ORIGINAL + 3 * BORDER, ORIGINAL,
                ORIGINAL + 3 * BORDER,
                ScaleAnimation.RELATIVE_TO_SELF, STARTX,
                ScaleAnimation.RELATIVE_TO_SELF, STARTY);
        sa.setDuration(SHOW_SPACING_TIME * 3);//播放时长
        sa.setRepeatCount(Animation.INFINITE);// 设置循环
        //透明度
        AlphaAnimation aa = new AlphaAnimation(1, 0.1f);
        aa.setDuration(SHOW_SPACING_TIME * 3);
        aa.setRepeatCount(Animation.INFINITE);//设置循环
        as.addAnimation(sa);
        as.addAnimation(aa);
        return as;
    }


    private void sendAllData(DataProtocolUtil.MSG_TYPE type) {
        if (type.equals(DataProtocolUtil.MSG_TYPE.MSG_WEATHER)) {
            sendWeather();
            ConstanceValue.curentType = DataProtocolUtil.MSG_TYPE.MSG_TIME;
        } else if (type.equals(DataProtocolUtil.MSG_TYPE.MSG_TIME)) {
            sendTime();
            ConstanceValue.curentType = DataProtocolUtil.MSG_TYPE.MSG_BATTERY;
        } else if (type.equals(DataProtocolUtil.MSG_TYPE.MSG_BATTERY)) {
            sendBattery();
            ConstanceValue.curentType = DataProtocolUtil.MSG_TYPE.MSG_OPTIONSWITCH;
        } else if (type.equals(DataProtocolUtil.MSG_TYPE.MSG_OPTIONSWITCH)) {
            sendSwitch();
            ConstanceValue.curentType = DataProtocolUtil.MSG_TYPE.MSG_BRIGHTNESS;
        } else if (type.equals(DataProtocolUtil.MSG_TYPE.MSG_BRIGHTNESS)) {
            sendlight();
            ConstanceValue.curentType = DataProtocolUtil.MSG_TYPE.MSG_CALL;
        } else if (type.equals(DataProtocolUtil.MSG_TYPE.MSG_CALL)) {
            sendCall();
            ConstanceValue.curentType = DataProtocolUtil.MSG_TYPE.MSG_MESSAGE;
        } else if (type.equals(DataProtocolUtil.MSG_TYPE.MSG_MESSAGE)) {
            sendMsg();
            ConstanceValue.curentType = DataProtocolUtil.MSG_TYPE.MSG_EMAIL;
        } else if (type.equals(DataProtocolUtil.MSG_TYPE.MSG_EMAIL)) {
            sendGmail();
            ConstanceValue.curentType = DataProtocolUtil.MSG_TYPE.MSG_END;
        }
    }


    private void sendWeather() {
        centerService.sendData(DataProtocolUtil.getData(DataProtocolUtil.MSG_TYPE.MSG_WEATHER, ConstanceValue.WEATHER));
    }

    private void sendTime() {
        centerService.sendData(DataProtocolUtil.getData(DataProtocolUtil.MSG_TYPE.MSG_TIME, ConmmonUtil.getCurrentTimeForByte()));
    }

    private void sendSwitch() {
        centerService.sendData(DataProtocolUtil.getData(DataProtocolUtil.MSG_TYPE.MSG_OPTIONSWITCH, ConmmonUtil.getSwitchSate()));
    }

    private void sendCall() {
        byte[] call = new byte[1];
        if (ConstanceValue.MISS_CALL > 0) {
            call[0] = 1;
        }
        centerService.sendData(DataProtocolUtil.getData(DataProtocolUtil.MSG_TYPE.MSG_CALL, call));
    }

    private void sendBattery() {
        byte[] battery = new byte[3];
        battery[0] = (byte) ConstanceValue.CURRENT_BATTERY_PERCENT;
        int state = ConstanceValue.IS_CHARGING ? 1 : 0;
        battery[1] = (byte) state;
        battery[2] = (byte) ((100 - ConstanceValue.CURRENT_BATTERY_PERCENT) / 10);
        centerService.sendData(DataProtocolUtil.getData(DataProtocolUtil.MSG_TYPE.MSG_BATTERY, battery));
    }


    private void sendMsg() {
        byte[] message = new byte[1];
        if (ConstanceValue.UNREAD_SMS > 0) {
            message[0] = 1;
        }
        centerService.sendData(DataProtocolUtil.getData(DataProtocolUtil.MSG_TYPE.MSG_MESSAGE, message));
    }


    private void sendlight() {
        byte[] light = new byte[1];
        int ll = ConstanceValue.current_light;
        light[0] = (byte) ll;
        centerService.sendData(DataProtocolUtil.getData(DataProtocolUtil.MSG_TYPE.MSG_BRIGHTNESS, light));
    }

    private void sendGmail() {
        byte[] gmial = new byte[1];
        gmial[0] = (byte) ConstanceValue.UNREAD_MAIL;
        centerService.sendData(DataProtocolUtil.getData(DataProtocolUtil.MSG_TYPE.MSG_EMAIL, gmial));
    }


    @Override
    public void onDetach() {
        super.onDetach();
        EventBus.getDefault().unregister(this);
        if (centerService != null) {
            centerService.unbindService(connection);
        }
    }
}