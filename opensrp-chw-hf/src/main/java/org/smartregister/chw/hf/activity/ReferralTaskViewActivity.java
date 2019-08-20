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

import org.apache.commons.lang3.StringUtils;
import org.smartregister.chw.core.utils.ChildDBConstants;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.hf.R;
import org.smartregister.chw.hf.listener.ReferralsTaskViewClickListener;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.Task;
import org.smartregister.family.util.DBConstants;
import org.smartregister.family.util.Utils;
import org.smartregister.view.activity.SecuredActivity;
import org.smartregister.view.customcontrols.CustomFontTextView;

import timber.log.Timber;

public class ReferralTaskViewActivity extends SecuredActivity {
    protected AppBarLayout appBarLayout;
    private CommonPersonObjectClient personObjectClient;
    private Task task;
    private CustomFontTextView clientName;
    private CustomFontTextView clientAge;
    private CustomFontTextView careGiverName;
    private CustomFontTextView careGiverPhone;
    private CustomFontTextView clientReferralProblem;
    private CustomFontTextView referralDate;
    private CustomFontTextView chwDetailsNames;
    private ReferralsTaskViewClickListener referralsTaskViewClickListener = new ReferralsTaskViewClickListener();
    private String name;

    public static void startReferralTaskViewActivity(Activity activity, CommonPersonObjectClient personObjectClient, Task task) {
        Intent intent = new Intent(activity, ReferralTaskViewActivity.class);
        intent.putExtra(CoreConstants.INTENT_KEY.USERS_TASKS, task);
        intent.putExtra(CoreConstants.INTENT_KEY.CHILD_COMMON_PERSON, personObjectClient);
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
        }
        referralsTaskViewClickListener.setReferralTaskViewActivity(this);
        inflateToolbar();
        setUpViews();
        if (getPersonObjectClient() == null) {
            Timber.d("The person object is null");
            finish();
        }
    }

    @Override
    protected void onResumption() {
        //// TODO: 15/08/19
    }

    private void extractPersonObjectClient() {
        setPersonObjectClient((CommonPersonObjectClient) getIntent().getSerializableExtra(CoreConstants.INTENT_KEY.CHILD_COMMON_PERSON));
        if (getPersonObjectClient() != null) {
            name = Utils.getValue(getPersonObjectClient().getColumnmaps(), DBConstants.KEY.FIRST_NAME, true) + " " + Utils.getValue(getPersonObjectClient().getColumnmaps(), DBConstants.KEY.MIDDLE_NAME, true) + " " + Utils.getValue(getPersonObjectClient().getColumnmaps(), DBConstants.KEY.LAST_NAME, true);
        }
    }

    private void extraClientTask() {
        setTask((Task) getIntent().getSerializableExtra(CoreConstants.INTENT_KEY.USERS_TASKS));
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

        if (TextUtils.isEmpty(name)) {
            toolBarTextView.setText(R.string.back_to_referrals);
        } else {
            toolBarTextView.setText(getString(R.string.return_to, name));
        }
        appBarLayout = findViewById(R.id.app_bar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            appBarLayout.setOutlineProvider(null);
        }

    }

    public void setUpViews() {
        clientName = findViewById(R.id.client_name);
        clientAge = findViewById(R.id.client_age);
        careGiverName = findViewById(R.id.care_giver_name);
        careGiverPhone = findViewById(R.id.care_giver_phone);
        clientReferralProblem = findViewById(R.id.client_referral_problem);
        chwDetailsNames = findViewById(R.id.chw_details_names);
        referralDate = findViewById(R.id.referral_date);

        CustomFontTextView markAskDone = findViewById(R.id.mark_ask_done);
        markAskDone.setOnClickListener(referralsTaskViewClickListener);

        CustomFontTextView viewProfile = findViewById(R.id.view_profile);
        viewProfile.setOnClickListener(referralsTaskViewClickListener);

        getReferralDetails();
    }

    public CommonPersonObjectClient getPersonObjectClient() {
        return personObjectClient;
    }

    public void setPersonObjectClient(CommonPersonObjectClient personObjectClient) {
        this.personObjectClient = personObjectClient;
    }

    private void getReferralDetails() {
        if (getPersonObjectClient() != null) {
            clientReferralProblem.setText(getTask().getDescription());
            clientName.setText(getString(R.string.client_name_suffix, name));
            clientAge.setText(Utils.getTranslatedDate(Utils.getDuration(Utils.getValue(getPersonObjectClient().getColumnmaps(), DBConstants.KEY.DOB, false)), getBaseContext()));
            referralDate.setText(org.smartregister.chw.core.utils.Utils.dd_MMM_yyyy.format(task.getExecutionStartDate().toDate()));

            String parentFirstName = Utils.getValue(getPersonObjectClient().getColumnmaps(), ChildDBConstants.KEY.FAMILY_FIRST_NAME, true);
            String parentLastName = Utils.getValue(getPersonObjectClient().getColumnmaps(), ChildDBConstants.KEY.FAMILY_LAST_NAME, true);
            String parentMiddleName = Utils.getValue(getPersonObjectClient().getColumnmaps(), ChildDBConstants.KEY.FAMILY_MIDDLE_NAME, true);

            String parentName = getString(R.string.care_giver_prefix, org.smartregister.util.Utils.getName(parentFirstName, parentMiddleName + " " + parentLastName));
            careGiverName.setText(parentName);
            careGiverPhone.setText(getFamilyMemberContacts());

            chwDetailsNames.setText(getTask().getRequester());
        }
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
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
        }

        return phoneNumber;
    }

    public void closeTask() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.mark_as_done_title));
        builder.setMessage(getString(R.string.mark_as_done_message));
        builder.setCancelable(true);

        builder.setPositiveButton(this.getString(R.string.mark_as_done), (dialog, id) -> dialog.cancel());
        builder.setNegativeButton(this.getString(R.string.dismiss), ((dialog, id) -> dialog.cancel()));

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

  /*  private Event createReferralCloseEvent() {
        Event event = new Event();
        try {

            JSONObject metadata = FormUtils.getInstance(getApplicationContext())
                    .getFormJson(Utils.metadata().familyRegister.formName)
                    .getJSONObject(org.smartregister.family.util.JsonFormUtils.METADATA);

            metadata.put(org.smartregister.family.util.JsonFormUtils.ENCOUNTER_LOCATION, lastLocationId);

            FormTag formTag = new FormTag();
            formTag.providerId = Utils.context().allSharedPreferences().fetchRegisteredANM();
            formTag.appVersion = FamilyLibrary.getInstance().getApplicationVersion();
            formTag.databaseVersion = FamilyLibrary.getInstance().getDatabaseVersion();

            event = createEvent(new JSONArray(), metadata, formTag, familyMember.getFamilyID(),
                    CoreConstants.EventType.UPDATE_FAMILY_RELATIONS, Utils.metadata().familyRegister.tableName);
            tagSyncMetadata(Utils.context().allSharedPreferences(), event);

            return event;
        } catch (Exception e) {
            Timber.e(e, "ReferralTaskViewActivity -- > createReferralCloseEvent");
        }

        return event;
    }*/
}
