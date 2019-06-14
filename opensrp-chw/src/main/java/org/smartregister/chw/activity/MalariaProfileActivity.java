package org.smartregister.chw.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import org.smartregister.chw.R;
import org.smartregister.chw.contract.FamilyOtherMemberProfileExtendedContract;
import org.smartregister.chw.contract.MalariaProfileContract;
import org.smartregister.chw.listener.OnClickFloatingMenu;
import org.smartregister.chw.model.MalariaProfileModel;
import org.smartregister.chw.presenter.FamilyOtherMemberActivityPresenter;
import org.smartregister.chw.presenter.MalariaProfilePresenter;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.model.BaseFamilyOtherMemberProfileActivityModel;
import org.smartregister.family.util.Constants;
import org.smartregister.helper.ImageRenderHelper;
import org.smartregister.view.activity.BaseProfileActivity;

public class MalariaProfileActivity extends BaseProfileActivity {

    private String baseEntityId;
    private String familyBaseEntityId;
    private String familyHead;
    private String primaryCaregiver;
    private String villageTown;
    private String familyName;
    private String PhoneNumber;
    private CommonPersonObjectClient commonPersonObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.malaria_profile_activity);

        Toolbar toolbar = findViewById(R.id.family_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("");
        }

        appBarLayout = findViewById(R.id.toolbar_appbarlayout);

        imageRenderHelper = new ImageRenderHelper(this);

        initializePresenter();

        setupViews();
    }

    @Override
    protected void initializePresenter() {
        commonPersonObject = (CommonPersonObjectClient) getIntent().getSerializableExtra(org.smartregister.chw.util.Constants.INTENT_KEY.CHILD_COMMON_PERSON);
        familyBaseEntityId = getIntent().getStringExtra(Constants.INTENT_KEY.FAMILY_BASE_ENTITY_ID);
        baseEntityId = getIntent().getStringExtra(Constants.INTENT_KEY.BASE_ENTITY_ID);
        familyHead = getIntent().getStringExtra(Constants.INTENT_KEY.FAMILY_HEAD);
        primaryCaregiver = getIntent().getStringExtra(Constants.INTENT_KEY.PRIMARY_CAREGIVER);
        villageTown = getIntent().getStringExtra(Constants.INTENT_KEY.VILLAGE_TOWN);
        familyName = getIntent().getStringExtra(Constants.INTENT_KEY.FAMILY_NAME);
        PhoneNumber = commonPersonObject.getColumnmaps().get(org.smartregister.chw.util.Constants.JsonAssets.FAMILY_MEMBER.PHONE_NUMBER);
        presenter = new MalariaProfilePresenter((MalariaProfileContract.View) this, new MalariaProfileModel(familyName), baseEntityId);

    }

    @Override
    protected ViewPager setupViewPager(ViewPager viewPager) {
        return null;
    }

    @Override
    protected void fetchProfileData() {

    }
}
