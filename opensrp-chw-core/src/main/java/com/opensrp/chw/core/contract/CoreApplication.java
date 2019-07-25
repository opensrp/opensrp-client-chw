package com.opensrp.chw.core.contract;

import org.jetbrains.annotations.NotNull;
import org.smartregister.Context;
import org.smartregister.sync.helper.ECSyncHelper;

import java.util.Map;

public interface CoreApplication {
    @NotNull Map<String, Class> getRegisteredActivities();

    void saveLanguage(String language);

    Context getContext();

    ECSyncHelper getEcSyncHelper();

    void logoutCurrentUser();

    void notifyAppContextChange();
}
