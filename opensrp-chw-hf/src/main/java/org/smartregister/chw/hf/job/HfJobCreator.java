package org.smartregister.chw.hf.job;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;

import org.smartregister.chw.core.job.VaccineRecurringServiceJob;
import org.smartregister.chw.hf.sync.intent.HfSyncTaskIntentService;
import org.smartregister.job.ExtendedSyncServiceJob;
import org.smartregister.job.ImageUploadServiceJob;
import org.smartregister.job.LocationStructureServiceJob;
import org.smartregister.job.PlanIntentServiceJob;
import org.smartregister.job.PullUniqueIdsServiceJob;
import org.smartregister.job.SyncServiceJob;
import org.smartregister.job.SyncTaskServiceJob;
import org.smartregister.job.ValidateSyncDataServiceJob;
import org.smartregister.sync.intent.SyncIntentService;

import timber.log.Timber;

/**
 *
 */
public class HfJobCreator implements JobCreator {
    @Nullable
    @Override
    public Job create(@NonNull String tag) {
        switch (tag) {
            case SyncServiceJob.TAG:
                return new SyncServiceJob(SyncIntentService.class);
            case ExtendedSyncServiceJob.TAG:
                return new ExtendedSyncServiceJob();
            case PullUniqueIdsServiceJob.TAG:
                return new PullUniqueIdsServiceJob();
            case ValidateSyncDataServiceJob.TAG:
                return new ValidateSyncDataServiceJob();
            case ImageUploadServiceJob.TAG:
                return new ImageUploadServiceJob();
            case VaccineRecurringServiceJob.TAG:
                return new VaccineRecurringServiceJob();
            case LocationStructureServiceJob.TAG:
                return new LocationStructureServiceJob();
            case SyncTaskServiceJob.TAG:
                return new SyncTaskServiceJob(HfSyncTaskIntentService.class);
            case PlanIntentServiceJob.TAG:
                return new PlanIntentServiceJob();
            default:
                Timber.d("Please create job and specify the right job tag");
                return null;
        }
    }
}
