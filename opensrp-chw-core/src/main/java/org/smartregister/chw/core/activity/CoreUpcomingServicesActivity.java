package org.smartregister.chw.core.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.smartregister.chw.core.R;
import org.smartregister.chw.core.custom_views.UpcomingServicesFragmentView;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.util.Utils;
import org.smartregister.view.activity.SecuredActivity;

public class CoreUpcomingServicesActivity extends SecuredActivity {


    private ProgressBar progressBar;
    private String name;

    public static void startUpcomingServicesActivity(Activity activity, CommonPersonObjectClient childClient) {
        Intent intent = new Intent(activity, CoreUpcomingServicesActivity.class);
        intent.putExtra(CoreConstants.INTENT_KEY.CHILD_COMMON_PERSON, childClient);

        activity.startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    protected void onCreation() {
        setContentView(R.layout.activity_upcoming_services);
        progressBar = findViewById(R.id.progress_bar);
        CommonPersonObjectClient childClient = (CommonPersonObjectClient) getIntent().getSerializableExtra(CoreConstants.INTENT_KEY.CHILD_COMMON_PERSON);
        name = Utils.getValue(childClient.getColumnmaps(), "first_name", true) + " " +
                Utils.getValue(childClient.getColumnmaps(), "last_name", true);
        UpcomingServicesFragmentView upcomingServicesView = findViewById(R.id.upcomingServicesHolder);
        upcomingServicesView.setChildClient(this, childClient);

        setUpActionBar();
    }

    @Override
    protected void onResumption() {
        // upcomingServicesView.updateImmunizationState();
    }

    private void setUpActionBar() {
        Toolbar toolbar = findViewById(R.id.collapsing_toolbar);
        TextView textViewTitle = toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            final Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp);
            upArrow.setColorFilter(getResources().getColor(R.color.text_blue), PorterDuff.Mode.SRC_ATOP);
            actionBar.setHomeAsUpIndicator(upArrow);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
        if (TextUtils.isEmpty(name)) {
            textViewTitle.setVisibility(View.GONE);
        } else {
            textViewTitle.setText(getString(R.string.medical_title, name));
        }

    }

    public void progressBarVisibility(boolean flag) {
        progressBar.setVisibility(flag ? View.VISIBLE : View.GONE);
    }

}
