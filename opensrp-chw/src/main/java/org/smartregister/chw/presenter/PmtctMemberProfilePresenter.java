package org.smartregister.chw.presenter;

import android.app.Activity;

import org.smartregister.chw.activity.MotherChampionProfileActivity;
import org.smartregister.chw.core.contract.CorePmtctProfileContract;
import org.smartregister.chw.core.presenter.CorePmtctMemberProfilePresenter;
import org.smartregister.chw.model.ReferralTypeModel;
import org.smartregister.chw.pmtct.domain.MemberObject;
import org.smartregister.chw.util.Utils;

import java.util.List;

public class PmtctMemberProfilePresenter extends CorePmtctMemberProfilePresenter {
    public PmtctMemberProfilePresenter(CorePmtctProfileContract.View view, CorePmtctProfileContract.Interactor interactor, MemberObject memberObject) {
        super(view, interactor, memberObject);
    }

    public void referToFacility() {
        List<ReferralTypeModel> referralTypeModels = ((MotherChampionProfileActivity) getView()).getReferralTypeModels();
        Utils.launchClientReferralActivity((Activity) getView(), referralTypeModels, memberObject.getBaseEntityId());
    }
}
