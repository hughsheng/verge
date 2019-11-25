package com.tl.veger.navigation.bluetooth.service;

import android.app.Notification;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import com.tl.veger.busbean.NotificationPostedBusBean;
import com.tl.veger.busbean.NotificationRemovedBusBean;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by tl on 2018-9-27
 * 通知使用权service用于获取android通知
 */
public class NotificationService extends NotificationListenerService {

  // 通知被移除时回调
  @Override
  public void onNotificationRemoved(StatusBarNotification sbn) {
    super.onNotificationRemoved(sbn);
    Notification notification = sbn.getNotification();
    EventBus.getDefault().post(new NotificationRemovedBusBean(notification));
  }

  // 增加一条通知时回调
  @Override
  public void onNotificationPosted(StatusBarNotification sbn) {
    super.onNotificationPosted(sbn);
    Notification notification = sbn.getNotification();
    EventBus.getDefault().post(new NotificationPostedBusBean(notification));
  }


}
