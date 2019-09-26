package org.smartregister.chw.hf.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.json.JSONObject;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.dao.PNCDao;
import org.smartregister.chw.core.model.ChildModel;
import org.smartregister.chw.core.utils.ChildDBConstants;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.hf.BuildConfig;
import org.smartregister.chw.hf.HealthFacilityApplication;
import org.smartregister.chw.hf.R;
import org.smartregister.chw.hf.listener.ReferralsTaskViewClickListener;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.clientandeventmodel.Obs;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.Task;
import org.smartregister.family.FamilyLibrary;
import org.smartregister.family.util.DBConstants;
import org.smartregister.family.util.Utils;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.BaseRepository;
import org.smartregister.sync.helper.ECSyncHelper;
import org.smartregister.util.JsonFormUtils;
import org.smartregister.view.activity.SecuredActivity;
import org.smartregister.view.customcontrols.CustomFontTextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

public class ReferralTaskViewActivity extends SecuredActivity {
    protected AppBarLayout appBarLayout;
    protected String startingActivity;
    private CommonPersonObjectClient personObjectClient;
    private Task task;
    private CustomFontTextView clientName;
    private String clientAge;
    private CustomFontTextView careGiverName;
    private CustomFontTextView childName;
    private CustomFontTextView careGiverPhone;
    private CustomFontTextView clientReferralProblem;
    private CustomFontTextView referralDate;
    private CustomFontTextView chwDetailsNames;
    private CustomFontTextView womanGa;
    private LinearLayout womanGaLayout;
    private LinearLayout careGiverLayout;
    private LinearLayout childNameLayout;
    private ReferralsTaskViewClickListener referralsTaskViewClickListener = new ReferralsTaskViewClickListener();
    private String name;
    private String baseEntityId;
    private MemberObject memberObject;
    private String familyHeadName;
    private String familyHeadPhoneNumber;

    public static void startReferralTaskViewActivity(Activity activity, CommonPersonObjectClient personObjectClient, Task task, String startingActivity) {
        Intent intent = new Intent(activity, ReferralTaskViewActivity.class);
        intent.putExtra(CoreConstants.INTENT_KEY.USERS_TASKS, task);
        intent.putExtra(CoreConstants.INTENT_KEY.CHILD_COMMON_PERSON, personObjectClient);
        intent.putExtra(CoreConstants.INTENT_KEY.STARTING_ACTIVITY, startingActivity);
        activity.startActivity(intent);
    }

    public static void startReferralTaskViewActivity(Activity activity, MemberObject memberObject, String familyHeadName, String familiyHeadPhoneNumber, CommonPersonObjectClient personObjectClient, Task task, String startingActivity) {
        Intent intent = new Intent(activity, ReferralTaskViewActivity.class);
        intent.putExtra(CoreConstants.INTENT_KEY.USERS_TASKS, task);
        intent.putExtra(CoreConstants.INTENT_KEY.MEMBER_OBJECT, memberObject);
        intent.putExtra(CoreConstants.INTENT_KEY.FAMILY_HEAD_NAME, familyHeadName);
        intent.putExtra(CoreConstants.INTENT_KEY.FAMILY_HEAD_PHONE_NUMBER, familiyHeadPhoneNumber);
        intent.putExtra(CoreConstants.INTENT_KEY.CHILD_COMMON_PERSON, personObjectClient);
        intent.putExtra(CoreConstants.INTENT_KEY.STARTING_ACTIVITY, startingActivity);
        activity.startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    protected void onCreation() {
        setContentView(R.layout.referrals_tasks_view_layout);
        if (getIntent().getExtras() != null) {
            extractPersonObjectClient();
            extraClientTask();
            extraAncDetails();
            setStartingActivity((String) getIntent().getSerializableExtra(CoreConstants.INTENT_KEY.STARTING_ACTIVITY));
        }

        referralsTaskViewClickListener.setReferralTaskViewActivity(this);
        if (getTask() != null) {
            referralsTaskViewClickListener.setTaskFocus(getTask().getFocus());
        }
        referralsTaskViewClickListener.setCommonPersonObjectClient(getPersonObjectClient());
        if (CoreConstants.TASKS_FOCUS.ANC_DANGER_SIGNS.equals(getTask().getFocus()) || (CoreConstants.TASKS_FOCUS.PNC_DANGER_SIGNS.equals(getTask().getFocus()))) {
            referralsTaskViewClickListener.setMemberObject(getMemberObject());
            referralsTaskViewClickListener.setFamilyHeadName(getFamilyHeadName());
            referralsTaskViewClickListener.setFamilyHeadPhoneNumber(getFamilyHeadPhoneNumber());
        }
        inflateToolbar();
        setUpViews();

    }

    @Override
    protected void onResumption() {
        //// TODO: 15/08/19
    }

    private void extractPersonObjectClient() {
        setPersonObjectClient((CommonPersonObjectClient) getIntent().getSerializableExtra(CoreConstants.INTENT_KEY.CHILD_COMMON_PERSON));
        if (getPersonObjectClient() != null) {
            name = Utils.getValue(getPersonObjectClient().getColumnmaps(), DBConstants.KEY.FIRST_NAME, true) + " " + Utils.getValue(getPersonObjectClient().getColumnmaps(), DBConstants.KEY.MIDDLE_NAME, true) + " " + Utils.getValue(getPersonObjectClient().getColumnmaps(), DBConstants.KEY.LAST_NAME, true);
            setBaseEntityId(Utils.getValue(getPersonObjectClient().getColumnmaps(), DBConstants.KEY.BASE_ENTITY_ID, false));
        }

        if (getPersonObjectClient() == null) {
            Timber.e("ReferralTaskViewActivity --> The person object is null");
            finish();
        }
    }

    private void extraClientTask() {
        setTask((Task) getIntent().getSerializableExtra(CoreConstants.INTENT_KEY.USERS_TASKS));

        if (getTask() == null) {
            Timber.e("ReferralTaskViewActivity --> The task object is null");
            finish();
        }
    }

    private void extraAncDetails() {
        if (CoreConstants.TASKS_FOCUS.ANC_DANGER_SIGNS.equals(getTask().getFocus()) || CoreConstants.TASKS_FOCUS.PNC_DANGER_SIGNS.equals(getTask().getFocus())) {
            setMemberObject((MemberObject) getIntent().getSerializableExtra(CoreConstants.INTENT_KEY.MEMBER_OBJECT));
            setFamilyHeadName((String) getIntent().getSerializableExtra(CoreConstants.INTENT_KEY.FAMILY_HEAD_NAME));
            setFamilyHeadPhoneNumber((String) getIntent().getSerializableExtra(CoreConstants.INTENT_KEY.FAMILY_HEAD_PHONE_NUMBER));
        }
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public CommonPersonObjectClient getPersonObjectClient() {
        return personObjectClient;
    }

    public void setPersonObjectClient(CommonPersonObjectClient personObjectClient) {
        this.personObjectClient = personObjectClient;
    }

    public MemberObject getMemberObject() {
        return memberObject;
    }

    public void setMemberObject(MemberObject memberObject) {
        this.memberObject = memberObject;
    }

    public String getFamilyHeadName() {
        return familyHeadName;
    }

    public void setFamilyHeadName(String familyHeadName) {
        this.familyHeadName = familyHeadName;
    }

    public String getFamilyHeadPhoneNumber() {
        return familyHeadPhoneNumber;
    }

    public void setFamilyHeadPhoneNumber(String familyHeadPhoneNumber) {
        this.familyHeadPhoneNumber = familyHeadPhoneNumber;
    }

    private void inflateToolbar() {
        Toolbar toolbar = findViewById(R.id.back_referrals_toolbar);
        CustomFontTextView toolBarTextView = toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            final Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp);
            upArrow.setColorFilter(getResources().getColor(R.color.text_blue), PorterDuff.Mode.SRC_ATOP);
            actionBar.setHomeAsUpIndicator(upArrow);
            actionBar.setElevation(0);
        }

        toolbar.setNavigationOnClickListener(v -> finish());

        if (getStartingActivity().equals(CoreConstants.REGISTERED_ACTIVITIES.REFERRALS_REGISTER_ACTIVITY)) {
            toolBarTextView.setText(R.string.back_to_referrals);
        } else {
            if (TextUtils.isEmpty(name)) {
                toolBarTextView.setText(R.string.back_to_referrals);
            } else {
                toolBarTextView.setText(getString(R.string.return_to, name));
            }
        }
        toolBarTextView.setOnClickListener(v -> finish());
        appBarLayout = findViewById(R.id.app_bar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            appBarLayout.setOutlineProvider(null);
        }

    }

    public void setUpViews() {
        clientName = findViewById(R.id.client_name);
        careGiverName = findViewById(R.id.care_giver_name);
        childName = findViewById(R.id.child_name);
        careGiverPhone = findViewById(R.id.care_giver_phone);
        clientReferralProblem = findViewById(R.id.client_referral_problem);
        chwDetailsNames = findViewById(R.id.chw_details_names);
        referralDate = findViewById(R.id.referral_date);

        womanGaLayout = findViewById(R.id.woman_ga_layout);
        careGiverLayout = findViewById(R.id.care_giver_name_layout);
        childNameLayout = findViewById(R.id.child_name_layout);

        womanGa = findViewById(R.id.woman_ga);
        CustomFontTextView viewProfile = findViewById(R.id.view_profile);

        CustomFontTextView markAskDone = findViewById(R.id.mark_ask_done);
        markAskDone.setOnClickListener(referralsTaskViewClickListener);

        if (getStartingActivity().equals(CoreConstants.REGISTERED_ACTIVITIES.REFERRALS_REGISTER_ACTIVITY)) {
            viewProfile.setOnClickListener(referralsTaskViewClickListener);
        } else {
            viewProfile.setVisibility(View.INVISIBLE);
        }
        getReferralDetails();
    }

    public String getStartingActivity() {
        return startingActivity;
    }

    public void setStartingActivity(String startingActivity) {
        this.startingActivity = startingActivity;
    }

    private void getReferralDetails() {
        if (getPersonObjectClient() != null && getTask() != null) {
            updateProblemDisplay();
            clientAge = (Utils.getTranslatedDate(Utils.getDuration(Utils.getValue(getPersonObjectClient().getColumnmaps(), DBConstants.KEY.DOB, false)), getBaseContext()));
            clientName.setText(getString(R.string.client_name_age_suffix, name, clientAge));
            referralDate.setText(org.smartregister.chw.core.utils.Utils.dd_MMM_yyyy.format(task.getExecutionStartDate().toDate()));

            String parentFirstName = Utils.getValue(getPersonObjectClient().getColumnmaps(), ChildDBConstants.KEY.FAMILY_FIRST_NAME, true);
            String parentLastName = Utils.getValue(getPersonObjectClient().getColumnmaps(), ChildDBConstants.KEY.FAMILY_LAST_NAME, true);
            String parentMiddleName = Utils.getValue(getPersonObjectClient().getColumnmaps(), ChildDBConstants.KEY.FAMILY_MIDDLE_NAME, true);
            String parentName = getString(R.string.care_giver_prefix, org.smartregister.util.Utils.getName(parentFirstName, parentMiddleName + " " + parentLastName));

            //For PNC get children belonging to the woman
            String childrenForPncWoman = getChildrenForPncWoman(getPersonObjectClient().entityId());
            if (getTask().getFocus().equalsIgnoreCase(CoreConstants.TASKS_FOCUS.PNC_DANGER_SIGNS) &&
                    StringUtils.isNoneEmpty(childrenForPncWoman)) {
                childName.setText(childrenForPncWoman);
                childNameLayout.setVisibility(View.VISIBLE);
            }

            //Hide Care giver for ANC referral
            if (getTask().getFocus().equalsIgnoreCase(CoreConstants.TASKS_FOCUS.ANC_DANGER_SIGNS) ||
                    getTask().getFocus().equalsIgnoreCase(CoreConstants.TASKS_FOCUS.PNC_DANGER_SIGNS)) {
                careGiverLayout.setVisibility(View.GONE);
            }

            careGiverName.setText(parentName);
            careGiverPhone.setText(getFamilyMemberContacts().isEmpty() || getFamilyMemberContacts() == null ? getString(R.string.phone_not_provided) : getFamilyMemberContacts());

            chwDetailsNames.setText(getTask().getRequester());

            addGaDisplay();
        }
    }

    private String getChildrenForPncWoman(String baseEntityId) {
        List<ChildModel> childModels = PNCDao.childrenForPncWoman(baseEntityId);
        StringBuilder stringBuilder = new StringBuilder();
        for (ChildModel childModel : childModels) {
            String childAge = (Utils.getTranslatedDate(Utils.getDuration(childModel.getDateOfBirth()), getBaseContext()));
            stringBuilder.append(childModel.getChildFullName())
                    .append(", ")
                    .append(childAge)
                    .append("; ");
        }

        String children = stringBuilder.toString().replaceAll("; $", "").trim();

        return childModels.size() > 0 && childModels.size() == 1 ?
                getString(R.string.child_prefix, children) : getString(R.string.children_prefix, children);
    }

    private void updateProblemDisplay() {
        if (CoreConstants.TASKS_FOCUS.ANC_DANGER_SIGNS.equals(getTask().getFocus())) {
            clientReferralProblem.setText(getString(R.string.anc_danger_sign_prefix, getTask().getDescription()));
        } else {
            clientReferralProblem.setText(getTask().getDescription());
        }
    }

    private String getFamilyMemberContacts() {
        String phoneNumber = "";
        String familyPhoneNumber = Utils.getValue(getPersonObjectClient().getColumnmaps(), ChildDBConstants.KEY.FAMILY_MEMBER_PHONENUMBER, true);
        String familyPhoneNumberOther = Utils.getValue(getPersonObjectClient().getColumnmaps(), ChildDBConstants.KEY.FAMILY_MEMBER_PHONENUMBER_OTHER, true);
        if (StringUtils.isNoneEmpty(familyPhoneNumber)) {
            phoneNumber = familyPhoneNumber;
        } else if (StringUtils.isEmpty(familyPhoneNumber) && StringUtils.isNoneEmpty(familyPhoneNumberOther)) {
            phoneNumber = familyPhoneNumberOther;
        } else if (StringUtils.isNoneEmpty(familyPhoneNumber) && StringUtils.isNoneEmpty(familyPhoneNumberOther)) {
            phoneNumber = familyPhoneNumber + ", " + familyPhoneNumberOther;
        } else if (StringUtils.isNoneEmpty(getFamilyHeadPhoneNumber())) {
            phoneNumber = getFamilyHeadPhoneNumber();
        }

        return phoneNumber;
    }

    private void addGaDisplay() {
        if (CoreConstants.TASKS_FOCUS.ANC_DANGER_SIGNS.equals(getTask().getFocus())) {
            womanGaLayout.setVisibility(View.VISIBLE);

            String gaWeeks = getMemberObject().getGestationAge() + " " + getString(R.string.weeks);
            womanGa.setText(gaWeeks);
        }
    }

    public void closeReferral() {
        closeReferralDialog();
    }

    private void closeReferralDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.mark_as_done_title));
        builder.setMessage(getString(R.string.mark_as_done_message));
        builder.setCancelable(true);

        builder.setPositiveButton(this.getString(R.string.mark_done), (dialog, id) -> {
            try {
                saveCloseReferralEvent();
                completeTask();
                finish();
            } catch (Exception e) {
                Timber.e(e, "ReferralTaskViewActivity --> closeReferralDialog");
            }
        });
        builder.setNegativeButton(this.getString(R.string.cancel), ((dialog, id) -> dialog.cancel()));

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void saveCloseReferralEvent() {
        try {
            AllSharedPreferences sharedPreferences = Utils.getAllSharedPreferences();
            ECSyncHelper syncHelper = FamilyLibrary.getInstance().getEcSyncHelper();
            Event baseEvent = (Event) new Event()
                    .withBaseEntityId(getBaseEntityId())
                    .withEventDate(new Date())
                    .withEventType(CoreConstants.EventType.CLOSE_REFERRAL)
                    .withFormSubmissionId(JsonFormUtils.generateRandomUUIDString())
                    .withEntityType(CoreConstants.TABLE_NAME.CLOSE_REFERRAL)
                    .withProviderId(sharedPreferences.fetchRegisteredANM())
                    .withLocationId(sharedPreferences.fetchDefaultLocalityId(sharedPreferences.fetchRegisteredANM()))
                    .withTeamId(sharedPreferences.fetchDefaultTeamId(sharedPreferences.fetchRegisteredANM()))
                    .withTeam(sharedPreferences.fetchDefaultTeam(sharedPreferences.fetchRegisteredANM()))
                    .withClientDatabaseVersion(BuildConfig.DATABASE_VERSION)
                    .withClientApplicationVersion(BuildConfig.VERSION_CODE)
                    .withDateCreated(new Date());

            baseEvent.addObs((new Obs()).withFormSubmissionField(CoreConstants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.REFERRAL_TASK).withValue(getTask().getIdentifier())
                    .withFieldCode(CoreConstants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.REFERRAL_TASK).withFieldType("formsubmissionField").withFieldDataType("text").withParentCode("").withHumanReadableValues(new ArrayList<>()));
            baseEvent.addObs((new Obs()).withFormSubmissionField(CoreConstants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.REFERRAL_TASK_PREVIOUS_STATUS).withValue(getTask().getStatus())
                    .withFieldCode(CoreConstants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.REFERRAL_TASK_PREVIOUS_STATUS).withFieldType("formsubmissionField").withFieldDataType("text").withParentCode("").withHumanReadableValues(new ArrayList<>()));
            baseEvent.addObs((new Obs()).withFormSubmissionField(CoreConstants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.REFERRAL_TASK_PREVIOUS_BUSINESS_STATUS).withValue(getTask().getBusinessStatus())
                    .withFieldCode(CoreConstants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.REFERRAL_TASK_PREVIOUS_BUSINESS_STATUS).withFieldType("formsubmissionField").withFieldDataType("text").withParentCode("").withHumanReadableValues(new ArrayList<>()));

            org.smartregister.chw.hf.utils.JsonFormUtils.tagSyncMetadata(Utils.context().allSharedPreferences(), baseEvent);// tag docs

            JSONObject eventJson = new JSONObject(JsonFormUtils.gson.toJson(baseEvent));
            syncHelper.addEvent(getBaseEntityId(), eventJson);
            long lastSyncTimeStamp = HealthFacilityApplication.getInstance().getContext().allSharedPreferences().fetchLastUpdatedAtDate(0);
            Date lastSyncDate = new Date(lastSyncTimeStamp);
            HealthFacilityApplication.getClientProcessor(HealthFacilityApplication.getInstance().getContext().applicationContext()).processClient(syncHelper.getEvents(lastSyncDate, BaseRepository.TYPE_Unprocessed));
            HealthFacilityApplication.getInstance().getContext().allSharedPreferences().saveLastUpdatedAtDate(lastSyncDate.getTime());
        } catch (Exception e) {
            Timber.e(e, "ReferralTaskViewActivity --> saveCloseReferralEvent");
        }

    }

    private void completeTask() {
        Task currentTask = getTask();
        DateTime now = new DateTime();
        currentTask.setExecutionEndDate(now);
        currentTask.setLastModified(now);
        currentTask.setForEntity(getBaseEntityId());
        currentTask.setStatus(Task.TaskStatus.COMPLETED);
        currentTask.setBusinessStatus(CoreConstants.BUSINESS_STATUS.COMPLETE);
        currentTask.setSyncStatus(BaseRepository.TYPE_Unsynced);
        CoreChwApplication.getInstance().getTaskRepository().addOrUpdate(currentTask);
    }

    public String getBaseEntityId() {
        return baseEntityId;
    }

    public void setBaseEntityId(String baseEntityId) {
        this.baseEntityId = baseEntityId;
    }


}