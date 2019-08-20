package org.smartregister.chw.interactor;

import android.content.Context;

import com.evernote.android.job.JobCreator;
import com.evernote.android.job.JobManager;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.smartregister.chw.presenter.LoginPresenter;
import org.smartregister.job.BaseJob;

@RunWith(PowerMockRunner.class)
@PrepareForTest({BaseJob.class, JobManager.class})
public class LoginInteractorTest {

    private LoginInteractor loginInteractor;

    @Mock
    private Context context;

    @Mock
    private LoginPresenter loginPresenter;

    @Mock
    private JobCreator jobCreator;

    @Mock
    private JobManager jobManager;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        loginInteractor = new LoginInteractor(loginPresenter);
    }


    @Test
    @Ignore
    public void jobsAreScheduledOnScheduleJobsPeriodically() {
        PowerMockito.mockStatic(BaseJob.class);
        PowerMockito.mockStatic(JobManager.class);
        PowerMockito.when(JobManager.create(context)).thenReturn(jobManager);
        jobManager.addJobCreator(jobCreator);
        loginInteractor.scheduleJobsPeriodically();
        PowerMockito.verifyStatic(BaseJob.class, Mockito.times(8));
        BaseJob.scheduleJob(ArgumentMatchers.anyString(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong());
        PowerMockito.verifyNoMoreInteractions(BaseJob.class);
    }

}
