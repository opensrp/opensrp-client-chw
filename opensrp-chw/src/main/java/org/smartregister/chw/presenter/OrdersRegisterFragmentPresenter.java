package org.smartregister.chw.presenter;

import org.smartregister.chw.cdp.contract.BaseOrdersRegisterFragmentContract;
import org.smartregister.chw.cdp.presenter.BaseOrdersRegisterFragmentPresenter;
import org.smartregister.chw.cdp.util.Constants;
import org.smartregister.chw.cdp.util.DBConstants;

public class OrdersRegisterFragmentPresenter extends BaseOrdersRegisterFragmentPresenter {
    public OrdersRegisterFragmentPresenter(BaseOrdersRegisterFragmentContract.View view, BaseOrdersRegisterFragmentContract.Model model) {
        super(view, model);
    }

    @Override
    public String getMainCondition() {
        return super.getMainCondition() + " AND " + getMainTable() + "." + DBConstants.KEY.REQUEST_TYPE + " = '" + Constants.ORDER_TYPES.COMMUNITY_TO_FACILITY_ORDER + "' ";
    }
}
