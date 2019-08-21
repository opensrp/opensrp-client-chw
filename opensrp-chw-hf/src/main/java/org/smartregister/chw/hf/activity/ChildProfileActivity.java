package org.smartregister.chw.hf.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.smartregister.chw.core.activity.CoreChildMedicalHistoryActivity;
import org.smartregister.chw.core.activity.CoreChildProfileActivity;
import org.smartregister.chw.core.activity.CoreUpcomingServicesActivity;
import org.smartregister.chw.core.custom_views.CoreFamilyMemberFloatingMenu;
import org.smartregister.chw.core.fragment.FamilyCallDialogFragment;
import org.smartregister.chw.core.listener.OnClickFloatingMenu;
import org.smartregister.chw.core.model.CoreChildProfileModel;
import org.smartregister.chw.core.presenter.CoreChildProfilePresenter;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.hf.R;
import org.smartregister.chw.hf.adapter.ReferralCardViewAdapter;
import org.smartregister.chw.hf.presenter.HfChildProfilePresenter;
import org.smartregister.domain.Task;
import org.smartregister.family.util.Constants;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class ChildProfileActivity extends CoreChildProfileActivity {
    public CoreFamilyMemberFloatingMenu familyFloatingMenu;
    public RelativeLayout referralRow;
    public RecyclerView referralRecyclerView;

    @Override
    protected void onCreation() {
        super.onCreation();
        initializePresenter();
        onClickFloatingMenu = getOnClickFloatingMenu(this, (HfChildProfilePresenter) presenter);
        setupViews();
        setUpToolbar();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int i = view.getId();
        if (i == R.id.last_visit_row) {
            openMedicalHistoryScreen();
        } else if (i == R.id.most_due_overdue_row) {
            openUpcomingServicePage();
        } else if (i == R.id.textview_record_visit || i == R.id.record_visit_done_bar) {
            openVisitHomeScreen(false);
        } else if (i == R.id.family_has_row) {
            openFamilyDueTab();
        } else if (i == R.id.textview_edit) {
            openVisitHomeScreen(true);
        }
    }

    @Override
    protected void initializePresenter() {
        childBaseEntityId = getIntent().getStringExtra(Constants.INTENT_KEY.BASE_ENTITY_ID);
        isComesFromFamily = getIntent().getBooleanExtra(CoreConstants.INTENT_KEY.IS_COMES_FROM_FAMILY, false);
        String familyName = getIntent().getStringExtra(Constants.INTENT_KEY.FAMILY_NAME);
        if (familyName == null) {
            familyName = "";
        }

        presenter = new HfChildProfilePresenter(this, new CoreChildProfileModel(familyName), childBaseEntityId);
    }

    @Override
    protected void setupViews() {
        super.setupViews();
        initializeTasksRecyclerView();
        View recordVisitPanel = findViewById(R.id.record_visit_panel);
        recordVisitPanel.setVisibility(View.GONE);
        familyFloatingMenu = new CoreFamilyMemberFloatingMenu(this);
        LinearLayout.LayoutParams linearLayoutParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
        familyFloatingMenu.setGravity(Gravity.BOTTOM | Gravity.END);
        addContentView(familyFloatingMenu, linearLayoutParams);
        familyFloatingMenu.setClickListener(onClickFloatingMenu);
        fetchProfileData();
        presenter().fetchTasks();
    }

    @Override
    public void updateHasPhone(boolean hasPhone) {
        hideProgressBar();
    }

    @Override
    public void setClientTasks(Set<Task> taskList) {
        if (referralRecyclerView != null && taskList.size() > 0) {
            referralRow.setVisibility(View.VISIBLE);
            RecyclerView.Adapter mAdapter = new ReferralCardViewAdapter(taskList, this, ((CoreChildProfilePresenter) presenter()).getChildClient());
            referralRecyclerView.setAdapter(mAdapter);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sick_child_follow_up:
                displayShortToast(R.string.clicked_sick_child);
                return true;
            case R.id.action_malaria_diagnosis:
                displayShortToast(R.string.clicked_malaria_diagnosis);
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.findItem(R.id.action_anc_registration).setVisible(false);
        menu.findItem(R.id.action_malaria_registration).setVisible(false);
        menu.findItem(R.id.action_remove_member).setVisible(false);
        menu.findItem(R.id.action_sick_child_follow_up).setVisible(true);
        menu.findItem(R.id.action_malaria_diagnosis).setVisible(true);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void openMedicalHistoryScreen() {
        Map<String, Date> vaccine = ((HfChildProfilePresenter) presenter()).getVaccineList();
        CoreChildMedicalHistoryActivity.startMedicalHistoryActivity(this, ((CoreChildProfilePresenter) presenter()).getChildClient(), patientName, lastVisitDay,
                ((HfChildProfilePresenter) presenter()).getDateOfBirth(), new LinkedHashMap<>(vaccine));
    }

    private void openUpcomingServicePage() {
        CoreUpcomingServicesActivity.startUpcomingServicesActivity(this, ((CoreChildProfilePresenter) presenter()).getChildClient());
    }

    //TODO Child Refactor
    private void openVisitHomeScreen(boolean isEditMode) {
    }

    private void openFamilyDueTab() {
        Intent intent = new Intent(this, FamilyProfileActivity.class);

        intent.putExtra(Constants.INTENT_KEY.FAMILY_BASE_ENTITY_ID, ((CoreChildProfilePresenter) presenter()).getFamilyId());
        intent.putExtra(Constants.INTENT_KEY.FAMILY_HEAD, ((CoreChildProfilePresenter) presenter()).getFamilyHeadID());
        intent.putExtra(Constants.INTENT_KEY.PRIMARY_CAREGIVER, ((CoreChildProfilePresenter) presenter()).getPrimaryCareGiverID());
        intent.putExtra(Constants.INTENT_KEY.FAMILY_NAME, ((CoreChildProfilePresenter) presenter()).getFamilyName());

        intent.putExtra(CoreConstants.INTENT_KEY.SERVICE_DUE, true);
        startActivity(intent);
    }

    public OnClickFloatingMenu getOnClickFloatingMenu(final Activity activity, final HfChildProfilePresenter presenter) {
        return viewId -> {
            switch (viewId) {
                case R.id.call_layout:
                    FamilyCallDialogFragment.launchDialog(activity, presenter.getFamilyId());
                    break;
                case R.id.refer_to_facility_fab:
                    Toast.makeText(activity, "Refer to facility", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        };
    }

    private void initializeTasksRecyclerView() {
        referralRecyclerView = findViewById(R.id.referral_card_recycler_view);
        referralRow = findViewById(R.id.referal_row);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        referralRecyclerView.setLayoutManager(layoutManager);
    }
}
