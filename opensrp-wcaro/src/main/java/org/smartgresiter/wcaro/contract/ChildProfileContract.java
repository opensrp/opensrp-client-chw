package org.smartgresiter.wcaro.contract;

import android.content.Context;
import android.content.Intent;
import android.util.Pair;

import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONObject;
import org.smartgresiter.wcaro.util.ChildVisit;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.FetchStatus;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.view.contract.BaseProfileContract;

public interface ChildProfileContract {

    interface View extends BaseProfileContract.View {

        Context getApplicationContext();

        String getString(int resourceId);

        void startFormActivity(JSONObject form);

        void refreshMemberList(final FetchStatus fetchStatus);

        void displayShortToast(int resourceId);

        void setProfileImage(String baseEntityId);

        void setParentName(String parentName);

        void setGender(String gender);

        void setAddress(String address);

        void setId(String id);

        void setProfileName(String fullName);

        void setAge(String age);
        void setVisitButtonDueStatus();
        void setVisitButtonOverdueStatus();
        void setLastVisitRowView(String days);
        void setServiceName(String serviceName);
        void setServiceDueDate(String date);
        void setSeviceOverdueDate(String date);
        void setServiceUpcomingDueDate(String upcomingDate);
        void setVisitLessTwentyFourView(String monthName);
        void setVisitAboveTwentyFourView();
        void setFamilyHasNothingDue();
        void setFamilyHasServiceDue();
        void setFamilyHasServiceOverdue();

        ChildProfileContract.Presenter presenter();

    }

    interface Presenter extends BaseProfileContract.Presenter {

        ChildProfileContract.View getView();

        void startForm(String formName, String entityId, String metadata, String currentLocationId) throws Exception;

        void fetchProfileData();

        void refreshProfileView();

        void processFormDetailsSave(Intent data, AllSharedPreferences allSharedPreferences);

        String childBaseEntityId();
        void fetchVisitStatus(String baseEntityId);
        void fetchFamilyMemberServiceDue(String baseEntityId);

    }

    interface Interactor {
        void refreshChildVisitBar(String baseEntityId,ChildProfileContract.InteractorCallBack callback);
        void refreshFamilyMemberServiceDue(String familyId,String baseEntityId,ChildProfileContract.InteractorCallBack callback);

        void onDestroy(boolean isChangingConfiguration);

        void refreshProfileView(String baseEntityId, boolean isForEdit, ChildProfileContract.InteractorCallBack callback);

        void getNextUniqueId(Triple<String, String, String> triple, ChildProfileContract.InteractorCallBack callBack);

        void saveRegistration(final Pair<Client, Event> pair, final String jsonString, final boolean isEditMode, final ChildProfileContract.InteractorCallBack callBack);

    }

    interface InteractorCallBack {
        void updateChildVisit(ChildVisit childVisit);
        void updateFamilyMemberServiceDue(String serviceDueStatus);

        void startFormForEdit(CommonPersonObjectClient client);

        void refreshProfileTopSection(CommonPersonObjectClient client);

        void onUniqueIdFetched(Triple<String, String, String> triple, String entityId);

        void onNoUniqueId();

        void onRegistrationSaved(boolean isEditMode);

    }

    interface Model {

        JSONObject getFormAsJson(String formName, String entityId, String currentLocationId) throws Exception;

        Pair<Client, Event> processMemberRegistration(String jsonString, String familyBaseEntityId);

    }

}
