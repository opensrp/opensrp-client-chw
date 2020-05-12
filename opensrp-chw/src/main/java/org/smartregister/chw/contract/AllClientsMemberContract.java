package org.smartregister.chw.contract;

import org.smartregister.opd.pojo.OpdEventClient;

import java.util.List;

public interface AllClientsMemberContract {

    interface Model {
        List<OpdEventClient> processUpdateForm(String jsonString);
    }

    interface Presenter {
        void updateClientDetails(String jsonString);
    }

    interface Interactor {
        void performUpdateClientDetails(List<OpdEventClient> opdEventClientList, String jsonString);
    }

    interface View {
        Presenter getAllClientsMemberPresenter();
    }
}
