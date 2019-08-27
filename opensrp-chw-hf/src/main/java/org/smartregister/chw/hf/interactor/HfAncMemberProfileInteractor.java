package org.smartregister.chw.hf.interactor;

import android.content.Context;

import org.joda.time.DateTime;
import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.interactor.CoreAncMemberProfileInteractor;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.hf.contract.AncMemberProfileContract;
import org.smartregister.domain.Task;
import org.smartregister.repository.BaseRepository;

import java.util.Set;

public class HfAncMemberProfileInteractor extends CoreAncMemberProfileInteractor implements AncMemberProfileContract.Interactor {
    public HfAncMemberProfileInteractor(Context context) {
        super(context);
    }

    @Override
    public void getClientTasks(String planId, String baseEntityId, AncMemberProfileContract.InteractorCallBack callback) {
        Set<Task> taskList = CoreChwApplication.getInstance().getTaskRepository().getTasksByEntityAndStatus(planId, baseEntityId, Task.TaskStatus.READY);
        Task task = new Task();
        task.setIdentifier("iudsfsigdfdsyud");
        task.setFocus("ANC Referral");
        task.setGroupIdentifier("iudsfsigdfdsyud");
        task.setStatus(Task.TaskStatus.READY);
        task.setBusinessStatus(CoreConstants.BUSINESS_STATUS.REFERRED);
        task.setPriority(3);
        task.setCode("Referral");
        task.setDescription("Review and perform the referral for the client"); //set to string
        task.setForEntity(baseEntityId);
        DateTime now = new DateTime();
        task.setExecutionStartDate(now);
        task.setAuthoredOn(now);
        task.setLastModified(now);
        task.setOwner("iudsfsigdfdsyud");
        task.setSyncStatus(BaseRepository.TYPE_Created);
        task.setRequester("iudsfsigdfdsyud");
        task.setLocation("iudsfsigdfdsyud");
        taskList.add(task);
        callback.setClientTasks(taskList);
    }
}
