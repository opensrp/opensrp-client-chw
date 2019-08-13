package org.smartregister.chw.interactor;

import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;
import android.util.Pair;

import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.interactor.CoreChildHomeVisitInteractor;
import org.smartregister.chw.core.model.BirthIllnessFormModel;

import org.json.JSONObject;
import org.smartregister.chw.repository.ChwRepository;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.util.AppExecutors;
import org.smartregister.repository.EventClientRepository;

import timber.log.Timber;

import static org.smartregister.chw.core.utils.ChildDBConstants.KEY.BIRTH_CERT;
import static org.smartregister.util.Utils.getValue;

public class ChildHomeVisitInteractor extends CoreChildHomeVisitInteractor {

    @VisibleForTesting
    ChildHomeVisitInteractor(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
        setFlavor(new ChildHomeVisitInteractorFlv());
    }

    public ChildHomeVisitInteractor() {
        this(new AppExecutors());
    }

    @Override
    public void saveForm(CommonPersonObjectClient childClient) {
        for (String json : saveList.keySet()) {
            BirthIllnessFormModel birthIllnessFormModel = saveList.get(json);
            saveRegistration(birthIllnessFormModel.getPair(), childClient);
        }
    }

    private void saveRegistration(Pair<Client, Event> pair, CommonPersonObjectClient childClient) {

        try {

            Client baseClient = pair.first;
            Event baseEvent = pair.second;

            if (baseClient != null) {
                JSONObject clientjsonFromForm = new JSONObject(org.smartregister.family.util.JsonFormUtils.gson.toJson(baseClient));
                ChwRepository pathRepository = new ChwRepository(CoreChwApplication.getInstance().getApplicationContext(), CoreChwApplication.getInstance().getContext());
                EventClientRepository eventClientRepository = new EventClientRepository(pathRepository);
                JSONObject clientJson = eventClientRepository.getClient(CoreChwApplication.getInstance().getRepository().getReadableDatabase(), baseClient.getBaseEntityId());
                updateClientAttributes(clientjsonFromForm, clientJson);
                String birthCert = getValue(childClient.getColumnmaps(), BIRTH_CERT, true);
                if (TextUtils.isEmpty(birthCert)) {
                    getSyncHelper().addClient(baseClient.getBaseEntityId(), clientJson);
                } else {
                    org.smartregister.family.util.JsonFormUtils.mergeAndSaveClient(getSyncHelper(), baseClient);
                }

            }

            if (baseEvent != null) {
                JSONObject eventJson = new JSONObject(org.smartregister.family.util.JsonFormUtils.gson.toJson(baseEvent));
                getSyncHelper().addEvent(baseEvent.getBaseEntityId(), eventJson);
            }

        } catch (Exception e) {
            Timber.e(e);
        }
    }


}
