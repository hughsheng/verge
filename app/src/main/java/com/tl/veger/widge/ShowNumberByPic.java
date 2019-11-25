package com.tl.veger.widge;

import android.content.Context;
import android.database.sqlite.SQLiteBindOrColumnIndexOutOfRangeException;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import com.tl.veger.R;

import java.util.EventListener;
import java.util.List;

import butterknife.BindView;

/**
 * created by tl on 2019/10/6
 * 用于数字图片替换数字显示
 */
public class ShowNumberByPic extends LinearLayout {

  public static final int TIME=0x601;
  public static final int PERCENT=0x602;


  private LinearLayout time_layout;
  private RelativeLayout percent_layout;
  private ImageView snbp_one, snbp_two, snbp_three, snbp_four, snbp_p_one, snbp_p_two,snbp_p_three;


  public ShowNumberByPic(Context context) {
    super(context);
  }

  public ShowNumberByPic(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  public ShowNumberByPic(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }


  @Override
  protected void onFinishInflate() {
    super.onFinishInflate();
    LayoutInflater.from(getContext()).inflate(R.layout.widget_snbp, this);
    initView();
  }



  public void setData(List<Integer> data){
    if(data.size()==4){
      showType(TIME);
      replacePicFromNumber(data.get(0),snbp_one);
      replacePicFromNumber(data.get(1),snbp_two);
      replacePicFromNumber(data.get(2),snbp_three);
      replacePicFromNumber(data.get(3),snbp_four);
    }else {
      showType(PERCENT);
      replacePicFromNumber(data.get(0),snbp_p_one);
      replacePicFromNumber(data.get(1),snbp_p_two);
      if(data.size()==3){
        replacePicFromNumber(data.get(1),snbp_p_three);
      }
    }
  }

  public void showType(int type){
    switch (type){
      case TIME:
        time_layout.setVisibility(VISIBLE);
        percent_layout.setVisibility(INVISIBLE);
        break;

      case PERCENT:
        time_layout.setVisibility(INVISIBLE);
        percent_layout.setVisibility(VISIBLE);
        break;
    }
  }

  private void initView() {
    time_layout=findViewById(R.id.time_layout);
    percent_layout=findViewById(R.id.percent_layout);
    snbp_one = findViewById(R.id.snbp_one);
    snbp_two = findViewById(R.id.snbp_two);
    snbp_three = findViewById(R.id.snbp_three);
    snbp_four = findViewById(R.id.snbp_four);
    snbp_p_one = findViewById(R.id.snbp_p_one);
    snbp_p_two = findViewById(R.id.snbp_p_two);
    snbp_p_three = findViewById(R.id.snbp_p_three);
  }


  private void replacePicFromNumber(int number,ImageView view){
    switch (number){
      case 0:
        view.setImageResource(R.mipmap.zero);
        break;
      case 1:
        view.setImageResource(R.mipmap.one);
        break;
      case 2:
        view.setImageResource(R.mipmap.two);
        break;
      case 3:
        view.setImageResource(R.mipmap.three);
        break;
      case 4:
        view.setImageResource(R.mipmap.four);
        break;
      case 5:
        view.setImageResource(R.mipmap.five);
        break;
      case 6:
        view.setImageResource(R.mipmap.six);
        break;
      case 7:
        view.setImageResource(R.mipmap.seven);
        break;
      case 8:
        view.setImageResource(R.mipmap.eight);
        break;
      case 9:
        view.setImageResource(R.mipmap.nine);
        break;

    }
  }

}
