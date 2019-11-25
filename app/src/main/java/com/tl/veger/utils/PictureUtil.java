package com.tl.veger.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * created by tl on 2019-1-15
 */
public class PictureUtil {


  //给view设置毛玻璃效果
  public static void applyBlur(final Context context, final ImageView imageView,
                               final ImageView goalView) {
    imageView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
      @Override
      public boolean onPreDraw() {
        imageView.getViewTreeObserver().removeOnPreDrawListener(this);
        imageView.buildDrawingCache();

        Bitmap bmp = imageView.getDrawingCache();
         blur(context, bmp, goalView);
        return true;
      }
    });

  }

  private static void blur(final Context context, final Bitmap bkg, final ImageView imageView) {
    float scaleFactor = 8;
    final float radius = 10;

    Disposable disposable = Observable.just(bkg)
        .subscribeOn(AndroidSchedulers.mainThread())
        .observeOn(Schedulers.io())
        .map(new Function<Bitmap, Bitmap>() {
          @Override
          public Bitmap apply(Bitmap bkg) {
//            Bitmap overlay = Bitmap.createBitmap(bkg,
//                0, bkg.getHeight() - DensityUtil.dip2px(context, 40),
//                bkg.getWidth(), DensityUtil.dip2px(context, 40));
//            overlay = FastBlur.doBlur(overlay, (int) radius, true);
//            return overlay;

            return FastBlur.doBlur(bkg, (int) radius, true);
          }
        })
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<Bitmap>() {
          @Override
          public void accept(Bitmap overlay) {
            imageView.setImageDrawable(new BitmapDrawable(context.getResources(), overlay));
          }
        });
  }
}
