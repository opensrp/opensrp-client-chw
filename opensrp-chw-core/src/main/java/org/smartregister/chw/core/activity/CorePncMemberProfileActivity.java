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
import org.smartregister.chw.core.interactor.CorePncMemberProfileInteractor;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.CoreJsonFormUtils;
import org.smartregister.chw.pnc.activity.BasePncMemberProfileActivity;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.AlertStatus;
import org.smartregister.family.util.JsonFormUtils;

import java.util.List;

import timber.log.Timber;

public abstract class CorePncMemberProfileActivity extends BasePncMemberProfileActivity {

    protected ImageView imageViewCross;
    protected boolean hasDueServices = false;
    protected CorePncMemberProfileInteractor pncMemberProfileInteractor = getPncMemberProfileInteractor();

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (i == R.id.action_pnc_member_registration) {
            JSONObject form = CoreJsonFormUtils.getAncPncForm(R.string.edit_member_form_title, CoreConstants.JSON_FORM.getFamilyMemberRegister(), memberObject, this);
            startFormForEdit(form);
            return true;
        } else if (i == R.id.action_pnc_registration) {
            CoreChildProfileInteractor childProfileInteractor = new CoreChildProfileInteractor();

            List<CommonPersonObjectClient> children = pncMemberProfileInteractor.pncChildrenUnder29Days(memberObject.getBaseEntityId());
            if (!children.isEmpty()) {
                CommonPersonObjectClient client = children.get(0);
                JSONObject childEnrollmentForm = childProfileInteractor.getAutoPopulatedJsonEditFormString(CoreConstants.JSON_FORM.getChildRegister(), getString(R.string.edit_child_form_title), this, client);

                startFormForEdit(org.smartregister.chw.anc.util.JsonFormUtils.setRequiredFieldsToFalseForPncChild(childEnrollmentForm, memberObject.getFamilyBaseEntityId(),
                        memberObject.getBaseEntityId()));
            }
            return true;
        } else if (i == R.id.action__pnc_remove_member) {
            removePncMember();
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
        if (requestCode == CoreConstants.ProfileActivityResults.CHANGE_COMPLETED) {
            Intent intent = new Intent(this, getPncRegisterActivityClass());
            intent.putExtras(getIntent().getExtras());
            startActivity(intent);
            finish();
        }
    }

    public void startFormForEdit(JSONObject form) {
        try {
            startActivityForResult(CoreJsonFormUtils.getAncPncStartFormIntent(form, this), JsonFormUtils.REQUEST_CODE_GET_JSON);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    @Override
    public void setupViews() {
        super.setupViews();
        imageViewCross = findViewById(R.id.tick_image);
        imageViewCross.setOnClickListener(this);
    }

    @Override
    public void openFamilyDueServices() {
        Intent intent = new Intent(this, getFamilyProfileActivityClass());

        intent.putExtra(org.smartregister.family.util.Constants.INTENT_KEY.FAMILY_BASE_ENTITY_ID, memberObject.getFamilyBaseEntityId());
        intent.putExtra(org.smartregister.family.util.Constants.INTENT_KEY.FAMILY_HEAD, memberObject.getFamilyHead());
        intent.putExtra(org.smartregister.family.util.Constants.INTENT_KEY.PRIMARY_CAREGIVER, memberObject.getPrimaryCareGiver());
        intent.putExtra(org.smartregister.family.util.Constants.INTENT_KEY.FAMILY_NAME, memberObject.getFamilyName());

        intent.putExtra(CoreConstants.INTENT_KEY.SERVICE_DUE, hasDueServices);
        startActivity(intent);
    }

    @Override
    public void updateVisitNotDone(long value) {
        //Overridden
    }

    @Override
    public void setFamilyStatus(AlertStatus status) {
        TextView tvFamilyStatus;
        tvFamilyStatus = findViewById(org.smartregister.chw.opensrp_chw_anc.R.id.textview_family_has);

        view_family_row.setVisibility(View.VISIBLE);
        rlFamilyServicesDue.setVisibility(View.VISIBLE);

        if (status == AlertStatus.complete) {
            hasDueServices = false;
            tvFamilyStatus.setText(getString(org.smartregister.chw.opensrp_chw_anc.R.string.family_has_nothing_due));
        } else if (status == AlertStatus.normal) {
            hasDueServices = true;
            tvFamilyStatus.setText(getString(org.smartregister.chw.opensrp_chw_anc.R.string.family_has_services_due));
        } else if (status == AlertStatus.urgent) {
            hasDueServices = true;
            tvFamilyStatus.setText(NCUtils.fromHtml(getString(org.smartregister.chw.opensrp_chw_anc.R.string.family_has_service_overdue)));
        }
    }

    protected abstract Class<? extends CoreFamilyProfileActivity> getFamilyProfileActivityClass();

    protected abstract CorePncMemberProfileInteractor getPncMemberProfileInteractor();

    protected abstract void removePncMember();

    protected abstract Class<? extends CorePncRegisterActivity> getPncRegisterActivityClass();
}