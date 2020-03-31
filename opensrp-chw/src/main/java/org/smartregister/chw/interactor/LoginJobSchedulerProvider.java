package org.smartregister.chw.interactor;

import org.smartregister.chw.BuildConfig;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.contract.LoginJobScheduler;
import org.smartregister.chw.core.job.ChwIndicatorGeneratingJob;
import org.smartregister.chw.core.job.HomeVisitServiceJob;
import org.smartregister.chw.core.job.StockUsageReportJob;
import org.smartregister.chw.core.job.VaccineRecurringServiceJob;
import org.smartregister.chw.job.BasePncCloseJob;
import org.smartregister.chw.job.ScheduleJob;
import org.smartregister.immunization.job.VaccineServiceJob;
import org.smartregister.job.ImageUploadServiceJob;
import org.smartregister.job.PlanIntentServiceJob;
import org.smartregister.job.PullUniqueIdsServiceJob;
import org.smartregister.job.SyncLocationsByLevelAndTagsServiceJob;
import org.smartregister.job.SyncServiceJob;
import org.smartregister.job.SyncTaskServiceJob;

import java.util.concurrent.TimeUnit;

public class LoginJobSchedulerProvider implements LoginJobScheduler {

    private static final int MINIMUM_JOB_FLEX_VALUE = 5;

    @Override
    public void scheduleJobsPeriodically() {
        SyncServiceJob.scheduleJob(SyncServiceJob.TAG, TimeUnit.MINUTES.toMinutes(BuildConfig.DATA_SYNC_DURATION_MINUTES), getFlexValue(BuildConfig
                .DATA_SYNC_DURATION_MINUTES));

        VaccineRecurringServiceJob.scheduleJob(VaccineRecurringServiceJob.TAG, TimeUnit.MINUTES.toMinutes(BuildConfig.VACCINE_SYNC_PROCESSING_MINUTES), getFlexValue(BuildConfig.VACCINE_SYNC_PROCESSING_MINUTES));

        ImageUploadServiceJob.scheduleJob(ImageUploadServiceJob.TAG, TimeUnit.MINUTES.toMinutes(BuildConfig.IMAGE_UPLOAD_MINUTES), getFlexValue(BuildConfig.IMAGE_UPLOAD_MINUTES));

        PullUniqueIdsServiceJob.scheduleJob(PullUniqueIdsServiceJob.TAG, TimeUnit.MINUTES.toMinutes(BuildConfig.PULL_UNIQUE_IDS_MINUTES), getFlexValue(BuildConfig.PULL_UNIQUE_IDS_MINUTES));

        ChwIndicatorGeneratingJob.scheduleJob(ChwIndicatorGeneratingJob.TAG, TimeUnit.MINUTES.toMinutes(BuildConfig.REPORT_INDICATOR_GENERATION_MINUTES), getFlexValue(BuildConfig.REPORT_INDICATOR_GENERATION_MINUTES));

        HomeVisitServiceJob.scheduleJob(HomeVisitServiceJob.TAG, TimeUnit.MINUTES.toMinutes(BuildConfig.HOME_VISIT_MINUTES), getFlexValue(BuildConfig.HOME_VISIT_MINUTES));

        BasePncCloseJob.scheduleJob(BasePncCloseJob.TAG, TimeUnit.MINUTES.toMinutes(BuildConfig.BASE_PNC_CLOSE_MINUTES), getFlexValue(BuildConfig.BASE_PNC_CLOSE_MINUTES));

        PlanIntentServiceJob.scheduleJob(PlanIntentServiceJob.TAG, TimeUnit.MINUTES.toMinutes(BuildConfig.DATA_SYNC_DURATION_MINUTES), getFlexValue(BuildConfig.DATA_SYNC_DURATION_MINUTES));

        if (ChwApplication.getApplicationFlavor().hasTasks())
            SyncTaskServiceJob.scheduleJob(PlanIntentServiceJob.TAG, TimeUnit.MINUTES.toMinutes(BuildConfig.DATA_SYNC_DURATION_MINUTES), getFlexValue(BuildConfig.DATA_SYNC_DURATION_MINUTES));

        ScheduleJob.scheduleJob(ScheduleJob.TAG, TimeUnit.MINUTES.toMinutes(BuildConfig.SCHEDULE_SERVICE_MINUTES), getFlexValue(BuildConfig.SCHEDULE_SERVICE_MINUTES));

        StockUsageReportJob.scheduleJob(StockUsageReportJob.TAG, TimeUnit.MINUTES.toMinutes(BuildConfig.STOCK_USAGE_REPORT_MINUTES), getFlexValue(BuildConfig.STOCK_USAGE_REPORT_MINUTES));

    }

    @Override
    public void scheduleJobsImmediately() {
        // Run initial job immediately on log in since the job will run a bit later (~ 15 mins +)
        ScheduleJob.scheduleJobImmediately(ScheduleJob.TAG);
        SyncServiceJob.scheduleJobImmediately(SyncServiceJob.TAG);
        HomeVisitServiceJob.scheduleJobImmediately(HomeVisitServiceJob.TAG);
        BasePncCloseJob.scheduleJobImmediately(BasePncCloseJob.TAG);
        PlanIntentServiceJob.scheduleJobImmediately(PlanIntentServiceJob.TAG);
        if (ChwApplication.getApplicationFlavor().hasTasks())
            SyncTaskServiceJob.scheduleJobImmediately(SyncTaskServiceJob.TAG);
        VaccineServiceJob.scheduleJobImmediately(VaccineServiceJob.TAG);
        VaccineRecurringServiceJob.scheduleJobImmediately(VaccineRecurringServiceJob.TAG);
        SyncLocationsByLevelAndTagsServiceJob.scheduleJobImmediately(SyncLocationsByLevelAndTagsServiceJob.TAG);
        StockUsageReportJob.scheduleJobImmediately(StockUsageReportJob.TAG);
    }

    @Override
    public long getFlexValue(int value) {
        int minutes = MINIMUM_JOB_FLEX_VALUE;

        if (value > MINIMUM_JOB_FLEX_VALUE) {

            minutes = (int) Math.ceil(value / 3);
        }

        return Math.max(minutes, MINIMUM_JOB_FLEX_VALUE);
    }
}
