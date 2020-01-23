package org.smartregister.chw.interactor;

import org.smartregister.chw.BuildConfig;
import org.smartregister.chw.core.job.ChwIndicatorGeneratingJob;
import org.smartregister.chw.core.job.HomeVisitServiceJob;
import org.smartregister.chw.core.job.VaccineRecurringServiceJob;
import org.smartregister.chw.job.BasePncCloseJob;
import org.smartregister.chw.job.ScheduleJob;
import org.smartregister.immunization.job.VaccineServiceJob;
import org.smartregister.job.DistrictFacilitiesServiceJob;
import org.smartregister.job.ImageUploadServiceJob;
import org.smartregister.job.PlanIntentServiceJob;
import org.smartregister.job.PullUniqueIdsServiceJob;
import org.smartregister.job.SyncServiceJob;
import org.smartregister.job.SyncTaskServiceJob;
import org.smartregister.login.interactor.BaseLoginInteractor;
import org.smartregister.view.contract.BaseLoginContract;

import java.util.concurrent.TimeUnit;


public class LoginInteractor extends BaseLoginInteractor implements BaseLoginContract.Interactor {

    public LoginInteractor(BaseLoginContract.Presenter loginPresenter) {
        super(loginPresenter);
    }

    @Override
    protected void scheduleJobsPeriodically() {
        SyncServiceJob.scheduleJob(SyncServiceJob.TAG, TimeUnit.MINUTES.toMinutes(BuildConfig.DATA_SYNC_DURATION_MINUTES), getFlexValue(BuildConfig
                .DATA_SYNC_DURATION_MINUTES));

        VaccineRecurringServiceJob.scheduleJob(VaccineRecurringServiceJob.TAG, TimeUnit.MINUTES.toMinutes(BuildConfig.VACCINE_SYNC_PROCESSING_MINUTES), getFlexValue(BuildConfig.VACCINE_SYNC_PROCESSING_MINUTES));

        ImageUploadServiceJob.scheduleJob(ImageUploadServiceJob.TAG, TimeUnit.MINUTES.toMinutes(BuildConfig.IMAGE_UPLOAD_MINUTES), getFlexValue(BuildConfig.IMAGE_UPLOAD_MINUTES));

        PullUniqueIdsServiceJob.scheduleJob(PullUniqueIdsServiceJob.TAG, TimeUnit.MINUTES.toMinutes(BuildConfig.PULL_UNIQUE_IDS_MINUTES), getFlexValue(BuildConfig.PULL_UNIQUE_IDS_MINUTES));

        ChwIndicatorGeneratingJob.scheduleJob(ChwIndicatorGeneratingJob.TAG, TimeUnit.MINUTES.toMinutes(BuildConfig.REPORT_INDICATOR_GENERATION_MINUTES), getFlexValue(BuildConfig.REPORT_INDICATOR_GENERATION_MINUTES));

        HomeVisitServiceJob.scheduleJob(HomeVisitServiceJob.TAG, TimeUnit.MINUTES.toMinutes(BuildConfig.HOME_VISIT_MINUTES), getFlexValue(BuildConfig.HOME_VISIT_MINUTES));

        BasePncCloseJob.scheduleJob(BasePncCloseJob.TAG, TimeUnit.MINUTES.toMinutes(BuildConfig.BASE_PNC_CLOSE_MINUTES), getFlexValue(BuildConfig.BASE_PNC_CLOSE_MINUTES));

        PlanIntentServiceJob.scheduleJob(PlanIntentServiceJob.TAG, TimeUnit.MINUTES.toMinutes(BuildConfig.DATA_SYNC_DURATION_MINUTES), getFlexValue(BuildConfig.DATA_SYNC_DURATION_MINUTES));

        SyncTaskServiceJob.scheduleJob(PlanIntentServiceJob.TAG, TimeUnit.MINUTES.toMinutes(BuildConfig.DATA_SYNC_DURATION_MINUTES), getFlexValue(BuildConfig.DATA_SYNC_DURATION_MINUTES));

        ScheduleJob.scheduleJob(ScheduleJob.TAG, TimeUnit.MINUTES.toMinutes(BuildConfig.SCHEDULE_SERVICE_MINUTES), getFlexValue(BuildConfig.SCHEDULE_SERVICE_MINUTES));

    }

    @Override
    protected void scheduleJobsImmediately() {
        super.scheduleJobsImmediately();
        // Run initial job immediately on log in since the job will run a bit later (~ 15 mins +)
        ScheduleJob.scheduleJobImmediately(ScheduleJob.TAG);
        SyncServiceJob.scheduleJobImmediately(SyncServiceJob.TAG);
        HomeVisitServiceJob.scheduleJobImmediately(HomeVisitServiceJob.TAG);
        BasePncCloseJob.scheduleJobImmediately(BasePncCloseJob.TAG);
        PlanIntentServiceJob.scheduleJobImmediately(PlanIntentServiceJob.TAG);
        SyncTaskServiceJob.scheduleJobImmediately(SyncTaskServiceJob.TAG);
        VaccineServiceJob.scheduleJobImmediately(VaccineServiceJob.TAG);
        VaccineRecurringServiceJob.scheduleJobImmediately(VaccineRecurringServiceJob.TAG);
        DistrictFacilitiesServiceJob.scheduleJobImmediately(DistrictFacilitiesServiceJob.TAG);
    }
}
