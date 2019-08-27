package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import org.smartregister.chw.R;
import org.smartregister.chw.core.activity.CoreChildProfileActivity;
import org.smartregister.chw.core.activity.CoreUpcomingServicesActivity;
import org.smartregister.chw.core.listener.OnClickFloatingMenu;
import org.smartregister.chw.core.model.CoreChildProfileModel;
import org.smartregister.chw.core.presenter.CoreChildProfilePresenter;
import org.smartregister.chw.custom_view.FamilyMemberFloatingMenu;
import org.smartregister.chw.presenter.ChildProfilePresenter;
import org.smartregister.family.util.Constants;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class ChildProfileActivity extends CoreChildProfileActivity {
    public FamilyMemberFloatingMenu familyFloatingMenu;
    private ChildProfileActivityFlv flavor = new ChildProfileActivityFlv();

    @Override
    protected void onCreation() {
        super.onCreation();
        initializePresenter();
        onClickFloatingMenu = flavor.getOnClickFloatingMenu(this, (ChildProfilePresenter) presenter);
        setupViews();
        setUpToolbar();
        registerReceiver(mDateTimeChangedReceiver, sIntentFilter);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int i = view.getId();
        if (i == R.id.last_visit_row) {
            openMedicalHistoryScreen();
        } else if (i == R.id.most_due_overdue_row) {
            openUpcomingServicePage();
        } else if (i == R.id.textview_record_visit || i == R.id.record_visit_done_bar) {
            openVisitHomeScreen(false);
        } else if (i == R.id.family_has_row) {
            openFamilyDueTab();
        } else if (i == R.id.textview_edit) {
            openVisitHomeScreen(true);
        }
    }

    @Override
    protected void initializePresenter() {
        String familyName = getIntent().getStringExtra(Constants.INTENT_KEY.FAMILY_NAME);
        if (familyName == null) {
            familyName = "";
        }

        presenter = new ChildProfilePresenter(this, new CoreChildProfileModel(familyName), childBaseEntityId);
    }

    @Override
    protected void setupViews() {
        super.setupViews();
        familyFloatingMenu = new FamilyMemberFloatingMenu(this);
        LinearLayout.LayoutParams linearLayoutParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
        familyFloatingMenu.setGravity(Gravity.BOTTOM | Gravity.END);
        addContentView(familyFloatingMenu, linearLayoutParams);

        familyFloatingMenu.setClickListener(onClickFloatingMenu);
        fetchProfileData();

    }

    @Override
    public void updateHasPhone(boolean hasPhone) {
        if (familyFloatingMenu != null) {
            familyFloatingMenu.reDraw(hasPhone);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_malaria_registration:
                MalariaRegisterActivity.startMalariaRegistrationActivity(ChildProfileActivity.this,
                        ((CoreChildProfilePresenter) presenter()).getChildClient().getCaseId());
                return true;
            case R.id.action_remove_member:
                IndividualProfileRemoveActivity.startIndividualProfileActivity(ChildProfileActivity.this, ((ChildProfilePresenter) presenter()).getChildClient(),
                        ((ChildProfilePresenter) presenter()).getFamilyID()
                        , ((ChildProfilePresenter) presenter()).getFamilyHeadID(), ((ChildProfilePresenter) presenter()).getPrimaryCareGiverID(), ChildRegisterActivity.class.getCanonicalName());

                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        if (flavor.showMalariaConfirmationMenu()) {
            menu.findItem(R.id.action_malaria_registration).setVisible(true);
        } else {
            menu.findItem(R.id.action_malaria_registration).setVisible(false);
        }
        if(flavor.showFollowUpVisit()){
            menu.findItem(R.id.action_malaria_followup_visit).setVisible(true);
        }else{
            menu.findItem(R.id.action_malaria_followup_visit).setVisible(false);
        }
        menu.findItem(R.id.action_sick_child_follow_up).setVisible(false);
        menu.findItem(R.id.action_malaria_diagnosis).setVisible(false);
        return true;
    }

    private void openMedicalHistoryScreen() {
        Map<String, Date> vaccine = ((ChildProfilePresenter) presenter()).getVaccineList();
        ChildMedicalHistoryActivity.startMedicalHistoryActivity(this, ((ChildProfilePresenter) presenter()).getChildClient(), patientName, lastVisitDay,
                ((ChildProfilePresenter) presenter()).getDateOfBirth(), new LinkedHashMap<>(vaccine), ChildMedicalHistoryActivity.class);

    }

    private void openUpcomingServicePage() {
        CoreUpcomingServicesActivity.startUpcomingServicesActivity(this, ((ChildProfilePresenter) presenter()).getChildClient());
    }

    private void openVisitHomeScreen(boolean isEditMode) {
        ChildHomeVisitActivity.startMe(this, memberObject, isEditMode, ChildHomeVisitActivity.class);
    }

    private void openFamilyDueTab() {
        Intent intent = new Intent(this, FamilyProfileActivity.class);

        intent.putExtra(Constants.INTENT_KEY.FAMILY_BASE_ENTITY_ID, ((ChildProfilePresenter) presenter()).getFamilyId());
        intent.putExtra(Constants.INTENT_KEY.FAMILY_HEAD, ((ChildProfilePresenter) presenter()).getFamilyHeadID());
        intent.putExtra(Constants.INTENT_KEY.PRIMARY_CAREGIVER, ((ChildProfilePresenter) presenter()).getPrimaryCareGiverID());
        intent.putExtra(Constants.INTENT_KEY.FAMILY_NAME, ((ChildProfilePresenter) presenter()).getFamilyName());

        intent.putExtra(org.smartregister.chw.util.Constants.INTENT_KEY.SERVICE_DUE, true);
        startActivity(intent);
    }

    public interface Flavor {
        OnClickFloatingMenu getOnClickFloatingMenu(Activity activity, ChildProfilePresenter presenter);

        boolean showMalariaConfirmationMenu();

        boolean showFollowUpVisit();
    }
}
