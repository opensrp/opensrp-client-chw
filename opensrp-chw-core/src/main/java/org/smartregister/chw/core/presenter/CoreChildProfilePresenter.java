package org.smartregister.chw.core.presenter;


import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.json.JSONObject;
import org.smartregister.chw.core.R;
import org.smartregister.chw.core.contract.CoreChildProfileContract;
import org.smartregister.chw.core.contract.FamilyProfileExtendedContract;
import org.smartregister.chw.core.interactor.CoreChildProfileInteractor;
import org.smartregister.chw.core.model.ChildVisit;
import org.smartregister.chw.core.utils.ChildDBConstants;
import org.smartregister.chw.core.utils.CoreChildService;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.FetchStatus;
import org.smartregister.domain.Task;
import org.smartregister.family.util.DBConstants;
import org.smartregister.family.util.Utils;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.util.FormUtils;

import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import timber.log.Timber;

public class CoreChildProfilePresenter implements CoreChildProfileContract.Presenter, CoreChildProfileContract.InteractorCallBack, FamilyProfileExtendedContract.PresenterCallBack {

    public CoreChildProfileContract.Model model;
    public String childBaseEntityId;
    public String familyID;
    protected WeakReference<CoreChildProfileContract.View> view;
    private CoreChildProfileContract.Interactor interactor;
    protected String dob;
    private String familyName;
    private String familyHeadID;
    private String primaryCareGiverID;
    private FormUtils formUtils;

    public CoreChildProfilePresenter(CoreChildProfileContract.View childView, CoreChildProfileContract.Model model, String childBaseEntityId) {
        this.view = new WeakReference<>(childView);
        this.interactor = new CoreChildProfileInteractor();
        this.interactor.setChildBaseEntityId(childBaseEntityId);
        this.model = model;
        this.childBaseEntityId = childBaseEntityId;
    }

    public CoreChildProfilePresenter() {
    }

    public String getChildBaseEntityId() {
        return childBaseEntityId;
    }

    public void setChildBaseEntityId(String childBaseEntityId) {
        this.childBaseEntityId = childBaseEntityId;
    }

    public CoreChildProfileContract.Interactor getInteractor() {
        return interactor;
    }

    public void setInteractor(CoreChildProfileContract.Interactor interactor) {
        this.interactor = interactor;
    }

    public CoreChildProfileContract.Model getModel() {
        return model;
    }

    public void setModel(CoreChildProfileContract.Model model) {
        this.model = model;
    }

    public String getFamilyID() {
        return familyID;
    }

    @Override
    public void setFamilyID(String familyID) {
        this.familyID = familyID;
        verifyHasPhone();
    }

    @Override
    public void verifyHasPhone() {
        //todo
    }

    @Override
    public void notifyHasPhone(boolean hasPhone) {
        if (view.get() != null) {
            view.get().updateHasPhone(hasPhone);
        }
    }

    public String getFamilyName() {
        return familyName;
    }

    @Override
    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getFamilyHeadID() {
        return familyHeadID;
    }

    @Override
    public void setFamilyHeadID(String familyHeadID) {
        this.familyHeadID = familyHeadID;
    }

    public String getPrimaryCareGiverID() {
        return primaryCareGiverID;
    }

    @Override
    public void setPrimaryCareGiverID(String primaryCareGiverID) {
        this.primaryCareGiverID = primaryCareGiverID;
    }

    @Override
    public void updateVisitNotDone() {
        hideProgressBar();
        getView().openVisitMonthView();

    }

    @Override
    public void updateAfterBackGroundProcessed() {
        if (getView() != null) {
            getView().updateAfterBackgroundProcessed();
        }
    }

    @Override
    public void setClientTasks(Set<Task> taskList) {
        if (getView() != null) {
            getView().setClientTasks(taskList);
        }
    }

    public CommonPersonObjectClient getChildClient() {
        return ((CoreChildProfileInteractor) interactor).getpClient();
    }

    public Map<String, Date> getVaccineList() {
        return ((CoreChildProfileInteractor) interactor).getVaccineList();
    }

    public String getDateOfBirth() {
        return dob;
    }

    @Override
    public void updateChildProfile(String jsonObject) {
        //todo
    }

    @Override
    public CoreChildProfileContract.View getView() {
        if (view != null) {
            return view.get();
        } else {
            return null;
        }
    }

    @Override
    public void fetchProfileData() {
        interactor.refreshProfileView(childBaseEntityId, false, this);

    }

    @Override
    public void fetchTasks() {
        // // TODO: 08/08/19  Change to use correct plan id
        interactor.getClientTasks("5270285b-5a3b-4647-b772-c0b3c52e2b71", childBaseEntityId, this);
    }

    @Override
    public void updateChildCommonPerson(String baseEntityId) {
        interactor.updateChildCommonPerson(baseEntityId);
    }

    @Override
    public void updateVisitNotDone(long value) {
        interactor.updateVisitNotDone(value, this);
    }

    @Override
    public void undoVisitNotDone() {
        hideProgressBar();
        getView().showUndoVisitNotDoneView();
    }

    @Override
    public void fetchVisitStatus(String baseEntityId) {
        interactor.refreshChildVisitBar(view.get().getContext(), childBaseEntityId, this);
    }

    @Override
    public void fetchUpcomingServiceAndFamilyDue(String baseEntityId) {
        interactor.refreshUpcomingServiceAndFamilyDue(view.get().getContext(), getFamilyId(), childBaseEntityId, this);
    }

    public String getFamilyId() {
        return familyID;
    }

    @Override
    public void processBackGroundEvent() {
        interactor.processBackGroundEvent(this);

    }

    @Override
    public void createSickChildEvent(AllSharedPreferences allSharedPreferences, String jsonString) throws Exception {
        interactor.createSickChildEvent(allSharedPreferences, jsonString);
    }

    public void setView(WeakReference<CoreChildProfileContract.View> view) {
        this.view = view;
    }

    @Override
    public void updateChildVisit(ChildVisit childVisit) {
        if (childVisit != null) {
            if (childVisit.getVisitStatus().equalsIgnoreCase(CoreConstants.VisitType.DUE.name())) {
                getView().setVisitButtonDueStatus();
            }
            if (childVisit.getVisitStatus().equalsIgnoreCase(CoreConstants.VisitType.OVERDUE.name())) {
                getView().setVisitButtonOverdueStatus();
            }
            if (childVisit.getVisitStatus().equalsIgnoreCase(CoreConstants.VisitType.LESS_TWENTY_FOUR.name())) {
                getView().setVisitLessTwentyFourView(childVisit.getLastVisitMonthName());
            }
            if (childVisit.getVisitStatus().equalsIgnoreCase(CoreConstants.VisitType.VISIT_THIS_MONTH.name())) {
                getView().setVisitAboveTwentyFourView();
            }
            if (childVisit.getVisitStatus().equalsIgnoreCase(CoreConstants.VisitType.NOT_VISIT_THIS_MONTH.name())) {
                getView().setVisitNotDoneThisMonth();
            }
            if (childVisit.getLastVisitTime() != 0) {
                getView().setLastVisitRowView(childVisit.getLastVisitDays());
            }
            if (!childVisit.getVisitStatus().equalsIgnoreCase(CoreConstants.VisitType.NOT_VISIT_THIS_MONTH.name()) && childVisit.getLastVisitTime() != 0) {
                getView().enableEdit(new Period(new DateTime(childVisit.getLastVisitTime()), DateTime.now()).getHours() <= 24);
            }

        }

    }

    @Override
    public void updateChildService(CoreChildService childService) {
        if (getView() != null) {
            if (childService != null) {
                if (childService.getServiceStatus().equalsIgnoreCase(CoreConstants.ServiceType.UPCOMING.name())) {
                    getView().setServiceNameUpcoming(childService.getServiceName().trim(), childService.getServiceDate());
                } else if (childService.getServiceStatus().equalsIgnoreCase(CoreConstants.ServiceType.OVERDUE.name())) {
                    getView().setServiceNameOverDue(childService.getServiceName().trim(), childService.getServiceDate());
                } else {
                    getView().setServiceNameDue(childService.getServiceName().trim(), childService.getServiceDate());
                }
            } else {
                getView().setServiceNameDue("", "");
            }
        }
    }

    @Override
    public void updateFamilyMemberServiceDue(String serviceDueStatus) {
        if (getView() != null) {
            if (serviceDueStatus.equalsIgnoreCase(CoreConstants.FamilyServiceType.DUE.name())) {
                getView().setFamilyHasServiceDue();
            } else if (serviceDueStatus.equalsIgnoreCase(CoreConstants.FamilyServiceType.OVERDUE.name())) {
                getView().setFamilyHasServiceOverdue();
            } else {
                getView().setFamilyHasNothingDue();
            }
        }

    }

    @Override
    public void startFormForEdit(String title, CommonPersonObjectClient client) {
        JSONObject form = interactor.getAutoPopulatedJsonEditFormString(CoreConstants.JSON_FORM.getChildRegister(), title, getView().getApplicationContext(), client);
        try {

            if (!StringUtils.isBlank(client.getColumnmaps().get(ChildDBConstants.KEY.RELATIONAL_ID))) {
                JSONObject metaDataJson = form.getJSONObject("metadata");
                JSONObject lookup = metaDataJson.getJSONObject("look_up");
                lookup.put("entity_id", "family");
                lookup.put("value", client.getColumnmaps().get(ChildDBConstants.KEY.RELATIONAL_ID));
            }
            getView().startFormActivity(form);
        } catch (Exception e) {
            Timber.e(e, "CoreChildProfilePresenter --> startFormForEdit");
        }
    }

    @Override
    public void startSickChildReferralForm() {
        try {
            getView().startFormActivity(getFormUtils().getFormJson(CoreConstants.JSON_FORM.getChildReferralForm()));
        } catch (Exception e) {
            Timber.e(e);
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
        getView().setAge(Utils.getTranslatedDate(Utils.getDuration(Utils.getValue(client.getColumnmaps(), DBConstants.KEY.DOB, false)), view.get().getContext()));

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
    public void hideProgressBar() {
        if (getView() != null) {
            getView().hideProgressBar();
        }
    }

    @Override
    public void onRegistrationSaved(boolean isEditMode) {
        if (isEditMode) {
            getView().hideProgressDialog();
            getView().refreshProfile(FetchStatus.fetched);
        }
    }

    public FormUtils getFormUtils() throws Exception {
        if (this.formUtils == null) {
            this.formUtils = new FormUtils(getView().getApplicationContext());
        }
        return formUtils;
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
