package org.smartregister.chw.presenter;

import android.app.Activity;

import com.nerdstone.neatformcore.domain.model.NFormViewData;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.smartregister.chw.core.custom_views.NavigationMenu;
import org.smartregister.chw.model.LTFURecordFeedbackModel;
import org.smartregister.chw.referral.contract.BaseIssueReferralContract;
import org.smartregister.chw.referral.model.AbstractIssueReferralModel;
import org.smartregister.chw.referral.presenter.BaseIssueReferralPresenter;
import org.smartregister.chw.referral.util.DBConstants;
import org.smartregister.chw.util.Constants;
import org.smartregister.domain.Location;
import org.smartregister.repository.LocationRepository;

import java.util.HashMap;

import androidx.annotation.NonNull;


public class LTFURecordFeedbackPresenter extends BaseIssueReferralPresenter {
    private final String referralHf;

    public LTFURecordFeedbackPresenter(@NonNull String baseEntityID, String referralHf, @NonNull BaseIssueReferralContract.View view, @NonNull Class<? extends AbstractIssueReferralModel> viewModelClass, @NonNull BaseIssueReferralContract.Interactor interactor) {
        super(baseEntityID, view, viewModelClass, interactor);
        this.referralHf = referralHf;
    }


    @Override
    public Class<? extends AbstractIssueReferralModel> getViewModel() {
        return LTFURecordFeedbackModel.class;
    }

    @NonNull
    @Override
    public String getMainCondition() {
        return " " + Constants.TABLE_NAME.FAMILY_MEMBER + "." + DBConstants.Key.BASE_ENTITY_ID + " = '" + getBaseEntityID() + "'";
    }

    @NotNull
    @Override
    public String getMainTable() {
        return Constants.TABLE_NAME.FAMILY_MEMBER;
    }

    @Override
    public void onRegistrationSaved(boolean saveSuccessful) {
        super.onRegistrationSaved(saveSuccessful);
        NavigationMenu navigationMenu = NavigationMenu.getInstance((Activity) getView(),
                null, null);
        if (navigationMenu != null) {
            navigationMenu.refreshCount();
        }
    }

    @Override
    public void saveForm(@NonNull HashMap<String, NFormViewData> valuesHashMap, @NonNull JSONObject jsonObject) {
        //first close the referral task
        //if from the valuesHasMap followupStatus value is continuing_with_services then call save form super
        //else create an event that just sends the feedback to the server
        if (StringUtils.containsIgnoreCase(String.valueOf(valuesHashMap.get("followup_status").getValue()), "continuing_with_services")) {
            tagWithReferralDetails(valuesHashMap);
            super.saveForm(valuesHashMap, jsonObject);
        }

    }

    private void tagWithReferralDetails(HashMap<String, NFormViewData> valuesHashMap) {

        LocationRepository locationRepository = new LocationRepository();
        Location location = locationRepository.getLocationById(referralHf);

        valuesHashMap.put("problem", generateProblem());
        valuesHashMap.put("chw_referral_hf", generateChwReferralHf(referralHf, location.getProperties().getName()));
    }

    private NFormViewData generateProblem() {
        NFormViewData problem = new NFormViewData();
        NFormViewData problemValue = new NFormViewData();
        HashMap<String, NFormViewData> problemValueHash = new HashMap<>();


        HashMap<String, String> problemMetaData = new HashMap<>();
        problemMetaData.put("openmrs_entity", "concept");
        problemMetaData.put("openmrs_entity_id", "problem");

        HashMap<String, String> problemValueMetaData = new HashMap<>();
        problemValueMetaData.put("openmrs_entity", "concept");
        problemValueMetaData.put("openmrs_entity_id", "client_returning_to_services");

        problemValue.setMetadata(problemValueMetaData);
        problemValue.setValue("LTF Client Returning to Service");

        problemValueHash.put("client_returning_to_services", problemValue);

        problem.setMetadata(problemMetaData);
        problem.setValue(problemValueHash);
        problem.setVisible(true);
        problem.setType("Calculation");

        return problem;
    }

    private NFormViewData generateChwReferralHf(String referralHfCode, String referralHfName) {
        NFormViewData chwReferralHf = new NFormViewData();
        HashMap<String, String> chwReferralHfMetaData = new HashMap<>();

        NFormViewData chwReferralHfValue = new NFormViewData();
        HashMap<String, String> chwReferralHfValueMetaData = new HashMap<>();

        chwReferralHfValueMetaData.put("openmrs_entity", "concept");
        chwReferralHfValueMetaData.put("openmrs_entity_id", referralHfCode);

        chwReferralHfValue.setMetadata(chwReferralHfValueMetaData);

        chwReferralHfValue.setValue(referralHfName);

        chwReferralHfMetaData.put("openmrs_entity", "concept");
        chwReferralHfMetaData.put("openmrs_entity_id", "chw_referral_hf");

        chwReferralHf.setMetadata(chwReferralHfMetaData);
        chwReferralHf.setValue(chwReferralHfValue);
        chwReferralHf.setVisible(true);
        chwReferralHf.setType("Calculation");

        return chwReferralHf;
    }
}
