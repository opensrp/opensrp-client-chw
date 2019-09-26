package org.smartregister.chw.hf.interactor;

import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.interactor.CorePncMemberProfileInteractor;
import org.smartregister.chw.hf.contract.PncMemberProfileContract;
import org.smartregister.domain.Task;

import java.util.Set;

public class PncMemberProfileInteractor extends CorePncMemberProfileInteractor
        implements PncMemberProfileContract.Interactor {

    @Override
    public void getReferralTasks(String planId, String baseEntityId,
                                 PncMemberProfileContract.InteractorCallback callback) {

        Set<Task> taskList = CoreChwApplication.getInstance().getTaskRepository()
                .getTasksByEntityAndStatus(planId, baseEntityId, Task.TaskStatus.READY);

        callback.updateReferralTasks(taskList);
    }
}
