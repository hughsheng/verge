
package com.tl.veger.gmail;

import android.os.AsyncTask;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.tl.veger.utils.ConmmonUtil;

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
			mActivity.updateResultsText(ConmmonUtil.getDataFromApi(mActivity));
		} catch (final GooglePlayServicesAvailabilityIOException availabilityException) {
			// 设备没有谷歌相关服务
			ConmmonUtil.showGooglePlayServicesAvailabilityErrorDialog(mActivity, availabilityException.getConnectionStatusCode());
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


}