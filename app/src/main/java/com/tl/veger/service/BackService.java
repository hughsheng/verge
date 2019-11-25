package com.tl.veger.service;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;

import androidx.annotation.Nullable;

/**
 * created by tl on 2019/10/25
 */
public class BackService extends IntentService {
  /**
   * Creates an IntentService.  Invoked by your subclass's constructor.
   *
   * @param name Used to name the worker thread, important only for debugging.
   */
  public BackService(String name) {
    super(name);
  }

  @Override
  protected void onHandleIntent(@Nullable Intent intent) {

  }
}
