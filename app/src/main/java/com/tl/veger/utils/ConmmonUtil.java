package com.tl.veger.utils;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Label;
import com.google.gson.Gson;
import com.tl.veger.R;
import com.tl.veger.base.app.AppApplication;
import com.tl.veger.gmail.ApiAsyncTask;
import com.tl.veger.gmail.BackApisyncTask;
import com.tl.veger.gmail.GoogleLoginActivity;
import com.tl.veger.navigation.bluetooth.adapter.WeatherBean;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import static com.tl.veger.utils.ConstanceValue.PREF_ACCOUNT_NAME;
import static com.tl.veger.utils.ConstanceValue.REQUEST_GOOGLE_PLAY_SERVICES;
import static com.tl.veger.utils.ConstanceValue.SCOPES;

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

    public static void postTestLog(String url, int temp, int icon, int flag) {

        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    String result = "";
                    String authHost = "http://139.219.5.166:8080/api/test";

                    URL postUrl = new URL(authHost);
                    HttpURLConnection connection = (HttpURLConnection) postUrl.openConnection();
                    // 设置请求方式
                    connection.setRequestMethod("POST");
                    // 设置是否向HttpURLConnection输出
                    connection.setDoOutput(true);
                    // 设置是否从httpUrlConnection读入
                    connection.setDoInput(true);
                    // 设置是否使用缓存
                    connection.setUseCaches(false);
                    //设置参数类型是json格式
                    connection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
                    connection.connect();

                    String body = "{" + "\"url\":" + "\"" + url + "\"" + ", \"temp\":" + temp + ", \"icon\":" + icon + ", \"flag\":" + flag + "}";

                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
                    writer.write(body);
                    writer.close();

                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        //定义 BufferedReader输入流来读取URL的响应
                        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        String line;
                        while ((line = in.readLine()) != null) {
                            result += line;
                        }
                    }
                    Log.e("Post_Log", result);
                    Log.e("Post_Log", body);
                } catch (Exception ex) {
                    Log.e("Post_Log", ex.getMessage());
                }
            }
        }.start();
    }

    public static void getWeather(double latitude, double longitude) {
        ConstanceValue.LONG = longitude;
        ConstanceValue.LAT = latitude;

        Log.i("mylocation", "long=" + ConstanceValue.LONG + "LAT=" + ConstanceValue.LAT);
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

                Log.e("weatherUrl", weatherUrl);
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

                    postTestLog(weatherUrl, ConstanceValue.WEATHER[0], ConstanceValue.WEATHER[1], ConstanceValue.WEATHER[2]);
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
    public static List<BluetoothDevice> getVergeDevice() {
        List<BluetoothDevice> vergedevices = null;

        BluetoothManager bluetoothManager = (BluetoothManager) AppApplication.getInstance().getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager != null) {
            List<BluetoothDevice>   devices = bluetoothManager.getConnectedDevices(BluetoothProfile.GATT);
            for(BluetoothDevice bluetoothDevice:devices){
                if(bluetoothDevice.getName().contains("VERGE")){
                    vergedevices.add(bluetoothDevice);
                }
            }
        }

        return vergedevices;
    }

    public static void addFragmentToActivity(@NonNull FragmentManager fragmentManager,
                                             @NonNull Fragment fragment, int frameId, String tag) {
        addFragmentToActivity(fragmentManager, fragment, frameId, tag, 0, 0);
    }

    private static void addFragmentToActivity(@NonNull FragmentManager fragmentManager,
                                              @NonNull Fragment fragment, int frameId, String tag, int animStar, int animEnd) {

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(frameId, fragment, tag)
                .commitAllowingStateLoss();
    }


    public static void logInGmail(Activity activity) {
        if (isGooglePlayServicesAvailable(activity)) {
            if (ConstanceValue.mService == null) {
                activity.startActivity(new Intent(activity, GoogleLoginActivity.class));
            }
        } else {
            Toast.makeText(activity, "The phone does not support Google service and cannot use Gmail function", Toast.LENGTH_LONG).show();
        }
    }


    public static void setGmailService(Activity activity) {
        String account = (String) AppApplication.getInstance().getCacheData(PREF_ACCOUNT_NAME, "");
        if (!TextUtils.isEmpty(account)) {
            GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(activity, Arrays.asList(SCOPES))
                    .setBackOff(new ExponentialBackOff())
                    .setSelectedAccountName(account);
            ConstanceValue.mService = new Gmail.Builder(ConstanceValue.TRANSPORT, ConstanceValue.JSONFACTORY, credential)
                    .setApplicationName(activity.getString(R.string.app_name))
                    .build();
        }
    }


    /**
     * Check that Google Play services APK is installed and up to date. Will
     * launch an error dialog for the user to update Google Play Services if
     * possible.
     *
     * @return true if Google Play Services is available and up to
     * date on this device; false otherwise.
     */
    public static boolean isGooglePlayServicesAvailable(Activity activity) {
        int connectionStatusCode =
                GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(activity);
        if (GoogleApiAvailability.getInstance().isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(activity, connectionStatusCode);
            return false;
        } else return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     *
     * @param connectionStatusCode code describing the presence (or lack of)
     *                             Google Play Services on this device.
     */
    public static void showGooglePlayServicesAvailabilityErrorDialog(Activity activity, final int connectionStatusCode) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(activity,
                        connectionStatusCode, REQUEST_GOOGLE_PLAY_SERVICES);
                dialog.show();
            }
        });
    }

    /**
     * Fetch a list of Gmail labels attached to the specified account.
     *
     * @return List of Strings labels.
     */
    public static List<String> getDataFromApi(Activity activity) throws IOException {
        // Get the labels in the user's account.
        String user = "me";
        List<String> labels = new ArrayList<>();
        if (ConstanceValue.mService == null) {
            setGmailService(activity);
        }

        if (ConstanceValue.mService != null) {
            Label label = ConstanceValue.mService.users().labels().get(user, "INBOX").execute(); // have
            // the lableName to execute together
            labels.add("所有邮件：" + label.getThreadsTotal());
            labels.add("未读邮件：" + label.getThreadsUnread());
            if (ConstanceValue.UNREAD_MAIL != label.getThreadsUnread()) {
                ConstanceValue.UNREAD_MAIL = label.getThreadsUnread();
                labels.add("有新邮件");
            }
            ConstanceValue.isLoginGmail = true;
            Log.i("mylog", labels.toString());
        }

        return labels;
    }

    public static int getDataUnreadNum(Activity activity) throws IOException {
        int unreadNum = 0;

        // Get the labels in the user's account.
        String user = "me";
        List<String> labels = new ArrayList<>();
        if (ConstanceValue.mService == null) {
            setGmailService(activity);
        }

        if (ConstanceValue.mService != null) {
            Label label = ConstanceValue.mService.users().labels().get(user, "INBOX").execute(); // have
            // the lableName to execute together
            labels.add("所有邮件：" + label.getThreadsTotal());
            labels.add("未读邮件：" + label.getThreadsUnread());
            ConstanceValue.UNREAD_MAIL = label.getThreadsUnread();
            unreadNum = label.getThreadsUnread();
            ConstanceValue.isLoginGmail = true;
            Log.i("mylog", labels.toString());
        }

        return unreadNum;
    }

}
