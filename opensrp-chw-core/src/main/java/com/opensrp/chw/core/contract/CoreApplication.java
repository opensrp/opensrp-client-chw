package com.opensrp.chw.core.contract;

import com.opensrp.chw.core.helper.RulesEngineHelper;

import org.smartregister.Context;
import org.smartregister.sync.helper.ECSyncHelper;

public interface CoreApplication {
    void saveLanguage(String language);

    Context getContext();

    ECSyncHelper getEcSyncHelper();

    void notifyAppContextChange();

    RulesEngineHelper getRulesEngineHelper();
}
