package org.smartregister.chw.interactor;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.model.BaseUpcomingService;

import java.util.Date;
import java.util.List;

public class AncUpcomingServicesInteractorFlvTest extends BaseHomeVisitInteractorFlvTest {

    private AncUpcomingServicesInteractorFlv interactor;
    @Mock
    private List<BaseUpcomingService> services;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        interactor = Mockito.mock(AncUpcomingServicesInteractorFlv.class, Mockito.CALLS_REAL_METHODS);
    }


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
