package org.smartregister.chw.core.contract;

import org.smartregister.Context;
import org.smartregister.chw.core.helper.RulesEngineHelper;
import org.smartregister.family.domain.FamilyMetadata;
import org.smartregister.sync.helper.ECSyncHelper;

public interface CoreApplication {
    void saveLanguage(String language);

    Context getContext();

    ECSyncHelper getEcSyncHelper();

    void notifyAppContextChange();

    RulesEngineHelper getRulesEngineHelper();

    FamilyMetadata getMetadata();
}
