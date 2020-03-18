package org.smartregister.chw.configs;

import android.database.Cursor;

import androidx.annotation.NonNull;

import org.smartregister.chw.core.configs.CoreAllClientsRegisterRowOptions;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.opd.holders.OpdRegisterViewHolder;
import org.smartregister.view.contract.SmartRegisterClient;

public class AllClientsRegisterRowOptions extends CoreAllClientsRegisterRowOptions {

    @Override
    public void populateClientRow(@NonNull Cursor cursor, @NonNull CommonPersonObjectClient commonPersonObjectClient, @NonNull SmartRegisterClient smartRegisterClient, @NonNull OpdRegisterViewHolder opdRegisterViewHolder) {
//        if (opdRegisterViewHolder instanceof CoreAllClientsRegisterViewHolder) {
//            CoreAllClientsRegisterViewHolder allClientsRegisterViewHolder = (CoreAllClientsRegisterViewHolder) opdRegisterViewHolder;
//            String registerType = commonPersonObjectClient.getDetails().get(HfReferralUtils.REGISTER_TYPE);
//            HfReferralUtils.displayReferralDay(commonPersonObjectClient, HfReferralUtils.getTaskFocus(registerType), allClientsRegisterViewHolder.textViewReferralDay);
//        }
    }
}
