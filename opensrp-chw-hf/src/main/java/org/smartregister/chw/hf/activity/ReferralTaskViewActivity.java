package org.smartregister.chw.hf.activity;

import android.app.Activity;
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

import org.smartregister.chw.core.utils.ChildDBConstants;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.hf.listener.ReferralsTaskViewClickListener;
import com.opensrp.hf.R;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.Task;
import org.smartregister.family.util.DBConstants;
import org.smartregister.family.util.Utils;
import org.smartregister.view.activity.SecuredActivity;
import org.smartregister.view.customcontrols.CustomFontTextView;

import static org.smartregister.chw.core.utils.CoreJsonFormUtils.tagSyncMetadata;
import static org.smartregister.util.Utils.getValue;

public class ReferralTaskViewActivity extends SecuredActivity {
    private CommonPersonObjectClient personObjectClient;
    private Task task;
    private CustomFontTextView toolBarTextView;
    protected AppBarLayout appBarLayout;
    private CustomFontTextView clientName;
    private CustomFontTextView clientAge;
    private CustomFontTextView careGiverName;
    private CustomFontTextView careGiverPhone;
    private CustomFontTextView clientReferralProblem;
    private CustomFontTextView referralDate;
    private CustomFontTextView chwDetailsNames;
    private CustomFontTextView chwDetailsPhone;
    private CustomFontTextView markAskDone;
    private CustomFontTextView viewProfile;
    private ReferralsTaskViewClickListener referralsTaskViewClickListener = new ReferralsTaskViewClickListener();
    private String name;

    public static void startReferralTaskViewActivity(Activity activity, CommonPersonObjectClient personObjectClient, Task task) {
        Intent intent = new Intent(activity, ReferralTaskViewActivity.class);
        intent.putExtra(CoreConstants.INTENT_KEY.USERS_TASKS, task);
        intent.putExtra(CoreConstants.INTENT_KEY.CHILD_COMMON_PERSON, personObjectClient);
        activity.startActivity(intent);
    }

    public CommonPersonObjectClient getPersonObjectClient() {
        return personObjectClient;
    }

    public void setPersonObjectClient(CommonPersonObjectClient personObjectClient) {
        this.personObjectClient = personObjectClient;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
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
            finish();
        }
    }

    @Override
    protected void onResumption() {

    }

    private void extractPersonObjectClient() {
        setPersonObjectClient((CommonPersonObjectClient) getIntent().getSerializableExtra(CoreConstants.INTENT_KEY.CHILD_COMMON_PERSON));
        if (getPersonObjectClient() != null) {
            name = getValue(getPersonObjectClient().getColumnmaps(), DBConstants.KEY.FIRST_NAME, true) + " " + getValue(getPersonObjectClient().getColumnmaps(), DBConstants.KEY.LAST_NAME, true);
        }
    }

    private void extraClientTask() {
        setTask((Task) getIntent().getSerializableExtra(CoreConstants.INTENT_KEY.USERS_TASKS));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    private void inflateToolbar() {
        Toolbar toolbar = findViewById(R.id.back_referrals_toolbar);
        toolBarTextView = toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            final Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp);
            upArrow.setColorFilter(getResources().getColor(R.color.text_blue), PorterDuff.Mode.SRC_ATOP);
            actionBar.setHomeAsUpIndicator(upArrow);
            actionBar.setElevation(0);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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
        referralDate = findViewById(R.id.referral_date);
        chwDetailsNames = findViewById(R.id.chw_details_names);
        chwDetailsPhone = findViewById(R.id.chw_details_phone);

        markAskDone = findViewById(R.id.mark_ask_done);
        markAskDone.setOnClickListener(referralsTaskViewClickListener);

        viewProfile = findViewById(R.id.view_profile);
        viewProfile.setOnClickListener(referralsTaskViewClickListener);

        getReferralDetails();
    }

    private void getReferralDetails() {
        if (getPersonObjectClient() != null) {
            clientReferralProblem.setText(getTask().getDescription());
            clientName.setText(getString(R.string.client_name_suffix, name));
            clientAge.setText(Utils.getTranslatedDate(Utils.getDuration(Utils.getValue(getPersonObjectClient().getColumnmaps(), DBConstants.KEY.DOB, false)), getBaseContext()));
            referralDate.setText(org.smartregister.chw.core.utils.Utils.DD_MM_YYYY.format(task.getExecutionStartDate().toDate()));

            String parentFirstName = Utils.getValue(getPersonObjectClient().getColumnmaps(), ChildDBConstants.KEY.FAMILY_FIRST_NAME, true);
            String parentLastName = Utils.getValue(getPersonObjectClient().getColumnmaps(), ChildDBConstants.KEY.FAMILY_LAST_NAME, true);
            String parentMiddleName = Utils.getValue(getPersonObjectClient().getColumnmaps(), ChildDBConstants.KEY.FAMILY_MIDDLE_NAME, true);

            String parentName = getString(R.string.care_giver_prefix, org.smartregister.util.Utils.getName(parentFirstName, parentMiddleName + " " + parentLastName));
            careGiverName.setText(parentName);
            careGiverPhone.setText(getFamilyMemberContacts());

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
        }

        return phoneNumber;
    }

    public void closeTask() {

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
