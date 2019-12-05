package com.tl.veger.welcome;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.widget.ImageView;
import com.tl.veger.HomeActivity;
import com.tl.veger.R;
import com.tl.veger.base.BaseActivity;
import com.tl.veger.gmail.GoogleLoginActivity;
import com.tl.veger.utils.AnimationUtils;
import com.tl.veger.utils.PermissionUtils;

import butterknife.BindView;

/**
 * created by tl on 2019/8/30
 */
public class WelcomeActivity extends BaseActivity  {

  @BindView(R.id.welcome_iv)
  ImageView welcome_iv;

  @Override
  protected int getLayoutResId() {
    return R.layout.activity_welcome;
  }

  @Override
  protected void initFragment() {
    ObjectAnimator objectAnimator = AnimationUtils.alpha(welcome_iv);
    objectAnimator.setDuration(5000);
    objectAnimator.start();
    objectAnimator.addListener(new AnimatorListenerAdapter() {
      @Override
      public void onAnimationEnd(Animator animation) {
        super.onAnimationEnd(animation);
        startActivity(new Intent(WelcomeActivity.this, GoogleLoginActivity.class));
        finish();
      }
    });
  }

}
