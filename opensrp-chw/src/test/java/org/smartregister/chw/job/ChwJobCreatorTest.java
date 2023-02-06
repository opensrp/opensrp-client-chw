package org.smartregister.chw.job;

import com.evernote.android.job.Job;

import org.junit.Before;
import org.junit.Test;
import  org.junit.Assert;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.core.job.ChwIndicatorGeneratingJob;
import org.smartregister.chw.core.job.HomeVisitServiceJob;
import org.smartregister.chw.core.job.StockUsageReportJob;
import org.smartregister.chw.core.job.VaccineRecurringServiceJob;
import org.smartregister.job.DocumentConfigurationServiceJob;
import org.smartregister.job.ExtendedSyncServiceJob;
import org.smartregister.job.ImageUploadServiceJob;
import org.smartregister.job.P2pServiceJob;
import org.smartregister.job.PlanIntentServiceJob;
import org.smartregister.job.PullUniqueIdsServiceJob;
import org.smartregister.job.SyncLocationsByLevelAndTagsServiceJob;
import org.smartregister.job.SyncServiceJob;
import org.smartregister.job.SyncTaskServiceJob;
import org.smartregister.job.ValidateSyncDataServiceJob;

public class ChwJobCreatorTest {

    private ChwJobCreator chwJobCreator;

    @Before
    public void setUp(){
        chwJobCreator = new ChwJobCreator();
    }

    @Test
    public void testCreate() {
        Assert.assertTrue(chwJobCreator.create(SyncServiceJob.TAG) instanceof SyncServiceJob);
        Assert.assertTrue(chwJobCreator.create(ExtendedSyncServiceJob.TAG) instanceof ExtendedSyncServiceJob);
        Assert.assertTrue(chwJobCreator.create(PullUniqueIdsServiceJob.TAG) instanceof PullUniqueIdsServiceJob);
        Assert.assertTrue(chwJobCreator.create(ValidateSyncDataServiceJob.TAG) instanceof ValidateSyncDataServiceJob);
        Assert.assertTrue(chwJobCreator.create(VaccineRecurringServiceJob.TAG) instanceof VaccineRecurringServiceJob);
        Assert.assertTrue(chwJobCreator.create(ImageUploadServiceJob.TAG) instanceof ImageUploadServiceJob);
        Assert.assertTrue(chwJobCreator.create(P2pServiceJob.TAG) instanceof P2pServiceJob);
        Assert.assertTrue(chwJobCreator.create(HomeVisitServiceJob.TAG) instanceof HomeVisitServiceJob);
        Assert.assertTrue(chwJobCreator.create(ChwIndicatorGeneratingJob.TAG) instanceof ChwIndicatorGeneratingJob);
        Assert.assertTrue(chwJobCreator.create(BasePncCloseJob.TAG) instanceof BasePncCloseJob);
        Job createdSyncTaskServiceJob = chwJobCreator.create(SyncTaskServiceJob.TAG);
        if (ChwApplication.getApplicationFlavor().hasTasks()){
            Assert.assertTrue(createdSyncTaskServiceJob instanceof SyncTaskServiceJob);
        }else{
            Assert.assertNull(createdSyncTaskServiceJob);
        }
        Assert.assertTrue(chwJobCreator.create(ScheduleJob.TAG) instanceof ScheduleJob);
        Assert.assertTrue(chwJobCreator.create(SyncLocationsByLevelAndTagsServiceJob.TAG) instanceof SyncLocationsByLevelAndTagsServiceJob);
        Assert.assertTrue(chwJobCreator.create(StockUsageReportJob.TAG) instanceof StockUsageReportJob);
        Assert.assertTrue(chwJobCreator.create(DocumentConfigurationServiceJob.TAG) instanceof DocumentConfigurationServiceJob);

        Assert.assertNull(chwJobCreator.create(PlanIntentServiceJob.TAG));
    }
}