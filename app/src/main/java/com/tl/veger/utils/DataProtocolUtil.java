package com.tl.veger.utils;

/**
 * created by tl on 2019/10/19
 * 蓝牙通信组包
 */


import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/********************************** 数据包格式说明 **********************************
 //数据包buffer
 unsigned char acData[100];

 //消息-头
 acData[0] = 0xF0;              // 帧头，数据包的起始位置（固定为0xF0）
 acData[1] = DEV_IOS;           // 系统类型（取值范围-枚举DEV_INFO）
 acData[2] = MSG_BatteryLevel;  // 消息类型（取值范围-枚举MSG_TYPE）
 acData[3] = 6;                 // 消息数据段长度（比如是长度为6）

 //消息-数据段
 acData[4]
 acData[5]
 acData[6]
 acData[7]
 acData[8]
 acData[9]

 //消息-校验尾
 acData[10] = 11;   // 末尾最后1字节保存b数据包的总长度

 ----------------------------------------------------------------
 说明： 数据包长度无限制， 类容分为三大部分：
 第一部分：消息头
 消息头用于描述数据包；
 第二部分：消息数据段
 消息数据段从数据包acData[4]位置开始往后取，具体数据长度由acData[3]决定；
 第三部分：消息尾
 消息尾为1字节，保存这个数据包的总长度，用于接收端收到数据包后校验包是否合法；
 ----------------------------------------------------------------
 */



public class DataProtocolUtil {

  // 系统类型
 public static enum DEV_INFO {
    DEV_IOS,
    DEV_ANDROID
  }

  // 消息类型
  public enum MSG_TYPE {
    MSG_WEATHER, // 天气(摄氏度+气象代码'多云')
    MSG_TIME,    // 时间（小时、分钟、秒）
    MSG_BATTERY,  // 电池信息(电量+充电状态)
    MSG_OPTIONSWITCH, // 开关选项值
    MSG_BRIGHTNESS,  // 亮度级别 (LIGHT_VALUE)
      MSG_END,//一组数据发送完成
    adsad,
    asdas,
      rgrge,
      erger,
      ergerg,
      ergertertrg,
      ergerge,
      getrrErgerg,
      ergerrrrrgerg,
      ergerrrrg,
      ergergerrrg,
    MSG_CALL,//未接电话17
    MSG_MESSAGE,//未读短信18
    MSG_EMAIL,//未读邮件19
  }

  public static enum SWITCH_TYPE {
    SWITCH_BATTERY,  // 电池信息 开关
    SWITCH_WEATHER, // 天气 开关
    SWITCH_TIME,    // 时间 开关
    SWITCH_PHONE,  // 电话 开关
    SWITCH_SMS,  // 短信 开关
    SWITCH_EMAIL, // email 开关
    SWITCH_GESTURE, // 手势开关
    SWITCH_DISCONNECT, // 断开蓝牙连接
    SWITCH_END
  }


  public static enum LIGHT_VALUE {

    LIGHT_OFF_VALUE(0), //  关按钮对应值

    LIGHT_GLIMMER_VALUE(20), // Glimmer按钮对应值

    LIGHT_GENTLE_VALUE(60), // Gentle按钮对应值

    LIGHT_BEAM_VALUE(100); // Beam按钮对应值

    private int light;

    private LIGHT_VALUE(int light) {
      this.light = light;
    }
  }


  public static byte[] getData(MSG_TYPE type,byte[] data){
    byte[] dataPacket=new byte[data.length+5];
    dataPacket[0]= (byte) 0xF0;
    dataPacket[1]= (byte) DEV_INFO.DEV_ANDROID.ordinal();
    dataPacket[2]= (byte) type.ordinal();
    dataPacket[3]= (byte) data.length;
    System.arraycopy(data,0,dataPacket,4,data.length);
    return dataPacket;
  }



}
