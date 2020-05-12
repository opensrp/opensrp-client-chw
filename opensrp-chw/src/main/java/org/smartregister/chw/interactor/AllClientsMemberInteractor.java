package org.smartregister.chw.interactor;

import org.smartregister.chw.contract.AllClientsMemberContract;
import org.smartregister.opd.pojo.OpdEventClient;
import org.smartregister.opd.pojo.RegisterParams;

import java.util.List;

public class AllClientsMemberInteractor implements AllClientsMemberContract.Interactor {

    private ChwAllClientsRegisterInteractor allClientsRegisterInteractor;

    public AllClientsMemberInteractor() {
        allClientsRegisterInteractor= new ChwAllClientsRegisterInteractor();
    }

    @Override
    public void performUpdateClientDetails(List<OpdEventClient> opdEventClientList, String jsonString) {
        RegisterParams registrationParam = new RegisterParams();
        registrationParam.setEditMode(true);
        allClientsRegisterInteractor.saveRegistration(opdEventClientList, jsonString, registrationParam);
    }
}
