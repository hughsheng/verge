
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
            mActivity.startActivityForResult(userRecoverableException.getIntent(), GoogleLoginActivity.REQUEST_AUTHORIZATION);
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
    private List<String> getDataFromApi() throws IOException {
        // Get the labels in the user's account.
        String user = "me";
        List<String> labels = new ArrayList<>();
        Label label = mActivity.mService.users().labels().get(user, "INBOX").execute(); // have the lableName to execute together
        labels.add("所有邮件：" + label.getThreadsTotal());
        labels.add("未读邮件：" + label.getThreadsUnread());
        ConstanceValue.UNREAD_MAIL=label.getThreadsUnread();
        Log.i("mylog",labels.toString());
//        ListLabelsResponse listResponse =
//                mActivity.mService.users().labels().list(user).execute(); // this way just execute and get the label list
//        for (Label labelz : listResponse.getLabels()) {
//            String name = labelz.getName();
//            if (name != null && (name.equals("SPAM") || name.equals("UNREAD"))) {
//                labels.add(labelz.getName());
//                Log.d("sophia", "label name is " + labelz.getName());
//                Log.d("sophia", " get Message Unread is " + labelz.getMessagesUnread());
//                Log.d("sophia", " get message total is " + labelz.getMessagesTotal());
//            }
//
//        }
//        List<String> selectedMesLable = new ArrayList<String>();
//        selectedMesLable.add("INBOX");
//        int unreadMesNum = 0;
//        ListMessagesResponse listMesResponse = mActivity.mService.users().messages().list(user).setLabelIds(selectedMesLable).execute();
//        Log.d("sophia", "inbox message's num is " + listMesResponse.getMessages().size());
//        if (listMesResponse.getMessages() != null) {
//            for (Message msg : listMesResponse.getMessages()) {
//                if (msg.getLabelIds() != null && msg.getLabelIds().size() > 0) {
//                    for (String labelz : msg.getLabelIds()) {
//                        Log.d("sophia", "label is " + labelz);
//                        if (labelz.equals("INBOX"))
//                            unreadMesNum++;
//                    }
//                }
//            }
//        }
//        Log.d("sophia","unread message num is: " + unreadMesNum);

        return labels;
    }
}