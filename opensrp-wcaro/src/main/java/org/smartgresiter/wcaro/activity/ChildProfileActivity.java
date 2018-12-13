package org.smartgresiter.wcaro.activity;

import android.app.AppComponentFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONObject;
import org.smartgresiter.wcaro.R;
import org.smartgresiter.wcaro.contract.ChildProfileContract;
import org.smartgresiter.wcaro.contract.ChildRegisterContract;
import org.smartgresiter.wcaro.custom_view.IndividualMemberFloatingMenu;
import org.smartgresiter.wcaro.listener.OnClickFloatingMenu;
import org.smartregister.domain.FetchStatus;
import org.smartregister.family.util.Constants;
import org.smartregister.helper.ImageRenderHelper;
import org.smartregister.view.activity.BaseProfileActivity;


public class ChildProfileActivity extends BaseProfileActivity implements ChildProfileContract.View,ChildRegisterContract.InteractorCallBack{

    private String childBaseEntityId;
    private TextView textViewParentName,textViewChildName,textViewGender,textViewAddress,textViewId;
    private ImageView imageViewProfile;
    @Override
    protected void onCreation() {

        setContentView(R.layout.activity_child_profile);
        ((IndividualMemberFloatingMenu)findViewById(R.id.individual_floating_menu)).setClickListener(onClickFloatingMenu);
        Toolbar toolbar = findViewById(R.id.collapsing_toolbar);
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
    protected void setupViews() {
        textViewParentName=findViewById(R.id.textview_parent_name);
        textViewChildName=findViewById(R.id.textview_name);
        textViewGender=findViewById(R.id.textview_gender);
        textViewAddress=findViewById(R.id.textview_address);
        textViewId=findViewById(R.id.textview_id);
        imageViewProfile=findViewById(R.id.imageview_profile);

    }

    @Override
    protected void initializePresenter() {
        childBaseEntityId = getIntent().getStringExtra(Constants.INTENT_KEY.BASE_ENTITY_ID);

    }

    @Override
    protected ViewPager setupViewPager(ViewPager viewPager) {
        return null;
    }

    @Override
    protected void fetchProfileData() {
        presenter().fetchProfileData();
    }

    //    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_child_profile);
//        ((IndividualMemberFloatingMenu)findViewById(R.id.individual_floating_menu)).setClickListener(onClickFloatingMenu);
//        Toolbar toolbar = findViewById(R.id.collapsing_toolbar);
//        setSupportActionBar(toolbar);
//
//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.setDisplayHomeAsUpEnabled(true);
//        }
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//               finish();
//            }
//        });
//    }
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

    }

    @Override
    public void setParentName(String parentName) {
        textViewParentName.setText("CG:"+parentName);

    }

    @Override
    public void setGender(String gender) {
        textViewGender.setText(gender);

    }

    @Override
    public void setAddress(String address) {
        textViewAddress.setText(address);

    }

    @Override
    public void setId(String id) {

    }

    @Override
    public void setProfileName(String fullName) {
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
}
