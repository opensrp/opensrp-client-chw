package org.smartregister.chw.activity;

import android.annotation.SuppressLint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import org.smartregister.chw.R;
import org.smartregister.chw.anc.util.DBConstants;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.helper.ImageRenderHelper;
import org.smartregister.util.Utils;
import org.smartregister.view.activity.BaseProfileActivity;

public class MalariaProfileActivity extends BaseProfileActivity {
    private String full_name;
    private String location;
    private String gender;
    private String unique_id;
    private CommonPersonObjectClient client;
    private TextView textViewFullName;
    private TextView textViewLocation;
    private TextView textViewGender;
    private TextView textViewUniqueId;
    private TextView textViewTitle;

    @SuppressLint("DefaultLocale")
    protected void onCreation() {

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            full_name = String.format("%s ,%d", extras.getString("full_name"), extras.getInt("age"));
            location = extras.getString("location");
            gender = extras.getString("gender");
            unique_id = extras.getString("unique_id");
            client = (CommonPersonObjectClient) getIntent().getSerializableExtra("client");

        }

        textViewFullName = findViewById(R.id.textview_name);
        textViewLocation = findViewById(R.id.textview_address);
        textViewGender = findViewById(R.id.textview_gender);
        textViewUniqueId = findViewById(R.id.textview_id);
        fillValues(full_name, location, gender, unique_id);
    }

    @Override
    protected void initializePresenter() {

    }

    @Override
    protected ViewPager setupViewPager(ViewPager viewPager) {
        return null;
    }

    @Override
    protected void fetchProfileData() {

    }

    private void fillValues(String full_name, String location, String gender, String unique_id) {
        String baseEntityId = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.BASE_ENTITY_ID, true);
        textViewFullName.setText(full_name);
        textViewLocation.setText(location);
        textViewLocation.setText(location);
        textViewGender.setText(gender);
        textViewUniqueId.setText(unique_id);

        Log.e("COMMON PERSON", client.getColumnmaps() + "");
        Log.e("COMMON PERSON", Utils.getValue(client.getColumnmaps(), DBConstants.KEY.FIRST_NAME, true));
    }
}
