package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import org.smartregister.chw.R;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.core.activity.CoreChildProfileActivity;
import org.smartregister.chw.core.activity.CoreUpcomingServicesActivity;
import org.smartregister.chw.core.listener.OnClickFloatingMenu;
import org.smartregister.chw.core.model.CoreChildProfileModel;
import org.smartregister.chw.core.presenter.CoreChildProfilePresenter;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.custom_view.FamilyMemberFloatingMenu;
import org.smartregister.chw.dao.MalariaDao;
import org.smartregister.chw.model.ReferralTypeModel;
import org.smartregister.chw.presenter.ChildProfilePresenter;
import org.smartregister.chw.schedulers.ChwScheduleTaskExecutor;
import org.smartregister.family.util.Constants;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.smartregister.chw.anc.util.Constants.ANC_MEMBER_OBJECTS.MEMBER_PROFILE_OBJECT;

public class ChildProfileActivity extends CoreChildProfileActivity {
    public FamilyMemberFloatingMenu familyFloatingMenu;
    private Flavor flavor = new ChildProfileActivityFlv();
    private List<ReferralTypeModel> referralTypeModels = new ArrayList<>();

    public List<ReferralTypeModel> getReferralTypeModels() {
        return referralTypeModels;
    }


    @Override
    protected void onCreation() {
        super.onCreation();
        initializePresenter();
        onClickFloatingMenu = flavor.getOnClickFloatingMenu(this, (ChildProfilePresenter) presenter);
        setupViews();
        setUpToolbar();
        registerReceiver(mDateTimeChangedReceiver, sIntentFilter);
        if (((ChwApplication) ChwApplication.getInstance()).hasReferrals()) {
            addChildReferralTypes();
        }
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
        if (i == R.id.textview_visit_not) {
            presenter().updateVisitNotDone(System.currentTimeMillis());
            imageViewCrossChild.setVisibility(View.VISIBLE);
            imageViewCrossChild.setImageResource(R.drawable.activityrow_notvisited);
        } else if (i == R.id.textview_undo) {
            presenter().updateVisitNotDone(0);
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
        menu.findItem(R.id.action_sick_child_form).setVisible(ChwApplication.getApplicationFlavor().hasChildSickForm());
        menu.findItem(R.id.action_sick_child_follow_up).setVisible(false);
        menu.findItem(R.id.action_malaria_diagnosis).setVisible(false);
        menu.findItem(R.id.action_malaria_followup_visit).setVisible(false);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CoreConstants.ProfileActivityResults.CHANGE_COMPLETED && resultCode == Activity.RESULT_OK) {
            Intent intent = new Intent(ChildProfileActivity.this, ChildProfileActivity.class);
            intent.putExtras(getIntent().getExtras());
            startActivity(intent);
            finish();
        }
        ChwScheduleTaskExecutor.getInstance().execute(memberObject.getBaseEntityId(), CoreConstants.EventType.CHILD_HOME_VISIT, new Date());
    }

    private void openMedicalHistoryScreen() {
        ChildMedicalHistoryActivity.startMe(this, memberObject);
    }

    private void openUpcomingServicePage() {
        MemberObject memberObject = new MemberObject(((ChildProfilePresenter) presenter()).getChildClient());
        CoreUpcomingServicesActivity.startMe(this, memberObject);
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

    private void addChildReferralTypes() {
        referralTypeModels.add(new ReferralTypeModel(getString(R.string.sick_child),
                org.smartregister.chw.util.Constants.JSON_FORM.getChildReferralForm()));
        if (MalariaDao.isRegisteredForMalaria(childBaseEntityId)) {
            referralTypeModels.add(new ReferralTypeModel(getString(R.string.client_malaria_follow_up), null));
        }
    }

    @Override
    protected View.OnClickListener getSickListener() {
        return v -> {
            Intent intent = new Intent(getApplication(), SickFormMedicalHistory.class);
            intent.putExtra(MEMBER_PROFILE_OBJECT, memberObject);
            startActivity(intent);
        };
    }

    public interface Flavor {
        OnClickFloatingMenu getOnClickFloatingMenu(Activity activity, ChildProfilePresenter presenter);
    }
}
