package com.tl.veger.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * created by tl on 2019/7/27
 * fragment基类
 */
public abstract class BaseFragment extends Fragment {

  protected View rootView;
  protected Unbinder mUnbinder;

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    if (null != rootView) {
      ViewGroup parent = (ViewGroup) rootView.getParent();
      if (null != parent) {
        parent.removeView(rootView);
      }
    } else {
      rootView = inflater.inflate(getLayoutResId(), container, false);
    }
    mUnbinder = ButterKnife.bind(this, rootView);

    return rootView;
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    init();
  }

  protected abstract int getLayoutResId();

  protected abstract void init();

  @Override
  public void onDetach() {
    super.onDetach();
    mUnbinder.unbind();
    mUnbinder = null;
  }
}
