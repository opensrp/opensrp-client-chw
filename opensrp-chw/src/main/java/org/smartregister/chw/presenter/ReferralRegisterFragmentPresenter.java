package org.smartregister.chw.presenter;

import org.smartregister.chw.R;
import org.smartregister.chw.referral.contract.BaseReferralRegisterFragmentContract;
import org.smartregister.chw.referral.presenter.BaseReferralRegisterFragmentPresenter;
import org.smartregister.chw.referral.util.DBConstants;
import org.smartregister.chw.util.Constants;

public class ReferralRegisterFragmentPresenter extends BaseReferralRegisterFragmentPresenter {

    public ReferralRegisterFragmentPresenter(BaseReferralRegisterFragmentContract.View view, BaseReferralRegisterFragmentContract.Model model, String viewConfigurationIdentifier) {
        super(view, model, viewConfigurationIdentifier);
    }

    @Override
    public String getMainCondition() {
        return " " + Constants.TABLE_NAME.FAMILY_MEMBER + "." + DBConstants.KEY.DATE_REMOVED + " is null " +
                "AND " + org.smartregister.chw.referral.util.Constants.TABLES.REFERRAL + "." + DBConstants.KEY.REFERRAL_STATUS + " = '" + org.smartregister.chw.referral.util.Constants.REFERRAL_STATUS.PENDING + "' "+
                "AND " + org.smartregister.chw.referral.util.Constants.TABLES.REFERRAL + "." + DBConstants.KEY.REFERRAL_TYPE + " = '" + org.smartregister.chw.referral.util.Constants.REFERRAL_TYPE.COMMUNITY_TO_FACILITY_REFERRAL + "' ";

    }

    @Override
    public void processViewConfigurations() {
        super.processViewConfigurations();
        if (config.getSearchBarText() != null && getView() != null) {
            getView().updateSearchBarHint(getView().getContext().getString(R.string.search_name_or_id));
        }
    }

    @Override
    public String getMainTable() {
        return org.smartregister.chw.referral.util.Constants.TABLES.REFERRAL;
    }
}
