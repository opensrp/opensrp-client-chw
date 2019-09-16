package org.smartregister.brac.hnpp.presenter;

import org.json.JSONObject;
import org.smartregister.brac.hnpp.model.ChildRegisterModel;
import org.smartregister.brac.hnpp.utils.JsonFormUtils;
import org.smartregister.chw.core.contract.FamilyProfileExtendedContract;
import org.smartregister.chw.core.model.CoreChildRegisterModel;
import org.smartregister.chw.core.presenter.CoreFamilyProfilePresenter;
import org.smartregister.brac.hnpp.interactor.HnppFamilyProfileInteractor;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.CoreJsonFormUtils;
import org.smartregister.chw.core.utils.Utils;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.contract.FamilyProfileContract;

import timber.log.Timber;

public class FamilyProfilePresenter extends CoreFamilyProfilePresenter {

    public FamilyProfilePresenter(FamilyProfileExtendedContract.View loginView, FamilyProfileContract.Model model, String familyBaseEntityId, String familyHead, String primaryCaregiver, String familyName) {
        super(loginView, model, familyBaseEntityId, familyHead, primaryCaregiver, familyName);
        interactor = new HnppFamilyProfileInteractor();
        verifyHasPhone();
    }

    @Override
    public void startFormForEdit(CommonPersonObjectClient client) {
        JSONObject form =JsonFormUtils.getAutoPopulatedJsonEditFormString(CoreConstants.JSON_FORM.getFamilyDetailsRegister(), getView().getApplicationContext(), client, Utils.metadata().familyRegister.updateEventType);
        try {
            getView().startFormActivity(form);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    @Override
    protected CoreChildRegisterModel getChildRegisterModel() {
        return new ChildRegisterModel();
    }

}
