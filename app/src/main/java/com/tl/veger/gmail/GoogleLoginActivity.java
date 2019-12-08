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
import com.tl.veger.utils.ConstanceValue;
import com.tl.veger.utils.PermissionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GoogleLoginActivity extends AppCompatActivity  {

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
      case REQUEST_GOOGLE_PLAY_SERVICES:
        if (resultCode != RESULT_OK) {
          isGooglePlayServicesAvailable();
        }
        break;
      case REQUEST_ACCOUNT_PICKER:
        if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
          String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
          if (accountName != null) {
            credential.setSelectedAccountName(accountName);
            SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(PREF_ACCOUNT_NAME, accountName);
            editor.commit();
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
    SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = settings.edit();
    editor.putString(PREF_ACCOUNT_NAME, null);
    editor.commit();
  }

  static final int REQUEST_ACCOUNT_PICKER = 1000;
  static final int REQUEST_AUTHORIZATION = 1001;
  static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
  private static final String PREF_ACCOUNT_NAME = "accountName";
  private static final String[] SCOPES = {GmailScopes.GMAIL_LABELS/*, GmailScopes
  .MAIL_GOOGLE_COM, GmailScopes.GMAIL_READONLY, GmailScopes.GMAIL_MODIFY*/};
  final HttpTransport transport = AndroidHttp.newCompatibleTransport();
  final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
  /**
   * A Gmail API service object used to access the API.
   * Note: Do not confuse this class with API library's model classes, which
   * represent specific data structures.
   */

  GoogleAccountCredential credential;
  ProgressDialog mProgress;

  public void apiInit() {
    if (!isGooglePlayServicesAvailable()) {
      login.setEnabled(false);
      return;
    }

    mProgress = new ProgressDialog(this);
    mProgress.setMessage("Calling Gmail API ...");

    SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
    String account = settings.getString(PREF_ACCOUNT_NAME, null);
    credential = GoogleAccountCredential.usingOAuth2(getApplicationContext(), Arrays.asList(SCOPES))
        .setBackOff(new ExponentialBackOff())
        .setSelectedAccountName(account);
    ConstanceValue.mService = new Gmail.Builder(transport, jsonFactory, credential)
        .setApplicationName(getString(R.string.app_name))
        .build();
    if (!TextUtils.isEmpty(account)) {
      refreshEmailData();
    }
  }

  /**
   * Starts an activity in Google Play Services so the user can pick an
   * account.
   */
  private void chooseAccount() {
    SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
    String account = settings.getString(PREF_ACCOUNT_NAME, null);
    if (!TextUtils.isEmpty(account)) {
      Toast.makeText(this, "您已经登录了", Toast.LENGTH_SHORT).show();
      refreshEmailData();
      return;
    }
    startActivityForResult(credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
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
          startActivity(new Intent(GoogleLoginActivity.this, HomeActivity.class));
        }
      }
    });
  }

  private void refreshEmailData() {
    if (isGooglePlayServicesAvailable()) {
      mProgress.show();
      new ApiAsyncTask(this).execute();
    }
  }

  /**
   * Check that Google Play services APK is installed and up to date. Will
   * launch an error dialog for the user to update Google Play Services if
   * possible.
   *
   * @return true if Google Play Services is available and up to
   * date on this device; false otherwise.
   */
  private boolean isGooglePlayServicesAvailable() {
    int connectionStatusCode =
        GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
    if (GoogleApiAvailability.getInstance().isUserResolvableError(connectionStatusCode)) {
      showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
      return false;
    } else return connectionStatusCode == ConnectionResult.SUCCESS;
  }

  /**
   * Display an error dialog showing that Google Play Services is missing
   * or out of date.
   *
   * @param connectionStatusCode code describing the presence (or lack of)
   *                             Google Play Services on this device.
   */
  void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(GoogleLoginActivity.this,
            connectionStatusCode, REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
      }
    });
  }



}
