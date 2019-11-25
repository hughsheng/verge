package com.tl.veger.base;


import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * created by tl on 2019/7/27
 * activity基类
 */
public abstract class BaseActivity extends AppCompatActivity {
  protected FragmentManager mFragmentManager;
  protected Unbinder mUnbinder;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    mFragmentManager = getSupportFragmentManager();
    int layoutId = getLayoutResId();
    if (layoutId != 0) {
      setContentView(layoutId);
    }
    mUnbinder = ButterKnife.bind(this);
    initFragment();
  }


  protected View getRootView() {
    return ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
  }


  protected abstract int getLayoutResId();

  protected abstract void initFragment();

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (mUnbinder != null) {
      mUnbinder.unbind();
      mUnbinder = null;
    }
  }
}
