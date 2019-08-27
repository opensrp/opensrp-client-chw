package org.smartregister.chw.core.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.jeasy.rules.api.Rules;
import org.joda.time.DateTime;
import org.smartregister.chw.anc.activity.BaseAncMemberProfileActivity;
import org.smartregister.chw.anc.domain.Visit;
import org.smartregister.chw.anc.presenter.BaseAncMemberProfilePresenter;
import org.smartregister.chw.anc.util.Constants;
import org.smartregister.chw.anc.util.VisitUtils;
import org.smartregister.chw.core.R;
import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.contract.AncMemberProfileContract;
import org.smartregister.chw.core.interactor.CoreAncMemberProfileInteractor;
import org.smartregister.chw.core.interactor.CoreChildProfileInteractor;
import org.smartregister.chw.core.presenter.CoreAncMemberProfilePresenter;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.HomeVisitUtil;
import org.smartregister.chw.core.utils.VisitSummary;
import org.smartregister.domain.Task;

import java.util.Set;

public abstract class CoreAncMemberProfileActivity extends BaseAncMemberProfileActivity implements AncMemberProfileContract.View {

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (itemId == R.id.action_anc_member_registration) {
            startFormForEdit(R.string.edit_member_form_title,
                    CoreConstants.JSON_FORM.getFamilyMemberRegister());
            return true;
        } else if (itemId == R.id.action_anc_registration) {
            startFormForEdit(R.string.edit_anc_registration_form_title,
                    CoreConstants.JSON_FORM.getAncRegistration());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.anc_member_profile_menu, menu);
        return true;
    }

    @Override // to chw
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CoreConstants.ProfileActivityResults.CHANGE_COMPLETED && resultCode == Activity.RESULT_OK) {
            Intent intent = new Intent(CoreAncMemberProfileActivity.this, CoreAncRegisterActivity.class);
            intent.putExtras(getIntent().getExtras());
            startActivity(intent);
            finish();
        }
    }

    // to chw
    public void startFormForEdit(Integer title_resource, String formName) {
        //// TODO: 22/08/19
    }

    public CoreAncMemberProfilePresenter ancMemberProfilePresenter() {
        return new CoreAncMemberProfilePresenter(this, new CoreAncMemberProfileInteractor(this), MEMBER_OBJECT);
    }

    @Override
    protected void registerPresenter() {
        presenter = new BaseAncMemberProfilePresenter(this, new CoreAncMemberProfileInteractor(this), MEMBER_OBJECT);
    }

    @Override
    public void setupViews() {
        super.setupViews();
        Rules rules = CoreChwApplication.getInstance().getRulesEngineHelper().rules(CoreConstants.RULE_FILE.ANC_HOME_VISIT);

        VisitSummary visitSummary = HomeVisitUtil.getAncVisitStatus(this, rules, MEMBER_OBJECT.getLastMenstrualPeriod(), MEMBER_OBJECT.getLastContactVisit(), null, new DateTime(MEMBER_OBJECT.getDateCreated()).toLocalDate());
        String visitStatus = visitSummary.getVisitStatus();

        if (!visitStatus.equalsIgnoreCase(CoreConstants.VISIT_STATE.DUE) &&
                !visitStatus.equalsIgnoreCase(CoreChildProfileInteractor.VisitType.OVERDUE.name())) {
            textview_record_anc_visit.setVisibility(View.GONE);
            view_anc_record.setVisibility(View.GONE);
            textViewAncVisitNot.setVisibility(View.GONE);
        }

        Visit lastVisit = getVisit(Constants.EVENT_TYPE.ANC_HOME_VISIT);
        boolean within24Hours = VisitUtils.isVisitWithin24Hours(lastVisit);
        if (visitStatus.equalsIgnoreCase(CoreConstants.VISIT_STATE.OVERDUE) && !within24Hours) {
            textview_record_anc_visit.setBackgroundResource(R.drawable.record_btn_selector_overdue);
            layoutRecordView.setVisibility(View.VISIBLE);
            record_reccuringvisit_done_bar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
    }

    @Override
    public void openMedicalHistory() {
        CoreAncMedicalHistoryActivity.startMe(this, MEMBER_OBJECT);
    }

    @Override
    public abstract void openUpcomingService();

    @Override
    public abstract void openFamilyDueServices();

    @Override
    public abstract void setClientTasks(Set<Task> taskList);

}
