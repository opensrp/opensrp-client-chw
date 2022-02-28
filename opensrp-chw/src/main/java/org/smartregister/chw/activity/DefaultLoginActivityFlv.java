package org.smartregister.chw.activity;

import android.content.Context;
import android.content.pm.PackageManager;
import android.widget.TextView;

import org.smartregister.chw.R;

public class DefaultLoginActivityFlv implements LoginActivity.Flavor {

    @Override
    public void setAppVersionDetails(TextView appVersionDetails, Context context) throws PackageManager.NameNotFoundException {
        appVersionDetails.setText(String.format(context.getString(R.string.app_version),
                org.smartregister.util.Utils.getVersion(context), org.smartregister.util.Utils.getBuildDate(true)));
    }
}
