package com.opensrp.chw.hf;

import android.content.Intent;
import android.os.Bundle;

import com.opensrp.hf.R;

import org.smartregister.family.util.Constants;
import org.smartregister.view.activity.BaseLoginActivity;
import org.smartregister.view.contract.BaseLoginContract;


public class LoginActivity extends BaseLoginActivity implements BaseLoginContract.View {
    public static final String TAG = BaseLoginActivity.class.getCanonicalName();

    @Override
    protected int getContentView() {
        return R.layout.activity_login;
    }

    @Override
    protected void initializePresenter() {
     //   mLoginPresenter = new LoginPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void goToHome(boolean remote) {
        if (remote) {
        //    Utils.startAsyncTask(new SaveTeamLocationsTask(), null);
        }

        getToFamilyList(remote);

        finish();
    }

    private void getToFamilyList(boolean remote) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(Constants.INTENT_KEY.IS_REMOTE_LOGIN, remote);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLoginPresenter.processViewCustomizations();
        if (!mLoginPresenter.isUserLoggedOut()) {
            goToHome(false);
        }
    }

}