package org.smartregister.chw.interactor;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.smartregister.family.util.AppExecutors;

import java.util.concurrent.Executor;

public abstract class BaseInteractorTest {

    @Mock
    protected AppExecutors appExecutors;

    public void setUp() {
        MockitoAnnotations.initMocks(this);

        Executor mt = new DirectExecutor();
        PowerMockito.doReturn(mt).when(appExecutors).networkIO();
        PowerMockito.doReturn(mt).when(appExecutors).diskIO();
        PowerMockito.doReturn(mt).when(appExecutors).mainThread();
    }


    protected class DirectExecutor implements Executor {
        public void execute(Runnable r) {
            r.run();
        }
    }
}
