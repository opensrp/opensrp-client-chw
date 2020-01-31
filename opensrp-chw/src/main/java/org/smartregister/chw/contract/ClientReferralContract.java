package org.smartregister.chw.contract;

import org.json.JSONObject;
import org.smartregister.chw.model.ReferralTypeModel;
import org.smartregister.util.FormUtils;

public interface ClientReferralContract {
    interface View {

        void setUpView();

        void startReferralForm(JSONObject jsonObject, ReferralTypeModel referralTypeModel);

        FormUtils getFormUtils() throws Exception;

    }
}
