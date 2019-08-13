package org.smartregister.chw.core.contract;

import android.content.Context;
import android.util.Pair;

import org.smartregister.chw.core.domain.FamilyMember;

import java.util.List;

public interface FamilyChangeContract {

    interface Presenter {

        void saveCompleted(String familyHeadID, String careGiverID);

        void getAdultMembersExcludePCG();

        void saveFamilyMember(Context context, Pair<String, FamilyMember> member);

        void renderAdultMembersExcludePCG(List<FamilyMember> clients, String primaryCareID, String headOfHouseID);

        void getAdultMembersExcludeHOF();

        void renderAdultMembersExcludeHOF(List<FamilyMember> clients, String primaryCareID, String headOfHouseID);
    }

    interface View {

        void refreshMembersView(List<FamilyMember> familyMembers);

        void saveComplete(String familyHeadID, String careGiverID);

        void updateFamilyMember(Pair<String, FamilyMember> familyMember);

        void showProgressDialog(String Title);

        void close();
    }

    interface Model {

        List<FamilyMember> getMembersExcluding(List<FamilyMember> members, String primaryCareID, String headOfHouseID, String... ids);

    }

    interface Interactor {

        void getAdultMembersExcludeHOF(String familyID, Presenter presenter);

        void getAdultMembersExcludePCG(String familyID, Presenter presenter);

        void updateFamilyMember(Context context, Pair<String, FamilyMember> familyMember, String familyID, String lastLocationID, Presenter presenter);

    }

}
