package com.tl.veger.utils;

import com.google.api.services.gmail.Gmail;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * created by tl on 2019/8/2
 */
public class ConstanceValue {

  //ancs相关
  public static final String SERVICE_ANCS = "0000FFE0-1212-EFDE-1523-785FEABCD123";
  public static final String CHARACTERISTICS_NOTIFICATION_SOURCE = "9fbf120d-6301-42d9-8c58" +
      "-25e699a21dbd";
  public static final String CHARACTERISTICS_DATA_SOURCE = "22eac6e9-24d6-4bb5-be44-b36ace7c7bfb";
  public static final String CHARACTERISTICS_CONTROL_POINT = "69d1d8f3-45e1-49a8-9821-9bbdfdaad9d9";
  public static final String DESCRIPTOR_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
  public static final String SERVICE_UUID = "0000FFE0-1212-EFDE-1523-785FEABCD123";//服务uuid
  public static final String SEND_CHARACTERISTIC = "00001526-1212-EFDE-1523-785FEABCD123";
  //往该特征值写入数据
  public static final String OBSERVE_CHARACTERISTIC = "00001527-1212-EFDE-1523-785FEABCD123";
  //监听该特征值触发写入


  public static int UNREAD_SMS = 0;//短信未读数
  public static byte[] WEATHER = new byte[3];//天气
  public static int MISS_CALL = 0;//未接电话
  public static double LONG;//经度
  public static double LAT;//纬度
  public static int UNREAD_MAIL=0;//邮件未读数
  public static int CURRENT_BATTERY_PERCENT;//当前手机电量
  public static boolean IS_CHARGING = false;//是否在充电
  public static boolean isActiveDisconnect=false;//是否主动断开
  public static Gmail mService;

  public static final String SWITCH = "switch";
  public static final String SWITCH_LIGHT = "light";
  public static final String SWITCH_BLUETOOTH = "bluetooth";
  public static final String SWITCH_JESTURE = "jesture";
  public static final String SWITCH_TIME = "time";
  public static final String SWITCH_WEATHER = "weather";
  public static final String SWITCH_BATTERY = "battery";
  public static final String SWITCH_MESSAGE = "message";
  public static final String SWITCH_CALL = "call";
  public static final String SWITCH_EMAIL = "email";


  //默认开关状态
  public static boolean sendFinish = true;//是否发送完成
  public static int current_light = 60;
  public static int current_bluetooth = 1;
  public static int current_jesture = 1;
  public static int current_time = 1;
  public static int current_weather = 1;
  public static int current_battery = 1;
  public static int current_message = 1;
  public static int current_call = 1;
  public static int current_email = 1;


  //蓝牙数据发送队列
  public static Queue<byte[]> bluetoothQueue;

}
