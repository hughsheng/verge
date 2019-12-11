package com.tl.veger.utils;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.Log;

import com.google.gson.Gson;
import com.tl.veger.R;
import com.tl.veger.base.app.AppApplication;
import com.tl.veger.navigation.bluetooth.adapter.WeatherBean;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

public class ConmmonUtil {

  public static final int INTEGER = 0x1001;
  public static final int FLOAT = 0x1002;

  public static List<Integer> getNumberList(String num, int type) {
    List<Integer> dataList = new ArrayList<>();
    if (type == INTEGER) {
      char[] ar = num.toCharArray();
      for (char s : ar) {
        dataList.add(Integer.parseInt(s + ""));
      }

    } else {
      String[] data = num.split("\\.");
      //整数部分
      if (Integer.valueOf(data[0]) < 10) {
        dataList.add(0);
        dataList.add(Integer.parseInt(data[0]));
      } else {
        char[] ar = data[0].toCharArray();
        for (char s : ar) {
          dataList.add(Integer.parseInt(s + ""));
        }
      }

      //小数部分(需要小时转分钟)
      int num_f = Integer.valueOf(data[1]) * 6;
      if (num_f < 10) {
        dataList.add(0);
        dataList.add(num_f);
      } else {
        String str = num_f + "";
        char[] num_c = str.toCharArray();
        for (char s : num_c) {
          dataList.add(Integer.parseInt(s + ""));
        }
      }
    }

    return dataList;
  }

  public static float getCurrentTime() {
    Calendar calendars = Calendar.getInstance();
    calendars.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
    int hour = calendars.get(Calendar.HOUR);
    int min = calendars.get(Calendar.MINUTE);
    DecimalFormat df = new DecimalFormat("0.0");
    float tt = Float.parseFloat(df.format(min / 60.0));
    return hour + tt;
  }

  public static byte[] getCurrentTimeForByte() {
    byte[] time = new byte[4];
    Calendar calendars = Calendar.getInstance();
    calendars.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
    int hour = calendars.get(Calendar.HOUR_OF_DAY);
    int min = calendars.get(Calendar.MINUTE);
    int sec = calendars.get(Calendar.SECOND);
    boolean is24HourMode = DateFormat.is24HourFormat(AppApplication.getInstance());

    time[0] = (byte) hour;
    time[1] = (byte) min;
    time[2] = (byte) sec;
    if (is24HourMode) {
      time[3] = 1;
    } else {
      time[3] = 0;
    }
    return time;
  }

  public static void getWeather() {
    LocationManager locationManager =
        (LocationManager) AppApplication.getInstance().getSystemService(Context.LOCATION_SERVICE);
    List<String> providers = locationManager.getProviders(true);
    Location bestLocation = null;
    for (String provider : providers) {
      Location l = locationManager.getLastKnownLocation(provider);
      if (l == null) {
        continue;
      }
      if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
        // Found best last known location: %s", l);
        bestLocation = l;
      }
    }

    if (bestLocation != null) {
      ConstanceValue.LONG = bestLocation.getLongitude();
      ConstanceValue.LAT = bestLocation.getLatitude();
    }


    new Thread(new Runnable() {//开启xian
      @Override
      public void run() {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        String weatherUrl =
            "http://api.openweathermap.org/data/2.5/weather?" + "lat=" + ConstanceValue.LAT +
                "&" + "lon=" + ConstanceValue.LONG + "&" + "APPID" +
                "=a4ae2495b1086cf372587e0c51e507df" +
                "&units=metric";
        try {
          URL url = new URL(weatherUrl);//新建URL
          connection = (HttpURLConnection) url.openConnection();//发起网络请求
          connection.setRequestMethod("GET");//请求方式
          connection.setConnectTimeout(8000);//连接最大时间
          connection.setReadTimeout(8000);//读取最大时间
          InputStream in = connection.getInputStream();
          reader = new BufferedReader(new InputStreamReader(in));//写入reader
          StringBuilder response = new StringBuilder();
          String line;
          while ((line = reader.readLine()) != null) {
            response.append(line);
          }
          WeatherBean weatherBean = new Gson().fromJson(response.toString(), WeatherBean.class);
          int temp = (int) weatherBean.getMain().getTemp();
          ConstanceValue.WEATHER[0] = (byte) temp;
          String icon = weatherBean.getWeather().get(0).getIcon();
          if (icon.equals("01d")) {
            ConstanceValue.WEATHER[1] = 1;
          } else if (icon.equals("02d")) {
            ConstanceValue.WEATHER[1] = 2;
          } else if (icon.equals("02n") || icon.equals("03d") || icon.equals("03n") || icon.equals("04d") || icon.equals("04n") || icon.equals("50d") || icon.equals("50n")) {
            ConstanceValue.WEATHER[1] = 3;
          } else if (icon.equals("09d") || icon.equals("09n") || icon.equals("10d") || icon.equals("10n") || icon.equals("11d") || icon.equals("11n")) {
            ConstanceValue.WEATHER[1] = 4;
          } else if (icon.equals("13d") || icon.equals("13n")) {
            ConstanceValue.WEATHER[1] = 5;
          } else if (icon.equals("01n")) {
            ConstanceValue.WEATHER[1] = 6;
          }
          ConstanceValue.WEATHER[2] = 1;
          if (weatherBean.getMain().getTemp() < 0) {
            ConstanceValue.WEATHER[2] = 0;
          }
          Log.i("mylog", response.toString());
          Log.i("mylog",
              "温度：" + ConstanceValue.WEATHER[0] + "---------" + "icon=" + ConstanceValue.WEATHER[1] + "---------" + "温度零上还是零下=" + ConstanceValue.WEATHER[2]);
        } catch (Exception e) {
          e.printStackTrace();
        } finally {
          if (reader != null) {
            try {
              reader.close();
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
          if (connection != null) {
            connection.disconnect();
          }
        }
      }
    }).start();
  }

  //得到未读短信的数量  通过查询数据库得到
  public static boolean getUnreadSmsCount() {
    boolean hasNew = false;
    Cursor csr = AppApplication.getInstance().getContentResolver().query(Uri.parse("content://sms"
        ), null,
        "type = 1 and read = 0", null, null);
    if (csr != null) {
      int unReadMSG = csr.getCount();
      if (ConstanceValue.UNREAD_SMS != unReadMSG) {
        ConstanceValue.UNREAD_SMS = unReadMSG;
        hasNew = true;
      }
      Log.i("mylog", "得到的未读短信数量是：" + ConstanceValue.UNREAD_SMS);
      csr.close();
    }
    return hasNew;
  }

  //获取未接电话数量
  public static boolean getMissCallCount() {
    boolean hasNew = false;
    Cursor cursor =
        AppApplication.getInstance().getContentResolver().query(CallLog.Calls.CONTENT_URI,
            new String[]{CallLog.Calls.TYPE}, " type=? and new=?",
            new String[]{CallLog.Calls.MISSED_TYPE + "", "1"}, "date desc");
    if (cursor != null) {
      int missCall = cursor.getCount();
      if (ConstanceValue.MISS_CALL != missCall) {
        ConstanceValue.MISS_CALL = missCall;
        hasNew = true;
      }
      Log.i("mylog", "得到的未接电话数量是：" + ConstanceValue.MISS_CALL);
      cursor.close();
    }
    return hasNew;
  }

  //获取开关数据
  public static byte[] getSwitchSate() {
    byte[] ss = new byte[8];
    int bluetooth = ConstanceValue.current_bluetooth;
    int jesture = ConstanceValue.current_jesture;
    int time = ConstanceValue.current_time;
    int weather = ConstanceValue.current_weather;
    int battery = ConstanceValue.current_battery;
    int message = ConstanceValue.current_message;
    int call = ConstanceValue.current_call;
    int email = ConstanceValue.current_email;

    ss[0] = (byte) battery;
    ss[1] = (byte) weather;
    ss[2] = (byte) time;
    ss[3] = (byte) call;
    ss[4] = (byte) message;
    ss[5] = (byte) email;
    ss[6] = (byte) jesture;
    ss[7] = (byte) bluetooth;

    return ss;
  }


  //获取已连上verge设备
  public static BluetoothDevice getVergeDevice() {
    BluetoothDevice vergeDevice = null;
    BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

    Class<BluetoothAdapter> bluetoothAdapterClass = BluetoothAdapter.class;//得到BluetoothAdapter
    // 的Class对象

    try {//得到连接状态的方法

      Method method = bluetoothAdapterClass.getDeclaredMethod("getConnectionState", (Class[]) null);

//打开权限

      method.setAccessible(true);

      int state = (int) method.invoke(adapter, (Object[]) null);


      if (state == BluetoothAdapter.STATE_CONNECTED) {

        Set<BluetoothDevice> devices = adapter.getBondedDevices();

        for (BluetoothDevice device : devices) {

          Method isConnectedMethod = BluetoothDevice.class.getDeclaredMethod("isConnected",
              (Class[]) null);

          method.setAccessible(true);

          boolean isConnected = (boolean) isConnectedMethod.invoke(device, (Object[]) null);

          if (isConnected && device.getName().contains("VERGE")) {
            vergeDevice = device;
          }

        }

      }


    } catch (Exception e) {

      e.printStackTrace();

    }
    return vergeDevice;
  }
}
