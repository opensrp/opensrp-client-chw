package org.smartregister.chw.presenter;

import org.smartregister.chw.model.IssueReferralActivityModel;
import org.smartregister.chw.referral.contract.BaseIssueReferralContract;
import org.smartregister.chw.referral.model.AbstractIssueReferralModel;
import org.smartregister.chw.referral.presenter.BaseIssueReferralPresenter;
import org.smartregister.chw.referral.util.DBConstants;
import org.smartregister.chw.util.Constants;

public class IssueReferralActivityPresenter extends BaseIssueReferralPresenter {

    public IssueReferralActivityPresenter(String baseEntityId, BaseIssueReferralContract.View view, Class<? extends AbstractIssueReferralModel> viewModelClass, BaseIssueReferralContract.Interactor interactor) {
        super(baseEntityId, view, viewModelClass, interactor);
    }

    @Override
    public Class<? extends AbstractIssueReferralModel> getViewModel() {
        return IssueReferralActivityModel.class;
    }


    @Override
    public String getMainCondition() {
        return " " + Constants.TABLE_NAME.FAMILY_MEMBER + "." + DBConstants.KEY.BASE_ENTITY_ID + " = '" + getBaseEntityID() + "'";
    }


    @Override
    public String getMainTable() {
        return Constants.TABLE_NAME.FAMILY_MEMBER;
    }
}
