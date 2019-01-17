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

        void processMember(Map<String,String> familyDetails, CommonPersonObject client);

        void displayChangeFamilyHeadDialog(CommonPersonObjectClient client);

        void displayChangeCareGiverDialog(CommonPersonObjectClient client);

        void changeCareGiver(String familyID, String memberID);

        void changeHeadOfFamily(String familyID, String memberID);

        void removeEveryone();

        void initialize();

        void processMember(HashMap<String, String> res);
    }

    interface View extends FamilyProfileMemberContract.View {

        void removeMember(CommonPersonObjectClient client);

        void displayChangeFamilyHeadDialog(CommonPersonObjectClient client);

        void displayChangeCareGiverDialog(CommonPersonObjectClient client);

        void closeFamily();

        void gotToPrevious();

        void startJsonActivity(JSONObject form);

    }

    interface Interactor {

        void removeMember(CommonPersonObject memberID, String lastLocationId);

        void removeFamily(String familyID, String lastLocationId, Presenter presenter);

        void processFamilyMember(String familyID, Presenter presenter);
    }

    interface Model extends FamilyProfileMemberContract.Model {

        JSONObject prepareJsonForm(CommonPersonObjectClient client);

        DataModel renderObject(String memberID);

    }

    interface DataModel {

        String baseEntityID();
        Boolean isPrimaryCareGiver();
        Boolean isHeadOfHouse();
        String firstName();
        String lastName();
        String Age();
        Date DateOfBirth();

    }

}
