package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;

import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONObject;
import org.opensrp.api.constants.Gender;
import org.smartregister.CoreLibrary;
import org.smartregister.chw.R;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.contract.ChildProfileContract;
import org.smartregister.chw.contract.ChildRegisterContract;
import org.smartregister.chw.custom_view.FamilyMemberFloatingMenu;
import org.smartregister.chw.fragment.ChildHomeVisitFragment;
import org.smartregister.chw.listener.OnClickFloatingMenu;
import org.smartregister.chw.model.ChildProfileModel;
import org.smartregister.chw.presenter.ChildProfilePresenter;
import org.smartregister.chw.util.ChildUtils;
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
import timber.log.Timber;

import static org.smartregister.chw.util.Constants.EventType.*;
import static org.smartregister.chw.util.Constants.INTENT_KEY.IS_COMES_FROM_FAMILY;


public class ChildProfileActivity extends BaseProfileActivity implements ChildProfileContract.View, ChildRegisterContract.InteractorCallBack {
    private static final String TAG = ChildProfileActivity.class.getCanonicalName();
    private static IntentFilter sIntentFilter;

    static {
        sIntentFilter = new IntentFilter();
        sIntentFilter.addAction(Intent.ACTION_DATE_CHANGED);
        sIntentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        sIntentFilter.addAction(Intent.ACTION_TIME_CHANGED);
    }

    protected TextView textViewParentName, textViewLastVisit, textViewMedicalHistory;
    protected CircleImageView imageViewProfile;
    protected View recordVisitPanel;
    private boolean appBarTitleIsShown = true;
    private int appBarLayoutScrollRange = -1;
    private String childBaseEntityId;
    private boolean isComesFromFamily = false;
    private TextView textViewTitle, textViewChildName, textViewGender, textViewAddress, textViewId, textViewRecord, textViewVisitNot, tvEdit;
    private RelativeLayout layoutNotRecordView, layoutLastVisitRow, layoutMostDueOverdue, layoutFamilyHasRow;
    private RelativeLayout layoutRecordButtonDone;
    private LinearLayout layoutRecordView;
    private View viewLastVisitRow, viewMostDueRow, viewFamilyRow;
    private TextView textViewNotVisitMonth, textViewUndo, textViewNameDue, textViewFamilyHas;
    private ImageView imageViewCross;
    private ProgressBar progressBar;
    private String gender;
    private Handler handler = new Handler();
    private final BroadcastReceiver mDateTimeChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            assert action != null;
            if (action.equals(Intent.ACTION_TIME_CHANGED) ||
                    action.equals(Intent.ACTION_TIMEZONE_CHANGED)) {
                fetchProfileData();

            }
        }
    };
    private String lastVisitDay;
    private FamilyMemberFloatingMenu familyFloatingMenu;
    private OnClickFloatingMenu onClickFloatingMenu;
    private Flavor flavor = new ChildProfileActivityFlv();

    @Override
    protected void onCreation() {
        initializePresenter();
        setContentView(R.layout.activity_child_profile);
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

        onClickFloatingMenu = flavor.getOnClickFloatingMenu(this, (ChildProfilePresenter) presenter);

        setupViews();
        setUpToolbar();
        registerReceiver(mDateTimeChangedReceiver, sIntentFilter);
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
                showProgressBar();
                presenter().updateVisitNotDone(System.currentTimeMillis());
                tvEdit.setVisibility(View.GONE);

                break;
            case R.id.textview_undo:
                showProgressBar();
                presenter().updateVisitNotDone(0);

                break;
            case R.id.textview_edit:
                openVisitHomeScreen(true);
                break;
//            case R.id.cross_image:
//                openVisitButtonView();
//                break;
            default:
                break;
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

        if (presenter == null) {
            presenter = new ChildProfilePresenter(this, new ChildProfileModel(familyName), childBaseEntityId);
        }

        fetchProfileData();
    }

    @Override
    protected void setupViews() {

        textViewParentName = findViewById(R.id.textview_parent_name);
        textViewChildName = findViewById(R.id.textview_name_age);
        textViewGender = findViewById(R.id.textview_gender);
        textViewAddress = findViewById(R.id.textview_address);
        textViewId = findViewById(R.id.textview_id);
        tvEdit = findViewById(R.id.textview_edit);
        imageViewProfile = findViewById(R.id.imageview_profile);
        recordVisitPanel = findViewById(R.id.record_visit_panel);
        textViewRecord = findViewById(R.id.textview_record_visit);
        textViewVisitNot = findViewById(R.id.textview_visit_not);
        textViewNotVisitMonth = findViewById(R.id.textview_not_visit_this_month);
        textViewLastVisit = findViewById(R.id.textview_last_vist_day);
        textViewUndo = findViewById(R.id.textview_undo);
        imageViewCross = findViewById(R.id.cross_image);
        layoutRecordView = findViewById(R.id.record_visit_bar);
        layoutNotRecordView = findViewById(R.id.record_visit_status_bar);
        layoutLastVisitRow = findViewById(R.id.last_visit_row);
        textViewMedicalHistory = findViewById(R.id.text_view_medical_hstory);
        layoutMostDueOverdue = findViewById(R.id.most_due_overdue_row);
        textViewNameDue = findViewById(R.id.textview_name_due);
        layoutFamilyHasRow = findViewById(R.id.family_has_row);
        textViewFamilyHas = findViewById(R.id.textview_family_has);
        layoutRecordButtonDone = findViewById(R.id.record_visit_done_bar);
        viewLastVisitRow = findViewById(R.id.view_last_visit_row);
        viewMostDueRow = findViewById(R.id.view_most_due_overdue_row);
        viewFamilyRow = findViewById(R.id.view_family_row);
        progressBar = findViewById(R.id.progress_bar);
        textViewRecord.setOnClickListener(this);
        textViewVisitNot.setOnClickListener(this);
        textViewUndo.setOnClickListener(this);
        imageViewCross.setOnClickListener(this);
        layoutLastVisitRow.setOnClickListener(this);
        layoutMostDueOverdue.setOnClickListener(this);
        layoutFamilyHasRow.setOnClickListener(this);
        layoutRecordButtonDone.setOnClickListener(this);

        familyFloatingMenu = new FamilyMemberFloatingMenu(this);
        LinearLayout.LayoutParams linearLayoutParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
        familyFloatingMenu.setGravity(Gravity.BOTTOM | Gravity.END);
        addContentView(familyFloatingMenu, linearLayoutParams);

        familyFloatingMenu.setClickListener(onClickFloatingMenu);


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

    private void setUpToolbar() {
        if (isComesFromFamily) {
            textViewTitle.setText(getString(R.string.return_to_family_members));
        } else {
            textViewTitle.setText(getString(R.string.return_to_all_children));
        }

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

    private void openUpcomingServicePage() {
        UpcomingServicesActivity.startUpcomingServicesActivity(this, ((ChildProfilePresenter) presenter()).getChildClient());
    }

    private void openMedicalHistoryScreen() {
        Map<String, Date> vaccine = ((ChildProfilePresenter) presenter()).getVaccineList();
        ChildMedicalHistoryActivity.startMedicalHistoryActivity(this, ((ChildProfilePresenter) presenter()).getChildClient(), patientName, lastVisitDay,
                ((ChildProfilePresenter) presenter()).getDateOfBirth(), new LinkedHashMap<>(vaccine));

    }

    private void openVisitHomeScreen(boolean isEditMode) {
        ChildHomeVisitFragment childHomeVisitFragment = ChildHomeVisitFragment.newInstance();
        childHomeVisitFragment.setEditMode(isEditMode);
        childHomeVisitFragment.setContext(this);
        childHomeVisitFragment.setChildClient(((ChildProfilePresenter) presenter()).getChildClient());
//                childHomeVisitFragment.setFamilyBaseEntityId(getFamilyBaseEntityId());
        childHomeVisitFragment.show(getFragmentManager(), ChildHomeVisitFragment.DIALOG_TAG);
    }

    private void openVisitRecordDoneView() {
        layoutRecordButtonDone.setVisibility(View.VISIBLE);
        layoutNotRecordView.setVisibility(View.GONE);
        layoutRecordView.setVisibility(View.GONE);

    }

    private void openVisitButtonView() {
        layoutNotRecordView.setVisibility(View.GONE);
        layoutRecordButtonDone.setVisibility(View.GONE);
        layoutRecordView.setVisibility(View.VISIBLE);
    }

    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    protected void updateTopbar() {
        if (gender.equalsIgnoreCase(Gender.MALE.toString())) {
            imageViewProfile.setBorderColor(getResources().getColor(R.color.light_blue));
        } else if (gender.equalsIgnoreCase(Gender.FEMALE.toString())) {
            imageViewProfile.setBorderColor(getResources().getColor(R.color.light_pink));
        }

    }

    /**
     * update immunization data and commonpersonobject for child as data may be updated
     * from childhomevisitfragment screen and need at medical history/upcoming service data.
     * need postdelay to update the client map
     */
    private void updateImmunizationData() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                layoutMostDueOverdue.setVisibility(View.GONE);
                viewMostDueRow.setVisibility(View.GONE);
                presenter().fetchVisitStatus(childBaseEntityId);
                presenter().fetchUpcomingServiceAndFamilyDue(childBaseEntityId);
                presenter().updateChildCommonPerson(childBaseEntityId);
            }
        }, 100);
    }

    /**
     * By this method it'll process the event client at home visit in background. After finish
     * it'll update the child client because for edit it's need the vaccine card,illness,birthcert.
     */
    public void processBackgroundEvent() {

        layoutMostDueOverdue.setVisibility(View.GONE);
        viewMostDueRow.setVisibility(View.GONE);
        presenter().fetchVisitStatus(childBaseEntityId);
        presenter().fetchUpcomingServiceAndFamilyDue(childBaseEntityId);
        presenter().updateChildCommonPerson(childBaseEntityId);
        presenter().processBackGroundEvent();
    }

    @Override
    public Context getContext() {
        return this;
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

    @Override
    public void setVisitButtonDueStatus() {
        openVisitButtonView();
        textViewRecord.setBackgroundResource(R.drawable.record_btn_selector_due);
        textViewRecord.setTextColor(getResources().getColor(R.color.white));
    }

    @Override
    public void setVisitButtonOverdueStatus() {
        openVisitButtonView();
        textViewRecord.setBackgroundResource(R.drawable.record_btn_selector_overdue);
        textViewRecord.setTextColor(getResources().getColor(R.color.white));
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
    public void setServiceNameDue(String serviceName, String dueDate) {
        if (!TextUtils.isEmpty(serviceName)) {
            layoutMostDueOverdue.setVisibility(View.VISIBLE);
            viewMostDueRow.setVisibility(View.VISIBLE);
            textViewNameDue.setText(ChildUtils.fromHtml(getString(R.string.vaccine_service_due, serviceName, dueDate)));
        } else {
            layoutMostDueOverdue.setVisibility(View.GONE);
            viewMostDueRow.setVisibility(View.GONE);
        }

    }

    @Override
    public void setServiceNameOverDue(String serviceName, String dueDate) {
        layoutMostDueOverdue.setVisibility(View.VISIBLE);
        viewMostDueRow.setVisibility(View.VISIBLE);
        textViewNameDue.setText(ChildUtils.fromHtml(getString(R.string.vaccine_service_overdue, serviceName, dueDate)));

    }

    @Override
    public void setServiceNameUpcoming(String serviceName, String dueDate) {
        layoutMostDueOverdue.setVisibility(View.VISIBLE);
        viewMostDueRow.setVisibility(View.VISIBLE);
        textViewNameDue.setText(ChildUtils.fromHtml(getString(R.string.vaccine_service_upcoming, serviceName, dueDate)));

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
        openVisitRecordDoneView();
        textViewRecord.setBackgroundResource(R.drawable.record_btn_selector_above_twentyfr);
        textViewRecord.setTextColor(getResources().getColor(R.color.light_grey_text));


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
    public ChildProfileContract.Presenter presenter() {
        if (presenter == null) {
            initializePresenter();
        }
        return (ChildProfileContract.Presenter) presenter;
    }

    @Override
    public void updateHasPhone(boolean hasPhone) {
        if (familyFloatingMenu != null) {
            familyFloatingMenu.reDraw(hasPhone);
        }
    }

    @Override
    public void enableEdit(boolean enable) {
        if (enable) {
            tvEdit.setVisibility(View.VISIBLE);
            tvEdit.setOnClickListener(this);
        } else {
            tvEdit.setVisibility(View.GONE);
            tvEdit.setOnClickListener(null);
        }
    }

    @Override
    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    public void openVisitMonthView() {
        layoutNotRecordView.setVisibility(View.VISIBLE);
        layoutRecordButtonDone.setVisibility(View.GONE);
        layoutRecordView.setVisibility(View.GONE);

    }

    @Override
    public void showUndoVisitNotDoneView() {
        presenter().fetchVisitStatus(childBaseEntityId);
    }

    @Override
    public void updateAfterBackgroundProcessed() {

        presenter().updateChildCommonPerson(childBaseEntityId);
    }

    @Override
    public void onNoUniqueId() {
        //TODO
        Timber.d("onNoUniqueId unimplemented");
    }

    @Override
    public void onUniqueIdFetched(Triple<String, String, String> triple, String entityId, String familyId) {
        //TODO
        Timber.d("onUniqueIdFetched unimplemented");
    }

    @Override
    public void onRegistrationSaved(boolean isEdit) {
        //TODO
        Timber.d("onRegistrationSaved unimplemented");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.action_registration:
                ((ChildProfilePresenter) presenter()).startFormForEdit(getResources().getString(R.string.edit_child_form_title),
                        ((ChildProfilePresenter) presenter()).getChildClient());
                return true;


            case R.id.action_malaria_registration:
                MalariaRegisterActivity.startMalariaRegistrationActivity(ChildProfileActivity.this,
                        ((ChildProfilePresenter) presenter()).getChildClient().getCaseId());
                return true;

            case R.id.action_remove_member:
                IndividualProfileRemoveActivity.startIndividualProfileActivity(ChildProfileActivity.this, ((ChildProfilePresenter) presenter()).getChildClient(),
                        ((ChildProfilePresenter) presenter()).getFamilyID()
                        , ((ChildProfilePresenter) presenter()).getFamilyHeadID(), ((ChildProfilePresenter) presenter()).getPrimaryCareGiverID());

                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.other_member_menu, menu);
        if (flavor.showMalariaConfirmationMenu()) {
            menu.findItem(R.id.action_malaria_registration).setVisible(true);
        } else {
            menu.findItem(R.id.action_malaria_registration).setVisible(false);
        }
        menu.findItem(R.id.action_anc_registration).setVisible(false);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mDateTimeChangedReceiver);
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case org.smartregister.chw.util.Constants.ProfileActivityResults.CHANGE_COMPLETED:
                if (resultCode == Activity.RESULT_OK) {
                    finish();
                }
                break;
            case JsonFormUtils.REQUEST_CODE_GET_JSON:
                if (resultCode == RESULT_OK) {
                    try {
                        String jsonString = data.getStringExtra(Constants.JSON_FORM_EXTRA.JSON);

                        JSONObject form = new JSONObject(jsonString);
                        if (form.getString(JsonFormUtils.ENCOUNTER_TYPE).equals(UPDATE_CHILD_REGISTRATION)) {
                            presenter().updateChildProfile(jsonString);
                        } else if (form.getString(JsonFormUtils.ENCOUNTER_TYPE).equals(CHILD_REFERRAL)) {
                            presenter().createSickChildEvent(CoreLibrary.getInstance().context().allSharedPreferences(), jsonString);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;


        }
    }

    public interface Flavor {
        OnClickFloatingMenu getOnClickFloatingMenu(Activity activity, ChildProfilePresenter presenter);

        boolean showMalariaConfirmationMenu();
    }
}
