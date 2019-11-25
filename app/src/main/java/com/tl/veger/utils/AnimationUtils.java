package com.tl.veger.utils;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.ViewAnimationUtils;

import com.tl.veger.base.app.AppApplication;


/**
 * Created by tl on 2018-9-28
 * 封装属性动画一些基本操作
 */
public class AnimationUtils {

  //从左进入
  public static ObjectAnimator inFromLeft(View view) {
    return ObjectAnimator.ofFloat(view, "translationX", -view.getRight(), 0);
  }


  //从上进入
  public static ObjectAnimator inFromTop(View view) {
    return ObjectAnimator.ofFloat(view, "translationY", -view.getBottom(), 0);
  }

  //从右进入
  public static ObjectAnimator inFromRight(View view) {
    int rightDistance = AppApplication.getInstance().getWindowWidth() - view.getLeft();
    return ObjectAnimator.ofFloat(view, "translationX", rightDistance, 0);
  }

  //从下进入
  public static ObjectAnimator inFromBottom(View view) {
    int bottomDistance = AppApplication.getInstance().getWindowHeight() - view.getTop();
    return ObjectAnimator.ofFloat(view, "translationY", bottomDistance, 0);
  }


  //从左退出
  public static ObjectAnimator outToLeft(View view) {
    return ObjectAnimator.ofFloat(view, "translationX", 0, -view.getRight());
  }


  //从上退出
  public static ObjectAnimator outToTop(View view) {
    return ObjectAnimator.ofFloat(view, "translationY", 0, -view.getBottom());
  }

  //从右退出
  public static ObjectAnimator outToRight(View view) {
    int rightDistance = AppApplication.getInstance().getWindowWidth() - view.getLeft();
    return ObjectAnimator.ofFloat(view, "translationX", 0, rightDistance);
  }

  //从下退出
  public static ObjectAnimator outToBottom(View view) {
    int bottomDistance = AppApplication.getInstance().getWindowHeight() - view.getTop();
    return ObjectAnimator.ofFloat(view, "translationY", 0, bottomDistance);
  }

  //Z轴旋转
  public static ObjectAnimator rotateZ(View view) {
    return ObjectAnimator.ofFloat(view, "rotation", 0, 360);
  }

  //X轴旋转
  public static ObjectAnimator rotateX(View view) {
    return ObjectAnimator.ofFloat(view, "rotationX", 0, 360);
  }

  //Y轴旋转
  public static ObjectAnimator rotateY(View view) {
    return ObjectAnimator.ofFloat(view, "rotationY", 0, 360);
  }

  //X轴缩放
  public static ObjectAnimator scaleX(View view) {
    return ObjectAnimator.ofFloat(view, "scaleX", 1.0f, 0.5f, 2.0f, 1.0f);
  }

  //Y轴缩放
  public static ObjectAnimator scaleY(View view) {
    return ObjectAnimator.ofFloat(view, "scaleY", 1.0f, 0.5f, 2.0f, 1.0f);
  }

  //透明度变换
  public static ObjectAnimator alpha(View view) {
    return ObjectAnimator.ofFloat(view, "alpha", 1, 0);
  }

  //根据圆半径渐进显示或隐藏view
  public static Animator disappearIncircle(View view) {
    return ViewAnimationUtils.createCircularReveal(view, view.getWidth() / 2, view.getHeight() /
        2, view.getWidth(), 0);
  }

  //从左上角开始渐进显示view
  public static Animator showIncircle(View view) {
    return ViewAnimationUtils.createCircularReveal(view, 0, 0, 0, (float) Math.hypot(view
        .getWidth(), view.getHeight()));
  }

}
