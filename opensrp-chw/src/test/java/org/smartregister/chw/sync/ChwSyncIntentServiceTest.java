package org.smartregister.chw.sync;


import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.AllConstants;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.SyncConfiguration;
import org.smartregister.chw.BaseUnitTest;


public class ChwSyncIntentServiceTest extends BaseUnitTest {


    ChwSyncIntentService chwSyncIntentService;

    @Mock
    Context context;

    @Mock
    private SyncConfiguration syncConfiguration;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        CoreLibrary.init(context);
        Whitebox.setInternalState(CoreLibrary.getInstance(), "syncConfiguration", syncConfiguration);
        CoreLibrary.getInstance().context().allSharedPreferences().savePreference(AllConstants.DRISHTI_BASE_URL, "https://sample-stage.smartregister.org/opensrp");
        chwSyncIntentService = Mockito.spy(new ChwSyncIntentService());
        Whitebox.setInternalState(chwSyncIntentService, "mBase", RuntimeEnvironment.application);

    }

    @Test
    public void testHandleSync() {
        chwSyncIntentService.handleSync();
    }

    @Test
    public void testGetEventPullLimit() {
        int eventPullLimit = chwSyncIntentService.getEventPullLimit();
        assert (eventPullLimit == 1000);
    }

    @Test
    public void testGetEventBatchSize() {
        Integer eventBatchSize = chwSyncIntentService.getEventBatchSize();
        assert (eventBatchSize == 250);
    }
}
