package org.smartregister.chw.presenter;

import android.app.Activity;

import org.smartregister.chw.activity.FamilyPlanningMemberProfileActivity;
import org.smartregister.chw.contract.FamilyPlanningMemberProfileContract;
import org.smartregister.chw.core.presenter.CoreFamilyPlanningProfilePresenter;
import org.smartregister.chw.fp.domain.FpMemberObject;
import org.smartregister.chw.model.ReferralTypeModel;
import org.smartregister.chw.util.Utils;

import java.util.List;

public class FamilyPlanningMemberProfilePresenter extends CoreFamilyPlanningProfilePresenter
        implements org.smartregister.chw.contract.AncMemberProfileContract.Presenter {

    private FpMemberObject fpMemberObject;

    public FamilyPlanningMemberProfilePresenter(FamilyPlanningMemberProfileContract.View view, FamilyPlanningMemberProfileContract.Interactor interactor,
                                                FpMemberObject fpMemberObject) {
        super(view, interactor, fpMemberObject);
        this.fpMemberObject = fpMemberObject;
    }

    @Override
    public void referToFacility() {
        List<ReferralTypeModel> referralTypeModels = ((FamilyPlanningMemberProfileActivity) getView()).getReferralTypeModels();
        if (referralTypeModels.size() == 1) {
            startFamilyPlanningReferral();
        } else {
            Utils.launchClientReferralActivity((Activity) getView(), referralTypeModels, fpMemberObject.getBaseEntityId());
        }
    }
}
