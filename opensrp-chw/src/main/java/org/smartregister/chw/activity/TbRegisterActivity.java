package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.LabelVisibilityMode;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.core.custom_views.NavigationMenu;
import org.smartregister.chw.core.job.HomeVisitServiceJob;
import org.smartregister.chw.core.job.VaccineRecurringServiceJob;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.fragment.FollowupRegisterFragment;
import org.smartregister.chw.fragment.TbRegisterFragment;
import org.smartregister.chw.tb.activity.BaseTbRegisterActivity;
import org.smartregister.chw.tb.activity.BaseTbRegistrationFormsActivity;
import org.smartregister.chw.tb.fragment.BaseTbRegisterFragment;
import org.smartregister.chw.util.Constants;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.helper.BottomNavigationHelper;
import org.smartregister.job.ImageUploadServiceJob;
import org.smartregister.job.PullUniqueIdsServiceJob;
import org.smartregister.job.SyncServiceJob;
import org.smartregister.job.SyncTaskServiceJob;
import org.smartregister.listener.BottomNavigationListener;

import java.util.List;

import static org.smartregister.chw.core.utils.FormUtils.getFormUtils;

public class TbRegisterActivity extends BaseTbRegisterActivity {

    public static void startTbRegistrationActivity(Activity activity, String baseEntityID) {
        Intent intent = new Intent(activity, BaseTbRegistrationFormsActivity.class);
        intent.putExtra(org.smartregister.chw.tb.util.Constants.ActivityPayload.BASE_ENTITY_ID, baseEntityID);
        intent.putExtra(org.smartregister.chw.tb.util.Constants.ActivityPayload.JSON_FORM, getFormUtils().getFormJsonFromRepositoryOrAssets(Constants.JSON_FORM.getTbRegistration()).toString());
        intent.putExtra(org.smartregister.chw.tb.util.Constants.ActivityPayload.ACTION, org.smartregister.chw.tb.util.Constants.ActivityPayloadType.REGISTRATION);
        intent.putExtra(org.smartregister.chw.tb.util.Constants.ActivityPayload.USE_DEFAULT_NEAT_FORM_LAYOUT,false);

        activity.startActivityForResult(intent, JsonFormUtils.REQUEST_CODE_GET_JSON);
    }

    @NotNull
    @Override
    protected Fragment[] getOtherFragments() {
        Fragment fg = new FollowupRegisterFragment();
        return new Fragment[]{fg};
    }

    @NotNull
    @Override
    protected BaseTbRegisterFragment getRegisterFragment() {
        return new TbRegisterFragment();
    }

    @Override
    public List<String> getViewIdentifiers() {
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NavigationMenu.getInstance(this, null, null);
    }

    @Override
    protected void registerBottomNavigation() {
        bottomNavigationHelper = new BottomNavigationHelper();
        bottomNavigationView = findViewById(org.smartregister.R.id.bottom_navigation);

        if (bottomNavigationView != null) {
            bottomNavigationView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
            bottomNavigationView.getMenu().removeItem(org.smartregister.R.id.action_clients);
            bottomNavigationView.getMenu().removeItem(org.smartregister.chw.tb.R.id.action_register);
            bottomNavigationView.getMenu().removeItem(org.smartregister.R.id.action_search);
            bottomNavigationView.getMenu().removeItem(org.smartregister.R.id.action_library);

            bottomNavigationView.inflateMenu(getMenuResource());
            bottomNavigationHelper.disableShiftMode(bottomNavigationView);

            BottomNavigationListener tbBottomNavigationListener = getBottomNavigation(this);
            bottomNavigationView.setOnNavigationItemSelectedListener(tbBottomNavigationListener);

        }
    }

    @Override
    public void startFormActivity(JSONObject jsonForm) {
        //Implement
    }

    private void startRegisterActivity() {
        HomeVisitServiceJob.scheduleJobImmediately(HomeVisitServiceJob.TAG);
        VaccineRecurringServiceJob.scheduleJobImmediately(VaccineRecurringServiceJob.TAG);
        ImageUploadServiceJob.scheduleJobImmediately(ImageUploadServiceJob.TAG);
        SyncServiceJob.scheduleJobImmediately(SyncServiceJob.TAG);
        PullUniqueIdsServiceJob.scheduleJobImmediately(PullUniqueIdsServiceJob.TAG);
        HomeVisitServiceJob.scheduleJobImmediately(HomeVisitServiceJob.TAG);
        SyncTaskServiceJob.scheduleJobImmediately(SyncTaskServiceJob.TAG);
        Intent intent = new Intent(this, TbRegisterActivity.class);
        this.startActivity(intent);
        this.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
        this.finish();
    }

    @Override
    protected void onResumption() {
        super.onResumption();
        NavigationMenu menu = NavigationMenu.getInstance(this, null, null);
        if (menu != null) {
            menu.getNavigationAdapter().setSelectedView(Constants.DrawerMenu.TB_CLIENTS);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == org.smartregister.chw.malaria.util.Constants.REQUEST_CODE_GET_JSON) {
            startRegisterActivity();
        } else {
            finish();
        }

    }
}
 