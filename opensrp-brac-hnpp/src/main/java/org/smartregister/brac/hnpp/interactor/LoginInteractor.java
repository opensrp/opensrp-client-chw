package org.smartregister.brac.hnpp.interactor;

import org.smartregister.brac.hnpp.BuildConfig;
import org.smartregister.brac.hnpp.job.SSLocationFetchJob;
import org.smartregister.brac.hnpp.job.PullHouseholdIdsServiceJob;
import org.smartregister.brac.hnpp.job.VisitLogServiceJob;
import org.smartregister.chw.core.job.HomeVisitServiceJob;
import org.smartregister.job.PullUniqueIdsServiceJob;
import org.smartregister.job.SyncServiceJob;
import org.smartregister.login.interactor.BaseLoginInteractor;
import org.smartregister.view.contract.BaseLoginContract;

import java.util.concurrent.TimeUnit;


public class LoginInteractor extends BaseLoginInteractor implements BaseLoginContract.Interactor {
    public LoginInteractor(BaseLoginContract.Presenter loginPresenter) {
        super(loginPresenter);
    }

    @Override
    protected void scheduleJobsPeriodically() {
        SyncServiceJob.scheduleJob(SyncServiceJob.TAG, TimeUnit.MINUTES.toMinutes(
                BuildConfig.DATA_SYNC_DURATION_MINUTES), getFlexValue(BuildConfig.DATA_SYNC_DURATION_MINUTES));
////
////        ImageUploadServiceJob.scheduleJob(ImageUploadServiceJob.TAG, TimeUnit.MINUTES.toMinutes(
////                BuildConfig.IMAGE_UPLOAD_MINUTES), getFlexValue(BuildConfig.IMAGE_UPLOAD_MINUTES));
////
//        PullUniqueIdsServiceJob.scheduleJob(PullUniqueIdsServiceJob.TAG, TimeUnit.MINUTES.toMinutes(
//                BuildConfig.PULL_UNIQUE_IDS_MINUTES), getFlexValue(BuildConfig.PULL_UNIQUE_IDS_MINUTES));
//        SSLocationFetchJob.scheduleJob(PullUniqueIdsServiceJob.TAG, TimeUnit.MINUTES.toMinutes(
//                BuildConfig.PULL_SSLOCATION), getFlexValue(BuildConfig.PULL_SSLOCATION));
//        PullHouseholdIdsServiceJob.scheduleJob(PullHouseholdIdsServiceJob.TAG,
//                TimeUnit.MINUTES.toMinutes(BuildConfig.PULL_UNIQUE_IDS_MINUTES), getFlexValue(BuildConfig.PULL_UNIQUE_IDS_MINUTES));
////
////        VaccineRecurringServiceJob.scheduleJob(VaccineRecurringServiceJob.TAG, TimeUnit.MINUTES.toMinutes(
////                BuildConfig.VACCINE_SYNC_PROCESSING_MINUTES), getFlexValue(BuildConfig.VACCINE_SYNC_PROCESSING_MINUTES));
////
        HomeVisitServiceJob.scheduleJob(HomeVisitServiceJob.TAG, TimeUnit.MINUTES.toMinutes(
                BuildConfig.HOME_VISIT_MINUTES), getFlexValue(BuildConfig.HOME_VISIT_MINUTES));
////
////        SyncTaskServiceJob.scheduleJob(SyncTaskServiceJob.TAG, TimeUnit.MINUTES.toMinutes(
////                BuildConfig.DATA_SYNC_DURATION_MINUTES), getFlexValue(BuildConfig.DATA_SYNC_DURATION_MINUTES));

    }

    @Override
    protected void scheduleJobsImmediately() {
        PullUniqueIdsServiceJob.scheduleJobImmediately(PullUniqueIdsServiceJob.TAG);
        PullHouseholdIdsServiceJob.scheduleJobImmediately(PullHouseholdIdsServiceJob.TAG);
        SSLocationFetchJob.scheduleJobImmediately(SSLocationFetchJob.TAG);
        SyncServiceJob.scheduleJobImmediately(SyncServiceJob.TAG);
        HomeVisitServiceJob.scheduleJobImmediately(HomeVisitServiceJob.TAG);
        VisitLogServiceJob.scheduleJobImmediately(VisitLogServiceJob.TAG);
    }
}
