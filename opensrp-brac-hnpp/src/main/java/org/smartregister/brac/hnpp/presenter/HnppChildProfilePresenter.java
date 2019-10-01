package org.smartregister.brac.hnpp.presenter;

import android.text.TextUtils;
import android.util.Pair;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.smartregister.brac.hnpp.interactor.HnppChildProfileInteractor;
import org.smartregister.brac.hnpp.model.HnppChildRegisterModel;
import org.smartregister.brac.hnpp.utils.HnppConstants;
import org.smartregister.brac.hnpp.utils.HnppJsonFormUtils;
import org.smartregister.chw.core.contract.CoreChildProfileContract;
import org.smartregister.chw.core.presenter.CoreChildProfilePresenter;
import org.smartregister.brac.hnpp.R;
import org.smartregister.brac.hnpp.interactor.HfChildProfileInteractor;
import org.smartregister.brac.hnpp.interactor.HnppFamilyProfileInteractor;
import org.smartregister.chw.core.utils.ChildDBConstants;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.util.DBConstants;
import org.smartregister.family.util.Utils;

import java.lang.ref.WeakReference;

import timber.log.Timber;

public class HnppChildProfilePresenter extends CoreChildProfilePresenter {
    String houseHoldId = "";

    public HnppChildProfilePresenter(CoreChildProfileContract.View childView, CoreChildProfileContract.Model model, String houseHoldId, String childBaseEntityId) {
        this.houseHoldId = houseHoldId;
        setView(new WeakReference<>(childView));
        setInteractor(new HnppChildProfileInteractor());
        setModel(model);
        setChildBaseEntityId(childBaseEntityId);
    }
    @Override
    public void refreshProfileTopSection(CommonPersonObjectClient client) {
        if (client == null || client.getColumnmaps() == null) {
            return;
        }
        String motherName = org.smartregister.family.util.Utils.getValue(client.getColumnmaps(), HnppConstants.KEY.CHILD_MOTHER_NAME, true);
        if(TextUtils.isEmpty(motherName)){
            motherName = org.smartregister.family.util.Utils.getValue(client.getColumnmaps(), HnppConstants.KEY.CHILD_MOTHER_NAME_REGISTERED, true);
        }
        String parentName = view.get().getContext().getResources().getString(org.smartregister.chw.core.R.string.care_giver_initials,motherName);
        getView().setParentName(parentName);
        String firstName = org.smartregister.family.util.Utils.getValue(client.getColumnmaps(), DBConstants.KEY.FIRST_NAME, true);
        String lastName = org.smartregister.family.util.Utils.getValue(client.getColumnmaps(), DBConstants.KEY.LAST_NAME, true);
        String middleName = org.smartregister.family.util.Utils.getValue(client.getColumnmaps(), DBConstants.KEY.MIDDLE_NAME, true);
        String childName = org.smartregister.util.Utils.getName(firstName, middleName + " " + lastName);
        String ageStr = org.smartregister.family.util.Utils.getTranslatedDate(org.smartregister.family.util.Utils.getDuration(org.smartregister.family.util.Utils.getValue(client.getColumnmaps(), DBConstants.KEY.DOB, false)), view.get().getContext());
        String age = view.get().getContext().getResources().getString(org.smartregister.chw.core.R.string.age,ageStr);
        getView().setProfileName(childName);
        getView().setAge(age);

        dob = org.smartregister.family.util.Utils.getValue(client.getColumnmaps(), DBConstants.KEY.DOB, false);

        //dobString = dobString.contains("y") ? dobString.substring(0, dobString.indexOf("y")) : dobString;
        String address = org.smartregister.family.util.Utils.getValue(client.getColumnmaps(), ChildDBConstants.KEY.FAMILY_HOME_ADDRESS, true);
        String gender = org.smartregister.family.util.Utils.getValue(client.getColumnmaps(), DBConstants.KEY.GENDER, true);

        getView().setAddress(address);
        getView().setGender(gender);

        String uniqueId = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.UNIQUE_ID, false);
        uniqueId = String.format(getView().getString(org.smartregister.family.R.string.unique_id_text), uniqueId);
        getView().setId(uniqueId);


        getView().setProfileImage(client.getCaseId());

    }

    @Override
    public void verifyHasPhone() {
        new HnppFamilyProfileInteractor().verifyHasPhone(familyID, this);
    }

    @Override
    public void startFormForEdit(String title, CommonPersonObjectClient client) {
        try {
            JSONObject form = HnppJsonFormUtils.getAutoPopulatedJsonEditFormString(CoreConstants.JSON_FORM.getChildRegister(), getView().getApplicationContext(), client, CoreConstants.EventType.UPDATE_CHILD_REGISTRATION);

            if (!StringUtils.isBlank(client.getColumnmaps().get(ChildDBConstants.KEY.RELATIONAL_ID))) {
                JSONObject metaDataJson = form.getJSONObject("metadata");
                JSONObject lookup = metaDataJson.getJSONObject("look_up");
                lookup.put("entity_id", "family");
                lookup.put("value", client.getColumnmaps().get(ChildDBConstants.KEY.RELATIONAL_ID));
            }
            getView().startFormActivity(form);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void updateChildProfile(String jsonString) {
        getView().showProgressDialog(R.string.updating);
        Pair<Client, Event> pair = new HnppChildRegisterModel(houseHoldId,familyID).processRegistration(jsonString);
        if (pair == null) {
            return;
        }

        getInteractor().saveRegistration(pair, jsonString, true, this);
    }
}
