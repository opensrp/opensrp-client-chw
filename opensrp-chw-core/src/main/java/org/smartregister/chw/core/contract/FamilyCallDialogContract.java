package org.smartregister.chw.core.contract;

import android.content.Context;

public interface FamilyCallDialogContract {

    interface Presenter {

        void updateHeadOfFamily(Model model);

        void updateCareGiver(Model model);

        void initalize();

    }

    interface View {

        void refreshHeadOfFamilyView(Model model);

        void refreshCareGiverView(Model model);

        Dialer getPendingCallRequest();

        void setPendingCallRequest(Dialer dialer);

        Presenter initializePresenter();

        Context getCurrentContext();
    }

    interface Interactor {

        void getHeadOfFamily(Presenter presenter, Context context);

    }

    interface Dialer {
        void callMe();
    }

    interface Model {

        String getName();

        void setName(String name);

        String getRole();

        void setRole(String role);

        String getPhoneNumber();

        void setPhoneNumber(String phoneNumber);
    }
}
