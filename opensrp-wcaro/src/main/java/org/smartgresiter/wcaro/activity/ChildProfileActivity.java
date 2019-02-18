package org.smartgresiter.wcaro.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;

import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONObject;
import org.opensrp.api.constants.Gender;
import org.smartgresiter.wcaro.R;
import org.smartgresiter.wcaro.contract.ChildProfileContract;
import org.smartgresiter.wcaro.contract.ChildRegisterContract;
import org.smartgresiter.wcaro.custom_view.FamilyMemberFloatingMenu;
import org.smartgresiter.wcaro.fragment.ChildHomeVisitFragment;
import org.smartgresiter.wcaro.fragment.FamilyCallDialogFragment;
import org.smartgresiter.wcaro.listener.OnClickFloatingMenu;
import org.smartgresiter.wcaro.model.ChildProfileModel;
import org.smartgresiter.wcaro.presenter.ChildProfilePresenter;
import org.smartgresiter.wcaro.util.ChildUtils;
import org.smartregister.domain.FetchStatus;
import org.smartregister.family.util.Constants;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.family.util.Utils;
import org.smartregister.helper.ImageRenderHelper;
import org.smartregister.view.activity.BaseProfileActivity;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static org.smartgresiter.wcaro.util.Constants.INTENT_KEY.IS_COMES_FROM_FAMILY;


public class ChildProfileActivity extends BaseProfileActivity implements ChildProfileContract.View, ChildRegisterContract.InteractorCallBack {
    private boolean appBarTitleIsShown = true;
    private int appBarLayoutScrollRange = -1;
    private String childBaseEntityId;
    private boolean isComesFromFamily = false;
    private TextView textViewTitle, textViewParentName, textViewChildName, textViewGender, textViewAddress, textViewId, textViewRecord, textViewVisitNot;
    private CircleImageView imageViewProfile;
    private RelativeLayout layoutNotRecordView, layoutLastVisitRow, layoutMostDueOverdue, layoutFamilyHasRow;
    private RelativeLayout layoutRecordButtonDone;
    private LinearLayout layoutRecordView;
    private View viewLastVisitRow, viewMostDueRow, viewFamilyRow;
    private TextView textViewNotVisitMonth, textViewUndo, textViewLastVisit, textViewNameDue, textViewDueDate, textViewFamilyHas;
    private ImageView imageViewCross;
    private String gender;
    private Handler handler = new Handler();
    private String lastVisitDay;
    private OnClickFloatingMenu onClickFloatingMenu = new OnClickFloatingMenu() {
        @Override
        public void onClickMenu(int viewId) {
            switch (viewId) {
                case R.id.call_layout:
                    FamilyCallDialogFragment.launchDialog(ChildProfileActivity.this, ((ChildProfilePresenter) presenter).getFamilyId());
                    break;
                case R.id.registration_layout:
                    ((ChildProfilePresenter) presenter()).startFormForEdit(((ChildProfilePresenter) presenter()).getChildClient());
                    break;
                case R.id.remove_member_layout:

                    IndividualProfileRemoveActivity.startIndividualProfileActivity(ChildProfileActivity.this, ((ChildProfilePresenter) presenter()).getChildClient(),
                            ((ChildProfilePresenter) presenter()).getFamilyID()
                            , ((ChildProfilePresenter) presenter()).getFamilyHeadID(), ((ChildProfilePresenter) presenter()).getPrimaryCareGiverID());

                    break;
            }

        }
    };

    @Override
    protected void onCreation() {
        setContentView(R.layout.activity_child_profile);
        ((FamilyMemberFloatingMenu) findViewById(R.id.individual_floating_menu)).setClickListener(onClickFloatingMenu);
        Toolbar toolbar = findViewById(R.id.collapsing_toolbar);
        textViewTitle = toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            final Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp);
            upArrow.setColorFilter(getResources().getColor(R.color.text_blue), PorterDuff.Mode.SRC_ATOP);
            actionBar.setHomeAsUpIndicator(upArrow);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        appBarLayout = findViewById(R.id.collapsing_toolbar_appbarlayout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            appBarLayout.setOutlineProvider(null);
        }
        imageRenderHelper = new ImageRenderHelper(this);

        initializePresenter();
        setupViews();
        setUpToolbar();
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

        if (appBarLayoutScrollRange == -1) {
            appBarLayoutScrollRange = appBarLayout.getTotalScrollRange();
        }
        if (appBarLayoutScrollRange + verticalOffset == 0) {

            textViewTitle.setText(patientName);
            appBarTitleIsShown = true;
        } else if (appBarTitleIsShown) {
            setUpToolbar();
            appBarTitleIsShown = false;
        }

    }

    @Override
    protected void setupViews() {

        textViewParentName = findViewById(R.id.textview_parent_name);
        textViewChildName = findViewById(R.id.textview_name_age);
        textViewGender = findViewById(R.id.textview_gender);
        textViewAddress = findViewById(R.id.textview_address);
        textViewId = findViewById(R.id.textview_id);
        imageViewProfile = findViewById(R.id.imageview_profile);
        textViewRecord = findViewById(R.id.textview_record_visit);
        textViewVisitNot = findViewById(R.id.textview_visit_not);
        textViewNotVisitMonth = findViewById(R.id.textview_not_visit_this_month);
        textViewLastVisit = findViewById(R.id.textview_last_vist_day);
        textViewUndo = findViewById(R.id.textview_undo);
        imageViewCross = findViewById(R.id.cross_image);
        layoutRecordView = findViewById(R.id.record_visit_bar);
        layoutNotRecordView = findViewById(R.id.record_visit_status_bar);
        layoutLastVisitRow = findViewById(R.id.last_visit_row);
        layoutMostDueOverdue = findViewById(R.id.most_due_overdue_row);
        textViewNameDue = findViewById(R.id.textview_name_due);
        textViewDueDate = findViewById(R.id.textview_due_overdue_status);
        layoutFamilyHasRow = findViewById(R.id.family_has_row);
        textViewFamilyHas = findViewById(R.id.textview_family_has);
        layoutRecordButtonDone = findViewById(R.id.record_visit_done_bar);
        viewLastVisitRow = findViewById(R.id.view_last_visit_row);
        viewMostDueRow = findViewById(R.id.view_most_due_overdue_row);
        viewFamilyRow = findViewById(R.id.view_family_row);
        textViewRecord.setOnClickListener(this);
        textViewVisitNot.setOnClickListener(this);
        textViewUndo.setOnClickListener(this);
        imageViewCross.setOnClickListener(this);
        layoutLastVisitRow.setOnClickListener(this);
        layoutMostDueOverdue.setOnClickListener(this);
        layoutFamilyHasRow.setOnClickListener(this);
        layoutRecordButtonDone.setOnClickListener(this);

    }

    private void setUpToolbar() {
        if (isComesFromFamily) {
            textViewTitle.setText(getString(R.string.return_to_family_members));
        } else {
            textViewTitle.setText(getString(R.string.return_to_all_children));
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.last_visit_row:
                openMedicalHistoryScreen();
                break;
            case R.id.most_due_overdue_row:
                openUpcomingServicePage();
                break;
            case R.id.textview_record_visit:
            case R.id.record_visit_done_bar:
                openVisitHomeScreen(false);

                break;
            case R.id.family_has_row:
                openFamilyDueTab();
                break;
            case R.id.textview_visit_not:
                presenter().updateVisitNotDone(System.currentTimeMillis());

                openVisitMonthView();
                break;
            case R.id.textview_undo:

                if (textViewUndo.getText().toString().equalsIgnoreCase(getString(R.string.undo))) {
                    presenter().updateVisitNotDone(0);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            presenter().fetchVisitStatus(childBaseEntityId);
                        }
                    }, 200);

                } else {
                    openVisitHomeScreen(true);
                }

                break;
//            case R.id.cross_image:
//                openVisitButtonView();
//                break;
        }
    }

    private void openFamilyDueTab() {
        Intent intent = new Intent(this, FamilyProfileActivity.class);

        intent.putExtra(Constants.INTENT_KEY.FAMILY_BASE_ENTITY_ID, ((ChildProfilePresenter) presenter()).getFamilyId());
        intent.putExtra(Constants.INTENT_KEY.FAMILY_HEAD, ((ChildProfilePresenter) presenter()).getFamilyHeadID());
        intent.putExtra(Constants.INTENT_KEY.PRIMARY_CAREGIVER, ((ChildProfilePresenter) presenter()).getPrimaryCareGiverID());
        intent.putExtra(Constants.INTENT_KEY.FAMILY_NAME, ((ChildProfilePresenter) presenter()).getFamilyName());

        intent.putExtra(org.smartgresiter.wcaro.util.Constants.INTENT_KEY.SERVICE_DUE, true);
        startActivity(intent);
    }

    private void openUpcomingServicePage() {
        UpcomingServicesActivity.startUpcomingServicesActivity(this, ((ChildProfilePresenter) presenter()).getChildClient());
    }

    private void openMedicalHistoryScreen() {
        Map<String, Date> vaccine = ((ChildProfilePresenter) presenter()).getVaccineList();
        MedicalHistoryActivity.startMedicalHistoryActivity(this, ((ChildProfilePresenter) presenter()).getChildClient(), patientName, lastVisitDay,
                ((ChildProfilePresenter) presenter()).getDateOfBirth(), new LinkedHashMap<String, Date>(vaccine));

    }


    private void openVisitHomeScreen(boolean isEditMode) {
        ChildHomeVisitFragment childHomeVisitFragment = ChildHomeVisitFragment.newInstance();
        childHomeVisitFragment.setEditMode(isEditMode);
        childHomeVisitFragment.setContext(this);
        childHomeVisitFragment.setChildClient(((ChildProfilePresenter) presenter()).getChildClient());
//                childHomeVisitFragment.setFamilyBaseEntityId(getFamilyBaseEntityId());
        childHomeVisitFragment.show(getFragmentManager(), ChildHomeVisitFragment.DIALOG_TAG);
    }

    private void openVisitMonthView() {
        layoutNotRecordView.setVisibility(View.VISIBLE);
        layoutRecordView.setVisibility(View.GONE);

    }

    private void openVisitButtonView() {
        layoutNotRecordView.setVisibility(View.GONE);
        layoutRecordView.setVisibility(View.VISIBLE);
    }

    @Override
    public void setVisitButtonDueStatus() {
        recordBtnCenterAlign(false);
        openVisitButtonView();
        textViewRecord.setBackgroundResource(R.drawable.record_btn_selector_due);
        textViewRecord.setTextColor(getResources().getColor(R.color.white));
    }

    @Override
    public void setVisitButtonOverdueStatus() {
        recordBtnCenterAlign(false);
        openVisitButtonView();
        textViewRecord.setBackgroundResource(R.drawable.record_btn_selector_overdue);
        textViewRecord.setTextColor(getResources().getColor(R.color.white));
    }

    @Override
    public void setLastVisitRowView(String days) {
        lastVisitDay = days;
        if (TextUtils.isEmpty(days)) {
            layoutLastVisitRow.setVisibility(View.GONE);
            viewLastVisitRow.setVisibility(View.GONE);
        } else {
            layoutLastVisitRow.setVisibility(View.VISIBLE);
            textViewLastVisit.setText(getString(R.string.last_visit_40_days_ago, days));
            viewLastVisitRow.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void setServiceName(String serviceName) {
        textViewNameDue.setText(ChildUtils.fixVaccineCasing(serviceName));
    }

    @Override
    public void setServiceDueDate(String date) {
        layoutMostDueOverdue.setVisibility(View.VISIBLE);
        viewLastVisitRow.setVisibility(View.GONE);
        viewMostDueRow.setVisibility(View.VISIBLE);
        textViewDueDate.setText(date);
        textViewDueDate.setTextColor(getResources().getColor(R.color.black));

    }

    @Override
    public void setServiceUpcomingDueDate(String upcomingDate) {
        layoutMostDueOverdue.setVisibility(View.VISIBLE);
        viewMostDueRow.setVisibility(View.VISIBLE);
        textViewDueDate.setText(upcomingDate);
        textViewDueDate.setTextColor(getResources().getColor(R.color.light_grey_text));
    }

    @Override
    public void setSeviceOverdueDate(String date) {
        layoutMostDueOverdue.setVisibility(View.VISIBLE);
        viewMostDueRow.setVisibility(View.VISIBLE);
        textViewDueDate.setText(date);
        textViewDueDate.setTextColor(getResources().getColor(R.color.visit_status_over_due));
    }

    @Override
    public void setFamilyHasNothingDue() {
        layoutFamilyHasRow.setVisibility(View.VISIBLE);
        viewFamilyRow.setVisibility(View.VISIBLE);
        textViewFamilyHas.setText(getString(R.string.family_has_nothing_due));

    }

    @Override
    public void setFamilyHasServiceDue() {
        layoutFamilyHasRow.setVisibility(View.VISIBLE);
        viewFamilyRow.setVisibility(View.VISIBLE);
        textViewFamilyHas.setText(getString(R.string.family_has_services_due));
    }

    @Override
    public void setFamilyHasServiceOverdue() {
        layoutFamilyHasRow.setVisibility(View.VISIBLE);
        viewFamilyRow.setVisibility(View.VISIBLE);
        textViewFamilyHas.setText(ChildUtils.fromHtml(getString(R.string.family_has_service_overdue)));
    }

    @Override
    public void setVisitNotDoneThisMonth() {
        openVisitMonthView();
        textViewNotVisitMonth.setText(getString(R.string.not_visiting_this_month));
        textViewUndo.setText(getString(R.string.undo));
        textViewUndo.setVisibility(View.VISIBLE);
        imageViewCross.setImageResource(R.drawable.activityrow_notvisited);
    }

    @Override
    public void setVisitLessTwentyFourView(String monthName) {
        textViewNotVisitMonth.setText(getString(R.string.visit_month, monthName));
        textViewUndo.setText(getString(R.string.edit));
        textViewUndo.setVisibility(View.GONE);
        imageViewCross.setImageResource(R.drawable.activityrow_visited);
        openVisitMonthView();

    }

    @Override
    public void setVisitAboveTwentyFourView() {
        textViewVisitNot.setVisibility(View.GONE);
        recordBtnCenterAlign(true);
        textViewRecord.setBackgroundResource(R.drawable.record_btn_selector_above_twentyfr);
        textViewRecord.setTextColor(getResources().getColor(R.color.light_grey_text));


    }

    private void updateTopbar() {
        if (gender.equalsIgnoreCase(Gender.MALE.toString())) {
            imageViewProfile.setBorderColor(getResources().getColor(R.color.light_blue));
        } else if (gender.equalsIgnoreCase(Gender.FEMALE.toString())) {
            imageViewProfile.setBorderColor(getResources().getColor(R.color.light_pink));
        }

    }

    @Override
    protected void initializePresenter() {
        childBaseEntityId = getIntent().getStringExtra(Constants.INTENT_KEY.BASE_ENTITY_ID);
        isComesFromFamily = getIntent().getBooleanExtra(IS_COMES_FROM_FAMILY, false);
        String familyName = getIntent().getStringExtra(Constants.INTENT_KEY.FAMILY_NAME);
        if (familyName == null) {
            familyName = "";
        }

        presenter = new ChildProfilePresenter(this, new ChildProfileModel(familyName), childBaseEntityId);
        fetchProfileData();
    }

    @Override
    protected ViewPager setupViewPager(ViewPager viewPager) {
        return null;
    }

    @Override
    protected void fetchProfileData() {
        presenter().fetchProfileData();
        updateImmunizationData();

    }

    /**
     * update immunization data and commonpersonobject for child as data may be updated
     * from childhomevisitfragment screen and need at medical history/upcoming service data.
     */
    public void updateImmunizationData() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                presenter().fetchVisitStatus(childBaseEntityId);
                presenter().fetchFamilyMemberServiceDue(childBaseEntityId);
                presenter().updateChildCommonPerson(childBaseEntityId);
            }
        }, 100);
    }

    @Override
    public void startFormActivity(JSONObject jsonForm) {

        Intent intent = new Intent(this, Utils.metadata().familyMemberFormActivity);
        intent.putExtra(Constants.JSON_FORM_EXTRA.JSON, jsonForm.toString());


        Form form = new Form();
        form.setActionBarBackground(R.color.family_actionbar);
        form.setWizard(false);
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);


        startActivityForResult(intent, JsonFormUtils.REQUEST_CODE_GET_JSON);
    }

    @Override
    public void refreshProfile(FetchStatus fetchStatus) {
        if (fetchStatus.equals(FetchStatus.fetched)) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    presenter().fetchProfileData();
                }
            }, 100);
        }

    }

    @Override
    public void displayShortToast(int resourceId) {

    }

    @Override
    public void setProfileImage(String baseEntityId) {
        int defaultImage = R.drawable.rowavatar_child;// gender.equalsIgnoreCase(Gender.MALE.toString()) ? R.drawable.row_boy : R.drawable.row_girl;
        imageRenderHelper.refreshProfileImage(baseEntityId, imageViewProfile, defaultImage);


    }

    @Override
    public void setParentName(String parentName) {
        textViewParentName.setText(parentName);

    }

    @Override
    public void setGender(String gender) {
        this.gender = gender;
        textViewGender.setText(gender);
        updateTopbar();

    }

    @Override
    public void setAddress(String address) {
        textViewAddress.setText(address);

    }

    @Override
    public void setId(String id) {
        textViewId.setText(id);

    }

    @Override
    public void setProfileName(String fullName) {
        patientName = fullName;
        textViewChildName.setText(fullName);

    }

    @Override
    public void setAge(String age) {
        textViewChildName.append(", " + age);

    }

    private void recordBtnCenterAlign(boolean isCenter) {
        if (isCenter) {
            layoutRecordButtonDone.setVisibility(View.VISIBLE);
            layoutRecordView.setVisibility(View.GONE);
        } else {
            layoutRecordButtonDone.setVisibility(View.GONE);
            layoutRecordView.setVisibility(View.VISIBLE);
        }
    }

    private void addOrRemoveProperty(View view, int property, boolean flag) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
        if (flag) {
            layoutParams.addRule(property);
        } else {
            layoutParams.removeRule(property);
        }
        view.setLayoutParams(layoutParams);
    }

    @Override
    public ChildProfileContract.Presenter presenter() {
        return (ChildProfileContract.Presenter) presenter;
    }

    @Override
    public void onNoUniqueId() {

    }

    @Override
    public void onUniqueIdFetched(Triple<String, String, String> triple, String entityId, String familyId) {

    }

    @Override
    public void onRegistrationSaved(boolean isEdit) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case org.smartgresiter.wcaro.util.Constants.ProfileActivityResults.CHANGE_COMPLETED:
                if (resultCode == Activity.RESULT_OK) {
                    finish();
                }
                break;
            case JsonFormUtils.REQUEST_CODE_GET_JSON:
                if (resultCode == RESULT_OK) {
                    try {
                        String jsonString = data.getStringExtra(Constants.JSON_FORM_EXTRA.JSON);

                        JSONObject form = new JSONObject(jsonString);
                        if (form.getString(JsonFormUtils.ENCOUNTER_TYPE).equals(org.smartgresiter.wcaro.util.Constants.EventType.UPDATE_CHILD_REGISTRATION)) {
                            presenter().updateChildProfile(jsonString);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;


        }
    }

}
