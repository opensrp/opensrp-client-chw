package org.smartregister.brac.hnpp.sync.helper;

import org.smartregister.CoreLibrary;
import org.smartregister.chw.core.utils.CoreConstants;
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
        ArrayList<String> allowedLevels = new ArrayList<>();
        allowedLevels.add(CoreConstants.CONFIGURATION.HEALTH_FACILITY_TAG);
        List<String> locations = new ArrayList<>();
        locations.add(locationHelper.getOpenMrsLocationId(locationHelper.generateDefaultLocationHierarchy(allowedLevels).get(0)));
        return locations;
    }

    @Override
    protected Set<String> getPlanDefinitionIds() {
        return Collections.singleton("5270285b-5a3b-4647-b772-c0b3c52e2b71");
    }
}
