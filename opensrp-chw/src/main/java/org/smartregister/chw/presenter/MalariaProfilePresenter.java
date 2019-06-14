package org.smartregister.chw.presenter;

import android.util.Pair;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.contract.ChildProfileContract;
import org.smartregister.chw.contract.FamilyProfileExtendedContract;
import org.smartregister.chw.contract.MalariaProfileContract;
import org.smartregister.chw.interactor.ChildProfileInteractor;
import org.smartregister.chw.interactor.FamilyProfileInteractor;
import org.smartregister.chw.interactor.MalariaProfileInteractor;
import org.smartregister.chw.model.ChildRegisterModel;
import org.smartregister.chw.util.ChildDBConstants;
import org.smartregister.chw.util.ChildService;
import org.smartregister.chw.util.ChildVisit;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.FetchStatus;
import org.smartregister.family.util.DBConstants;
import org.smartregister.family.util.Utils;

import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class MalariaProfilePresenter implements MalariaProfileContract.Presenter, MalariaProfileContract.InteractorCallBack, FamilyProfileExtendedContract.PresenterCallBack {

    private static final String TAG = MalariaProfilePresenter.class.getCanonicalName();

    private WeakReference<MalariaProfileContract.View> view;
    private MalariaProfileInteractor interactor;
    private MalariaProfileContract.Model model;

    private String baseEntityId;
    private String dob;
    private String familyID;
    private String familyName;
    private String familyHeadID;
    private String primaryCareGiverID;

    public MalariaProfilePresenter(MalariaProfileContract.View malariaView, MalariaProfileContract.Model model, String baseEntityId) {
        this.view = new WeakReference<>(malariaView);
        this.interactor = new MalariaProfileInteractor();
        this.model = model;
        this.baseEntityId = baseEntityId;
    }

    public MalariaProfileContract.Model getModel() {
        return model;
    }

    public String getFamilyID() {
        return familyID;
    }

    public void setFamilyID(String familyID) {
        this.familyID = familyID;
        verifyHasPhone();
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getFamilyHeadID() {
        return familyHeadID;
    }

    public void setFamilyHeadID(String familyHeadID) {
        this.familyHeadID = familyHeadID;
    }

    public String getPrimaryCareGiverID() {
        return primaryCareGiverID;
    }

    public void setPrimaryCareGiverID(String primaryCareGiverID) {
        this.primaryCareGiverID = primaryCareGiverID;
    }

    public CommonPersonObjectClient getChildClient() {
        return ((MalariaProfileInteractor) interactor).getpClient();
    }

    public String getFamilyId() {
        return familyID;
    }

    public String getDateOfBirth() {
        return dob;
    }


    @Override
    public void fetchProfileData() {
        interactor.refreshProfileView(baseEntityId, false, this);

    }

    @Override
    public void updatePatientCommonPerson(String baseEntityId) {

    }

    @Override
    public void updatePatientProfile(String jsonObject) {

    }

    @Override
    public MalariaProfileContract.View getView() {
        if (view != null) {
            return view.get();
        } else {
            return null;
        }
    }

    @Override
    public void startFormForEdit(String title, CommonPersonObjectClient client) {
//        JSONObject form = interactor.getAutoPopulatedJsonEditFormString(org.smartregister.chw.util.Constants.JSON_FORM.CHILD_REGISTER, title, getView().getApplicationContext(), client);
//        try {
//
//            if (!isBlank(client.getColumnmaps().get(ChildDBConstants.KEY.RELATIONAL_ID))) {
//                JSONObject metaDataJson = form.getJSONObject("metadata");
//                JSONObject lookup = metaDataJson.getJSONObject("look_up");
//                lookup.put("entity_id", "family");
//                lookup.put("value", client.getColumnmaps().get(ChildDBConstants.KEY.RELATIONAL_ID));
//            }
//            getView().startFormActivity(form);
//        } catch (Exception e) {
//
//        }

    }

    @Override
    public void hideProgressBar() {
        if (getView() != null) {
            getView().hideProgressBar();
        }
    }

    @Override
    public void refreshProfileTopSection(CommonPersonObjectClient client) {
        if (client == null || client.getColumnmaps() == null) {
            return;
        }
        String parentFirstName = Utils.getValue(client.getColumnmaps(), ChildDBConstants.KEY.FAMILY_FIRST_NAME, true);
        String parentLastName = Utils.getValue(client.getColumnmaps(), ChildDBConstants.KEY.FAMILY_LAST_NAME, true);
        String parentMiddleName = Utils.getValue(client.getColumnmaps(), ChildDBConstants.KEY.FAMILY_MIDDLE_NAME, true);

        String parentName = view.get().getContext().getResources().getString(R.string.care_giver_initials) + ": " + org.smartregister.util.Utils.getName(parentFirstName, parentMiddleName + " " + parentLastName);
        getView().setParentName(parentName);
        String firstName = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.FIRST_NAME, true);
        String lastName = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.LAST_NAME, true);
        String middleName = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.MIDDLE_NAME, true);
        String childName = org.smartregister.util.Utils.getName(firstName, middleName + " " + lastName);
        getView().setProfileName(childName);
        getView().setAge(Utils.getDuration(Utils.getValue(client.getColumnmaps(), DBConstants.KEY.DOB, false)));

        dob = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.DOB, false);

        //dobString = dobString.contains("y") ? dobString.substring(0, dobString.indexOf("y")) : dobString;
        String address = Utils.getValue(client.getColumnmaps(), ChildDBConstants.KEY.FAMILY_HOME_ADDRESS, true);
        String gender = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.GENDER, true);

        getView().setAddress(address);
        getView().setGender(gender);

        String uniqueId = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.UNIQUE_ID, false);
        uniqueId = String.format(getView().getString(org.smartregister.family.R.string.unique_id_text), uniqueId);
        getView().setId(uniqueId);


        getView().setProfileImage(client.getCaseId());

    }

    @Override
    public void onRegistrationSaved(boolean isEditMode) {
        if (isEditMode) {
            getView().hideProgressDialog();
            getView().refreshProfile(FetchStatus.fetched);
        }
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

    @Override
    public void verifyHasPhone() {
        new FamilyProfileInteractor().verifyHasPhone(familyID, this);
    }

    @Override
    public void notifyHasPhone(boolean hasPhone) {
        if (view.get() != null) {
            view.get().updateHasPhone(hasPhone);
        }
    }
}
