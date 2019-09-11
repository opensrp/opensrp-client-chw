package org.smartregister.brac.hnpp.interactor;

import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.smartregister.brac.hnpp.contract.HnppFamilyRegisterContract;
import org.smartregister.brac.hnpp.location.SSLocationHelper;
import org.smartregister.brac.hnpp.location.SSLocations;
import org.smartregister.brac.hnpp.utils.HnppConstants;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.db.EventClient;
import org.smartregister.family.contract.FamilyRegisterContract;
import org.smartregister.family.domain.FamilyEventClient;
import org.smartregister.family.util.Constants;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.family.util.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HnppFamilyRegisterInteractor extends org.smartregister.family.interactor.FamilyRegisterInteractor {

    public String getHouseHoldId(final SSLocations locationForm, HnppFamilyRegisterContract.InteractorCallBack callBack){
        String lastFourDigit = locationForm.village.id+"";
        String houseHoldId = SSLocationHelper.getInstance().generateHouseHoldId(locationForm,lastFourDigit);
        return houseHoldId;
//        Runnable runnable = new Runnable() {
//            public void run() {
//                String lastFourDigit = "";
//                String houseHoldId = SSLocationHelper.getInstance().generateHouseHoldId(locationForm,lastFourDigit);
//                Log.v("HHID","houseHoldId>>"+houseHoldId);
//                appExecutors.mainThread().execute(new Runnable() {
//                    public void run() {
//                        if (!StringUtils.isBlank(houseHoldId)) {
//                            callBack.updateHouseHoldId(houseHoldId);
//                        }
//
//                    }
//                });
//            }
//        };
//        this.appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void saveRegistration(List<FamilyEventClient> familyEventClientList, String jsonString, boolean isEditMode, FamilyRegisterContract.InteractorCallBack callBack) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                saveRegistration(familyEventClientList, jsonString, isEditMode);
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callBack.onRegistrationSaved(isEditMode);
                    }
                });
            }
        };

        appExecutors.diskIO().execute(runnable);
    }
    private void saveRegistration(List<FamilyEventClient> familyEventClientList, String jsonString, boolean isEditMode) {

        try {

            List<EventClient> eventClientList = new ArrayList<>();
            for (int i = 0; i < familyEventClientList.size(); i++) {
                FamilyEventClient familyEventClient = familyEventClientList.get(i);
                Client baseClient = familyEventClient.getClient();
                Event baseEvent = familyEventClient.getEvent();
                JSONObject eventJson = null;
                JSONObject clientJson = null;

                if (baseClient != null) {
                    clientJson = new JSONObject(JsonFormUtils.gson.toJson(baseClient));
                    if (isEditMode) {
                        JsonFormUtils.mergeAndSaveClient(getSyncHelper(), baseClient);
                    } else {
                        getSyncHelper().addClient(baseClient.getBaseEntityId(), clientJson);
                    }
                }

                if (baseEvent != null) {
                    eventJson = new JSONObject(JsonFormUtils.gson.toJson(baseEvent));
                    getSyncHelper().addEvent(baseEvent.getBaseEntityId(), eventJson);
                }

                if (isEditMode) {
                    // Unassign current OPENSRP ID
                    if (baseClient != null) {
                        String newOpenSRPId = baseClient.getIdentifier(Utils.metadata().uniqueIdentifierKey).replace("-", "");
                        String currentOpenSRPId = JsonFormUtils.getString(jsonString, JsonFormUtils.CURRENT_OPENSRP_ID).replace("-", "");
                        if (!newOpenSRPId.equals(currentOpenSRPId)) {
                            //OPENSRP ID was changed
                            getUniqueIdRepository().open(currentOpenSRPId);
                        }
                    }

                } else {
                    if (baseClient != null) {
                        String opensrpId = baseClient.getIdentifier(Utils.metadata().uniqueIdentifierKey);
                        opensrpId = opensrpId.replace(Constants.IDENTIFIER.FAMILY_SUFFIX,"")
                                .replace(HnppConstants.IDENTIFIER.FAMILY_TEXT,"");
                        Log.v("HHID","after save>>"+opensrpId);
                        //if (StringUtils.isNotBlank(opensrpId) && !opensrpId.contains(Constants.IDENTIFIER.FAMILY_SUFFIX)) {
                            //mark OPENSRP ID as used
                            getUniqueIdRepository().close(opensrpId);
                        //}
                    }
                }

                if (baseClient != null || baseEvent != null) {
                    String imageLocation = null;
                    if (i == 0) {
                        imageLocation = JsonFormUtils.getFieldValue(jsonString, Constants.KEY.PHOTO);
                    } else if (i == 1) {
                        imageLocation = JsonFormUtils.getFieldValue(jsonString, JsonFormUtils.STEP2, Constants.KEY.PHOTO);
                    }

                    if (StringUtils.isNotBlank(imageLocation)) {
                        JsonFormUtils.saveImage(baseEvent.getProviderId(), baseClient.getBaseEntityId(), imageLocation);
                    }
                }
                org.smartregister.domain.db.Event domainEvent = JsonFormUtils.gson.fromJson(eventJson.toString(), org.smartregister.domain.db.Event.class);
                org.smartregister.domain.db.Client domainClient = JsonFormUtils.gson.fromJson(clientJson.toString(), org.smartregister.domain.db.Client.class);
                eventClientList.add(new EventClient(domainEvent, domainClient));
            }

            long lastSyncTimeStamp = getAllSharedPreferences().fetchLastUpdatedAtDate(0);
            Date lastSyncDate = new Date(lastSyncTimeStamp);

            processClient(eventClientList);
            getAllSharedPreferences().saveLastUpdatedAtDate(lastSyncDate.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
