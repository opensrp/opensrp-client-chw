package org.smartregister.chw.presenter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.chw.BaseUnitTest;
import org.smartregister.chw.contract.PinLoginContract;
import org.smartregister.chw.pinlogin.PinLogger;

public class PinLoginPresenterTest extends BaseUnitTest {

    @Mock
    private PinLoginContract.View view;

    private PinLoginPresenter presenter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        presenter = new PinLoginPresenter(view);
    }

    @Test
    public void testLocalLogin() {
        PinLoginContract.Interactor interactor = Mockito.mock(PinLoginContract.Interactor.class);
        PinLogger logger = Mockito.mock(PinLogger.class);
        String userName = "userName";
        String passWord = "passWord";
        String pin = "1234";

        Mockito.doReturn(false).when(logger).attemptPinVerification(pin, presenter);
        Mockito.doReturn(userName).when(logger).getLoggedInUserName();
        Mockito.doReturn(userName).when(logger).getPassword(pin);

        ReflectionHelpers.setField(presenter, "interactor", interactor);
        ReflectionHelpers.setField(presenter, "logger", logger);

        presenter.localLogin(pin);

        Mockito.verify(interactor).authenticateUser(userName, passWord, presenter);
    }

    @Test
    public void testOnError() {
        Exception exception = new Exception("Sample");
        presenter.onError(exception);
        Mockito.verify(view).onLoginAttemptFailed("Sample");
    }

    @Test
    public void testOnSuccess() {
        presenter.onSuccess();
        Mockito.verify(view).onLoginCompleted();
    }
}
