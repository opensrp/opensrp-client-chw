package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Pair;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.BuildConfig;
import org.smartregister.chw.R;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.core.activity.ChromeContainer;
import org.smartregister.chw.core.activity.CoreChildProfileActivity;
import org.smartregister.chw.core.activity.CoreUpcomingServicesActivity;
import org.smartregister.chw.core.adapter.NotificationListAdapter;
import org.smartregister.chw.core.dao.ChildDao;
import org.smartregister.chw.core.listener.OnClickFloatingMenu;
import org.smartregister.chw.core.listener.OnRetrieveNotifications;
import org.smartregister.chw.core.model.CoreChildProfileModel;
import org.smartregister.chw.core.presenter.CoreChildProfilePresenter;
import org.smartregister.chw.core.utils.ChwNotificationUtil;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.CoreConstants.JSON_FORM;
import org.smartregister.chw.custom_view.FamilyMemberFloatingMenu;
import org.smartregister.chw.dao.ChildFHIRBundleDao;
import org.smartregister.chw.model.ReferralTypeModel;
import org.smartregister.chw.presenter.ChildProfilePresenter;
import org.smartregister.chw.schedulers.ChwScheduleTaskExecutor;
import org.smartregister.chw.util.Utils;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.FamilyLibrary;
import org.smartregister.family.util.Constants;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.sync.helper.ECSyncHelper;
import org.smartregister.thinkmd.ThinkMDLibrary;
import org.smartregister.thinkmd.model.FHIRBundleModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

import static org.smartregister.chw.anc.util.Constants.ANC_MEMBER_OBJECTS.MEMBER_PROFILE_OBJECT;
import static org.smartregister.chw.core.utils.CoreConstants.INTENT_KEY.CONTENT_TO_DISPLAY;
import static org.smartregister.chw.core.utils.Utils.updateToolbarTitle;
import static org.smartregister.chw.util.Constants.MALARIA_REFERRAL_FORM;
import static org.smartregister.chw.util.Constants.ThinkMdConstants.CARE_PLAN_DATE;
import static org.smartregister.chw.util.Constants.ThinkMdConstants.HTML_ASSESSMENT;
import static org.smartregister.chw.util.NotificationsUtil.handleNotificationRowClick;
import static org.smartregister.chw.util.NotificationsUtil.handleReceivedNotifications;
import static org.smartregister.opd.utils.OpdConstants.DateFormat.YYYY_MM_DD;
import static org.smartregister.opd.utils.OpdJsonFormUtils.locationId;

public class ChildProfileActivity extends CoreChildProfileActivity implements OnRetrieveNotifications {
    public FamilyMemberFloatingMenu familyFloatingMenu;
    private Flavor flavor = new ChildProfileActivityFlv();
    private List<ReferralTypeModel> referralTypeModels = new ArrayList<>();
    private NotificationListAdapter notificationListAdapter = new NotificationListAdapter();

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
        notificationAndReferralRecyclerView.setAdapter(notificationListAdapter);
        notificationListAdapter.setOnClickListener(this);

        if (getIntent().hasExtra(context().getStringResource(R.string.fhir_bundle))) {
            createCarePlanEvent(getIntent().getStringExtra(context().getStringResource(R.string.fhir_bundle)));
        }
    }

    private void createCarePlanEvent(String encodedBundle) {
        try {
            String thinkMdId = ThinkMDLibrary.getInstance().getThinkMDPatientId(encodedBundle);
            String baseEntityId = ChildDao.getBaseEntityID(getResources().getString(R.string.thinkmd_identifier_type),
                    thinkMdId);
            Event carePlanEvent = ThinkMDLibrary.getInstance().createCarePlanEvent(encodedBundle,
                    Utils.getFormTag(ChwApplication.getInstance().getContext().allSharedPreferences()),
                    baseEntityId);
            JSONObject eventPartialJson = new JSONObject(JsonFormUtils.gson.toJson(carePlanEvent));
            ECSyncHelper.getInstance(getContext()).addEvent(baseEntityId, eventPartialJson);
            showToast(context().getStringResource(R.string.thinkmd_assessment_saved));
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    @Override
    public void setUpToolbar() {
        updateToolbarTitle(this, org.smartregister.chw.core.R.id.toolbar_title, memberObject.getFirstName());

    }

    @Override
    protected void onResume() {
        super.onResume();
        notificationListAdapter.canOpen = true;
        ChwNotificationUtil.retrieveNotifications(ChwApplication.getApplicationFlavor().hasReferrals(),
                childBaseEntityId, this);
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
        handleNotificationRowClick(this, view, notificationListAdapter, childBaseEntityId);
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
                MalariaRegisterActivity.startMalariaRegistrationActivity(ChildProfileActivity.this, presenter().getChildClient().getCaseId(), ((ChildProfilePresenter) presenter()).getFamilyID());
                return true;
            case R.id.action_remove_member:
                IndividualProfileRemoveActivity.startIndividualProfileActivity(ChildProfileActivity.this, presenter().getChildClient(),
                        ((ChildProfilePresenter) presenter()).getFamilyID()
                        , ((ChildProfilePresenter) presenter()).getFamilyHeadID(), ((ChildProfilePresenter) presenter()).getPrimaryCareGiverID(), ChildRegisterActivity.class.getCanonicalName());
                return true;
            case R.id.action_thinkmd_health_assessment:
                ChildFHIRBundleDao fhirBundleDao = new ChildFHIRBundleDao();
                FHIRBundleModel bundle = fhirBundleDao.fetchFHIRDateModel(this, childBaseEntityId);
                bundle.setEndPointPackageName(getClass().getName());
                addThinkmdIdentifier(bundle.getUniqueIdGeneratedForThinkMD());
                ThinkMDLibrary.getInstance().processHealthAssessment(bundle);
                break;
            case R.id.action_thinkmd_careplan:
                Intent intent = new Intent(this, ChromeContainer.class);
                intent.putExtra(CONTENT_TO_DISPLAY, ChildDao.queryColumnWithEntityId(childBaseEntityId,HTML_ASSESSMENT));
                startActivity(intent);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addThinkmdIdentifier(String uniqueIdGeneratedForThinkMD) {
        Event event = new Event()
                .withBaseEntityId(childBaseEntityId)
                .withEventType("Update ThinkMD Id")
                .withEntityType("ec_child")
                .addIdentifier(this.getString(R.string.thinkmd_identifier_type), uniqueIdGeneratedForThinkMD);
        tagSyncMetadata(ChwApplication.getInstance().getContext().allSharedPreferences(), event);

        try {
            JSONObject eventPartialJson = new JSONObject(JsonFormUtils.gson.toJson(event));
            getSyncHelper().addEvent(childBaseEntityId, eventPartialJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void tagSyncMetadata(AllSharedPreferences allSharedPreferences, Event event) {
        String providerId = allSharedPreferences.fetchRegisteredANM();
        event.setProviderId(providerId);
        event.setLocationId(locationId(allSharedPreferences));
        event.setChildLocationId(allSharedPreferences.fetchCurrentLocality());
        event.setTeam(allSharedPreferences.fetchDefaultTeam(providerId));
        event.setTeamId(allSharedPreferences.fetchDefaultTeamId(providerId));
        event.setClientDatabaseVersion(FamilyLibrary.getInstance().getDatabaseVersion());
        event.setClientApplicationVersion(FamilyLibrary.getInstance().getApplicationVersion());
    }

    @NotNull
    public ECSyncHelper getSyncHelper() {
        return ChwApplication.getInstance().getEcSyncHelper();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.findItem(R.id.action_sick_child_form).setVisible(ChwApplication.getApplicationFlavor().hasChildSickForm()
                && flavor.isChildOverTwoMonths(((CoreChildProfilePresenter) presenter).getChildClient())
                && !ChwApplication.getApplicationFlavor().useThinkMd());
        menu.findItem(R.id.action_sick_child_follow_up).setVisible(false);
        menu.findItem(R.id.action_malaria_diagnosis).setVisible(false);
        menu.findItem(R.id.action_malaria_followup_visit).setVisible(false);
        menu.findItem(R.id.action_thinkmd_health_assessment).setVisible(ChwApplication.getApplicationFlavor().useThinkMd()
                && flavor.isChildOverTwoMonths(((CoreChildProfilePresenter) presenter).getChildClient()));
        if(ChwApplication.getApplicationFlavor().useThinkMd()
                && ChildDao.isThinkmdCarePlanExist(childBaseEntityId)){
            menu.findItem(R.id.action_thinkmd_careplan).setVisible(true);
            menu.findItem(R.id.action_thinkmd_careplan).setTitle(
                    String.format(getResources().getString(R.string.thinkmd_careplan), ChildDao.queryColumnWithEntityId(childBaseEntityId,CARE_PLAN_DATE))
            );
        }
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
        MemberObject memberObject = new MemberObject(presenter().getChildClient());
        if (!ChwApplication.getApplicationFlavor().hasSurname()) memberObject.setLastName("");
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
                BuildConfig.USE_UNIFIED_REFERRAL_APPROACH ? JSON_FORM.getChildUnifiedReferralForm()
                        : JSON_FORM.getChildReferralForm(), CoreConstants.TASKS_FOCUS.SICK_CHILD));

        if (memberObject.getAge() >= 5) {
            referralTypeModels.add(new ReferralTypeModel(getString(R.string.suspected_malaria),
                    BuildConfig.USE_UNIFIED_REFERRAL_APPROACH ? CoreConstants.JSON_FORM.getMalariaReferralForm()
                            : MALARIA_REFERRAL_FORM, CoreConstants.TASKS_FOCUS.SUSPECTED_MALARIA));
        }
        if (BuildConfig.USE_UNIFIED_REFERRAL_APPROACH) {
            referralTypeModels.add(new ReferralTypeModel(getString(R.string.child_gbv_referral),
                    JSON_FORM.getChildGbvReferralForm(), CoreConstants.TASKS_FOCUS.SUSPECTED_CHILD_GBV));
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

    @Override
    public void startFormActivity(JSONObject jsonForm) {
        startActivityForResult(flavor.getSickChildFormActivityIntent(jsonForm, this), JsonFormUtils.REQUEST_CODE_GET_JSON);
    }

    @Override
    public void onReceivedNotifications(List<Pair<String, String>> notifications) {
        handleReceivedNotifications(this, notifications, notificationListAdapter);
    }

    @Override
    public void setServiceNameDue(String serviceName, String dueDate) {
        super.setServiceNameDue(serviceName, flavor.getFormattedDateForVisual(dueDate, YYYY_MM_DD));
    }

    @Override
    public void setServiceNameOverDue(String serviceName, String dueDate) {
        super.setServiceNameOverDue(serviceName, flavor.getFormattedDateForVisual(dueDate, YYYY_MM_DD));
    }

    @Override
    public void setServiceNameUpcoming(String serviceName, String dueDate) {
        super.setServiceNameUpcoming(serviceName, flavor.getFormattedDateForVisual(dueDate, YYYY_MM_DD));
    }

    public interface Flavor {
        OnClickFloatingMenu getOnClickFloatingMenu(Activity activity, ChildProfilePresenter presenter);

        boolean isChildOverTwoMonths(CommonPersonObjectClient client);

        Intent getSickChildFormActivityIntent(JSONObject jsonObject, Context context);

        String getFormattedDateForVisual(String dueDate, String inputFormat);

    }
}
