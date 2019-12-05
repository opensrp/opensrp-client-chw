package org.smartregister.chw.interactor;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
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
}
