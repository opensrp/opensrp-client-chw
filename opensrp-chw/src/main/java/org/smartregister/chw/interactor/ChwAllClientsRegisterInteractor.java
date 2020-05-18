package org.smartregister.chw.interactor;

import androidx.annotation.NonNull;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.UniqueId;
import org.smartregister.family.util.Constants;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.family.util.Utils;
import org.smartregister.opd.contract.OpdRegisterActivityContract;
import org.smartregister.opd.interactor.BaseOpdRegisterActivityInteractor;
import org.smartregister.opd.pojo.OpdEventClient;
import org.smartregister.opd.pojo.RegisterParams;
import org.smartregister.opd.utils.OpdJsonFormUtils;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.sync.helper.ECSyncHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

public class ChwAllClientsRegisterInteractor extends BaseOpdRegisterActivityInteractor {

    @Override
    public void getNextUniqueId(final Triple<String, String, String> triple, final OpdRegisterActivityContract.InteractorCallBack callBack) {
        Runnable runnable = () -> {
            UniqueId uniqueId = getUniqueIdRepository().getNextUniqueId();
            final String entityId = uniqueId != null ? uniqueId.getOpenmrsId() : "";
            appExecutors.mainThread().execute(() -> {
                if (StringUtils.isBlank(entityId)) {
                    callBack.onNoUniqueId();
                } else {
                    callBack.onUniqueIdFetched(triple, entityId);
                }
            });
        };

        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        //TODO set presenter or model to null
    }

    @Override
    public void saveRegistration(final List<OpdEventClient> opdEventClientList, final String jsonString,
                                 final RegisterParams registerParams, final OpdRegisterActivityContract.InteractorCallBack callBack) {
        Runnable runnable = () -> {
            saveRegistration(opdEventClientList, jsonString, registerParams);
            appExecutors.mainThread().execute(() -> callBack.onRegistrationSaved(registerParams.isEditMode()));
        };

        appExecutors.diskIO().execute(runnable);
    }


    public void saveRegistration(@NonNull List<OpdEventClient> allClientEventList, @NonNull String jsonString,
                                  @NonNull RegisterParams params) {
        try {
            List<String> currentFormSubmissionIds = new ArrayList<>();

            for (int i = 0; i < allClientEventList.size(); i++) {
                try {

                    OpdEventClient allClientEvent = allClientEventList.get(i);
                    Client baseClient = allClientEvent.getClient();
                    Event baseEvent = allClientEvent.getEvent();
                    addClient(params, baseClient);
                    addEvent(params, currentFormSubmissionIds, baseEvent);
                    updateOpenSRPId(jsonString, params, baseClient);
                    addImageLocation(jsonString, baseClient, baseEvent);
                } catch (Exception e) {
                    Timber.e(e, "ChwAllClientRegisterInteractor --> saveRegistration");
                }
            }

            long lastSyncTimeStamp = getAllSharedPreferences().fetchLastUpdatedAtDate(0);
            Date lastSyncDate = new Date(lastSyncTimeStamp);
            getClientProcessorForJava().processClient(getSyncHelper().getEvents(currentFormSubmissionIds));
            getAllSharedPreferences().saveLastUpdatedAtDate(lastSyncDate.getTime());
        } catch (Exception e) {
            Timber.e(e, "OpdRegisterInteractor --> saveRegistration");
        }
    }

    private void addClient(@NonNull RegisterParams params, Client baseClient) throws JSONException {
        JSONObject clientJson = new JSONObject(OpdJsonFormUtils.gson.toJson(baseClient));
        if (params.isEditMode()) {
            try {
                JsonFormUtils.mergeAndSaveClient(getSyncHelper(), baseClient);
            } catch (Exception e) {
                Timber.e(e, "ChwAllClientRegisterInteractor --> mergeAndSaveClient");
            }
        } else {
            getSyncHelper().addClient(baseClient.getBaseEntityId(), clientJson);
        }
    }

    private void addImageLocation(String jsonString, Client baseClient, Event baseEvent) {
        if (baseClient != null || baseEvent != null) {
            String imageLocation = OpdJsonFormUtils.getFieldValue(jsonString, Constants.KEY.PHOTO);
            if (StringUtils.isNotBlank(imageLocation)) {
                JsonFormUtils.saveImage(baseEvent.getProviderId(), baseClient.getBaseEntityId(), imageLocation);
            }
        }
    }

    private void updateOpenSRPId(String jsonString, RegisterParams params, Client baseClient) {
        if (params.isEditMode()) {
            // UnAssign current OpenSRP ID
            if (baseClient != null) {
                String newOpenSrpId = baseClient.getIdentifier(Utils.metadata().uniqueIdentifierKey).replace("-", "");
                String currentOpenSrpId = JsonFormUtils.getString(jsonString, JsonFormUtils.CURRENT_OPENSRP_ID).replace("-", "");
                if (!newOpenSrpId.equals(currentOpenSrpId)) {
                    //OpenSRP ID was changed
                    getUniqueIdRepository().open(currentOpenSrpId);
                }
            }

        } else {
            if (baseClient != null) {
                String openSrpId = baseClient.getIdentifier(Utils.metadata().uniqueIdentifierKey);
                if (StringUtils.isNotBlank(openSrpId) && !openSrpId.contains(Constants.IDENTIFIER.FAMILY_SUFFIX)) {
                    //Mark OpenSRP ID as used
                    getUniqueIdRepository().close(openSrpId);
                }
            }
        }
    }

    private void addEvent(RegisterParams params, List<String> currentFormSubmissionIds, Event baseEvent) throws JSONException {
        if (baseEvent != null) {
            JSONObject eventJson = new JSONObject(OpdJsonFormUtils.gson.toJson(baseEvent));
            getSyncHelper().addEvent(baseEvent.getBaseEntityId(), eventJson, params.getStatus());
            currentFormSubmissionIds.add(eventJson.getString(EventClientRepository.event_column.formSubmissionId.toString()));
        }
    }

    @NotNull
    public ECSyncHelper getSyncHelper() {
        return ChwApplication.getInstance().getEcSyncHelper();
    }

    @NotNull
    public AllSharedPreferences getAllSharedPreferences() {
        return ChwApplication.getInstance().getContext().allSharedPreferences();
    }
}
