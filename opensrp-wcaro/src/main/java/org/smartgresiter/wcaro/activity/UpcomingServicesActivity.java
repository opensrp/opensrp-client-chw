package org.smartgresiter.wcaro.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import org.smartgresiter.wcaro.R;
import org.smartgresiter.wcaro.custom_view.UpcomingServicesFragmentView;
import org.smartgresiter.wcaro.util.Constants;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.view.activity.SecuredActivity;

import static org.smartregister.util.Utils.getValue;

public class UpcomingServicesActivity extends SecuredActivity {


    private UpcomingServicesFragmentView upcomingServicesView;
    private TextView textViewTitle;
    private String name;

    public static void startUpcomingServicesActivity(Activity activity, CommonPersonObjectClient childClient) {
        Intent intent = new Intent(activity, UpcomingServicesActivity.class);
        intent.putExtra(Constants.INTENT_KEY.CHILD_COMMON_PERSON, childClient);

        activity.startActivity(intent);
    }

    @Override
    protected void onCreation() {
        setContentView(R.layout.activity_upcoming_services);
        CommonPersonObjectClient childClient = (CommonPersonObjectClient) getIntent().getSerializableExtra(Constants.INTENT_KEY.CHILD_COMMON_PERSON);
        name = getValue(childClient.getColumnmaps(), "first_name", true) + " " +
                getValue(childClient.getColumnmaps(), "last_name", true) + "'s profile";
        upcomingServicesView = (UpcomingServicesFragmentView) findViewById(R.id.upcomingServicesHolder);
        upcomingServicesView.setActivity(this);
        upcomingServicesView.setChildClient(childClient);

        setUpActionBar();


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       return false;
    }

    @Override
    protected void onResumption() {
        upcomingServicesView.updateImmunizationState();
    }

    private void setUpActionBar() {
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
        if (TextUtils.isEmpty(name)) {
            textViewTitle.setVisibility(View.GONE);
        } else {
            textViewTitle.setText(getString(R.string.medical_title, name));
        }

    }

}
