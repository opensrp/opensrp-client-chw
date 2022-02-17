package org.smartregister.chw.activity;

import android.content.Context;
import android.content.pm.PackageManager;
import android.widget.TextView;

import org.smartregister.chw.BuildConfig;
import org.smartregister.chw.R;

public class LoginActivityFlv extends DefaultLoginActivityFlv {

    @Override
    public void setAppVersionDetails(TextView appVersionDetails, Context context) throws PackageManager.NameNotFoundException {
        appVersionDetails.setText(String.format(context.getString(R.string.app_version_with_env),
                org.smartregister.util.Utils.getVersion(context), org.smartregister.util.Utils.getBuildDate(true), BuildConfig.SERVER_ENV));
    }
}
