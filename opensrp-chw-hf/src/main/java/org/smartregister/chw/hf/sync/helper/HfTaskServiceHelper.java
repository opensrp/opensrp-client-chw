package org.smartregister.chw.hf.sync.helper;

import org.smartregister.CoreLibrary;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.hf.HealthFacilityApplication;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.repository.TaskRepository;
import org.smartregister.sync.helper.TaskServiceHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class HfTaskServiceHelper extends TaskServiceHelper {
    protected static HfTaskServiceHelper instance;

    public HfTaskServiceHelper(TaskRepository taskRepository) {
        super(taskRepository);
    }

    public static HfTaskServiceHelper getInstance() {
        if (instance == null) {
            instance = new HfTaskServiceHelper(CoreLibrary.getInstance().context().getTaskRepository());
        }
        return instance;
    }

    @Override
    protected List<String> getLocationIds() {
        LocationHelper locationHelper = LocationHelper.getInstance();
        ArrayList<String> allowedLevels = HealthFacilityApplication.getInstance().getAllowedLocationLevels();
        List<String> locations = new ArrayList<>();
        if (allowedLevels != null) {
            List<String> locationIds = locationHelper.generateDefaultLocationHierarchy(allowedLevels);
            if (locationIds != null) {
                locations.add(locationHelper.getOpenMrsLocationId(locationIds.get(0)));
            }
        }
        return locations;
    }

    @Override
    protected Set<String> getPlanDefinitionIds() {
        return Collections.singleton(CoreConstants.REFERRAL_PLAN_ID);
    }
}
