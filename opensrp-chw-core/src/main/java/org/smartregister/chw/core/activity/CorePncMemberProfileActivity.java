package org.smartregister.chw.core.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;
import org.smartregister.chw.anc.util.NCUtils;
import org.smartregister.chw.core.R;
import org.smartregister.chw.core.interactor.CoreChildProfileInteractor;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.CoreJsonFormUtils;
import org.smartregister.chw.pnc.activity.BasePncMemberProfileActivity;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.domain.AlertStatus;
import org.smartregister.family.util.JsonFormUtils;

import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

public abstract class CorePncMemberProfileActivity extends BasePncMemberProfileActivity {

    private ImageView imageViewCross;

    private PncMemberProfileInteractor basePncMemberProfileInteractor = new PncMemberProfileInteractor(this);

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (i == R.id.action_pnc_member_registration) {
            JSONObject form = CoreJsonFormUtils.getAncPncForm(R.string.edit_member_form_title, CoreConstants.JSON_FORM.getFamilyMemberRegister(), MEMBER_OBJECT, this);
            startFormForEdit(form);
            return true;
        } else if (i == R.id.action_pnc_registration) {
            CoreChildProfileInteractor childProfileInteractor = new CoreChildProfileInteractor();

            List<CommonPersonObjectClient> children = basePncMemberProfileInteractor.pncChildrenUnder29Days(MEMBER_OBJECT.getBaseEntityId());
            if (!children.isEmpty()) {
                CommonPersonObjectClient client = children.get(0);
                JSONObject childEnrollmentForm = childProfileInteractor.getAutoPopulatedJsonEditFormString(CoreConstants.JSON_FORM.getChildRegister(), getString(R.string.edit_child_form_title), this, client);

                startFormForEdit(org.smartregister.chw.anc.util.JsonFormUtils.setRequiredFieldsToFalseForPncChild(childEnrollmentForm, MEMBER_OBJECT.getFamilyBaseEntityId(),
                        MEMBER_OBJECT.getBaseEntityId()));
            }
            return true;
        } else if (i == R.id.action__pnc_remove_member) {

            IndividualProfileRemoveActivity.startIndividualProfileActivity(this, clientObject(), MEMBER_OBJECT.getFamilyBaseEntityId(), MEMBER_OBJECT.getFamilyHead(), MEMBER_OBJECT.getPrimaryCareGiver(), PncRegisterActivity.class.getCanonicalName());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pnc_member_profile_menu, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) return;
        if(requestCode == CoreConstants.ProfileActivityResults.CHANGE_COMPLETED){
            Intent intent = new Intent(this, getPncRegisterActivityClass());
            intent.putExtras(getIntent().getExtras());
            startActivity(intent);
            finish();
        }
    }

    protected abstract Class<? extends CorePncRegisterActivity> getPncRegisterActivityClass();

    public void startFormForEdit(JSONObject form) {
        try {
            startActivityForResult(CoreJsonFormUtils.getAncPncStartFormIntent(form, this), JsonFormUtils.REQUEST_CODE_GET_JSON);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private CommonPersonObjectClient clientObject() {
        CommonRepository commonRepository = org.smartregister.chw.core.utils.Utils.context()
                .commonrepository(org.smartregister.chw.core.utils.Utils.metadata().familyMemberRegister.tableName);
        final CommonPersonObject commonPersonObject = commonRepository.findByBaseEntityId(MEMBER_OBJECT.getBaseEntityId());
        final CommonPersonObjectClient client =
                new CommonPersonObjectClient(commonPersonObject.getCaseId(), commonPersonObject.getDetails(), "");
        client.setColumnmaps(commonPersonObject.getColumnmaps());
        return client;
    }

    @Override
    public void setupViews() {
        super.setupViews();
        imageViewCross = findViewById(R.id.tick_image);
        imageViewCross.setOnClickListener(this);
    }



    @Override
    protected void setUpEditViews(boolean enable, boolean within24Hours, Long longDate) {

    }

    private void setEditViews(boolean enable, boolean within24Hours, Long longDate) {
        if (enable) {
            if (within24Hours) {
                Calendar cal = Calendar.getInstance();
                int offset = cal.getTimeZone().getOffset(cal.getTimeInMillis());
                new Date(longDate - (long) offset);
                String pncDay = basePncMemberProfileInteractor.getPncDay(MEMBER_OBJECT.getBaseEntityId());
                layoutNotRecordView.setVisibility(View.VISIBLE);
                tvEdit.setVisibility(View.VISIBLE);
                textViewUndo.setVisibility(View.GONE);
                textViewNotVisitMonth.setVisibility(View.VISIBLE);
                textViewNotVisitMonth.setText(MessageFormat.format(getContext().getString(R.string.pnc_visit_done), pncDay));
                imageViewCross.setImageResource(R.drawable.activityrow_visited);
                textview_record_visit.setVisibility(View.GONE);
            } else {
                layoutNotRecordView.setVisibility(View.GONE);

            }
        } else {
            layoutNotRecordView.setVisibility(View.GONE);
        }

    }

    @Override
    public void openFamilyDueServices() {
        Intent intent = new Intent(this, getFamilyProfileActivityClass());

        intent.putExtra(org.smartregister.family.util.Constants.INTENT_KEY.FAMILY_BASE_ENTITY_ID, MEMBER_OBJECT.getFamilyBaseEntityId());
        intent.putExtra(org.smartregister.family.util.Constants.INTENT_KEY.FAMILY_HEAD, MEMBER_OBJECT.getFamilyHead());
        intent.putExtra(org.smartregister.family.util.Constants.INTENT_KEY.PRIMARY_CAREGIVER, MEMBER_OBJECT.getPrimaryCareGiver());
        intent.putExtra(org.smartregister.family.util.Constants.INTENT_KEY.FAMILY_NAME, MEMBER_OBJECT.getFamilyName());

        intent.putExtra(CoreConstants.INTENT_KEY.SERVICE_DUE, true);
        startActivity(intent);
    }

    protected abstract Class<? extends CoreFamilyProfileActivity> getFamilyProfileActivityClass();

    @Override
    public void updateVisitNotDone(long value) {
        return;
    }

    @Override
    public void setFamilyStatus(AlertStatus status) {
        TextView tvFamilyStatus;
        tvFamilyStatus = findViewById(org.smartregister.chw.opensrp_chw_anc.R.id.textview_family_has);

        view_family_row.setVisibility(View.VISIBLE);
        rlFamilyServicesDue.setVisibility(View.VISIBLE);

        if (status == AlertStatus.complete) {
            tvFamilyStatus.setText(getString(org.smartregister.chw.opensrp_chw_anc.R.string.family_has_nothing_due));
        } else if (status == AlertStatus.normal) {
            tvFamilyStatus.setText(getString(org.smartregister.chw.opensrp_chw_anc.R.string.family_has_services_due));
        } else if (status == AlertStatus.urgent) {
            tvFamilyStatus.setText(NCUtils.fromHtml(getString(org.smartregister.chw.opensrp_chw_anc.R.string.family_has_service_overdue)));
        }
    }
}