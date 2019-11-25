package com.tl.veger.base;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.tl.veger.R;

/**
 * created by tl on 2018-12-4
 * dialogFragment基类
 */
public abstract class BaseDialogFragment extends DialogFragment {

  private boolean cancelable = true;
  private boolean cancelableOnTouchOutside = true;

  @NonNull
  @Override
  public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
    View view = LayoutInflater.from(getActivity()).inflate(getLayoutId(), null);
    setContentView(view);
    Dialog dialog = new Dialog(getActivity(), R.style.DialogFragment);
    dialog.setContentView(view);
    dialog.setCancelable(cancelable);
    dialog.setCanceledOnTouchOutside(cancelableOnTouchOutside);
    return dialog;
  }

  /**
   * 设置dialog位置
   *
   * @param gravity 位置
   */
  protected void setGravity(int gravity) {
    Window window = getDialog().getWindow();
    //window.setBackgroundDrawable( new ColorDrawable(Color.TRANSPARENT));
    WindowManager.LayoutParams params = window.getAttributes();
    params.gravity = gravity;
    params.width = WindowManager.LayoutParams.MATCH_PARENT;
    params.height = WindowManager.LayoutParams.MATCH_PARENT;
    window.setAttributes(params);

//    DisplayMetrics dm = new DisplayMetrics();
//    getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
//    window.setLayout((int) (dm.widthPixels * wp), ViewGroup.LayoutParams.WRAP_CONTENT);
  }


  protected void cancelable(boolean cacelable) {
    this.cancelable = cacelable;
  }

  protected void cancelableOnTouchOutside(boolean cancelableOnTouchOutside) {
    this.cancelableOnTouchOutside = cancelableOnTouchOutside;
  }


  protected abstract int getLayoutId();

  protected abstract void setContentView(View view);

}
