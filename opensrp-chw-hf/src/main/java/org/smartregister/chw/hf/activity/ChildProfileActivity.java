package org.smartregister.chw.hf.activity;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.core.activity.CoreChildHomeVisitActivity;
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
import org.smartregister.chw.hf.custom_view.FamilyMemberFloatingMenu;
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
        } else if (i == R.id.textview_edit) {
            openVisitHomeScreen(true);
        }
    }

    @Override
    protected void initializePresenter() {
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
        familyFloatingMenu = new FamilyMemberFloatingMenu(this);
        LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        familyFloatingMenu.setGravity(Gravity.BOTTOM | Gravity.END);
        addContentView(familyFloatingMenu, linearLayoutParams);
        prepareFab();
        fetchProfileData();
        presenter().fetchTasks();
    }

    @Override
    public void setFamilyHasNothingDue() {
        layoutFamilyHasRow.setVisibility(View.GONE);
    }

    @Override
    public void setFamilyHasServiceDue() {
        layoutFamilyHasRow.setVisibility(View.GONE);
    }

    @Override
    public void setFamilyHasServiceOverdue() {
        layoutFamilyHasRow.setVisibility(View.GONE);
    }

    @Override
    public void updateHasPhone(boolean hasPhone) {
        hideProgressBar();
        if (!hasPhone) {
            familyFloatingMenu.hideFab();
        }
    }

    @Override
    public void setClientTasks(Set<Task> taskList) {
        handler.postDelayed(() -> {
            if (referralRecyclerView != null && taskList.size() > 0) {
                RecyclerView.Adapter mAdapter = new ReferralCardViewAdapter(taskList, this, ((HfChildProfilePresenter) presenter()).getChildClient(), CoreConstants.REGISTERED_ACTIVITIES.CHILD_REGISTER_ACTIVITY);
                referralRecyclerView.setAdapter(mAdapter);
                referralRow.setVisibility(View.VISIBLE);

            }
        }, 100);

    }

    @Override
    public boolean onOptionsItemSelected(@NotNull MenuItem item) {
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
                ((HfChildProfilePresenter) presenter()).getDateOfBirth(), new LinkedHashMap<>(vaccine), CoreChildMedicalHistoryActivity.class);
    }

    private void openUpcomingServicePage() {
        CoreUpcomingServicesActivity.startUpcomingServicesActivity(this, ((CoreChildProfilePresenter) presenter()).getChildClient());
    }

    //TODO Child Refactor
    private void openVisitHomeScreen(boolean isEditMode) {
        CoreChildHomeVisitActivity.startMe(this, new MemberObject(((HfChildProfilePresenter) presenter()).getChildClient()), isEditMode);
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

    private void prepareFab() {
        familyFloatingMenu.fab.setOnClickListener(v -> FamilyCallDialogFragment.launchDialog(
                this, ((HfChildProfilePresenter) presenter).getFamilyId()));

    }
}
