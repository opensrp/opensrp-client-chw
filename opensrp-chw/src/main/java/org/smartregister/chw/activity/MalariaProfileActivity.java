package org.smartregister.chw.activity;


import android.app.Activity;
import android.content.Intent;
import org.smartregister.chw.malaria.activity.BaseMalariaProfileActivity;
import org.smartregister.commonregistry.CommonPersonObjectClient;

public class MalariaProfileActivity extends BaseMalariaProfileActivity {
    private static CommonPersonObjectClient client;

    public static void startMalariaActivity(Activity activity, Intent intent) {
//        client = (CommonPersonObjectClient)intent.getSerializableExtra("client");
        activity.startActivity(intent);

    }

}
