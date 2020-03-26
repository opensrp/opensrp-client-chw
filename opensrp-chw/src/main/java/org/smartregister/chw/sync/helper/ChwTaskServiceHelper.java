package org.smartregister.chw.sync.helper;

import org.smartregister.CoreLibrary;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.domain.Task;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.repository.TaskRepository;
import org.smartregister.sync.helper.TaskServiceHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ChwTaskServiceHelper extends TaskServiceHelper {
    private static ChwTaskServiceHelper instance;

    private ChwTaskServiceHelper(TaskRepository taskRepository) {
        super(taskRepository);
        setSyncByGroupIdentifier(false);
    }

    public static ChwTaskServiceHelper getInstance() {
        if (instance == null) {
            instance = new ChwTaskServiceHelper(CoreLibrary.getInstance().context().getTaskRepository());
        }
        return instance;
    }

    @Override
    protected List<String> getLocationIds() {
        LocationHelper locationHelper = LocationHelper.getInstance();
        ArrayList<String> allowedLevels = ChwApplication.getInstance().getAllowedLocationLevels();
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

    @Override
    public List<Task> fetchTasksFromServer() {
        return super.fetchTasksFromServer();
    }
}
