package org.smartregister.brac.hnpp.presenter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.Context;
import android.os.Build;

import com.evernote.android.job.JobManager;

import org.smartregister.brac.hnpp.job.PullHouseholdIdsServiceJob;
import org.smartregister.brac.hnpp.job.VisitLogServiceJob;
import org.smartregister.chw.core.contract.CoreApplication;
import org.smartregister.chw.core.contract.NavigationContract;
import org.smartregister.chw.core.model.NavigationModel;
import org.smartregister.chw.core.presenter.NavigationPresenter;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.job.PullUniqueIdsServiceJob;
import org.smartregister.job.SyncServiceJob;

public class HnppNavigationPresenter extends NavigationPresenter {
    public HnppNavigationPresenter(CoreApplication application, NavigationContract.View view, NavigationModel.Flavor modelFlavor) {
        super(application, view, modelFlavor);
    }

    @Override
    protected void initialize() {
        super.initialize();
        tableMap.put(CoreConstants.DrawerMenu.ALL_MEMBER, CoreConstants.TABLE_NAME.FAMILY_MEMBER);
    }

    @Override
    public void sync(Activity activity) {
        SyncServiceJob.scheduleJobImmediately(SyncServiceJob.TAG);
        PullUniqueIdsServiceJob.scheduleJobImmediately(PullUniqueIdsServiceJob.TAG);
        PullHouseholdIdsServiceJob.scheduleJobImmediately(PullHouseholdIdsServiceJob.TAG);
        VisitLogServiceJob.scheduleJobImmediately(VisitLogServiceJob.TAG);

//        if (JobManager.instance().getAllJobRequestsForTag(VisitLogServiceJob.TAG).isEmpty()){
//        }

    }

}
