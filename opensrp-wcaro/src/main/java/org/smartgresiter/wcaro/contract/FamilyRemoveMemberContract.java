package org.smartgresiter.wcaro.contract;

import org.json.JSONObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.contract.FamilyProfileMemberContract;

import java.util.Date;

public interface FamilyRemoveMemberContract {

    interface Presenter extends FamilyProfileMemberContract.Presenter {

        void removeMember(CommonPersonObjectClient client);

        void changeCareGiver(String familyID, String memberID);

        void changeHeadOfFamily(String familyID, String memberID);

        void removeEveryone();

        void initialize();

    }

    interface View extends FamilyProfileMemberContract.View {

        void removeMember(CommonPersonObjectClient client);

        void displayChangeHeadDialog();

        void displayChangeOptionDialog();

        void closeFamily();

        void gotToPrevious();

        void startJsonActivity(JSONObject form);

    }

    interface Interactor {

        void removeMember(String memberID);

        void removeFamily(String familyID, Presenter presenter);

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
