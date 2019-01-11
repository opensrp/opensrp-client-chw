package org.smartgresiter.wcaro.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;
import org.smartgresiter.wcaro.R;
import org.smartgresiter.wcaro.contract.FamilyProfileExtendedContract;
import org.smartgresiter.wcaro.custom_view.FamilyFloatingMenu;
import org.smartgresiter.wcaro.event.PermissionEvent;
import org.smartgresiter.wcaro.fragment.FamilyProfileActivityFragment;
import org.smartgresiter.wcaro.fragment.FamilyProfileDueFragment;
import org.smartgresiter.wcaro.fragment.FamilyProfileMemberFragment;
import org.smartgresiter.wcaro.listener.FloatingMenuListener;
import org.smartgresiter.wcaro.model.FamilyProfileModel;
import org.smartgresiter.wcaro.presenter.FamilyProfilePresenter;
import org.smartregister.family.activity.BaseFamilyProfileActivity;
import org.smartregister.family.adapter.ViewPagerAdapter;
import org.smartregister.family.fragment.BaseFamilyProfileActivityFragment;
import org.smartregister.family.fragment.BaseFamilyProfileDueFragment;
import org.smartregister.family.fragment.BaseFamilyProfileMemberFragment;
import org.smartregister.family.util.Constants;
import org.smartregister.util.PermissionUtils;

import de.hdodenhof.circleimageview.CircleImageView;

public class FamilyProfileActivity extends BaseFamilyProfileActivity implements FamilyProfileExtendedContract.View {

    private static final String TAG = FamilyProfileActivity.class.getCanonicalName();
    private String familyBaseEntityId;
    private boolean isFromFamilyServiceDue = false;

    @Override
    protected void initializePresenter() {
        familyBaseEntityId = getIntent().getStringExtra(Constants.INTENT_KEY.BASE_ENTITY_ID);
        isFromFamilyServiceDue = getIntent().getBooleanExtra(org.smartgresiter.wcaro.util.Constants.INTENT_KEY.SERVICE_DUE, false);
        presenter = new FamilyProfilePresenter(this, new FamilyProfileModel(), familyBaseEntityId);
    }

    @Override
    protected void setupViews() {
        super.setupViews();

        // Update profile border
        CircleImageView profileView = findViewById(R.id.imageview_profile);
        profileView.setBorderWidth(2);

        // add floating menu
        FamilyFloatingMenu familyFloatingMenu = new FamilyFloatingMenu(this);
        LinearLayout.LayoutParams linearLayoutParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
        familyFloatingMenu.setGravity(Gravity.BOTTOM | Gravity.RIGHT);
        addContentView(familyFloatingMenu, linearLayoutParams);
        familyFloatingMenu.setClickListener(new FloatingMenuListener(this));
    }

    @Override
    protected ViewPager setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());

        BaseFamilyProfileMemberFragment profileMemberFragment = FamilyProfileMemberFragment.newInstance(this.getIntent().getExtras());
        BaseFamilyProfileDueFragment profileDueFragment = FamilyProfileDueFragment.newInstance(this.getIntent().getExtras());
        BaseFamilyProfileActivityFragment profileActivityFragment = FamilyProfileActivityFragment.newInstance(this.getIntent().getExtras());

        adapter.addFragment(profileMemberFragment, this.getString(org.smartregister.family.R.string.member).toUpperCase());
        adapter.addFragment(profileDueFragment, this.getString(org.smartregister.family.R.string.due).toUpperCase());
        adapter.addFragment(profileActivityFragment, this.getString(org.smartregister.family.R.string.activity).toUpperCase());

        viewPager.setAdapter(adapter);
        if (isFromFamilyServiceDue) {
            // int position=adapter.getItemPosition(profileDueFragment);
            viewPager.setCurrentItem(1);
            adapter.notifyDataSetChanged();
        }

        return viewPager;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuItem addMember = menu.findItem(R.id.add_member);
        if (addMember != null) {
            addMember.setVisible(false);
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PermissionUtils.PHONE_STATE_PERMISSION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                Boolean granted = (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED);
                if (granted) {
                    PermissionEvent event = new PermissionEvent(requestCode, granted);
                    EventBus.getDefault().post(event);
                } else {
                    Toast.makeText(this, getText(R.string.allow_calls_denied), Toast.LENGTH_LONG).show();
                }
            }
            break;
        }
    }

    public String getFamilyBaseEntityId() {
        return familyBaseEntityId;
    }

    public void startFormForEdit() {
        if (familyBaseEntityId != null) {
            ((FamilyProfilePresenter) presenter).fetchProfileData();
        }
    }

    // Child Form

    @Override
    public void startChildForm(String formName, String entityId, String metadata, String currentLocationId) throws Exception {
        ((FamilyProfilePresenter) presenter).startChildForm(formName, entityId, metadata, currentLocationId);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == org.smartregister.family.util.JsonFormUtils.REQUEST_CODE_GET_JSON && resultCode == Activity.RESULT_OK) {
            try {
                String jsonString = data.getStringExtra(org.smartregister.family.util.Constants.JSON_FORM_EXTRA.JSON);
                Log.d("JSONResult", jsonString);

                JSONObject form = new JSONObject(jsonString);
                if (form.getString(org.smartregister.family.util.JsonFormUtils.ENCOUNTER_TYPE).equals(org.smartregister.family.util.Utils.metadata().familyRegister.registerEventType)
                        || form.getString(org.smartregister.family.util.JsonFormUtils.ENCOUNTER_TYPE).equals(org.smartgresiter.wcaro.util.Constants.EventType.CHILD_REGISTRATION)
                        ) {
                    ((FamilyProfilePresenter) presenter).saveChildForm(jsonString, false);
                }
            } catch (Exception e) {
                Log.e(TAG, Log.getStackTraceString(e));
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }

    }
}
