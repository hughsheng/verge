
package com.tl.veger.gmail;

import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.services.gmail.model.Label;
import com.tl.veger.utils.ConstanceValue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * An asynchronous task that handles the Gmail API call.
 * Placing the API calls in their own task ensures the UI stays responsive.
 */
public class ApiAsyncTask extends AsyncTask<Void, Void, Void> {
  private GoogleLoginActivity mActivity;

  /**
   * Constructor.
   *
   * @param activity GoogleLoginActivity that spawned this task.
   */
  ApiAsyncTask(GoogleLoginActivity activity) {
    this.mActivity = activity;
  }

  /**
   * Background task to call Gmail API.
   *
   * @param params no parameters needed for this task.
   */
  @Override
  protected Void doInBackground(Void... params) {
    try {
      mActivity.updateResultsText(getDataFromApi());
    } catch (final GooglePlayServicesAvailabilityIOException availabilityException) {
      // 设备没有谷歌相关服务
      mActivity.showGooglePlayServicesAvailabilityErrorDialog(availabilityException.getConnectionStatusCode());
    } catch (UserRecoverableAuthIOException userRecoverableException) {
      System.out.println("没有权限，请求授权");
      mActivity.startActivityForResult(userRecoverableException.getIntent(),
          GoogleLoginActivity.REQUEST_AUTHORIZATION);
    } catch (Exception e) {
      mActivity.updateStatus("The following error occurred:\n" + e.getMessage());
    }
    if (mActivity.mProgress.isShowing()) {
      mActivity.mProgress.dismiss();
    }
    return null;
  }

  /**
   * Fetch a list of Gmail labels attached to the specified account.
   *
   * @return List of Strings labels.
   */
  public static List<String> getDataFromApi() throws IOException {
    // Get the labels in the user's account.
    String user = "me";
    List<String> labels = new ArrayList<>();
    Label label = ConstanceValue.mService.users().labels().get(user, "INBOX").execute(); // have
      // the lableName to execute together
    labels.add("所有邮件：" + label.getThreadsTotal());
    labels.add("未读邮件：" + label.getThreadsUnread());
    if (ConstanceValue.UNREAD_MAIL != label.getThreadsUnread()) {
      ConstanceValue.UNREAD_MAIL = label.getThreadsUnread();
      labels.add("有新邮件");
    }
    Log.i("mylog", labels.toString());

    return labels;
  }
}