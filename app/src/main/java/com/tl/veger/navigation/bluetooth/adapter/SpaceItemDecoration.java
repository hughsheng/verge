package com.tl.veger.navigation.bluetooth.adapter;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * created by tl on 2019/10/18
 * 设置recycleviewitem间距
 */
public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

  @Override
  public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                             @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
    super.getItemOffsets(outRect, view, parent, state);
    int position = parent.getChildAdapterPosition(view);
    if (position % 2 == 0) {
      outRect.set(100, 0, 0, 0);
    }else {
      outRect.set(0, 0, 100, 0);
    }
  }


  @Override
  public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent,
                     @NonNull RecyclerView.State state) {
    super.onDraw(c, parent, state);
  }

  @Override
  public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent,
                         @NonNull RecyclerView.State state) {
    super.onDrawOver(c, parent, state);
  }
}
