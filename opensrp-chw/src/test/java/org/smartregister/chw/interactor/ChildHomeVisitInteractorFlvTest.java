package org.smartregister.chw.interactor;

import android.content.Context;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.chw.BaseUnitTest;
import org.smartregister.chw.util.ServiceTask;
import org.smartregister.chw.util.TestConstant;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.util.DBConstants;
import java.util.ArrayList;
import java.util.HashMap;
import static org.powermock.api.mockito.PowerMockito.mock;

public class ChildHomeVisitInteractorFlvTest extends BaseUnitTest {

    ChildHomeVisitInteractorFlv childHomeVisitInteractorFlv;
//    @Mock
//    TaskServiceCalculate taskServiceCalculate;
//    String today = "2019-12-12T03:00:00.000+03:00";

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        childHomeVisitInteractorFlv = Mockito.spy(ChildHomeVisitInteractorFlv.class);
    }

//    @Test
//    public void getTaskServiceBelowSixMonth() throws Exception{
//        CommonPersonObjectClient client = new CommonPersonObjectClient(null, null, null);
//        Context context = mock(Context.class);
//        client.setColumnmaps(new HashMap<String, String>());
//        client.getColumnmaps().put(DBConstants.KEY.DOB, "2019-03-01T03:00:00.000+03:00");
//        LocalDate localDate = new LocalDate(Utils.dobStringToDate(today));
//        Whitebox.setInternalState(taskServiceCalculate,"todayDate",localDate);
//        ArrayList<ServiceTask> serviceTasks = childHomeVisitInteractorFlv.getTaskService(client,false,context);
//        Assert.assertEquals(2,serviceTasks.size());
//    }

    @Test
    public void getTaskServiceAboveSixMonth(){
        //above six month only 4 service will be visible
        CommonPersonObjectClient client = new CommonPersonObjectClient(null, null, null);
        Context context = mock(Context.class);
        client.setColumnmaps(new HashMap<String, String>());
        client.getColumnmaps().put(DBConstants.KEY.DOB, "2019-01-01T03:00:00.000+03:00");
        ArrayList<ServiceTask> serviceTasks = childHomeVisitInteractorFlv.getTaskService(client,false,context);
        if ((TestConstant.IS_TASK_VISIBLE)) {
            Assert.assertEquals(4, serviceTasks.size());
        } else {
            Assert.assertEquals(0, serviceTasks.size());
        }
    }
}
