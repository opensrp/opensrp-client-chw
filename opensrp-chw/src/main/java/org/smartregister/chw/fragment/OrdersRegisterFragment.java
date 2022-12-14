package org.smartregister.chw.fragment;

import android.content.Intent;

import org.json.JSONObject;
import org.smartregister.chw.activity.OrderDetailsActivity;
import org.smartregister.chw.cdp.util.Constants;
import org.smartregister.chw.core.fragment.CoreOrdersRegisterFragment;
import org.smartregister.chw.presenter.OrdersRegisterFragmentPresenter;
import org.smartregister.chw.util.JsonFormUtils;
import org.smartregister.commonregistry.CommonPersonObjectClient;

import static org.smartregister.chw.core.utils.FormUtils.getStartFormActivity;

public class OrdersRegisterFragment extends CoreOrdersRegisterFragment {

    @Override
    public void startOrderForm() {
        try {
            JSONObject form = model().getOrderFormAsJson(Constants.FORMS.CDP_CONDOM_ORDER);
            Intent startFormIntent = getStartFormActivity(form, null, requireActivity());
            requireActivity().startActivityForResult(startFormIntent, JsonFormUtils.REQUEST_CODE_GET_JSON);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showDetails(CommonPersonObjectClient cp) {
        OrderDetailsActivity.startMe(requireActivity(), cp);
    }

    @Override
    protected void initializePresenter() {
       presenter = new OrdersRegisterFragmentPresenter(this, model());
    }


}
