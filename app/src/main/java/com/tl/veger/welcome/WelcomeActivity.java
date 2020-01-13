package com.tl.veger.welcome;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Handler;
import android.widget.ImageView;

import com.tl.veger.HomeActivity;
import com.tl.veger.R;
import com.tl.veger.base.BaseActivity;
import com.tl.veger.gmail.GoogleLoginActivity;
import com.tl.veger.utils.AnimationUtils;
import com.tl.veger.utils.ConmmonUtil;
import com.tl.veger.utils.ConstanceValue;
import com.tl.veger.utils.PermissionUtils;

import butterknife.BindView;

/**
 * created by tl on 2019/8/30
 */
public class WelcomeActivity extends BaseActivity implements PermissionUtils.PermissionListener {

  @BindView(R.id.welcome_iv)
  ImageView welcome_iv;

  @Override
  protected int getLayoutResId() {
    return R.layout.activity_welcome;
  }

  @Override
  protected void initFragment() {
    checkPermissions();

  }


  private void toHome() {
    ObjectAnimator objectAnimator = AnimationUtils.alpha(welcome_iv);
    objectAnimator.setDuration(5000);
    objectAnimator.start();
    objectAnimator.addListener(new AnimatorListenerAdapter() {
      @Override
      public void onAnimationEnd(Animator animation) {
        super.onAnimationEnd(animation);
        startActivity(new Intent(WelcomeActivity.this, HomeActivity.class));
        finish();
      }
    });
  }

  private void checkPermissions() {
    PermissionUtils permissionUtils = PermissionUtils.getInstance(this, this);
    permissionUtils.checkPermission(PermissionUtils.REQUEST_SMS);
    permissionUtils.checkPermission(PermissionUtils.REQUEST_CALL);
    permissionUtils.checkPermission(PermissionUtils.REQUEST_LOCATION);
    permissionUtils.checkPermission(PermissionUtils.REQUEST_BLUETOOTH);
    ConmmonUtil.getWeather();
    ConmmonUtil.getUnreadSmsCount();
    ConmmonUtil.getMissCallCount();
    //  permissionUtils.checkPermission(PermissionUtils.REQUEST_NOTIFICATION);
  }

  @Override
  public void onPermissionResult(boolean hasPermission, int permissionCode) {
    switch (permissionCode) {
      case PermissionUtils.REQUEST_BLUETOOTH:
        toHome();
        break;
    }
  }

}
