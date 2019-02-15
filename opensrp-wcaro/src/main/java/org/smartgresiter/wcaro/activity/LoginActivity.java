package org.smartgresiter.wcaro.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.TextView;

import org.smartgresiter.wcaro.R;
import org.smartgresiter.wcaro.presenter.LoginPresenter;
import org.smartgresiter.wcaro.util.Utils;
import org.smartregister.family.util.Constants;
import org.smartregister.task.SaveTeamLocationsTask;
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

    private void getToFamilyList(boolean remote) {
        Intent intent = new Intent(this, FamilyRegisterActivity.class);
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

    @Override
    protected void renderBuildInfo() {
        TextView application_version = findViewById(org.smartregister.R.id.login_build_text_view);
        if (application_version != null) {
            try {
                application_version.setText(String.format(getString(org.smartregister.R.string.app_version), Utils.getVersion(this), Utils.getBuildDate(true)));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

    }
}