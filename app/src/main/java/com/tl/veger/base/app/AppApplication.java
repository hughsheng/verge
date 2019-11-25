package com.tl.veger.base.app;

import android.app.Application;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.tl.veger.utils.FontsUtils;
import com.tl.veger.utils.SharedPreferencesUtils;

import java.util.List;
import java.util.Map;

/**
 * created by tl on 2019/7/28
 */
public class AppApplication extends Application {

  private static AppApplication application;
  private int width = 0, height = 0;
  private SharedPreferencesUtils mSharedPreferencesUtils;
  @Override
  public void onCreate() {
    super.onCreate();
    application = this;
    mSharedPreferencesUtils = SharedPreferencesUtils.getInstance(this);
    FontsUtils.setDefaultFont(this,"SERIF","font/texgyreadventor.otf");
  }

  private void initProperty() {
    DisplayMetrics metric = new DisplayMetrics();
    WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
    wm.getDefaultDisplay().getMetrics(metric);
    width = metric.widthPixels; // 屏幕宽度（像素）
    height = metric.heightPixels; // 屏幕高度（像素
  }


  public static AppApplication getInstance() {
    return application;
  }

  public int getWindowWidth() {
    if (width == 0) {
      initProperty();
    }
    return this.width;
  }

  public int getWindowHeight() {
    if (height == 0) {
      initProperty();
    }
    return this.height;
  }


  /**
   * 采用andriod本身数据格式缓存数据
   * 后期可以更改成其他缓存
   */
  public void saveCacheData(String key, Object data) {
    saveCacheData(SharedPreferencesUtils.SP_NAME, key, data);
  }

  private void saveCacheData(final String fileName, final String key, final Object defaultObject) {
    mSharedPreferencesUtils.saveData(fileName, key, defaultObject);
  }

  public Object getCacheData(String key, Object defaultObject) {
    return getCacheData(SharedPreferencesUtils.SP_NAME, key, defaultObject);
  }

  private Object getCacheData(final String fileName, final String key, final Object defaultObject) {
    return mSharedPreferencesUtils.getData(fileName, key, defaultObject);
  }

  public void saveCacheListData(String key, final List<Map<String, String>> dataList) {
    saveCacheListData(SharedPreferencesUtils.SP_NAME, key, dataList);
  }

  private void saveCacheListData(final String fileName, final String key, final List<Map<String, String>> dataList) {
    mSharedPreferencesUtils.saveListData(fileName, key, dataList);
  }

  public List<Map<String, String>> getCacheListData(final String key) {
    return getCacheListData(SharedPreferencesUtils.SP_NAME, key);
  }

  private List<Map<String, String>> getCacheListData(final String fileName, final String key) {
    return mSharedPreferencesUtils.getListData(fileName, key);
  }

  public void removeListData(final String key) {
    mSharedPreferencesUtils.removeListData(SharedPreferencesUtils.SP_NAME, key);
  }

  public void saveCacheStringListData(String key, final List<String> dataList) {
    mSharedPreferencesUtils.saveStringListData(SharedPreferencesUtils.SP_NAME, key, dataList);
  }

  public List<String> getCacheStringListData(final String key) {
    return mSharedPreferencesUtils.getStringListData(SharedPreferencesUtils.SP_NAME, key);
  }

  public void saveMapData(String key, Map<String, String> mapData) {
    mSharedPreferencesUtils.saveMapData(SharedPreferencesUtils.SP_NAME, key, mapData);
  }

  public Map<String, String> getMapData(String key) {
    return mSharedPreferencesUtils.getMapData(SharedPreferencesUtils.SP_NAME, key);
  }

  public void saveTreeMapData(String key, Map<String, String> mapData) {
    mSharedPreferencesUtils.saveTreeMapData(SharedPreferencesUtils.SP_NAME, key, mapData);
  }

  public Map<String, String> getTreeMapData(String key) {
    return mSharedPreferencesUtils.getTreeMapData(SharedPreferencesUtils.SP_NAME, key);
  }

}
