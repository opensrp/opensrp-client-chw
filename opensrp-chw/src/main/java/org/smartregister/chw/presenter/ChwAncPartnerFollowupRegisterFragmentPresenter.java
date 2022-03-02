package org.smartregister.chw.presenter;

import org.smartregister.chw.anc.contract.BaseAncRegisterFragmentContract;
import org.smartregister.chw.anc.util.DBConstants;
import org.smartregister.chw.core.presenter.AncRegisterFragmentPresenter;
import org.smartregister.chw.core.utils.CoreConstants;

public class ChwAncPartnerFollowupRegisterFragmentPresenter extends AncRegisterFragmentPresenter {

    public ChwAncPartnerFollowupRegisterFragmentPresenter(BaseAncRegisterFragmentContract.View view, BaseAncRegisterFragmentContract.Model model, String viewConfigurationIdentifier) {
        super(view, model, viewConfigurationIdentifier);
    }

    @Override
    public String getDefaultSortQuery() {
        return " MAX(ec_anc_register.last_interacted_with , ifnull(VISIT_SUMMARY.visit_date,0)) DESC ";
    }


    @Override
    public String getMainCondition() {
        return " " + CoreConstants.TABLE_NAME.FAMILY_MEMBER + "." + DBConstants.KEY.DATE_REMOVED + " is null " +
                "AND " + CoreConstants.TABLE_NAME.ANC_MEMBER + "." + DBConstants.KEY.IS_CLOSED + " is 0 ";
    }

    @Override
    public String getMainTable() {
        return CoreConstants.TABLE_NAME.ANC_MEMBER;
    }
}
