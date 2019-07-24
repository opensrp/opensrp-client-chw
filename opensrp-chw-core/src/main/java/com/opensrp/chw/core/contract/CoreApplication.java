package com.opensrp.chw.core.contract;

import org.smartregister.Context;
import org.smartregister.sync.helper.ECSyncHelper;

public interface CoreApplication {
    void saveLanguage(String language);

    Context getContext();

    ECSyncHelper getEcSyncHelper();

    void logoutCurrentUser();

    void notifyAppContextChange();
}
