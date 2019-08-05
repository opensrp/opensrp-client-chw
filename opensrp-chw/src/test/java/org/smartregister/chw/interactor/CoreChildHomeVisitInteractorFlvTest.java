package org.smartregister.chw.interactor;

import android.content.Context;

import com.opensrp.chw.core.utils.ServiceTask;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.chw.BaseUnitTest;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.util.DBConstants;

import java.util.ArrayList;
import java.util.HashMap;

import static org.powermock.api.mockito.PowerMockito.mock;

public class CoreChildHomeVisitInteractorFlvTest extends BaseUnitTest {

    private ChildHomeVisitInteractorFlv childHomeVisitInteractorFlv;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        childHomeVisitInteractorFlv = Mockito.spy(ChildHomeVisitInteractorFlv.class);
    }

    @Test
    public void getTaskServiceAboveSixMonth() {
        //above six month only 4 service will be visible
        CommonPersonObjectClient client = new CommonPersonObjectClient(null, null, null);
        Context context = mock(Context.class);
        client.setColumnmaps(new HashMap<String, String>());
        client.getColumnmaps().put(DBConstants.KEY.DOB, "2019-01-01T03:00:00.000+03:00");
        ArrayList<ServiceTask> serviceTasks = childHomeVisitInteractorFlv.getTaskService(client, false, context);
        Assert.assertNotNull(serviceTasks);
    }
}
