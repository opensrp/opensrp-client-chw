package org.smartregister.chw.interactor;

import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONObject;
import org.smartregister.chw.anc.interactor.BaseAncRegisterInteractor;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.UniqueId;
import org.smartregister.family.FamilyLibrary;
import org.smartregister.family.contract.FamilyRegisterContract;
import org.smartregister.family.domain.FamilyEventClient;
import org.smartregister.family.util.Constants;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.family.util.Utils;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.UniqueIdRepository;
import org.smartregister.sync.ClientProcessorForJava;
import org.smartregister.sync.helper.ECSyncHelper;

import java.util.Date;
import java.util.List;

import timber.log.Timber;

public class AncRegisterInteractor extends BaseAncRegisterInteractor {

    public void getNextUniqueId(final Triple<String, String, String> triple, final FamilyRegisterContract.InteractorCallBack callBack) {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                UniqueId uniqueId = getUniqueIdRepository().getNextUniqueId();
                final String entityId = uniqueId != null ? uniqueId.getOpenmrsId() : "";
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (StringUtils.isBlank(entityId)) {
                            callBack.onNoUniqueId();
                        } else {
                            callBack.onUniqueIdFetched(triple, entityId);
                        }
                    }
                });
            }
        };

        appExecutors.diskIO().execute(runnable);
    }

    public UniqueIdRepository getUniqueIdRepository() {
        return FamilyLibrary.getInstance().getUniqueIdRepository();
    }

    public void saveRegistration(final List<FamilyEventClient> familyEventClientList, final String jsonString, final boolean isEditMode, final FamilyRegisterContract.InteractorCallBack callBack) {

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

            for (int i = 0; i < familyEventClientList.size(); i++) {
                FamilyEventClient familyEventClient = familyEventClientList.get(i);
                Client baseClient = familyEventClient.getClient();
                Event baseEvent = familyEventClient.getEvent();

                if (baseClient != null) {
                    JSONObject clientJson = new JSONObject(JsonFormUtils.gson.toJson(baseClient));
                    if (isEditMode) {
                        JsonFormUtils.mergeAndSaveClient(getSyncHelper(), baseClient);
                    } else {
                        getSyncHelper().addClient(baseClient.getBaseEntityId(), clientJson);
                    }
                }

                if (baseEvent != null) {
                    JSONObject eventJson = new JSONObject(JsonFormUtils.gson.toJson(baseEvent));
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
                        if (StringUtils.isNotBlank(opensrpId) && !opensrpId.contains(Constants.IDENTIFIER.FAMILY_SUFFIX)) {
                            //mark OPENSRP ID as used
                            getUniqueIdRepository().close(opensrpId);
                        }
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
            }

            long lastSyncTimeStamp = getAllSharedPreferences().fetchLastUpdatedAtDate(0);
            Date lastSyncDate = new Date(lastSyncTimeStamp);
            getClientProcessorForJava().processClient(getSyncHelper().getEvents(lastSyncDate, BaseRepository.TYPE_Unsynced));
            getAllSharedPreferences().saveLastUpdatedAtDate(lastSyncDate.getTime());
        } catch (Exception e) {
            Timber.e(Log.getStackTraceString(e));
        }
    }

    public AllSharedPreferences getAllSharedPreferences() {
        return Utils.context().allSharedPreferences();
    }

    public ECSyncHelper getSyncHelper() {
        return FamilyLibrary.getInstance().getEcSyncHelper();
    }

    public ClientProcessorForJava getClientProcessorForJava() {
        return FamilyLibrary.getInstance().getClientProcessorForJava();
    }
}
