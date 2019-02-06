package org.smartgresiter.wcaro.activity;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.smartgresiter.wcaro.R;
import org.smartgresiter.wcaro.contract.ChildRemoveContract;
import org.smartgresiter.wcaro.fragment.AddMemberFragment;
import org.smartgresiter.wcaro.fragment.FamilyRemoveMemberConfrimDialog;
import org.smartgresiter.wcaro.presenter.ChildRemovePresenter;
import org.smartgresiter.wcaro.util.ChildDBConstants;
import org.smartgresiter.wcaro.util.Constants;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.util.DBConstants;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.family.util.Utils;
import org.smartregister.view.activity.SecuredActivity;

public class ChildRemoveActivity extends SecuredActivity implements ChildRemoveContract.View {
    private CommonPersonObjectClient pc;
    private String familyBaseEntityId;

    @Override
    protected void onCreation() {
        setContentView(R.layout.activity_family_remove_member);
        findViewById(R.id.detail_toolbar).setVisibility(View.GONE);
        findViewById(R.id.close).setVisibility(View.GONE);
        findViewById(R.id.tvDetails).setVisibility(View.GONE);
        findViewById(R.id.flFrame).setVisibility(View.GONE);
        pc = (CommonPersonObjectClient) getIntent().getSerializableExtra(Constants.INTENT_KEY.CHILD_COMMON_PERSON);
        openDeleteDialog();
    }

    private void openDeleteDialog() {
        String name = String.format("%s %s %s", pc.getColumnmaps().get(DBConstants.KEY.FIRST_NAME),
                pc.getColumnmaps().get(DBConstants.KEY.MIDDLE_NAME) == null ? "" : pc.getColumnmaps().get(DBConstants.KEY.MIDDLE_NAME),
                pc.getColumnmaps().get(DBConstants.KEY.LAST_NAME) == null ? "" : pc.getColumnmaps().get(DBConstants.KEY.LAST_NAME));

        String dod = pc.getColumnmaps().get(DBConstants.KEY.DOD);
        familyBaseEntityId = pc.getColumnmaps().get(ChildDBConstants.KEY.RELATIONAL_ID);
        if (StringUtils.isBlank(dod)) {
            FamilyRemoveMemberConfrimDialog dialog = FamilyRemoveMemberConfrimDialog.newInstance(
                    String.format("Are you sure you want to remove %s's record? This will remove their entire health record from your device. This action cannot be undone.", name)
            );
            dialog.setContext(this);
            dialog.show(getSupportFragmentManager(), AddMemberFragment.DIALOG_TAG);
            dialog.setOnRemove(new Runnable() {
                @Override
                public void run() {
                    getPresenter().removeMember(pc);
                }
            });
            dialog.setOnRemoveActivity(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            });
        }
    }

    @Override
    protected void onResumption() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            try {
                String jsonString = data.getStringExtra(org.smartregister.family.util.Constants.JSON_FORM_EXTRA.JSON);
                Log.d("JSONResult", jsonString);

                JSONObject form = new JSONObject(jsonString);
                getPresenter().processRemoveForm(form);
            } catch (Exception e) {
            }
        } else {
            finish();
        }
    }

    @Override
    public ChildRemoveContract.Presenter getPresenter() {
        return new ChildRemovePresenter(familyBaseEntityId, this);
    }

    @Override
    public void startJsonActivity(JSONObject jsonObject) {
        Intent intent = new Intent(this, Utils.metadata().familyMemberFormActivity);
        intent.putExtra(org.smartregister.family.util.Constants.JSON_FORM_EXTRA.JSON, jsonObject.toString());

        Form form = new Form();
        form.setActionBarBackground(org.smartregister.family.R.color.family_actionbar);
        form.setWizard(false);
        form.setSaveLabel("Remove");
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);

        startActivityForResult(intent, JsonFormUtils.REQUEST_CODE_GET_JSON);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void onChildRemove() {
        Intent intent = getIntent();
        setResult(RESULT_OK, intent);
        finish();
    }
}
