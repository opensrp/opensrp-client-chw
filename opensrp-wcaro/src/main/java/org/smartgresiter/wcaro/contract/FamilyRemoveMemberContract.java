package org.smartgresiter.wcaro.contract;

import org.json.JSONObject;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.contract.FamilyProfileMemberContract;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public interface FamilyRemoveMemberContract {

    interface Presenter extends FamilyProfileMemberContract.Presenter {

        void removeMember(CommonPersonObjectClient client);

        void processMember(Map<String,String> familyDetails, CommonPersonObjectClient client);

        void removeEveryone();

        void onFamilyRemoved(Boolean success);

    }

    interface View extends FamilyProfileMemberContract.View {

        void removeMember(CommonPersonObjectClient client);

        void displayChangeFamilyHeadDialog(CommonPersonObjectClient client);

        void displayChangeCareGiverDialog(CommonPersonObjectClient client);

        void closeFamily();

        void goToPrevious();

        void startJsonActivity(JSONObject form);

    }

    interface Interactor {

        void removeMember(String familyID, String memberID, String lastLocationId);

        void removeFamily(String familyID, String lastLocationId, Presenter presenter);

        void processFamilyMember(String familyID, CommonPersonObjectClient client, Presenter presenter);

        void getFamilyChildrenCount(String familyID, InteractorCallback<HashMap<String,Integer>> callback);
    }

    interface Model extends FamilyProfileMemberContract.Model {

        JSONObject prepareJsonForm(CommonPersonObjectClient client);

    }

    interface InteractorCallback<T> {
        void onResult(T result);

        void onError(Exception e);
    }

}
