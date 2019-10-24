package org.smartregister.brac.hnpp.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import org.smartregister.AllConstants;
import org.smartregister.brac.hnpp.BuildConfig;
import org.smartregister.brac.hnpp.HnppApplication;
import org.smartregister.brac.hnpp.R;
import org.smartregister.brac.hnpp.job.PullHouseholdIdsServiceJob;
import org.smartregister.brac.hnpp.job.SSLocationFetchJob;
import org.smartregister.brac.hnpp.presenter.LoginPresenter;
import org.smartregister.brac.hnpp.utils.HnppConstants;
import org.smartregister.family.util.Constants;
import org.smartregister.task.SaveTeamLocationsTask;
import org.smartregister.util.Utils;
import org.smartregister.view.activity.BaseLoginActivity;
import org.smartregister.view.contract.BaseLoginContract;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;


public class LoginActivity extends BaseLoginActivity implements BaseLoginContract.View {
    public static final String TAG = BaseLoginActivity.class.getCanonicalName();

    private EditText userNameText,passwordText;
    private View userNameView, passwordView;
    private Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userNameText = findViewById(R.id.login_user_name_edit_text);
        passwordText = findViewById(R.id.login_password_edit_text);
        userNameView = findViewById(R.id.login_user_name_view);
        passwordView = findViewById(R.id.login_password_view);

        userNameText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() > 0){
                    findViewById(R.id.login_login_btn).setAlpha(1.0f);
                }

            }
        });
        passwordText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() > 0){
                    findViewById(R.id.login_login_btn).setAlpha(1.0f);
                }

            }
        });

        userNameText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View arg0, boolean hasfocus) {
                if (hasfocus) {
                    userNameView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.hnpp_accent));
                } else {
                    userNameView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), android.R.color.black));

                }
            }
        });

        passwordText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View arg0, boolean hasfocus) {
                if (hasfocus) {
                    passwordView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.hnpp_accent));
                } else {
                    passwordView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), android.R.color.black));
                }
            }
        });


    }
    @Override
    protected void onResume() {
        super.onResume();
        mLoginPresenter.processViewCustomizations();
        if (!mLoginPresenter.isUserLoggedOut()) {
                    goToHome(false);
         }
        fillUserIfExists();
        findViewById(R.id.login_login_btn).setAlpha(1.0f);
        mActivity = this;

        if(!BuildConfig.DEBUG)app_version_status();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mActivity = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mActivity = null;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == org.smartregister.R.id.login_login_btn) {
            String userName = HnppApplication.getInstance().getContext().allSharedPreferences().fetchRegisteredANM();
            if(!TextUtils.isEmpty(userName) && !userName.equalsIgnoreCase(userNameText.getText().toString())){
                showClearDataMessage();
                return;
            }

            v.setAlpha(0.3f);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mLoginPresenter.attemptLogin(userNameText.getText().toString(), passwordText.getText().toString());
                }
            },500);

        }
    }

    private void fillUserIfExists() {
            String userName = HnppApplication.getInstance().getContext().allSharedPreferences().fetchRegisteredANM();
            if(!TextUtils.isEmpty(userName)){
                userNameText.setText(userName.trim());
            }
    }
    private void showClearDataMessage(){
        new AlertDialog.Builder(this).setMessage(getString(R.string.clear_data))
                .setTitle(R.string.title_clear_data).setCancelable(false)
                .setPositiveButton(R.string.yes_button_label, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                }).show();
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_login;
    }

    @Override
    protected void initializePresenter() {
        mLoginPresenter = new LoginPresenter(this);
    }

    @Override
    public void goToHome(boolean remote) {
        if (remote) {
            Utils.startAsyncTask(new SaveTeamLocationsTask(), null);
        }

        getToFamilyList(remote);

        finish();
    }
    public void app_version_status() {
        org.smartregister.util.Utils.startAsyncTask(new AsyncTask() {
            String version_code = "";
            String version = "";

            @Override
            protected Object doInBackground(Object[] objects) {
                try {
                    String baseUrl = Utils.getAllSharedPreferences().getPreference(AllConstants.DRISHTI_BASE_URL);
                    // Create a URL for the desired page
                    baseUrl = baseUrl.replace("opensrp/", "");
                    URL url = new URL(baseUrl + "opt/multimedia/app-version.txt");

                    // Read all the text returned by the server
                    BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                    String str;
                    str = "";
                    while ((str = in.readLine()) != null) {
                        // str is one line of text; readLine() strips the newline character(s)
                        version_code += str;
                    }
                    in.close();
                } catch (MalformedURLException e) {
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                try {
                    PackageInfo pInfo = LoginActivity.this.getPackageManager().getPackageInfo(getPackageName(), 0);
                    version = pInfo.versionName;
                    if (!version_code.trim().isEmpty()&&!version.equalsIgnoreCase(version_code.trim())) {
                        android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(LoginActivity.this).create();
                        alertDialog.setTitle("নতুন ভার্সন আপডেট করুন ");
                        alertDialog.setCancelable(false);

                        alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE, "আপডেট",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        try {
                                            final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                                            try {
                                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                            } catch (android.content.ActivityNotFoundException anfe) {
                                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));

                                            }


                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                        if ( mActivity!=null && alertDialog != null)
                            alertDialog.show();
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }, null);
    }

    private void getToFamilyList(boolean remote) {
        Intent intent = new Intent(this, FamilyRegisterActivity.class);
        intent.putExtra(Constants.INTENT_KEY.IS_REMOTE_LOGIN, remote);
        startActivity(intent);
        PullHouseholdIdsServiceJob.scheduleJobImmediately(PullHouseholdIdsServiceJob.TAG);
        SSLocationFetchJob.scheduleJobImmediately(SSLocationFetchJob.TAG);
    }

}