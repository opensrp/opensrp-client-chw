package org.smartregister.chw.presenter;

import android.app.Activity;
import android.content.Context;

import org.smartregister.chw.activity.AncMemberProfileActivity;
import org.smartregister.chw.activity.ReferralRegistrationActivity;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.core.contract.AncMemberProfileContract;
import org.smartregister.chw.core.presenter.CoreAncMemberProfilePresenter;
import org.smartregister.chw.model.ReferralTypeModel;
import org.smartregister.chw.util.Constants;
import org.smartregister.chw.util.Utils;
import org.smartregister.util.FormUtils;

import java.util.List;
import timber.log.Timber;

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

    public void startReferralForm(Context context, String baseEntityID) {
        try {
            ReferralRegistrationActivity.startReferralFormActivityForResults(((Activity)context),baseEntityID,FormUtils.getInstance(context).getFormJson(Constants.JSON_FORM.getChildReferralForm()));
        } catch (Exception var2) {
            Timber.e(var2);
        }

    }
}
