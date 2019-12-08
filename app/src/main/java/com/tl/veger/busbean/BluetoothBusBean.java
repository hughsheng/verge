package com.tl.veger.busbean;

import android.bluetooth.BluetoothDevice;

/**
 * created by tl on 2019/8/3
 */
public class BluetoothBusBean {
  private String notice;
  private BluetoothDevice connectedDev;

  public BluetoothDevice getConnectedDev() {
    return connectedDev;
  }

  public void setConnectedDev(BluetoothDevice connectedDev) {
    this.connectedDev = connectedDev;
  }

  public String getNotice() {
    return notice;
  }

  public void setNotice(String notice) {
    this.notice = notice;
  }
}
