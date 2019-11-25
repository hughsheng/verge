package com.tl.veger.widge;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import com.tl.veger.R;
import com.tl.veger.base.app.AppApplication;

/**
 * created by tl on 2019/9/19
 * 点击和滑动自动居中滚动条
 */
public class SmartTabBar extends HorizontalScrollView {


  private int currentX;//当前手指离开点的X

  private ScrollType currentScrollType = ScrollType.STOP;//当前滚动状态

  private Handler handler;

  private ViewGroup container;

  private int tabCount;//子控件数目

  private int currentIndex;//当前item索引

  private OnScrollViewSelectListener selecteListener;

  private Context context;

  private int moveSpace=10;

  /**
   * 滚动状态:
   * STOP=滚动停止
   * TOUCH_SCROLL=手指拖动滚动
   * SCROLL=滚动
   */
  enum ScrollType {
    STOP, TOUCH_SCROLL, SCROLL
  }


  public SmartTabBar(Context context) {
    super(context);
    initBar(context);
  }

  public SmartTabBar(Context context, AttributeSet attrs) {
    super(context, attrs);
    initBar(context);
  }

  public SmartTabBar(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initBar(context);
  }


  public void initBar(Context context) {
    this.context=context;
    handler = new Handler();

  }


  //滚动监听
  private Runnable scrollRunnable = new Runnable() {
    @Override
    public void run() {
      int scrollX = getScrollX();//获取view左边界的X坐标

      if (currentX == scrollX) {//滑动停止
        currentScrollType = ScrollType.STOP;

        for(int i=0;i<tabCount;i++){
          int viewdis=container.getChildAt(i).getLeft();
          int dis=currentX+getScreenWidth(context) / 2-container.getChildAt(i).getLeft();
          if(dis<0){
            if(-dis>moveSpace){//距离下一个item较远，选择上一个item
              currentIndex=i-1;
            }else {//距离下一个item较近，选择下一个item
              currentIndex=i;
            }
            break;
          }
        }

        selecteListener.OnSelectListener((TextView) container.getChildAt(currentIndex));
        handler.removeCallbacks(scrollRunnable);
      }else {
        //手指离开屏幕,但是view还在滚动
        currentScrollType = ScrollType.SCROLL;
      }


      currentX = getScrollX();
      //滚动监听间隔:milliseconds
      if(currentScrollType==ScrollType.SCROLL||currentScrollType==ScrollType.TOUCH_SCROLL){
        handler.postDelayed(this, 20);
      }
    }
  };


  @Override
  public boolean performClick() {
    return super.performClick();
  }


  //获取子控件信息
  public void getChildInfo() {
    container = (ViewGroup) getChildAt(0);
    if (container != null) {
      tabCount = container.getChildCount();

      for (int i = 0; i < tabCount; i++) {
        TextView tv = (TextView) container.getChildAt(i);
        tv.setTag(i);

        tv.setOnClickListener(new OnClickListener() {
          @Override
          public void onClick(View v) {
            currentIndex = (int) v.getTag();
            if(selecteListener!=null){
              setCurrentTabInMiddle(currentIndex);
              selecteListener.OnSelectListener((TextView) v);
            }
          }
        });

      }


    }
  }


  public void setCurrentTabInMiddle(int index) {
    TextView currentTab = (TextView) container.getChildAt(index);
    int distance = currentTab.getLeft();
    int scrollTo = distance + currentTab.getMeasuredWidth() / 2 - getScreenWidth(context) / 2;
    cancelOtherTextColor();
    setCurrentTab(currentTab);
    scrollToPosition(scrollTo, 0);
  }


  public void setDefaultPosition(int index){
    TextView currentTab = (TextView) container.getChildAt(index);
    int distance = getScreenWidth(context) / 3*(index+1);
    int scrollTo = distance - getScreenWidth(context) / 6 - getScreenWidth(context) / 2;
    cancelOtherTextColor();
    setCurrentTab(currentTab);
    scrollToPosition(scrollTo, 0);
  }


  public void setCurrentTab(TextView tab) {
    tab.setTextColor(getResources().getColor(R.color.text_color_gold));
    tab.setTextSize(TypedValue.COMPLEX_UNIT_SP,10);
//      Drawable bottomDrawable = getResources().getDrawable(R.mipmap.home_top_guaid);
//    tab.measure(0, 0);//执行View.measure(0, 0)  自己决定大小
//      bottomDrawable.setBounds(0, 0, tab.getMeasuredWidth(), bottomDrawable.getMinimumHeight());
//      tab.setCompoundDrawables(null, null, null, bottomDrawable);
  }

  private void scrollToPosition(int x,int y) {

    ObjectAnimator xTranslate = ObjectAnimator.ofInt(this, "scrollX", x);
    ObjectAnimator yTranslate = ObjectAnimator.ofInt(this, "scrollY", y);

    AnimatorSet animators = new AnimatorSet();
    animators.setDuration(400L);
    animators.playTogether(xTranslate, yTranslate);
    animators.start();
  }

  private void cancelOtherTextColor() {
    for (int i = 0; i < tabCount; i++) {
      TextView textView = (TextView) container.getChildAt(i);
      textView.setTextColor(getResources().getColor(R.color.gray));
      textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,10);
      textView.setCompoundDrawables(null, null, null, null);
    }
  }

  /**
   * 触摸监听时间
   */
  @Override
  public boolean onTouchEvent(MotionEvent ev) {
    switch (ev.getAction()) {
      case MotionEvent.ACTION_DOWN:
        performClick();
        currentScrollType = ScrollType.STOP;
        handler.removeCallbacks(scrollRunnable);
        break;
      case MotionEvent.ACTION_MOVE:
        currentScrollType = ScrollType.TOUCH_SCROLL;
        handler.removeCallbacks(scrollRunnable);
        break;
      case MotionEvent.ACTION_UP:
      case MotionEvent.ACTION_CANCEL:
        handler.post(scrollRunnable);
        break;
    }
    return super.onTouchEvent(ev);
  }


  /**
   * 获取屏幕宽度
   *
   * @return
   */
  public static int getScreenWidth(Context context) {
    return AppApplication.getInstance().getWindowWidth();
  }

  public void setSelecteListener(OnScrollViewSelectListener listener) {
    this.selecteListener = listener;
  }

  public interface OnScrollViewSelectListener {
    void OnSelectListener(TextView tab);
  }

}
