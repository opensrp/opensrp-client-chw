package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.fragment.app.Fragment;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.smartregister.chw.R;
import org.smartregister.chw.fragment.FindReportFragment;
import org.smartregister.chw.fragment.JobAidsDashboardFragment;
import org.smartregister.chw.fragment.RunReportFragment;
import org.smartregister.view.activity.SecuredActivity;

import timber.log.Timber;

public class FragmentBaseActivity extends SecuredActivity {
    public static final String DISPLAY_FRAGMENT = "DISPLAY_FRAGMENT";
    public static final String TITLE = "TITLE";

    public FragmentBaseActivity() {

    }

    public static void startMe(Activity activity, String fragmentName, String title) {
        Intent intent = new Intent(activity, FragmentBaseActivity.class);
        intent.putExtra(DISPLAY_FRAGMENT, fragmentName);
        intent.putExtra(TITLE, title);
        activity.startActivity(intent);
    }

    public static void startMe(Activity activity, String fragmentName, String title, Bundle bundle) {
        Intent intent = new Intent(activity, FragmentBaseActivity.class);
        intent.putExtras(bundle);
        intent.putExtra(DISPLAY_FRAGMENT, fragmentName);
        intent.putExtra(TITLE, title);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_base);
        Toolbar toolbar = findViewById(R.id.toolbar_top);
        TextView textView = findViewById(R.id.toolbar_title);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String title = bundle.getString(TITLE);
            if (StringUtils.isNotBlank(title)) {
                toolbar.setVisibility(View.VISIBLE);
                textView.setText(title);
            }

            String fragmentName = bundle.getString(DISPLAY_FRAGMENT);
            Fragment fragment = getRequestedFragment(fragmentName);
            if (fragment != null)
                switchToFragment(fragment);

        }

        onCreation();
    }

    private void switchToFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.content, fragment)
                .commit();
    }

    private @Nullable Fragment getRequestedFragment(@Nullable String name) {
        if (name == null || StringUtils.isBlank(name))
            return null;

        Fragment fragment;
        switch (name) {
            case FindReportFragment
                    .TAG:
                fragment = new FindReportFragment();
                break;
            case RunReportFragment
                    .TAG:
                fragment = new RunReportFragment();
                break;
            case JobAidsDashboardFragment
                    .TAG:
                fragment = new JobAidsDashboardFragment();
                break;
            default:
                fragment = null;
        }

        if (fragment != null)
            fragment.setArguments(getIntent().getExtras());

        return fragment;
    }

    @Override
    protected void onCreation() {
        Timber.v("Empty onCreation");
    }

    @Override
    protected void onResumption() {
        Timber.v("Empty onResumption");
    }
}
