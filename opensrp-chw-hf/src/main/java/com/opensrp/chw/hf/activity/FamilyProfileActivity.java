package com.opensrp.chw.hf.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Pair;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.opensrp.chw.core.activity.CoreFamilyProfileActivity;
import com.opensrp.chw.core.contract.FamilyProfileExtendedContract;
import com.opensrp.chw.core.custom_views.FamilyFloatingMenu;
import com.opensrp.chw.core.event.PermissionEvent;
import com.opensrp.chw.core.listener.FloatingMenuListener;
import com.opensrp.chw.hf.model.FamilyProfileModel;
import com.opensrp.chw.hf.presenter.FamilyProfilePresenter;
import com.opensrp.hf.R;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;
import org.smartregister.domain.FetchStatus;
import org.smartregister.family.adapter.ViewPagerAdapter;
import org.smartregister.family.fragment.BaseFamilyProfileMemberFragment;
import org.smartregister.family.util.Constants;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.family.util.Utils;
import org.smartregister.util.PermissionUtils;
import org.smartregister.view.fragment.BaseRegisterFragment;

import de.hdodenhof.circleimageview.CircleImageView;
import timber.log.Timber;

import static com.opensrp.chw.core.utils.Constants.EventType;
import static com.opensrp.chw.core.utils.Constants.INTENT_KEY;
import static com.opensrp.chw.core.utils.Constants.ProfileActivityResults;

public class FamilyProfileActivity extends CoreFamilyProfileActivity {

    private static final String TAG = FamilyProfileActivity.class.getCanonicalName();
    private String familyBaseEntityId;
    private String familyHead;
    private String primaryCaregiver;
    private String familyName;

    private FamilyFloatingMenu familyFloatingMenu;

    @Override
    protected void initializePresenter() {
        familyBaseEntityId = getIntent().getStringExtra(Constants.INTENT_KEY.FAMILY_BASE_ENTITY_ID);
        familyHead = getIntent().getStringExtra(Constants.INTENT_KEY.FAMILY_HEAD);
        primaryCaregiver = getIntent().getStringExtra(Constants.INTENT_KEY.PRIMARY_CAREGIVER);
        familyName = getIntent().getStringExtra(Constants.INTENT_KEY.FAMILY_NAME);
        presenter = new FamilyProfilePresenter(this, new FamilyProfileModel(familyName), familyBaseEntityId, familyHead, primaryCaregiver, familyName);
    }

    @Override
    protected ViewPager setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());

     /*   BaseFamilyProfileMemberFragment profileMemberFragment = FamilyProfileMemberFragment.newInstance(this.getIntent().getExtras());


        adapter.addFragment(profileMemberFragment, this.getString(org.smartregister.family.R.string.member).toUpperCase());
*/
        viewPager.setAdapter(adapter);

        if (getIntent().getBooleanExtra(INTENT_KEY.SERVICE_DUE, false) || getIntent().getBooleanExtra(Constants.INTENT_KEY.GO_TO_DUE_PAGE, false)) {
            viewPager.setCurrentItem(1);
        }

        return viewPager;
    }

    @Override
    protected void setupViews() {
        super.setupViews();

        // Update profile border
        CircleImageView profileView = findViewById(R.id.imageview_profile);
        profileView.setBorderWidth(2);

        // add floating menu
        familyFloatingMenu = new FamilyFloatingMenu(this);
        LinearLayout.LayoutParams linearLayoutParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
        familyFloatingMenu.setGravity(Gravity.BOTTOM | Gravity.RIGHT);
        addContentView(familyFloatingMenu, linearLayoutParams);
        familyFloatingMenu.setClickListener(FloatingMenuListener.getInstance(this, presenter().familyBaseEntityId()));
    }

    @Override
    protected void onResumption() {
        super.onResumption();
        FloatingMenuListener.getInstance(this, presenter().familyBaseEntityId());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuItem addMember = menu.findItem(R.id.add_member);
        if (addMember != null) {
            addMember.setVisible(false);
        }

        getMenuInflater().inflate(R.menu.profile_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_family_details:

                startFormForEdit();

                break;
           /* case R.id.action_remove_member:

                Intent frm_intent = new Intent(this, FamilyRemoveMemberActivity.class);
                frm_intent.putExtra(Constants.INTENT_KEY.FAMILY_BASE_ENTITY_ID, getFamilyBaseEntityId());
                frm_intent.putExtra(Constants.INTENT_KEY.FAMILY_HEAD, familyHead);
                frm_intent.putExtra(Constants.INTENT_KEY.PRIMARY_CAREGIVER, primaryCaregiver);
                startActivityForResult(frm_intent, ProfileActivityResults.CHANGE_COMPLETED);

                break;
            case R.id.action_change_head:

                Intent fh_intent = new Intent(this, FamilyProfileMenuActivity.class);
                fh_intent.putExtra(Constants.INTENT_KEY.BASE_ENTITY_ID, getFamilyBaseEntityId());
                fh_intent.putExtra(FamilyProfileMenuActivity.MENU, MenuType.ChangeHead);
                startActivityForResult(fh_intent, ProfileActivityResults.CHANGE_COMPLETED);

                break;
            case R.id.action_change_care_giver:


                Intent pc_intent = new Intent(this, FamilyProfileMenuActivity.class);
                pc_intent.putExtra(Constants.INTENT_KEY.BASE_ENTITY_ID, getFamilyBaseEntityId());
                pc_intent.putExtra(FamilyProfileMenuActivity.MENU, MenuType.ChangePrimaryCare);
                startActivityForResult(pc_intent, ProfileActivityResults.CHANGE_COMPLETED);

                break;*/
            default:
                super.onOptionsItemSelected(item);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case JsonFormUtils.REQUEST_CODE_GET_JSON:
                    try {
                        String jsonString = data.getStringExtra(Constants.JSON_FORM_EXTRA.JSON);
                        Timber.d("JSONResult : %s", jsonString);

                        JSONObject form = new JSONObject(jsonString);
                        String encounter_type = form.getString(JsonFormUtils.ENCOUNTER_TYPE);
                        // process child registration
                        if (form.getString(JsonFormUtils.ENCOUNTER_TYPE).equals(Utils.metadata().familyRegister.updateEventType)) {

                            presenter().updateFamilyRegister(jsonString);
                            presenter().verifyHasPhone();

                        } else if (encounter_type.equals(EventType.CHILD_REGISTRATION)) {

                            presenter().saveChildForm(jsonString, false);

                        } else if (encounter_type.equals(Utils.metadata().familyMemberRegister.registerEventType)) {

                            String careGiver = presenter().saveChwFamilyMember(jsonString);
                            if (presenter().updatePrimaryCareGiver(getApplicationContext(), jsonString, familyBaseEntityId, careGiver)) {
                                setPrimaryCaregiver(careGiver);
                                refreshPresenter();
                                refreshMemberFragment(careGiver, null);
                            }

                            presenter().verifyHasPhone();
                        }
                    } catch (Exception e) {
                        Timber.e(e);
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case ProfileActivityResults.CHANGE_COMPLETED:
                    try {

                        String careGiverID = data.getStringExtra(Constants.INTENT_KEY.PRIMARY_CAREGIVER);
                        String familyHeadID = data.getStringExtra(Constants.INTENT_KEY.FAMILY_HEAD);

                        setPrimaryCaregiver(careGiverID);
                        setFamilyHead(familyHeadID);

                        refreshMemberFragment(careGiverID, familyHeadID);
                        presenter().verifyHasPhone();

                    } catch (Exception e) {
                        Timber.e(e);
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    }

    @Override
    public void refreshMemberList(FetchStatus fetchStatus) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            for (int i = 0; i < adapter.getCount(); i++) {
                refreshList(adapter.getItem(i));
            }
        } else {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                public void run() {
                    for (int i = 0; i < adapter.getCount(); i++) {
                        refreshList(adapter.getItem(i));
                    }
                }
            });
        }
    }

    // Child Form

    @Override
    public FamilyProfileExtendedContract.Presenter presenter() {
        return (FamilyProfilePresenter) presenter;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PermissionUtils.PHONE_STATE_PERMISSION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                boolean granted = (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED);
                if (granted) {
                    PermissionEvent event = new PermissionEvent(requestCode, granted);
                    EventBus.getDefault().post(event);
                } else {
                    Toast.makeText(this, getText(R.string.allow_calls_denied), Toast.LENGTH_LONG).show();
                }
            }
            break;
            default:
                break;
        }
    }

    public String getFamilyBaseEntityId() {
        return familyBaseEntityId;
    }

    public void startFormForEdit() {
        if (familyBaseEntityId != null) {
            presenter().fetchProfileData();
        }
    }

    @Override
    public void startChildForm(String formName, String entityId, String metadata, String currentLocationId) throws Exception {
        presenter().startChildForm(formName, entityId, metadata, currentLocationId);
    }

    @Override
    public void updateHasPhone(boolean hasPhone) {
        if (familyFloatingMenu != null) {
            familyFloatingMenu.reDraw(hasPhone);
        }
    }

    private void refreshPresenter() {
        presenter = new FamilyProfilePresenter(this, new FamilyProfileModel(familyName), familyBaseEntityId, familyHead, primaryCaregiver, familyName);
    }

    private void refreshMemberFragment(String careGiverID, String familyHeadID) {
        BaseFamilyProfileMemberFragment memberFragment = this.getProfileMemberFragment();
        if (memberFragment != null) {
            if (StringUtils.isNotBlank(careGiverID)) {
                memberFragment.setPrimaryCaregiver(careGiverID);
            }
            if (StringUtils.isNotBlank(familyHeadID)) {
                memberFragment.setFamilyHead(familyHeadID);
            }
            refreshMemberList(FetchStatus.fetched);
        }
    }

    public void updateDueCount(final int dueCount) {

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            public void run() {
                adapter.updateCount(Pair.create(1, dueCount));
            }
        });
    }

    private void refreshList(Fragment fragment) {
        if (fragment != null && fragment instanceof BaseRegisterFragment) {
           /* if (fragment instanceof FamilyProfileMemberFragment) {
                FamilyProfileMemberFragment familyProfileMemberFragment = ((FamilyProfileMemberFragment) fragment);
                if (familyProfileMemberFragment.presenter() != null) {
                    familyProfileMemberFragment.refreshListView();
                }
            }*/
        }
    }

    public void setPrimaryCaregiver(String caregiver) {
        if (StringUtils.isNotBlank(caregiver)) {
            this.primaryCaregiver = caregiver;
            getIntent().putExtra(Constants.INTENT_KEY.PRIMARY_CAREGIVER, caregiver);
        }
    }

    public void setFamilyHead(String head) {
        if (StringUtils.isNotBlank(head)) {
            this.familyHead = head;
            getIntent().putExtra(Constants.INTENT_KEY.FAMILY_HEAD, head);
        }
    }

    //    @Override
//    public void startFormActivity(JSONObject jsonForm) {
//        Intent intent = new Intent(this, FamilyWizardFormFragment.class);
//        intent.putExtra("json", jsonForm.toString());
//        Form form = new Form();
//        form.setActionBarBackground(R.color.family_actionbar);
//        form.setWizard(false);
//        intent.putExtra("form", form);
//        this.startActivityForResult(intent, org.smartregister.family.util.JsonFormUtils.REQUEST_CODE_GET_JSON);
//    }

}
