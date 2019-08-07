package com.opensrp.chw.hf.sync.helper;

import org.smartregister.CoreLibrary;
import org.smartregister.repository.TaskRepository;
import org.smartregister.sync.helper.TaskServiceHelper;

import java.util.List;

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
        //// TODO: 07/08/19 Make sure I return all facility ID's
        return super.getLocationIds();
    }
}
