package com.opensrp.chw.hf.activity;

import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.opensrp.chw.core.activity.CoreChildProfileActivity;
import com.opensrp.chw.core.custom_views.CoreFamilyMemberFloatingMenu;
import com.opensrp.chw.core.model.CoreChildProfileModel;
import com.opensrp.chw.core.utils.CoreConstants;
import com.opensrp.chw.hf.presenter.ChildProfilePresenter;
import com.opensrp.hf.R;

import org.smartregister.family.util.Constants;

public class ChildProfileActivity extends CoreChildProfileActivity {
    public CoreFamilyMemberFloatingMenu familyFloatingMenu;

    @Override
    protected void onCreation() {
        super.onCreation();
        initializePresenter();
        setupViews();
        setUpToolbar();
        registerReceiver(mDateTimeChangedReceiver, sIntentFilter);
    }

    @Override
    protected void setupViews() {
        super.setupViews();
        familyFloatingMenu = new CoreFamilyMemberFloatingMenu(this);
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
    protected void initializePresenter() {
        childBaseEntityId = getIntent().getStringExtra(Constants.INTENT_KEY.BASE_ENTITY_ID);
        isComesFromFamily = getIntent().getBooleanExtra(CoreConstants.INTENT_KEY.IS_COMES_FROM_FAMILY, false);
        String familyName = getIntent().getStringExtra(Constants.INTENT_KEY.FAMILY_NAME);
        if (familyName == null) {
            familyName = "";
        }

        presenter = new ChildProfilePresenter(this, new CoreChildProfileModel(familyName), childBaseEntityId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(com.opensrp.chw.core.R.menu.other_member_menu, menu);
        menu.findItem(com.opensrp.chw.core.R.id.action_anc_registration).setVisible(false);
        menu.findItem(com.opensrp.chw.core.R.id.action_malaria_registration).setVisible(false);
        menu.findItem(com.opensrp.chw.core.R.id.action_remove_member).setVisible(false);
        menu.findItem(com.opensrp.chw.core.R.id.action_sick_child_follow_up).setVisible(true);
        menu.findItem(com.opensrp.chw.core.R.id.action_malaria_diagnosis).setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_malaria_registration:
                return true;
            case R.id.action_remove_member:
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
    }
}
