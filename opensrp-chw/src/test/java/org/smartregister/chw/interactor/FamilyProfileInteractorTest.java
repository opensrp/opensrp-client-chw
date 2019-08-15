package org.smartregister.chw.interactor;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.smartregister.chw.presenter.FamilyProfilePresenter;

@RunWith(PowerMockRunner.class)
@PrepareForTest({FamilyProfileInteractor.class})
public class FamilyProfileInteractorTest extends BaseInteractorTest {

    private FamilyProfileInteractor interactor;

    @Mock
    private FamilyProfilePresenter profilePresenter;

    @Before
    public void setUp() {
        super.setUp();

        interactor = PowerMockito.spy(new FamilyProfileInteractor());
        Whitebox.setInternalState(interactor, "appExecutors", appExecutors);
    }

    @Test
    public void testVerifyHasPhone() throws Exception {
        // verify that the has method
        PowerMockito.doReturn(false).when(interactor, "hasPhone", ArgumentMatchers.anyString());
        interactor.verifyHasPhone("12345", profilePresenter);
        Mockito.verify(profilePresenter).notifyHasPhone(Mockito.anyBoolean());
    }
}
