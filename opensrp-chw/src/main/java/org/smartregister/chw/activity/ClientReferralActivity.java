package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.adapter.ReferralTypeAdapter;
import org.smartregister.chw.contract.FacilityReferralContract;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.CoreJsonFormUtils;
import org.smartregister.chw.core.utils.CoreReferralUtils;
import org.smartregister.chw.model.ReferralTypeModel;
import org.smartregister.chw.util.Constants;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.family.util.Utils;
import org.smartregister.util.FormUtils;

import java.util.List;

import timber.log.Timber;

public class ClientReferralActivity extends AppCompatActivity implements FacilityReferralContract.View, View.OnClickListener {

    private ReferralTypeAdapter referralTypeAdapter;
    private FormUtils formUtils;
    private String baseEntityId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_referral);
        referralTypeAdapter = new ReferralTypeAdapter();
        referralTypeAdapter.setOnClickListener(this);
        setUpView();
    }

    @Override
    public void setUpView() {
        List<ReferralTypeModel> referralTypeModels = null;
        RecyclerView referralTypesRecyclerView = findViewById(R.id.referralTypeRecyclerView);

        ImageView closeImageView = findViewById(R.id.close);
        closeImageView.setOnClickListener(this);

        if (getIntent().getExtras() != null) {
            referralTypeModels = getIntent().getExtras().getParcelableArrayList(Constants.Referral.REFERRAL_TYPES);
            baseEntityId = getIntent().getStringExtra(Constants.ENTITY_ID);
        }

        referralTypeAdapter.setReferralTypes(referralTypeModels);
        referralTypesRecyclerView.setAdapter(referralTypeAdapter);
        referralTypesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.close) {
            finish();
        } else if (v.getTag() instanceof ReferralTypeAdapter.ReferralTypeViewHolder) {
            ReferralTypeAdapter.ReferralTypeViewHolder referralTypeViewHolder = (ReferralTypeAdapter.ReferralTypeViewHolder) v.getTag();
            ReferralTypeModel referralTypeModel = referralTypeAdapter.getReferralTypes().get(referralTypeViewHolder.getAdapterPosition());
            try {
                if (referralTypeModel.getFormName() == null) {
                    org.smartregister.util.Utils.showShortToast(this, getString(R.string.open_referral_form, referralTypeModel.getReferralType()));
                }
                startReferralForm(getFormUtils().getFormJson(referralTypeModel.getFormName()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void startReferralForm(JSONObject jsonObject) {
        startActivityForResult(CoreJsonFormUtils.getJsonIntent(this, jsonObject,
                Utils.metadata().familyMemberFormActivity), JsonFormUtils.REQUEST_CODE_GET_JSON);
    }

    @Override
    public FormUtils getFormUtils() throws Exception {
        if (this.formUtils == null) {
            this.formUtils = new FormUtils(this.getApplicationContext());
        }
        return this.formUtils;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) return;
        if (requestCode == JsonFormUtils.REQUEST_CODE_GET_JSON)
            try {
                String jsonString = data.getStringExtra(org.smartregister.family.util.Constants.JSON_FORM_EXTRA.JSON);
                JSONObject form = new JSONObject(jsonString);
                if (isReferralForm(form.getString(JsonFormUtils.ENCOUNTER_TYPE))) {
                    CoreReferralUtils.createReferralEvent(Utils.getAllSharedPreferences(),
                            jsonString, CoreConstants.TABLE_NAME.CHILD_REFERRAL, baseEntityId);
                    Utils.showToast(this, this.getString(R.string.referral_submitted));
                }
            } catch (Exception e) {
                Timber.e(e, "ClientReferralActivity --> onActivityResult");
            }
    }

    @Override
    public boolean isReferralForm(String encounterType) {
        switch (encounterType) {
            case CoreConstants.EventType.CHILD_REFERRAL:
            case CoreConstants.EventType.PNC_REFERRAL:
            case CoreConstants.EventType.ANC_REFERRAL:
                return true;
        }
        return false;
    }
}

