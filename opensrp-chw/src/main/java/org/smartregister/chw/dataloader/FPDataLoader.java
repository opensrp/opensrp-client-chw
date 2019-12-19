package org.smartregister.chw.dataloader;

import org.smartregister.chw.form_data.NativeFormsDataLoader;
import org.smartregister.chw.fp.util.FamilyPlanningConstants;

import java.util.ArrayList;
import java.util.List;

public class FPDataLoader extends NativeFormsDataLoader {

    @Override
    protected List<String> getEventTypes() {
        List<String> res = new ArrayList<>();
        res.add(FamilyPlanningConstants.EventType.FAMILY_PLANNING_REGISTRATION);
        res.add(FamilyPlanningConstants.EventType.UPDATE_FAMILY_PLANNING_REGISTRATION);
        return res;
    }

}
