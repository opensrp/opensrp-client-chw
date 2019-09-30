package org.smartregister.chw.core.interactor;

import android.content.Context;

import org.jetbrains.annotations.NotNull;
import org.smartregister.chw.anc.AncLibrary;
import org.smartregister.chw.anc.contract.BaseAncMemberProfileContract;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.domain.Visit;
import org.smartregister.chw.anc.interactor.BaseAncMemberProfileInteractor;
import org.smartregister.chw.anc.util.Constants;
import org.smartregister.chw.core.R;
import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.contract.AncMemberProfileContract;
import org.smartregister.chw.core.dao.AncDao;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.CoreReferralUtils;
import org.smartregister.domain.AlertStatus;
import org.smartregister.domain.Task;
import org.smartregister.repository.AllSharedPreferences;

import java.util.Date;
import java.util.Set;

public class CoreAncMemberProfileInteractor extends BaseAncMemberProfileInteractor implements AncMemberProfileContract.Interactor {
    protected Context context;

    public CoreAncMemberProfileInteractor(Context context) {
        this.context = context;
    }

    /**
     * Compute and process the lower profile info
     *
     * @param memberObject
     * @param callback
     */
    @Override
    public void refreshProfileInfo(final MemberObject memberObject, final BaseAncMemberProfileContract.InteractorCallBack callback) {
        Runnable runnable = new Runnable() {

            Date lastVisitDate = getLastVisitDate(memberObject);

            @Override
            public void run() {
                appExecutors.mainThread().execute(() -> {
                    callback.refreshLastVisit(lastVisitDate);
                    callback.refreshFamilyStatus(AlertStatus.normal);
                    callback.refreshUpComingServicesStatus(context.getString(R.string.anc_visit), AlertStatus.normal, new Date());
                });
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public MemberObject getMemberClient(String memberID) {
        // read all the member details from the database
        return AncDao.getMember(memberID);
    }

    protected Date getLastVisitDate(MemberObject memberObject) {
        Date lastVisitDate = null;
        Visit lastVisit = AncLibrary.getInstance().visitRepository().getLatestVisit(memberObject.getBaseEntityId(), Constants.EVENT_TYPE.ANC_HOME_VISIT);
        if (lastVisit != null) {
            lastVisitDate = lastVisit.getDate();
        }

        return lastVisitDate;
    }

    @Override
    public void createReferralEvent(AllSharedPreferences allSharedPreferences, String jsonString, String entityId) throws Exception {
        CoreReferralUtils.createReferralEvent(allSharedPreferences, jsonString, CoreConstants.TABLE_NAME.ANC_REFERRAL, entityId);
    }

    @Override
    public void getClientTasks(String planId, String baseEntityId, @NotNull AncMemberProfileContract.InteractorCallBack callback) {
        Set<Task> taskList = CoreChwApplication.getInstance().getTaskRepository().getTasksByEntityAndStatus(planId, baseEntityId, Task.TaskStatus.READY);
        callback.setClientTasks(taskList);
    }
}
