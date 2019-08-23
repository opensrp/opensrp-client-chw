package org.smartregister.chw.model;

import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.core.utils.WashCheck;
import org.smartregister.chw.util.JsonFormUtils;

import java.util.ArrayList;

public class WashCheckModel {
    private String familyId;

    public WashCheckModel(String familyId) {
        this.familyId = familyId;
    }

    public ArrayList<WashCheck> getAllWashCheckList() {
        return ChwApplication.getWashCheckRepository().getAllWashCheckTask(familyId);
    }

    public WashCheck getLatestWashCheck() {

        return ChwApplication.getWashCheckRepository().getLatestEntry(familyId);
    }

    public boolean saveWashCheckEvent(String jsonString) {

        return JsonFormUtils.saveWashCheckEvent(jsonString, familyId);
    }

}
