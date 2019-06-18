package org.smartregister.chw.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import org.smartregister.chw.malaria.activity.BaseMalariaProfileActivity;

public class MalariaProfileActivity extends BaseMalariaProfileActivity {
    private String full_name, address, id, gender;
    private int age;
    private View view;

    public static void startMalariaActivity(Activity activity, Intent intent) {
        activity.startActivity(intent);
    }

    @Override
    protected void onCreation() {
        super.onCreation();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            full_name = extras.getString("full_name");
            age = extras.getInt("age");
            address = extras.getString("location");
            id = extras.getString("unique_id");
            gender = extras.getString("gender");
        }
        setupViews(view);

    }

    @SuppressLint("DefaultLocale")
    private void setupViews(View view) {
        this.view = view;
        TextView patientName = findViewById(org.smartregister.malaria.R.id.textview_name);
        patientName.setText(String.format("%s ,%d", full_name, age));

        TextView patientAddress = findViewById(org.smartregister.malaria.R.id.textview_address);
        patientAddress.setText(address);

        TextView patientId = findViewById(org.smartregister.malaria.R.id.textview_id);
        patientId.setText(id);

        TextView patientGender = findViewById(org.smartregister.malaria.R.id.textview_gender);
        patientGender.setText(gender);


    }

    @Override
    public void onClick(View view) {
        if (view.getId() == org.smartregister.malaria.R.id.toolbar_title) {
            onBackPressed();
        }
    }
}
