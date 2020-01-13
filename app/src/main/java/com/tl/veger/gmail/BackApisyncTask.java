package com.tl.veger.gmail;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.tl.veger.utils.ConmmonUtil;

public class BackApisyncTask extends AsyncTask<Void, Void, Void> {
	private Activity mActivity;

	/**
	 * Constructor.
	 *
	 * @param activity GoogleLoginActivity that spawned this task.
	 */
	public BackApisyncTask(Activity activity) {
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
			ConmmonUtil.getDataFromApi(mActivity);
		} catch (UserRecoverableAuthIOException userRecoverableException) {
			System.out.println("没有权限，请求授权");
			mActivity.startActivityForResult(userRecoverableException.getIntent(),
					GoogleLoginActivity.REQUEST_AUTHORIZATION);
		} catch (Exception e) {
			Toast.makeText(mActivity, e.getMessage(), Toast.LENGTH_LONG).show();
		}

		return null;
	}

}
