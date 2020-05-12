package org.smartregister.chw.presenter;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.smartregister.chw.activity.AllClientsMemberProfileActivity;
import org.smartregister.chw.anc.util.NCUtils;
import org.smartregister.chw.contract.AllClientsMemberContract;
import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.domain.FamilyMember;
import org.smartregister.chw.dao.FamilyDao;
import org.smartregister.chw.interactor.AllClientsMemberInteractor;
import org.smartregister.chw.model.AllClientsMemberModel;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.contract.FamilyOtherMemberContract;
import org.smartregister.family.contract.FamilyProfileContract;
import org.smartregister.family.domain.FamilyEventClient;
import org.smartregister.family.util.DBConstants;
import org.smartregister.family.util.Utils;

import java.lang.ref.WeakReference;
import java.text.MessageFormat;

import static org.smartregister.util.Utils.getName;

public class AllClientsMemberPresenter implements AllClientsMemberContract.Presenter, FamilyProfileContract.InteractorCallBack, FamilyOtherMemberContract.InteractorCallBack {

    private AllClientsMemberContract.Interactor interactor;

    private WeakReference<AllClientsMemberContract.View> view;
    private String baseEntityId;

    public AllClientsMemberPresenter(AllClientsMemberProfileActivity allClientsMemberProfileActivity, String baseEntityId) {
        this.baseEntityId = baseEntityId;
        interactor = new AllClientsMemberInteractor();
        view = new WeakReference<>(allClientsMemberProfileActivity);
    }

    @Override
    public void updateLocationInfo(String jsonString, String familyBaseEntityId) {
        interactor.updateLocationInfo(jsonString, new AllClientsMemberModel().processJsonForm(jsonString, familyBaseEntityId), this);
    }

    @Override
    public AllClientsMemberContract.View getView() {
        return view.get();
    }

    @Override
    public void startFormForEdit(CommonPersonObjectClient client) {
        //Overridden: Not Needed
    }

    @Override
    public void refreshProfileTopSection(CommonPersonObjectClient client) {
        if (client != null && client.getColumnmaps() != null) {
            String firstName = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.FIRST_NAME, true);
            String middleName = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.MIDDLE_NAME, true);
            String lastName = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.LAST_NAME, true);

            String dob = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.DOB, true);
            int age = StringUtils.isNotBlank(dob) ? Utils.getAgeFromDate(dob) : 0;

            AllClientsMemberProfileActivity currentView = (AllClientsMemberProfileActivity) getView();
            currentView.setProfileName(MessageFormat.format("{0}, {1}", getName(getName(firstName, middleName), lastName), age));
            String gestationAge = CoreChwApplication.ancRegisterRepository().getGaIfAncWoman(client.getCaseId());
            if (gestationAge != null) {
                currentView.setProfileDetailOne(NCUtils.gestationAgeString(gestationAge, currentView.getContext(), true));
            }
            String gender = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.GENDER, true);
            currentView.setProfileDetailOne(gender);
            String villageTown =  FamilyDao.getFamilyDetail(client.getCaseId()).getVillageTown();
            currentView.setProfileDetailTwo(villageTown);
            String uniqueId = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.UNIQUE_ID, false);
            currentView.setProfileDetailThree(String.format(currentView.getString(org.smartregister.chw.core.R.string.id_with_value), uniqueId));
            String entityType = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.ENTITY_TYPE, false);
            currentView.setProfileImage(client.getCaseId(), entityType);
        }
    }

    @Override
    public void onUniqueIdFetched(Triple<String, String, String> triple, String entityId) {
        //Overridden: Not Needed
    }

    @Override
    public void onNoUniqueId() {
        //Overridden: Not Needed
    }

    @Override
    public void onRegistrationSaved(boolean editMode, boolean isSaved, FamilyEventClient familyEventClient) {
        AllClientsMemberProfileActivity view = (AllClientsMemberProfileActivity) getView();
        if (editMode) {
            view.hideProgressDialog();

            refreshProfileView();

            view.refreshList();
        }
    }

    @Override
    public void refreshProfileView() {
        interactor.updateProfileInfo(baseEntityId, this);
    }
}
