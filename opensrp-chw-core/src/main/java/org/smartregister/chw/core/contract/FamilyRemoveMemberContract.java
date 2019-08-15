package org.smartregister.chw.core.contract;

import org.json.JSONObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.contract.FamilyProfileMemberContract;

import java.util.HashMap;
import java.util.Map;

public interface FamilyRemoveMemberContract {

    interface Presenter extends FamilyProfileMemberContract.Presenter {

        void removeMember(CommonPersonObjectClient client);

        void processMember(Map<String, String> familyDetails, CommonPersonObjectClient client);

        void removeEveryone(String familyName, String details);

        void onFamilyRemoved(Boolean success);

        void processRemoveForm(JSONObject jsonObject);

        void memberRemoved(String removalType);

    }

    interface View extends FamilyProfileMemberContract.View {
        void removeMember(CommonPersonObjectClient client);

        void displayChangeFamilyHeadDialog(CommonPersonObjectClient client, String familyHeadID);

        void displayChangeCareGiverDialog(CommonPersonObjectClient client, String careGiverID);

        void closeFamily(String familyName, String details);

        void goToPrevious();

        void startJsonActivity(JSONObject form);

        void onMemberRemoved(String removalType);

        void onEveryoneRemoved();

    }

    interface Interactor {

        void removeMember(String familyID, String lastLocationId, JSONObject exitForm, Presenter presenter);

        void processFamilyMember(String familyID, CommonPersonObjectClient client, Presenter presenter);

        void getFamilySummary(String familyID, InteractorCallback<HashMap<String, String>> callback);
    }

    interface Model extends FamilyProfileMemberContract.Model {

        JSONObject prepareJsonForm(CommonPersonObjectClient client, String formType);

        String getForm(CommonPersonObjectClient client);

        JSONObject prepareFamilyRemovalForm(String familyID, String familyName, String details);

    }

    interface InteractorCallback<T> {
        void onResult(T result);

        void onError(Exception e);
    }

}
