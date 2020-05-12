package org.smartregister.chw.presenter;

import org.smartregister.chw.contract.AllClientsMemberContract;
import org.smartregister.chw.interactor.AllClientsMemberInteractor;
import org.smartregister.chw.model.AllClientsMemberModel;
import org.smartregister.opd.pojo.OpdEventClient;

import java.util.List;

public class AllClientsMemberPresenter implements AllClientsMemberContract.Presenter {

    private AllClientsMemberContract.Interactor interactor;

    public AllClientsMemberPresenter() {
        interactor = new AllClientsMemberInteractor();
    }

    @Override
    public void updateClientDetails(String jsonString) {
        AllClientsMemberContract.Model model = new AllClientsMemberModel();
        List<OpdEventClient> opdEventClientList = model.processUpdateForm(jsonString);
        if (opdEventClientList == null || opdEventClientList.isEmpty()) {
            return;
        }
        interactor.performUpdateClientDetails(opdEventClientList, jsonString);
    }
}
