package org.smartregister.chw.presenter;


import android.app.Activity;

import org.apache.commons.lang3.tuple.Triple;
import org.smartregister.chw.anc.contract.BaseAncMemberProfileContract;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.presenter.BaseAncMemberProfilePresenter;
import org.smartregister.chw.contract.PncMemberProfileContract;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.model.ReferralTypeModel;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.contract.FamilyProfileContract;
import org.smartregister.family.domain.FamilyEventClient;
import org.smartregister.family.util.Utils;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.util.FormUtils;

import java.util.List;

import timber.log.Timber;

public class PncMemberProfilePresenter extends BaseAncMemberProfilePresenter implements
        PncMemberProfileContract.Presenter, FamilyProfileContract.InteractorCallBack {

    private FormUtils formUtils;
    private String entityId;

    public PncMemberProfilePresenter(BaseAncMemberProfileContract.View view,
                                     BaseAncMemberProfileContract.Interactor interactor, MemberObject memberObject) {
        super(view, interactor, memberObject);
        setEntityId(memberObject.getBaseEntityId());
    }

    public void startFormForEdit(CommonPersonObjectClient commonPersonObject) {
//        TODO Implement
    }

    @Override
    public void refreshProfileTopSection(CommonPersonObjectClient client) {
//        TODO Implement
    }

    @Override
    public void onUniqueIdFetched(Triple<String, String, String> triple, String entityId) {
//        TODO Implement
        Timber.d("onUniqueIdFetched unimplemented");
    }

    @Override
    public void onNoUniqueId() {
//        TODO Implement
        Timber.d("onNoUniqueId unimplemented");
    }

    @Override
    public void onRegistrationSaved(boolean b, boolean b1, FamilyEventClient familyEventClient) {
        // TODO     
    }


    public PncMemberProfileContract.View getView() {
        if (view != null) {
            return (PncMemberProfileContract.View) view.get();
        } else {
            return null;
        }
    }

    @Override
    public void startPncReferralForm() {
        try {
            getView().startFormActivity(getFormUtils().getFormJson(CoreConstants.JSON_FORM.getPncReferralForm()));
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    @Override
    public void referToFacility() {
        List<ReferralTypeModel> referralTypeModels = getView().getReferralTypeModels();
        if (referralTypeModels.size() == 1) {
            startPncReferralForm();
        } else {
            org.smartregister.chw.util.Utils.launchClientReferralActivity((Activity) getView(), referralTypeModels, getEntityId());
        }
    }

    @Override
    public void createReferralEvent(AllSharedPreferences allSharedPreferences, String jsonString) throws Exception {
        ((PncMemberProfileContract.Interactor) interactor).createReferralEvent(allSharedPreferences, jsonString, getEntityId());
    }

    private FormUtils getFormUtils() {
        if (formUtils == null) {
            try {
                formUtils = FormUtils.getInstance(Utils.context().applicationContext());
            } catch (Exception e) {
                Timber.e(e);
            }
        }
        return formUtils;
    }

    public String getEntityId() {
        return entityId;
    }

    private void setEntityId(String entityId) {
        this.entityId = entityId;
    }
}


