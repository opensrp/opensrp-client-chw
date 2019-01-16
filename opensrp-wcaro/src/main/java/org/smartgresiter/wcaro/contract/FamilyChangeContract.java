package org.smartgresiter.wcaro.contract;

import android.content.Context;

import java.util.HashMap;
import java.util.List;

public interface FamilyChangeContract {

    interface Presenter {

        void saveCompleted();

        void getMembers(String familyID);

        void getAdultMembersExcludePCG();

        void saveFamilyMember(Context context, HashMap<String, String> member);

        void renderAdultMembersExcludePCG(List<HashMap<String, String>> clients, String primaryCareID, String headOfHouseID);

        void getAdultMembersExcludeHOF();

        void renderAdultMembersExcludeHOF(List<HashMap<String, String>> clients, String primaryCareID, String headOfHouseID);
    }

    interface View {

        void refreshMembersView(List<HashMap<String, String>> familyMembers);

        void saveComplete();

        void updateFamilyMember(HashMap<String, String> familyMember);

        void showProgressDialog(String Title);

        void close();
    }

    interface Model {

        List<HashMap<String, String>> getMembersExcluding(List<HashMap<String, String>> mmebers, String primaryCareID, String headOfHouseID, String... ids);

    }

    interface Interactor {

        void getAdultMembersExcludeHOF(String familyID, Presenter presenter);

        void getAdultMembersExcludePCG(String familyID, Presenter presenter);

        void updateFamilyMember(Context context, HashMap<String, String> familyMember, String familyID, Presenter presenter);

    }

}
