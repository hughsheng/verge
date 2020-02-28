package com.tl.veger.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

import com.tl.veger.busbean.BatteryBusBean;
import com.tl.veger.busbean.BluetoothBusBean;
import com.tl.veger.utils.ConstanceValue;

import org.greenrobot.eventbus.EventBus;

/**
 * 监听手机电量及充电状态
 */
public class BatteryReceiver extends BroadcastReceiver {
    private int lastPercent;
    private int lastChargeState;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        int currentPercent = intent.getIntExtra("level", 100);
        int currentState = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
            if (lastPercent != currentPercent) {
                ConstanceValue.CURRENT_BATTERY_PERCENT = currentPercent;
                lastPercent = currentPercent;
                EventBus.getDefault().post(new BatteryBusBean());
                sendBattery();
            }

            if (lastChargeState != currentState) {
                ConstanceValue.IS_CHARGING = (currentState == BatteryManager.BATTERY_STATUS_FULL || currentState == BatteryManager.BATTERY_STATUS_CHARGING);
                lastChargeState=currentState;
                EventBus.getDefault().post(new BatteryBusBean());
                sendBattery();
            }
        }
    }

    private void sendBattery(){
        BluetoothBusBean bluetoothBusBean = new BluetoothBusBean();
        bluetoothBusBean.setNotice(ConstanceValue.SWITCH_BATTERY);
        EventBus.getDefault().post(bluetoothBusBean);
    }
}
