package org.smartregister.chw.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.smartregister.chw.R;
import org.smartregister.chw.malaria.activity.BaseMalariaProfileActivity;
import org.smartregister.chw.presenter.ChildProfilePresenter;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.util.Utils;

public class MalariaProfileActivity extends BaseMalariaProfileActivity {
    private String first_name, middle_name, last_name, address, id, gender;
    private int age;
    private CommonPersonObjectClient client;
    private View view;
    private ChildProfileActivityFlv flavor = new ChildProfileActivityFlv();

    public static void startMalariaActivity(Activity activity, Intent intent) {
        activity.startActivity(intent);
    }

    @Override
    protected void onCreation() {
        super.onCreation();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            client = (CommonPersonObjectClient) getIntent().getSerializableExtra("client");
            first_name = Utils.getValue(client.getColumnmaps(), "first_name", true);
            middle_name = Utils.getValue(client.getColumnmaps(), "middle_name", true);
            last_name = Utils.getValue(client.getColumnmaps(), "last_name", true);
            age = (new Period(new DateTime(Utils.getValue(client.getColumnmaps(), "dob", false)), new DateTime())).getYears();
            gender = Utils.getValue(client.getColumnmaps(), "gender", true);
            address = Utils.getValue(client.getColumnmaps(), "village_town", true);
        }
        setupViews(view);

    }

    @SuppressLint("DefaultLocale")
    private void setupViews(View view) {
        this.view = view;
        TextView patientName = findViewById(org.smartregister.malaria.R.id.textview_name);
        patientName.setText(String.format("%s %s %s, %d", first_name, middle_name, last_name, age));

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.other_member_menu, menu);
        if (flavor.showMalariaConfirmationMenu()) {
            menu.findItem(R.id.action_malaria_registration).setVisible(false);
            menu.findItem(R.id.action_malaria_followup_visit).setVisible(true);
        }
        menu.findItem(R.id.action_anc_registration).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_registration:
//                AncRegisterActivity.startAncRegistrationActivity(FamilyOtherMemberProfileActivity.this, baseEntityId, PhoneNumber);
                return true;
            case R.id.action_malaria_followup_visit:
//                MalariaRegisterActivity.startMalariaRegistrationActivity(FamilyOtherMemberProfileActivity.this, baseEntityId);
                return true;
            case R.id.action_remove_member:
//                IndividualProfileRemoveActivity.startIndividualProfileActivity(FamilyOtherMemberProfileActivity.this, commonPersonObject, familyBaseEntityId, familyHead, primaryCaregiver);
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
