package com.tl.veger.receiver;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.tl.veger.navigation.bluetooth.service.CenterService;

public class BluetoothBondReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                switch (device.getBondState()) {
                    case BluetoothDevice.BOND_NONE:
                        Log.i("CenterService", "取消配对");
                        break;
                    case BluetoothDevice.BOND_BONDING:
                        Log.i("CenterService", "配对中");
                        break;
                    case BluetoothDevice.BOND_BONDED:
                        Log.i("CenterService", "配对成功");
                        break;
                }
            }
        }
    }

