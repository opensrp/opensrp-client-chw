package org.smartregister.chw.presenter;

import android.content.Context;

import com.opensrp.chw.core.contract.FamilyProfileExtendedContract;
import com.opensrp.chw.core.domain.FamilyMember;
import com.opensrp.chw.core.model.CoreChildRegisterModel;
import com.opensrp.chw.core.presenter.CoreFamilyProfilePresenter;
import com.opensrp.chw.core.utils.CoreJsonFormUtils;

import org.smartregister.chw.interactor.FamilyChangeContractInteractor;
import org.smartregister.chw.interactor.FamilyProfileInteractor;
import org.smartregister.chw.model.ChildRegisterModel;
import org.smartregister.family.contract.FamilyProfileContract;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.view.LocationPickerView;

import timber.log.Timber;

public class FamilyProfilePresenter extends CoreFamilyProfilePresenter {

    public FamilyProfilePresenter(FamilyProfileExtendedContract.View view, FamilyProfileContract.Model model, String familyBaseEntityId, String familyHead, String primaryCaregiver, String familyName) {
        super(view, model, familyBaseEntityId, familyHead, primaryCaregiver, familyName);
        interactor = new FamilyProfileInteractor();
        verifyHasPhone();
    }

    @Override
    protected CoreChildRegisterModel getChildRegisterModel() {
        return new ChildRegisterModel();
    }


    @Override
    public boolean updatePrimaryCareGiver(Context context, String jsonString, String familyBaseEntityId, String entityID) {

        boolean res = false;
        try {
            FamilyMember member = CoreJsonFormUtils.getFamilyMemberFromRegistrationForm(jsonString, familyBaseEntityId, entityID);
            if (member != null && member.getPrimaryCareGiver()) {
                LocationPickerView lpv = new LocationPickerView(context);
                lpv.init();
                String lastLocationId = LocationHelper.getInstance().getOpenMrsLocationId(lpv.getSelectedItem());

                new FamilyChangeContractInteractor().updateFamilyRelations(context, member, lastLocationId);
                res = true;
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        return res;
    }
}
