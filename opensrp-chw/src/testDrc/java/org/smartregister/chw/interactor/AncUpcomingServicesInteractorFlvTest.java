package org.smartregister.chw.interactor;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.Context;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.domain.VisitDetail;
import org.smartregister.chw.anc.model.BaseUpcomingService;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class AncUpcomingServicesInteractorFlvTest extends BaseHomeVisitInteractorFlvTest {

    private AncUpcomingServicesInteractorFlv interactor;
    @Mock
    private Map<String, List<VisitDetail>> details;

    @Mock
    protected Context mContext;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        interactor = Mockito.mock(AncUpcomingServicesInteractorFlv.class, Mockito.CALLS_REAL_METHODS);

/*        Mockito.doReturn(ancHomeVisitAction).when(builder).build();
        ReflectionHelpers.setField(interactor, "context", context);
        ReflectionHelpers.setField(interactor, "view", view);
        ReflectionHelpers.setField(interactor, "details", details);


        Mockito.doReturn(ancHomeVisitAction).when(builder).build();
        Constants.JSON_FORM.setLocaleAndAssetManager(locale, assetManager);
        Mockito.doReturn(title).when(context).getString(Mockito.anyInt());


        Mockito.doReturn(ancHomeVisitAction).when(builder).build();*/
    }

    @Mock
    List<BaseUpcomingService> services;

    @Test
    public void testEvaluateDeliveryKit() {
        Date createDate = new Date();
        MemberObject memberObject = new MemberObject();
        memberObject.setDeliveryKit("No");
        ReflectionHelpers.callInstanceMethod(interactor, "evaluateDeliveryKit",
                ReflectionHelpers.ClassParameter.from(List.class, services),
                ReflectionHelpers.ClassParameter.from(MemberObject.class, memberObject),
                ReflectionHelpers.ClassParameter.from(android.content.Context.class, context),
                ReflectionHelpers.ClassParameter.from(Date.class, createDate));

        Mockito.verify(services).add(Mockito.any(BaseUpcomingService.class));
    }
}
