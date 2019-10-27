package org.smartregister.brac.hnpp.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import org.opensrp.api.constants.Gender;
import org.smartregister.brac.hnpp.R;
import org.smartregister.brac.hnpp.utils.HnppConstants;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.core.activity.CoreChildProfileActivity;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.helper.ImageRenderHelper;

public class HnppCoreChildProfileActivity extends CoreChildProfileActivity {

    protected String houseHoldId = "";

    public static void startMe(Activity activity, String houseHoldId, boolean isComesFromFamily, MemberObject memberObject, Class<?> cls) {
        Intent intent = new Intent(activity, cls);
        intent.putExtra(HnppConstants.KEY.HOUSE_HOLD_ID,houseHoldId);
        intent.putExtra(CoreConstants.INTENT_KEY.IS_COMES_FROM_FAMILY, isComesFromFamily);
        intent.putExtra(org.smartregister.chw.anc.util.Constants.ANC_MEMBER_OBJECTS.MEMBER_PROFILE_OBJECT, memberObject);
        activity.startActivity(intent);
    }
    @Override
    protected void onCreation() {
        setContentView(org.smartregister.chw.core.R.layout.activity_child_profile);
        Toolbar toolbar = findViewById(org.smartregister.chw.core.R.id.collapsing_toolbar);
        textViewTitle = toolbar.findViewById(org.smartregister.chw.core.R.id.toolbar_title);
        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            memberObject = (MemberObject) getIntent().getSerializableExtra(org.smartregister.chw.anc.util.Constants.ANC_MEMBER_OBJECTS.MEMBER_PROFILE_OBJECT);
            childBaseEntityId = memberObject.getBaseEntityId();
            isComesFromFamily = getIntent().getBooleanExtra(CoreConstants.INTENT_KEY.IS_COMES_FROM_FAMILY, false);
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            final Drawable upArrow = getResources().getDrawable(org.smartregister.chw.core.R.drawable.ic_arrow_back_white_24dp);
            upArrow.setColorFilter(getResources().getColor(org.smartregister.chw.core.R.color.text_blue), PorterDuff.Mode.SRC_ATOP);
            actionBar.setHomeAsUpIndicator(upArrow);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        textViewTitle.setOnClickListener(v -> onBackPressed());
        appBarLayout = findViewById(org.smartregister.chw.core.R.id.collapsing_toolbar_appbarlayout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            appBarLayout.setOutlineProvider(null);
        }
        imageRenderHelper = new ImageRenderHelper(this);
        registerReceiver(mDateTimeChangedReceiver, getsIntentFilter());
        houseHoldId = getIntent().getStringExtra(HnppConstants.KEY.HOUSE_HOLD_ID);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(org.smartregister.chw.core.R.menu.other_member_menu, menu);
        menu.findItem(org.smartregister.chw.core.R.id.action_anc_registration).setVisible(false);
        menu.findItem(org.smartregister.chw.core.R.id.action_malaria_followup_visit).setVisible(false);
        menu.findItem(org.smartregister.chw.core.R.id.action_sick_child_follow_up).setVisible(false);
        menu.findItem(org.smartregister.chw.core.R.id.action_malaria_diagnosis).setVisible(false);
        return true;
    }

    @Override
    protected void updateTopBar() {
        if (gender.equalsIgnoreCase(getString(R.string.male))) {
            imageViewProfile.setBorderColor(getResources().getColor(org.smartregister.chw.core.R.color.light_blue));
        } else if (gender.equalsIgnoreCase(getString(R.string.female))) {
            imageViewProfile.setBorderColor(getResources().getColor(org.smartregister.chw.core.R.color.light_pink));
        }
    }
    @Override
    public void setId(String id) {
        textViewId.setText("ID:"+id.substring(id.length() - 9));
    }
}
