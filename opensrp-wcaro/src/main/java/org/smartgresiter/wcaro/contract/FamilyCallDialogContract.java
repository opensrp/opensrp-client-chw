package org.smartgresiter.wcaro.contract;

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

    }

    interface Interactor {

        void getHeadOfFamily(Presenter presenter);

        void getCareGiver(Presenter presenter);

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
