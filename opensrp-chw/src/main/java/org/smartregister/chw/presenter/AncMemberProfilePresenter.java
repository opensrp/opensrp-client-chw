package org.smartregister.chw.presenter;

import android.app.Activity;

import org.smartregister.chw.activity.AncMemberProfileActivity;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.core.contract.AncMemberProfileContract;
import org.smartregister.chw.core.presenter.CoreAncMemberProfilePresenter;
import org.smartregister.chw.model.ReferralTypeModel;
import org.smartregister.chw.util.Utils;

import java.util.List;

public class AncMemberProfilePresenter extends CoreAncMemberProfilePresenter
        implements org.smartregister.chw.contract.AncMemberProfileContract.Presenter {

    public AncMemberProfilePresenter(AncMemberProfileContract.View view, AncMemberProfileContract.Interactor interactor,
                                     MemberObject memberObject) {
        super(view, interactor, memberObject);
    }

    @Override
    public void referToFacility() {
        List<ReferralTypeModel> referralTypeModels = ((AncMemberProfileActivity) getView()).getReferralTypeModels();
        if (referralTypeModels.size() == 1) {
            startAncReferralForm();
        } else {
            Utils.launchClientReferralActivity((Activity) getView(), referralTypeModels, getEntityId());
        }
    }
}
