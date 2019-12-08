package com.tl.veger.navigation.controlPanel;


import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.tl.veger.R;
import com.tl.veger.base.BaseFragment;
import com.tl.veger.base.app.AppApplication;
import com.tl.veger.busbean.BluetoothBusBean;
import com.tl.veger.utils.ConstanceValue;
import com.tl.veger.utils.SharedPreferencesUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Handler;

import butterknife.BindView;


public class ControlPanelFragment extends BaseFragment implements RadioGroup.OnCheckedChangeListener,
    View.OnClickListener, CompoundButton.OnCheckedChangeListener {


  @BindView(R.id.rg)
  RadioGroup rg;
  @BindView(R.id.beam_rb)
  RadioButton beam_rb;
  @BindView(R.id.mild_rb)
  RadioButton mild_rb;
  @BindView(R.id.glimmer_rb)
  RadioButton glimmer_rb;
  @BindView(R.id.off_rb)
  RadioButton off_rb;
  @BindView(R.id.luminance_iv)
  ImageView luminance_iv;
  @BindView(R.id.connection_cb)
  CheckBox connection_cb;
  @BindView(R.id.gesture_cb)
  CheckBox gesture_cb;
  @BindView(R.id.clock_cb)
  CheckBox clock_cb;
  @BindView(R.id.weather_cb)
  CheckBox weather_cb;
  @BindView(R.id.battery_cb)
  CheckBox battery_cb;
  @BindView(R.id.message_cb)
  CheckBox message_cb;
  @BindView(R.id.call_cb)
  CheckBox call_cb;
  @BindView(R.id.email_cb)
  CheckBox email_cb;


  private boolean isOpen = true;
  private int lastOpenId;
  private AppApplication appApplication;

  @Override
  protected int getLayoutResId() {
    return R.layout.fragment_control_panel;
  }

  @Override
  protected void init() {
    appApplication = AppApplication.getInstance();
    lastOpenId = rg.getCheckedRadioButtonId();
    luminance_iv.setOnClickListener(this);
    rg.setOnCheckedChangeListener(this);
    connection_cb.setOnCheckedChangeListener(this);
    gesture_cb.setOnCheckedChangeListener(this);
    clock_cb.setOnCheckedChangeListener(this);
    weather_cb.setOnCheckedChangeListener(this);
    battery_cb.setOnCheckedChangeListener(this);
    message_cb.setOnCheckedChangeListener(this);
    call_cb.setOnCheckedChangeListener(this);
    email_cb.setOnCheckedChangeListener(this);
    setSwitch();
  }


  //初始化开关状态
  private void setSwitch() {
    int bluetooth, jesture, time, weather, battery, message, call, email, light;
    Map map = appApplication.getMapData(ConstanceValue.SWITCH);
    if (map.size() > 1) {
      light = Integer.parseInt((String) map.get(ConstanceValue.SWITCH_LIGHT));
      bluetooth = Integer.parseInt((String) map.get(ConstanceValue.SWITCH_BLUETOOTH));
      jesture = Integer.parseInt((String) map.get(ConstanceValue.SWITCH_JESTURE));
      time = Integer.parseInt((String) map.get(ConstanceValue.SWITCH_TIME));
      weather = Integer.parseInt((String) map.get(ConstanceValue.SWITCH_WEATHER));
      battery = Integer.parseInt((String) map.get(ConstanceValue.SWITCH_BATTERY));
      message = Integer.parseInt((String) map.get(ConstanceValue.SWITCH_MESSAGE));
      call = Integer.parseInt((String) map.get(ConstanceValue.SWITCH_CALL));
      email = Integer.parseInt((String) map.get(ConstanceValue.SWITCH_EMAIL));
    } else {
      light = ConstanceValue.current_light;
      bluetooth = ConstanceValue.current_bluetooth;
      jesture = ConstanceValue.current_jesture;
      time = ConstanceValue.current_time;
      weather = ConstanceValue.current_weather;
      battery = ConstanceValue.current_battery;
      message = ConstanceValue.current_message;
      call = ConstanceValue.current_call;
      email = ConstanceValue.current_email;
      saveSwitchState();
    }

    if (jesture == 1) {
      gesture_cb.setChecked(true);
    } else {
      gesture_cb.setChecked(false);
    }

    if (time == 1) {
      clock_cb.setChecked(true);
    } else {
      clock_cb.setChecked(false);
    }

    if (weather == 1) {
      weather_cb.setChecked(true);
    } else {
      weather_cb.setChecked(false);
    }

    if (battery == 1) {
      battery_cb.setChecked(true);
    } else {
      battery_cb.setChecked(false);
    }

    if (message == 1) {
      message_cb.setChecked(true);
    } else {
      message_cb.setChecked(false);
    }

    if (call == 1) {
      call_cb.setChecked(true);
    } else {
      call_cb.setChecked(false);
    }

    if (email == 1) {
      email_cb.setChecked(true);
    } else {
      email_cb.setChecked(false);
    }

    switch (light) {
      case 0:
        off_rb.setChecked(true);
        break;

      case 20:
        glimmer_rb.setChecked(true);
        break;

      case 60:
        mild_rb.setChecked(true);
        break;

      case 100:
        beam_rb.setChecked(true);
        break;
    }

  }


  //亮度
  @Override
  public void onCheckedChanged(RadioGroup group, int checkedId) {
    switch (checkedId) {
      case R.id.beam_rb:
        isOpen = true;
        lastOpenId = rg.getCheckedRadioButtonId();
        luminance_iv.setImageResource(R.drawable.luminance_selected);
        ConstanceValue.current_light = 100;
        sendSwitch(ConstanceValue.SWITCH_LIGHT);
        break;

      case R.id.mild_rb:
        isOpen = true;
        lastOpenId = rg.getCheckedRadioButtonId();
        luminance_iv.setImageResource(R.drawable.luminance_selected);
        ConstanceValue.current_light = 60;
        sendSwitch(ConstanceValue.SWITCH_LIGHT);
        break;

      case R.id.glimmer_rb:
        isOpen = true;
        lastOpenId = rg.getCheckedRadioButtonId();
        luminance_iv.setImageResource(R.drawable.luminance_selected);
        ConstanceValue.current_light = 20;
        sendSwitch(ConstanceValue.SWITCH_LIGHT);
        break;

      case R.id.off_rb:
        isOpen = false;
        luminance_iv.setImageResource(R.drawable.luminance_unselected);
        ConstanceValue.current_light = 0;
        sendSwitch(ConstanceValue.SWITCH_LIGHT);
        break;
    }
  }


  @Override
  public void onClick(View v) {
    int id = v.getId();
    switch (id) {
      case R.id.luminance_iv:
        if (!isOpen) {
          isOpen = true;
          luminance_iv.setImageResource(R.drawable.luminance_selected);
          switch (lastOpenId) {
            case R.id.beam_rb:
              beam_rb.setChecked(true);
              break;
            case R.id.mild_rb:
              mild_rb.setChecked(true);
              break;
            case R.id.glimmer_rb:
              glimmer_rb.setChecked(true);
              break;
          }
        } else {
          lastOpenId = rg.getCheckedRadioButtonId();
          isOpen = false;
          off_rb.setChecked(true);
          luminance_iv.setImageResource(R.drawable.luminance_unselected);
        }
        break;
    }
  }


  //开关
  @Override
  public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    int id = buttonView.getId();
    switch (id) {
      case R.id.connection_cb:
        if (!isChecked) {
          BluetoothBusBean bluetoothBusBean = new BluetoothBusBean();
          bluetoothBusBean.setNotice("开关断开蓝牙");
          EventBus.getDefault().post(bluetoothBusBean);
        }
        break;

      case R.id.gesture_cb:
        if (gesture_cb.isChecked()) {
          ConstanceValue.current_jesture = 1;
        } else {
          ConstanceValue.current_jesture = 0;
        }
        sendSwitch(ConstanceValue.SWITCH_JESTURE);
        break;
      case R.id.clock_cb:
        if (clock_cb.isChecked()) {
          ConstanceValue.current_time = 1;
        } else {
          ConstanceValue.current_time = 0;
        }
        sendSwitch(ConstanceValue.SWITCH_TIME);
        break;
      case R.id.weather_cb:
        if (weather_cb.isChecked()) {
          ConstanceValue.current_weather = 1;
        } else {
          ConstanceValue.current_weather = 0;
        }
        sendSwitch(ConstanceValue.SWITCH_WEATHER);
        break;
      case R.id.battery_cb:
        if (battery_cb.isChecked()) {
          ConstanceValue.current_battery = 1;
        } else {
          ConstanceValue.current_battery = 0;
        }
        sendSwitch(ConstanceValue.SWITCH_BATTERY);
        break;
      case R.id.message_cb:
        if (message_cb.isChecked()) {
          ConstanceValue.current_message = 1;
        } else {
          ConstanceValue.current_message = 0;
        }
        sendSwitch(ConstanceValue.SWITCH_MESSAGE);
        break;
      case R.id.call_cb:
        if (call_cb.isChecked()) {
          ConstanceValue.current_call = 1;
        } else {
          ConstanceValue.current_call = 0;
        }
        sendSwitch(ConstanceValue.SWITCH_CALL);
        break;
      case R.id.email_cb:
        if (email_cb.isChecked()) {
          ConstanceValue.current_email = 1;
        } else {
          ConstanceValue.current_email = 0;
        }
        sendSwitch(ConstanceValue.SWITCH_EMAIL);
        break;
    }
  }

  //保存开关状态
  private void saveSwitchState() {
    Map<String, String> switchMap = new HashMap<>();

    if (connection_cb.isChecked()) {
      switchMap.put(ConstanceValue.SWITCH_BLUETOOTH, "1");
    } else {
      switchMap.put(ConstanceValue.SWITCH_BLUETOOTH, "0");
    }
    if (gesture_cb.isChecked()) {
      switchMap.put(ConstanceValue.SWITCH_JESTURE, "1");
    } else {
      switchMap.put(ConstanceValue.SWITCH_JESTURE, "0");
    }

    if (clock_cb.isChecked()) {
      switchMap.put(ConstanceValue.SWITCH_TIME, "1");
    } else {
      switchMap.put(ConstanceValue.SWITCH_TIME, "0");
    }

    if (weather_cb.isChecked()) {
      switchMap.put(ConstanceValue.SWITCH_WEATHER, "1");
    } else {
      switchMap.put(ConstanceValue.SWITCH_WEATHER, "0");
    }

    if (battery_cb.isChecked()) {
      switchMap.put(ConstanceValue.SWITCH_BATTERY, "1");
    } else {
      switchMap.put(ConstanceValue.SWITCH_BATTERY, "0");
    }

    if (message_cb.isChecked()) {
      switchMap.put(ConstanceValue.SWITCH_MESSAGE, "1");
    } else {
      switchMap.put(ConstanceValue.SWITCH_MESSAGE, "0");
    }

    if (call_cb.isChecked()) {
      switchMap.put(ConstanceValue.SWITCH_CALL, "1");
    } else {
      switchMap.put(ConstanceValue.SWITCH_CALL, "0");
    }

    if (email_cb.isChecked()) {
      switchMap.put(ConstanceValue.SWITCH_EMAIL, "1");
    } else {
      switchMap.put(ConstanceValue.SWITCH_EMAIL, "0");
    }

    switch (rg.getCheckedRadioButtonId()) {
      case R.id.beam_rb:
        switchMap.put(ConstanceValue.SWITCH_LIGHT, "100");
        break;

      case R.id.mild_rb:
        switchMap.put(ConstanceValue.SWITCH_LIGHT, "60");
        break;

      case R.id.glimmer_rb:
        switchMap.put(ConstanceValue.SWITCH_LIGHT, "20");
        break;

      case R.id.off_rb:
        switchMap.put(ConstanceValue.SWITCH_LIGHT, "0");
        break;
    }
    appApplication.saveMapData(ConstanceValue.SWITCH, switchMap);
  }


  //根据类型发送具体数据
  private void sendSwitch(String type) {
    BluetoothBusBean bluetoothBusBean = new BluetoothBusBean();
    switch (type) {
      case ConstanceValue.SWITCH_LIGHT:
        bluetoothBusBean.setNotice(ConstanceValue.SWITCH_LIGHT);
        break;
      case ConstanceValue.SWITCH_JESTURE:
        bluetoothBusBean.setNotice(ConstanceValue.SWITCH_JESTURE);
        break;
      case ConstanceValue.SWITCH_TIME:
        bluetoothBusBean.setNotice(ConstanceValue.SWITCH_TIME);
        break;
      case ConstanceValue.SWITCH_WEATHER:
        bluetoothBusBean.setNotice(ConstanceValue.SWITCH_WEATHER);
        break;
      case ConstanceValue.SWITCH_BATTERY:
        bluetoothBusBean.setNotice(ConstanceValue.SWITCH_BATTERY);
        break;
      case ConstanceValue.SWITCH_MESSAGE:
        bluetoothBusBean.setNotice(ConstanceValue.SWITCH_MESSAGE);
        break;
      case ConstanceValue.SWITCH_CALL:
        bluetoothBusBean.setNotice(ConstanceValue.SWITCH_CALL);
        break;
      case ConstanceValue.SWITCH_EMAIL:
        bluetoothBusBean.setNotice(ConstanceValue.SWITCH_EMAIL);
        break;
    }
    EventBus.getDefault().post(bluetoothBusBean);
  }


  @Override
  public void onDestroyView() {
    super.onDestroyView();
    saveSwitchState();
  }
}