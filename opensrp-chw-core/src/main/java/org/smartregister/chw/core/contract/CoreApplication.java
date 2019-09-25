package org.smartregister.chw.core.contract;

import android.util.Pair;

import org.smartregister.Context;
import org.smartregister.chw.core.helper.RulesEngineHelper;
import org.smartregister.family.domain.FamilyMetadata;
import org.smartregister.sync.helper.ECSyncHelper;

import java.util.ArrayList;

public interface CoreApplication {
    void saveLanguage(String language);

    Context getContext();

    ECSyncHelper getEcSyncHelper();

    void notifyAppContextChange();

    RulesEngineHelper getRulesEngineHelper();

    FamilyMetadata getMetadata();

    ArrayList<String> getAllowedLocationLevels();

    ArrayList<String> getFacilityHierarchy();

    ArrayList<Pair<String, String>> getFamilyLocationFields();

    String getDefaultLocationLevel();
}
