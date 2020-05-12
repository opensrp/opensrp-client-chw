package org.smartregister.chw.contract;

import org.smartregister.family.contract.FamilyOtherMemberContract;
import org.smartregister.family.contract.FamilyProfileContract;
import org.smartregister.family.domain.FamilyEventClient;

public interface AllClientsMemberContract {

    interface Model {
        FamilyEventClient processJsonForm(String jsonString, String familyBaseEntityId);
    }

    interface Presenter {
        void updateLocationInfo(String jsonString, String familyBaseEntityId);

        View getView();

        void refreshProfileView();
    }

    interface Interactor {
        void updateLocationInfo(String jsonString, FamilyEventClient familyEventClient,
                                FamilyProfileContract.InteractorCallBack interactorCallback);

        void updateProfileInfo(String baseEntityId, FamilyOtherMemberContract.InteractorCallBack callback);
    }

    interface View {
        Presenter getAllClientsMemberPresenter();
    }
}
