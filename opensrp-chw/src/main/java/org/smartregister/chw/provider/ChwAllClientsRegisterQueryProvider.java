package org.smartregister.chw.provider;

import org.smartregister.chw.core.provider.CoreAllClientsRegisterQueryProvider;
import org.smartregister.chw.util.ChwQueryConstant;

import androidx.annotation.NonNull;

public class ChwAllClientsRegisterQueryProvider extends CoreAllClientsRegisterQueryProvider {
    @NonNull
    @Override
    public String mainSelectWhereIDsIn() {
        return ChwQueryConstant.ALL_CLIENTS_SELECT_QUERY;
    }
}
