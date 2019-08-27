package org.smartregister.chw.core.contract;

import android.content.Context;

import org.smartregister.family.contract.FamilyOtherMemberContract;

public interface FamilyOtherMemberProfileExtendedContract {

    interface Presenter extends FamilyOtherMemberContract.Presenter {

        void updateFamilyMember(String jsonString);

        void updateFamilyMemberServiceDue(String serviceDueStatus);
    }

    interface View extends FamilyOtherMemberContract.View {

        void showProgressDialog(int saveMessageStringIdentifier);

        void hideProgressDialog();

        void refreshList();

        void updateHasPhone(boolean hasPhone);

        void setFamilyServiceStatus(String status);

        Context getContext();
    }
}
