package org.smartregister.chw.contract;

import org.json.JSONObject;
import org.smartregister.util.FormUtils;

public interface ClientReferralContract {
    interface View {
        void setUpView();

        void startReferralForm(JSONObject jsonObject);

        FormUtils getFormUtils() throws Exception;

        boolean isReferralForm(String encounterType);
    }
}
