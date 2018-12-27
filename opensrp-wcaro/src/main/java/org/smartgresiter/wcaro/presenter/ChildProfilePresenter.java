package org.smartgresiter.wcaro.presenter;

import android.content.Intent;
import android.util.Log;

import org.apache.commons.lang3.tuple.Triple;
import org.smartgresiter.wcaro.contract.ChildProfileContract;
import org.smartgresiter.wcaro.interactor.ChildProfileInteractor;
import org.smartgresiter.wcaro.util.ChildDBConstants;
import org.smartgresiter.wcaro.util.ChildVisit;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.util.Constants;
import org.smartregister.family.util.DBConstants;
import org.smartregister.family.util.Utils;
import org.smartregister.repository.AllSharedPreferences;

import java.lang.ref.WeakReference;

import static org.smartregister.util.Utils.getName;

public class ChildProfilePresenter implements ChildProfileContract.Presenter, ChildProfileContract.InteractorCallBack{

    private static final String TAG = ChildProfilePresenter.class.getCanonicalName();

    private WeakReference<ChildProfileContract.View> view;
    private ChildProfileContract.Interactor interactor;
    private ChildProfileContract.Model model;

    private String familyBaseEntityId;
    public ChildProfilePresenter(ChildProfileContract.View childView, ChildProfileContract.Model model, String familyBaseEntityId) {
        this.view = new WeakReference<>(childView);
        this.interactor = new ChildProfileInteractor();
        this.model = model;
        this.familyBaseEntityId = familyBaseEntityId;
    }

    @Override
    public void fetchVisitStatus(String baseEntityId) {
        interactor.refreshChildVisitBar(familyBaseEntityId,this);
    }



    @Override
    public void fetchProfileData() {
        interactor.refreshProfileView(familyBaseEntityId, false, this);

    }

    @Override
    public void refreshProfileView() {
        interactor.refreshProfileView(familyBaseEntityId, false, this);
    }

    @Override
    public void processFormDetailsSave(Intent data, AllSharedPreferences allSharedPreferences) {
        try {
            String jsonString = data.getStringExtra(Constants.INTENT_KEY.JSON);
            Log.d("JSONResult", jsonString);
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    @Override
    public ChildProfileContract.View getView() {
        if (view != null) {
            return view.get();
        } else {
            return null;
        }
    }

    @Override
    public void startForm(String formName, String entityId, String metadata, String currentLocationId) throws Exception {

    }
    @Override
    public String childBaseEntityId() {
        return null;
    }

    @Override
    public void startFormForEdit(CommonPersonObjectClient client) {

    }
    @Override
    public void updateChildVisit(ChildVisit childVisit) {
        if(childVisit!=null){
            if(childVisit.getVisitStatus().equalsIgnoreCase(ChildProfileInteractor.VisitType.DUE.name())){
                getView().setVisitButtonDueStatus();
            }
            if(childVisit.getVisitStatus().equalsIgnoreCase(ChildProfileInteractor.VisitType.OVERDUE.name())){
                getView().setVisitButtonOverdueStatus();
            }
            if(childVisit.getVisitStatus().equalsIgnoreCase(ChildProfileInteractor.VisitType.LESS_TWENTY_FOUR.name())){
                getView().setVisitLessTwentyFourView(childVisit.getLastVisitMonth());
            }
            if(childVisit.getVisitStatus().equalsIgnoreCase(ChildProfileInteractor.VisitType.OVER_TWENTY_FOUR.name())){
                getView().setVisitAboveTwentyFourView();
            }
        }

    }
    @Override
    public void refreshProfileTopSection(CommonPersonObjectClient client) {
        if (client == null || client.getColumnmaps() == null) {
            return;
        }
        String parentFirstName=Utils.getValue(client.getColumnmaps(), ChildDBConstants.KEY.FAMILY_FIRST_NAME, true);
        String parentLastName=Utils.getValue(client.getColumnmaps(), ChildDBConstants.KEY.FAMILY_LAST_NAME, true);
        String parentName="CG:"+org.smartregister.util.Utils.getName(parentFirstName, parentLastName);
        getView().setParentName(parentName);
        String firstName = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.FIRST_NAME, true);
        String lastName = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.LAST_NAME, true);
        String childName = org.smartregister.util.Utils.getName(firstName, lastName);
        getView().setProfileName(childName);

        String dobString = Utils.getDuration(Utils.getValue(client.getColumnmaps(), DBConstants.KEY.DOB, false));
        getView().setAge(dobString);
        //dobString = dobString.contains("y") ? dobString.substring(0, dobString.indexOf("y")) : dobString;
        String address=Utils.getValue(client.getColumnmaps(),ChildDBConstants.KEY.FAMILY_HOME_ADDRESS,true);
        String gender=Utils.getValue(client.getColumnmaps(),DBConstants.KEY.GENDER,true);

        getView().setAddress(address);
        getView().setGender(gender);

        String uniqueId = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.UNIQUE_ID, false);
        uniqueId = String.format(getView().getString(org.smartregister.family.R.string.unique_id_text), uniqueId);
        getView().setId(uniqueId);


        getView().setProfileImage(client.getCaseId());

    }

    @Override
    public void onUniqueIdFetched(Triple<String, String, String> triple, String entityId) {

    }

    @Override
    public void onNoUniqueId() {

    }

    @Override
    public void onRegistrationSaved(boolean isEditMode) {

    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        view = null;//set to null on destroy

        // Inform interactor
        interactor.onDestroy(isChangingConfiguration);

        // Activity destroyed set interactor to null
        if (!isChangingConfiguration) {
            interactor = null;
        }
    }
}
