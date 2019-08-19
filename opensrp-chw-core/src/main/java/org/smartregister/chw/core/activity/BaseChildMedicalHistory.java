package org.smartregister.chw.core.activity;


import android.app.Activity;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import org.smartregister.chw.core.R;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.Utils;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.view.activity.SecuredActivity;

import java.util.Date;
import java.util.Map;

import timber.log.Timber;

public abstract class BaseChildMedicalHistory extends SecuredActivity {

    protected Map<String, Date> vaccineList;
    protected String dateOfBirth;
    protected CommonPersonObjectClient childClient;
    private TextView textViewTitle, textViewLastVisit;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    protected void onCreation() {
        setContentView(R.layout.activity_medical_history);
        setUpActionBar();
        parseBundleANdUpdateTopView();
    }

    private void setUpActionBar() {
        Toolbar toolbar = findViewById(R.id.collapsing_toolbar);
        textViewTitle = toolbar.findViewById(R.id.toolbar_title);
        textViewLastVisit = findViewById(R.id.home_visit_date);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            final Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp);
            upArrow.setColorFilter(getResources().getColor(R.color.text_blue), PorterDuff.Mode.SRC_ATOP);
            actionBar.setHomeAsUpIndicator(upArrow);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

    }

    private void parseBundleANdUpdateTopView() {
        childClient = (CommonPersonObjectClient) getIntent().getSerializableExtra(CoreConstants.INTENT_KEY.CHILD_COMMON_PERSON);
        String name = getIntent().getStringExtra(CoreConstants.INTENT_KEY.CHILD_NAME);
        String lastVisitDays = getIntent().getStringExtra(CoreConstants.INTENT_KEY.CHILD_LAST_VISIT_DAYS);
        dateOfBirth = getIntent().getStringExtra(CoreConstants.INTENT_KEY.CHILD_DATE_OF_BIRTH);
        vaccineList = (Map<String, Date>) getIntent().getSerializableExtra(CoreConstants.INTENT_KEY.CHILD_VACCINE_LIST);
        if (TextUtils.isEmpty(name)) {
            textViewTitle.setVisibility(View.GONE);
        } else {
            textViewTitle.setText(getString(R.string.medical_title, name));
        }
        textViewLastVisit.setText(getString(R.string.medical_last_visit, Utils.firstCharacterUppercase(lastVisitDays)));
        onViewCreated(this);

    }

    public abstract void onViewCreated(Activity activity);

    @Override
    protected void onResumption() {
        Timber.v("onResumption");

    }
}
