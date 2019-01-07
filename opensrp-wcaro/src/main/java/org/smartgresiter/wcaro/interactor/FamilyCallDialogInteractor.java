package org.smartgresiter.wcaro.interactor;

import android.support.annotation.VisibleForTesting;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartgresiter.wcaro.contract.FamilyCallDialogContract;
import org.smartgresiter.wcaro.model.FamilyCallDialogModel;
import org.smartgresiter.wcaro.util.Constants;
import org.smartregister.family.FamilyLibrary;
import org.smartregister.family.util.AppExecutors;
import org.smartregister.repository.EventClientRepository;

public class FamilyCallDialogInteractor implements FamilyCallDialogContract.Interactor {

    private AppExecutors appExecutors;
    String familyBaseEntityId;


    @VisibleForTesting
    FamilyCallDialogInteractor(AppExecutors appExecutors, String familyBaseEntityId) {
        this.appExecutors = appExecutors;
        this.familyBaseEntityId = familyBaseEntityId;
    }

    public FamilyCallDialogInteractor(String familyBaseEntityId) {
        this(new AppExecutors(), familyBaseEntityId);
    }

    @Override
    public void getHeadOfFamily(final FamilyCallDialogContract.Presenter presenter) {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                //TODO  replace this with actual query info for the HOF
                EventClientRepository eventClientRepository = FamilyLibrary.getInstance().context().getEventClientRepository();

                JSONObject familyJSON = eventClientRepository.getClientByBaseEntityId(familyBaseEntityId);

                if (familyJSON != null) {
                    try {
                        JSONObject relationships = familyJSON.getJSONObject("relationships");

                        String primaryCaregiverID = (String) relationships.getJSONArray("primary_caregiver").get(0);
                        String familyHeadID = (String) relationships.getJSONArray("family_head").get(0);

                        final FamilyCallDialogModel headModel = prepareModel(eventClientRepository, familyHeadID, primaryCaregiverID, true);
                        appExecutors.mainThread().execute(new Runnable() {
                            @Override
                            public void run() {
                                presenter.updateHeadOfFamily((headModel == null || headModel.getPhoneNumber() == null) ? null : headModel);
                            }
                        });


                        final FamilyCallDialogModel careGiverModel = prepareModel(eventClientRepository, familyHeadID, primaryCaregiverID, false);
                        appExecutors.mainThread().execute(new Runnable() {
                            @Override
                            public void run() {
                                presenter.updateCareGiver((careGiverModel == null || careGiverModel.getPhoneNumber() == null) ? null : careGiverModel);
                            }
                        });


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        appExecutors.diskIO().execute(runnable);
    }

    private FamilyCallDialogModel prepareModel(
            EventClientRepository eventClientRepository,
            String familyHeadID, String primaryCaregiverID,
            Boolean isHead
    ) throws JSONException {

        if (primaryCaregiverID.toLowerCase().equals(familyHeadID.toLowerCase()) && !isHead) {
            return null;
        }

        String baseID = (isHead) ? familyHeadID : primaryCaregiverID;
        JSONObject joClient = eventClientRepository.getClientByBaseEntityId(baseID);
        JSONObject joEvent = eventClientRepository.getEventsByBaseEntityIdAndEventType(baseID, Constants.EventType.FAMILY_MEMBER_REGISTRATION);

        String phoneNumber = null;
        JSONArray obs = joEvent.getJSONArray("obs");
        int x = 0;
        while (phoneNumber == null && obs.length() > x) {
            JSONObject obPhone = obs.getJSONObject(x);
            if (obPhone.getString("formSubmissionField").equals("phone_number")) {
                phoneNumber = (String) obPhone.getJSONArray("values").get(0);
            }
            x++;
        }

        FamilyCallDialogModel model = new FamilyCallDialogModel();
        model.setPhoneNumber(phoneNumber);
        model.setName(String.format("%s %s", joClient.getString("firstName"), joClient.getString("lastName")));
        model.setRole((primaryCaregiverID.toLowerCase().equals(familyHeadID.toLowerCase())) ? "Head of Family , Caregiver" : (isHead ? "Head of Family" : "Caregiver"));

        return model;
    }

}
