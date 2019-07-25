package org.smartregister.chw.model;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.util.JsonFormUtils;
import org.smartregister.chw.util.WashCheck;
import java.util.ArrayList;

public class WashCheckModel  {
    private String familyId;

    public WashCheckModel(String familyId){
        this.familyId = familyId;
    }

    public ArrayList<WashCheck> getAllWashCheckList() {
        return ChwApplication.getWashCheckRepo().getAllWashCheckTask(familyId);
    }
    public WashCheck getLatestWashCheck(){

        return ChwApplication.getWashCheckRepo().getLatestEntry(familyId);
    }
    public boolean saveWashCheckEvent(String jsonString){

        return JsonFormUtils.saveWashCheckEvent(jsonString,familyId);
    }

}
