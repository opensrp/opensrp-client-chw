package org.smartregister.chw.interactor;

import org.smartregister.chw.BuildConfig;
import org.smartregister.chw.job.VaccineRecurringServiceJob;
import org.smartregister.job.ImageUploadServiceJob;
import org.smartregister.job.PullUniqueIdsServiceJob;
import org.smartregister.job.SyncServiceJob;
import org.smartregister.login.interactor.BaseLoginInteractor;
import org.smartregister.reporting.job.RecurringIndicatorGeneratingJob;
import org.smartregister.view.contract.BaseLoginContract;

import java.util.concurrent.TimeUnit;

public class LoginInteractor extends BaseLoginInteractor implements BaseLoginContract.Interactor {

    public LoginInteractor(BaseLoginContract.Presenter loginPresenter) {
        super(loginPresenter);
    }

    @Override
    protected void scheduleJobsPeriodically() {
        SyncServiceJob.scheduleJob(SyncServiceJob.TAG, TimeUnit.MINUTES.toMillis(BuildConfig.DATA_SYNC_DURATION_MINUTES), getFlexValue(BuildConfig
                .DATA_SYNC_DURATION_MINUTES));

        VaccineRecurringServiceJob.scheduleJob(VaccineRecurringServiceJob.TAG, TimeUnit.MINUTES.toMillis(BuildConfig.VACCINE_SYNC_PROCESSING_MINUTES), getFlexValue(BuildConfig.VACCINE_SYNC_PROCESSING_MINUTES));

        ImageUploadServiceJob.scheduleJob(ImageUploadServiceJob.TAG, TimeUnit.MINUTES.toMillis(BuildConfig.IMAGE_UPLOAD_MINUTES), getFlexValue(BuildConfig
                .IMAGE_UPLOAD_MINUTES));

        PullUniqueIdsServiceJob.scheduleJob(PullUniqueIdsServiceJob.TAG, TimeUnit.MINUTES.toMillis(BuildConfig.PULL_UNIQUE_IDS_MINUTES), getFlexValue
                (BuildConfig.PULL_UNIQUE_IDS_MINUTES));

        RecurringIndicatorGeneratingJob.scheduleJob(RecurringIndicatorGeneratingJob.TAG,
                TimeUnit.MINUTES.toMillis(org.smartregister.reporting.BuildConfig.REPORT_INDICATOR_GENERATION_MINUTES), TimeUnit.MINUTES.toMillis(1));

    }

    @Override
    protected void scheduleJobsImmediately() {
        super.scheduleJobsImmediately();
    }
}
