package com.tl.veger.gmail;

import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.SignInButton;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.tl.veger.HomeActivity;
import com.tl.veger.R;
import com.tl.veger.base.app.AppApplication;
import com.tl.veger.utils.ConmmonUtil;
import com.tl.veger.utils.ConstanceValue;
import com.tl.veger.utils.SharedPreferencesUtils;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.tl.veger.utils.ConstanceValue.PREF_ACCOUNT_NAME;
import static com.tl.veger.utils.ConstanceValue.SCOPES;

public class GoogleLoginActivity extends AppCompatActivity {

	public TextView mInfo;
	SignInButton login;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_google);
		mInfo = findViewById(R.id.info);
		login = findViewById(R.id.login);
		login.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				chooseAccount();
			}
		});

		apiInit();

		findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				signOut();
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case ConstanceValue.REQUEST_GOOGLE_PLAY_SERVICES:
				if (resultCode != RESULT_OK) {
					ConmmonUtil.isGooglePlayServicesAvailable(GoogleLoginActivity.this);
				}
				break;
			case REQUEST_ACCOUNT_PICKER:
				if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
					String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
					if (accountName != null) {
						credential.setSelectedAccountName(accountName);
						AppApplication.getInstance().saveCacheData(PREF_ACCOUNT_NAME, accountName);
					}
					refreshEmailData();
				} else if (resultCode == RESULT_CANCELED) {
					// 取消选择
					signOut();
				}
				break;
			case REQUEST_AUTHORIZATION:
				if (resultCode == RESULT_OK) {
					refreshEmailData();
				} else {
					// 拒绝授权或授权失败
					signOut();
					// chooseAccount();
				}
				break;
		}
	}

	/**
	 * 退出登录
	 */
	public void signOut() {
		mInfo.setText(null);
		AppApplication.getInstance().saveCacheData(PREF_ACCOUNT_NAME, "");
	}

	static final int REQUEST_ACCOUNT_PICKER = 1000;
	static final int REQUEST_AUTHORIZATION = 1001;

	/**
	 * A Gmail API service object used to access the API.
	 * Note: Do not confuse this class with API library's model classes, which
	 * represent specific data structures.
	 */

	GoogleAccountCredential credential;
	ProgressDialog mProgress;

	public void apiInit() {
		if (!ConmmonUtil.isGooglePlayServicesAvailable(GoogleLoginActivity.this)) {
			login.setEnabled(false);
			return;
		}

		mProgress = new ProgressDialog(this);
		mProgress.setMessage("Calling Gmail API ...");
		if (ConstanceValue.mService == null) {
			ConmmonUtil.setGmailService(this);
		} else {
			refreshEmailData();
		}

	}

	/**
	 * Starts an activity in Google Play Services so the user can pick an
	 * account.
	 */
	private void chooseAccount() {
		String account = (String) AppApplication.getInstance().getCacheData(PREF_ACCOUNT_NAME, "");
		if (!TextUtils.isEmpty(account)) {
			Toast.makeText(this, "You're signed in", Toast.LENGTH_SHORT).show();
			refreshEmailData();
		} else {
			credential = GoogleAccountCredential.usingOAuth2(this, Arrays.asList(SCOPES))
					.setBackOff(new ExponentialBackOff())
					.setSelectedAccountName(account);
			startActivityForResult(credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
		}

	}

	/**
	 * Show a status message in the list header TextView; called from background
	 * threads and async tasks that need to update the UI (in the UI thread).
	 *
	 * @param message a String to display in the UI header TextView.
	 */
	public void updateStatus(final String message) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mInfo.setText(message);
			}
		});
	}

	/**
	 * Fill the data TextView with the given List of Strings; called from
	 * background threads and async tasks that need to update the UI (in the
	 * UI thread).
	 *
	 * @param dataStrings a List of Strings to populate the main TextView with.
	 */
	public void updateResultsText(final List<String> dataStrings) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (dataStrings == null) {
					mInfo.setText("Error retrieving data!");
				} else if (dataStrings.size() == 0) {
					mInfo.setText("No data found.");
				} else {
					mInfo.setText("Data retrieved using the Gmail API:");
					mInfo.setText(TextUtils.join("\n\n", dataStrings));
					//startActivity(new Intent(GoogleLoginActivity.this, HomeActivity.class));
					finish();
				}
			}
		});
	}

	private void refreshEmailData() {
		if (ConmmonUtil.isGooglePlayServicesAvailable(this)) {
			mProgress.show();
			new ApiAsyncTask(this).execute();
		}
	}


}
