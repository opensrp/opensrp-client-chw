package org.smartregister.chw.activity;


import android.app.Activity;
import android.content.Intent;
import android.view.View;
import org.smartregister.chw.malaria.activity.BaseMalariaProfileActivity;

public class MalariaProfileActivity extends BaseMalariaProfileActivity {
    public static void startMalariaActivity(Activity activity, Intent intent) {
        activity.startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == org.smartregister.malaria.R.id.toolbar_title) {
            onBackPressed();
        }
    }
}
