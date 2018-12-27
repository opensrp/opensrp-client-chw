package org.smartgresiter.wcaro.activity;

import android.app.AppComponentFactory;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONException;
import org.json.JSONObject;
import org.opensrp.api.constants.Gender;
import org.smartgresiter.wcaro.R;
import org.smartgresiter.wcaro.application.WcaroApplication;
import org.smartgresiter.wcaro.contract.ChildProfileContract;
import org.smartgresiter.wcaro.contract.ChildRegisterContract;
import org.smartgresiter.wcaro.custom_view.IndividualMemberFloatingMenu;
import org.smartgresiter.wcaro.fragment.AddMemberFragment;
import org.smartgresiter.wcaro.fragment.ChildHomeVisitFragment;
import org.smartgresiter.wcaro.fragment.ChildImmunizationFragment;
import org.smartgresiter.wcaro.listener.OnClickFloatingMenu;
import org.smartgresiter.wcaro.model.ChildProfileModel;
import org.smartgresiter.wcaro.presenter.ChildProfilePresenter;
import org.smartgresiter.wcaro.repository.WcaroRepository;
import org.smartregister.domain.FetchStatus;
import org.smartregister.family.activity.BaseFamilyProfileActivity;
import org.smartregister.family.util.Constants;
import org.smartregister.helper.ImageRenderHelper;
import org.smartregister.immunization.domain.Vaccine;
import org.smartregister.immunization.domain.VaccineWrapper;
import org.smartregister.immunization.listener.VaccinationActionListener;
import org.smartregister.immunization.repository.VaccineRepository;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.util.JsonFormUtils;
import org.smartregister.view.activity.BaseProfileActivity;

import java.util.ArrayList;

import static org.smartgresiter.wcaro.fragment.AddMemberFragment.DIALOG_TAG;


public class ChildProfileActivity extends BaseProfileActivity implements ChildProfileContract.View,ChildRegisterContract.InteractorCallBack,VaccinationActionListener {
    private boolean appBarTitleIsShown = true;
    private int appBarLayoutScrollRange = -1;
    private String childBaseEntityId;
    private TextView textViewTitle,textViewParentName,textViewChildName,textViewGender,textViewAddress,textViewId,textViewRecord,textViewVisitNot;
    private ImageView imageViewProfile;
    private RelativeLayout layoutRecordView,layoutNotRecordView;
    private TextView textViewNotVisitMonth,textViewUndo;
    private ImageView imageViewCross;
    private String gender;
    @Override
    protected void onCreation() {
        setContentView(R.layout.activity_child_profile);
        ((IndividualMemberFloatingMenu)findViewById(R.id.individual_floating_menu)).setClickListener(onClickFloatingMenu);
        Toolbar toolbar = findViewById(R.id.collapsing_toolbar);
        textViewTitle=toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        appBarLayout = findViewById(org.smartregister.R.id.collapsing_toolbar_appbarlayout);
        appBarLayout.addOnOffsetChangedListener(this);

        imageRenderHelper = new ImageRenderHelper(this);

        initializePresenter();
        setupViews();
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
            textViewTitle.setText(getString(R.string.return_to_all_children));
            appBarTitleIsShown = false;
        }

    }
    @Override
    protected void setupViews() {

        textViewParentName=findViewById(R.id.textview_parent_name);
        textViewChildName=findViewById(R.id.textview_name_age);
        textViewGender=findViewById(R.id.textview_gender);
        textViewAddress=findViewById(R.id.textview_address);
        textViewId=findViewById(R.id.textview_id);
        imageViewProfile=findViewById(R.id.imageview_profile);
        textViewRecord=findViewById(R.id.textview_record_visit);
        textViewVisitNot=findViewById(R.id.textview_visit_not);
        textViewNotVisitMonth=findViewById(R.id.textview_not_visit_this_month);
        textViewUndo=findViewById(R.id.textview_undo);
        imageViewCross=(ImageView) findViewById(R.id.cross_image);
        layoutRecordView=findViewById(R.id.record_visit_bar);
        layoutNotRecordView=findViewById(R.id.record_visit_status_bar);
        textViewRecord.setOnClickListener(this);
        textViewVisitNot.setOnClickListener(this);
        textViewUndo.setOnClickListener(this);
        imageViewCross.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.textview_record_visit:
                FragmentTransaction ft = this.getFragmentManager().beginTransaction();
                ChildHomeVisitFragment childHomeVisitFragment = ChildHomeVisitFragment.newInstance();
                childHomeVisitFragment.setContext(this);
                childHomeVisitFragment.setChildClient(((ChildProfilePresenter)presenter()).getChildClient());
//                childHomeVisitFragment.setFamilyBaseEntityId(getFamilyBaseEntityId());
                childHomeVisitFragment.show(getFragmentManager(),ChildHomeVisitFragment.DIALOG_TAG);

                break;
            case R.id.textview_visit_not:
                openVisitMonthView();
                break;
            case R.id.textview_undo:
                if(textViewUndo.getText().toString().equalsIgnoreCase(getString(R.string.undo))){
                    openVisitButtonView();
                }else{
                    Toast.makeText(this,"Edit previous visit",Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.cross_image:
                openVisitButtonView();
                break;
        }
    }
    private void openVisitMonthView(){
        layoutNotRecordView.setVisibility(View.VISIBLE);
        layoutRecordView.setVisibility(View.GONE);

    }
    private void openVisitButtonView(){
        layoutNotRecordView.setVisibility(View.GONE);
        layoutRecordView.setVisibility(View.VISIBLE);
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
    public void setVisitNotDoneView() {

    }

    @Override
    public void setVisitThisMonthView() {

    }

    @Override
    public void setVisitLessTwentyFourView(String monthName) {
        textViewNotVisitMonth.setText(getString(R.string.visit_month,monthName));
        textViewUndo.setText(getString(R.string.edit));
        imageViewCross.setImageResource(R.drawable.activityrow_visited);
        openVisitMonthView();

    }

    @Override
    public void setVisitAboveTwentyFourView() {
        textViewVisitNot.setVisibility(View.GONE);
        textViewRecord.setBackgroundResource(R.drawable.record_btn_selector_above_twentyfr);
        textViewRecord.setTextColor(getResources().getColor(R.color.light_grey_text));


    }
    private void updateTopbar(){
        if(gender.equalsIgnoreCase(Gender.MALE.toString())){
            appBarLayout.setBackgroundColor(getResources().getColor(R.color.light_blue));
        }else if(gender.equalsIgnoreCase(Gender.FEMALE.toString())){
            appBarLayout.setBackgroundColor(getResources().getColor(R.color.light_pink));
        }

    }

    @Override
    protected void initializePresenter() {
        childBaseEntityId = getIntent().getStringExtra(Constants.INTENT_KEY.BASE_ENTITY_ID);

        presenter = new ChildProfilePresenter(this, new ChildProfileModel(), childBaseEntityId);
        fetchProfileData();
    }

    @Override
    protected ViewPager setupViewPager(ViewPager viewPager) {
        return null;
    }

    @Override
    protected void fetchProfileData() {
        presenter().fetchProfileData();
        presenter().fetchVisitStatus(childBaseEntityId);
    }

    private OnClickFloatingMenu onClickFloatingMenu=new OnClickFloatingMenu() {
        @Override
        public void onClickMenu(int viewId) {
            switch (viewId){
                case R.id.call_layout:
                    break;
                case R.id.registration_layout:
                    break;
                case R.id.remove_member_layout:
                    break;
            }

        }
    };

    @Override
    public void startFormActivity(JSONObject form) {

    }

    @Override
    public void refreshMemberList(FetchStatus fetchStatus) {

    }

    @Override
    public void displayShortToast(int resourceId) {

    }

    @Override
    public void setProfileImage(String baseEntityId) {
        int defaultImage=gender.equalsIgnoreCase(Gender.MALE.toString())?R.drawable.row_boy:R.drawable.row_girl;
        imageRenderHelper.refreshProfileImage(baseEntityId, imageViewProfile,defaultImage);


    }

    @Override
    public void setParentName(String parentName) {
        textViewParentName.setText(parentName);

    }

    @Override
    public void setGender(String gender) {
        this.gender=gender;
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
        patientName=fullName;
        textViewChildName.setText(fullName);

    }

    @Override
    public void setAge(String age) {
        textViewChildName.append(","+age);

    }


    @Override
    public ChildProfileContract.Presenter presenter() {
        return (ChildProfileContract.Presenter)presenter;
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
    public void onVaccinateToday(ArrayList<VaccineWrapper> arrayList, View view) {
        ((ChildImmunizationFragment)getFragmentManager().findFragmentByTag(ChildImmunizationFragment.TAG)).onVaccinateToday(arrayList,view);
    }

    @Override
    public void onVaccinateEarlier(ArrayList<VaccineWrapper> arrayList, View view) {
        ((ChildImmunizationFragment)getFragmentManager().findFragmentByTag(ChildImmunizationFragment.TAG)).onVaccinateEarlier(arrayList,view);
    }

    @Override
    public void onUndoVaccination(VaccineWrapper vaccineWrapper, View view) {
        ((ChildImmunizationFragment)getFragmentManager().findFragmentByTag(ChildImmunizationFragment.TAG)).onUndoVaccination(vaccineWrapper,view);
    }



}
