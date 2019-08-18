package org.smartregister.chw.hf.utils;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.smartregister.CoreLibrary;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.domain.Task;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.TaskRepository;

import java.util.List;
import java.util.Random;
import java.util.UUID;

public class TestDataUtils {


    private static final String TEST_DATA_POPULATED = "TEST_DATA_POPULATED";
    private TaskRepository taskRepository = CoreLibrary.getInstance().context().getTaskRepository();
    private CommonRepository childRepository = CoreLibrary.getInstance().context().commonrepository(CoreConstants.TABLE_NAME.CHILD);
    private AllSharedPreferences sharedPreferences = CoreLibrary.getInstance().context().allSharedPreferences();

    public void populateTasks() {

        if (StringUtils.isBlank(sharedPreferences.getPreference(TEST_DATA_POPULATED))) {
            List<String> ids = childRepository.findSearchIds("select base_entity_id from ec_child");
            Random random = new Random();
            for (String id : ids) {
                //generate tasks for 1/3 of children
                if (random.nextInt(2) == 0) {
                    generateTask(id, "Referred", "Referral");
                }
            }

            if (!ids.isEmpty()) {
                sharedPreferences.savePreference(TEST_DATA_POPULATED, "true");
            }
        }
    }

    private Task generateTask(String entityId, String businessStatus, String intervention) {
        Task task = new Task();
        DateTime now = new DateTime();
        task.setIdentifier(UUID.randomUUID().toString());
        task.setPlanIdentifier("plan1");
        task.setGroupIdentifier("group1");
        task.setStatus(Task.TaskStatus.READY);
        task.setBusinessStatus(businessStatus);
        task.setPriority(3);
        task.setCode(intervention);
        task.setDescription("referralDescription");
        task.setFocus(intervention);
        task.setForEntity(entityId);
        task.setExecutionStartDate(now);
        task.setAuthoredOn(now);
        task.setLastModified(now);
        task.setOwner(sharedPreferences.fetchRegisteredANM());
        task.setRequester(sharedPreferences.fetchRegisteredANM());
        task.setLocation(sharedPreferences.fetchUserLocalityId(sharedPreferences.fetchRegisteredANM()));
        taskRepository.addOrUpdate(task);
        return task;
    }
}
