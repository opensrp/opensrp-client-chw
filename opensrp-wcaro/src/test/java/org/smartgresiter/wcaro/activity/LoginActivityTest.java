package org.smartgresiter.wcaro.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.Menu;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;
import org.smartgresiter.wcaro.BuildConfig;
import org.smartgresiter.wcaro.R;
import org.smartgresiter.wcaro.application.WcaroApplication;
import org.smartregister.view.contract.BaseLoginContract;

@RunWith(RobolectricTestRunner.class)
@Config(application = WcaroApplication.class, constants = BuildConfig.class, sdk = 22)
public class LoginActivityTest {

    private static final String STRING_SETTINGS = "Settings";
    private LoginActivity loginActivity;
    private ActivityController<LoginActivity> controller;

    @Mock
    private Menu menu;

    @Mock
    private BaseLoginContract.Presenter presenter;

    @Mock
    private ProgressDialog progressDialog;

    @Mock
    private Button loginButton;

    @Mock
    private TextView textView;

    @Mock
    private KeyEvent keyEvent;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        controller = Robolectric.buildActivity(LoginActivity.class).create().start();
        loginActivity = controller.get();
    }

    @After
    public void tearDown() {
        destroyController();
    }

    protected void destroyController() {
        try {
            getActivity().finish();
            getActivityController().pause().stop().destroy(); //destroy controller if we can

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.gc();
    }

    @Test
    public void testUserNameEditTextIsInitialized() {

        EditText userNameEditText = Whitebox.getInternalState(loginActivity, "userNameEditText");
        Assert.assertNotNull(userNameEditText);
    }

    @Test
    public void testPasswordEditTextIsInitialized() {

        EditText userPasswordEditText = Whitebox.getInternalState(loginActivity, "passwordEditText");
        Assert.assertNotNull(userPasswordEditText);
    }


    @Test
    public void testShowPasswordCheckBoxIsInitialized() {

        CheckBox showPasswordCheckBox = Whitebox.getInternalState(loginActivity, "showPasswordCheckBox");
        Assert.assertNotNull(showPasswordCheckBox);
    }

    @Test
    public void testProgressDialogIsInitialized() {

        ProgressDialog progressDialog = Whitebox.getInternalState(loginActivity, "progressDialog");
        Assert.assertNotNull(progressDialog);
    }

    @Test
    public void testGoToHome() {

        try {
            Whitebox.invokeMethod(loginActivity, LoginActivity.class, "goToHome", false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertActivityStarted(loginActivity, new FamilyRegisterActivity());
    }

    @Test
    public void testGoToHomeWithRemoteTrue() {

        try {
            Whitebox.invokeMethod(loginActivity, LoginActivity.class, "goToHome", true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertActivityStarted(loginActivity, new FamilyRegisterActivity());
    }

    private void assertActivityStarted(Activity currActivity, Activity nextActivity) {

        Intent expectedIntent = new Intent(currActivity, nextActivity.getClass());
        Intent actual = ShadowApplication.getInstance().getNextStartedActivity();
        Assert.assertEquals(expectedIntent.getComponent(), actual.getComponent());
    }

    @Test
    public void testOnCreateOptionsMenuShouldAddSettingsItem() {

        LoginActivity spyActivity = Mockito.spy(loginActivity);
        spyActivity.onCreateOptionsMenu(menu);
        Mockito.verify(menu).add(STRING_SETTINGS);
    }


    @Test
    public void testOnResumeShouldCallProcessViewCustomizationsPresenterMethod() {

        LoginActivity spyActivity = Mockito.spy(loginActivity);
        Whitebox.setInternalState(spyActivity, "mLoginPresenter", presenter);
        spyActivity.onResume();
        Mockito.verify(presenter).processViewCustomizations();
    }

    @Test
    public void testOnResumeShouldCallIsUserLoggedOutPresenterMethod() {

        LoginActivity spyActivity = Mockito.spy(loginActivity);

        Whitebox.setInternalState(spyActivity, "mLoginPresenter", presenter);
        Mockito.doReturn(false).when(presenter).isUserLoggedOut();
        Mockito.doNothing().when(spyActivity).goToHome(false);

        spyActivity.onResume();

        Mockito.verify(presenter).isUserLoggedOut();
        Mockito.verify(spyActivity).goToHome(false);
    }

    @Test
    public void testOnDestroyShouldCallOnDestroyPresenterMethod() {

        LoginActivity spyActivity = Mockito.spy(loginActivity);
        Whitebox.setInternalState(spyActivity, "mLoginPresenter", presenter);
        spyActivity.onDestroy();
        Mockito.verify(presenter).onDestroy(Mockito.anyBoolean());
    }

    @Test
    public void testShowProgressShouldShowProgressDialogWhenParamIsTrue() {
        LoginActivity spyActivity = Mockito.spy(loginActivity);
        Whitebox.setInternalState(spyActivity, "progressDialog", progressDialog);
        spyActivity.showProgress(true);
        Mockito.verify(progressDialog).show();
    }

    @Test
    public void testShowProgressShouldDismissProgressDialogWhenParamIsFalse() {
        LoginActivity spyActivity = Mockito.spy(loginActivity);
        Whitebox.setInternalState(spyActivity, "progressDialog", progressDialog);
        spyActivity.showProgress(false);
        Mockito.verify(progressDialog).dismiss();
    }

    @Test
    public void testEnableLoginShouldCallLoginButtonSetClickableMethodWithCorrectParameter() {
        LoginActivity spyActivity = Mockito.spy(loginActivity);
        Whitebox.setInternalState(spyActivity, "loginButton", loginButton);
        spyActivity.enableLoginButton(false);
        Mockito.verify(loginButton).setClickable(Mockito.anyBoolean());
    }


    @Test
    public void testResetPasswordErrorShouldInvokeSetUsernameErrorWithNull() {

        LoginActivity spyActivity = Mockito.spy(loginActivity);

        Whitebox.setInternalState(spyActivity, "mLoginPresenter", presenter);

        EditText passwordEditText = Mockito.spy(new EditText(RuntimeEnvironment.application));

        Whitebox.setInternalState(spyActivity, "passwordEditText", passwordEditText);

        spyActivity.resetPaswordError();

        Mockito.verify(passwordEditText).setError(null);
    }

    @Test
    public void testSetPasswordErrorShouldShowErrorDialogWithCorrectMessage() {

        LoginActivity spyActivity = Mockito.spy(loginActivity);

        Whitebox.setInternalState(spyActivity, "mLoginPresenter", presenter);

        EditText passwordEditText = Mockito.spy(new EditText(RuntimeEnvironment.application));

        Whitebox.setInternalState(spyActivity, "passwordEditText", passwordEditText);

        Mockito.doNothing().when(spyActivity).showErrorDialog(RuntimeEnvironment.application.getString(R.string.unauthorized));

        spyActivity.setUsernameError(R.string.unauthorized);

        Mockito.verify(spyActivity).showErrorDialog(RuntimeEnvironment.application.getString(R.string.unauthorized));
    }

    @Test
    public void testSetUsernameErrorShouldShowErrorDialogWithCorrectMessage() {

        LoginActivity spyActivity = Mockito.spy(loginActivity);

        Whitebox.setInternalState(spyActivity, "mLoginPresenter", presenter);

        EditText userNameEditText = Mockito.spy(new EditText(RuntimeEnvironment.application));

        Whitebox.setInternalState(spyActivity, "userNameEditText", userNameEditText);

        Mockito.doNothing().when(spyActivity).showErrorDialog(RuntimeEnvironment.application.getString(R.string.unauthorized));

        spyActivity.setPasswordError(R.string.unauthorized);

        Mockito.verify(spyActivity).showErrorDialog(RuntimeEnvironment.application.getString(R.string.unauthorized));
    }

    @Test
    public void testResetUsernameErrorShouldInvokeSetUsernameErrorWithNull() {

        LoginActivity spyActivity = Mockito.spy(loginActivity);

        Whitebox.setInternalState(spyActivity, "mLoginPresenter", presenter);

        EditText userNameEditText = Mockito.spy(new EditText(RuntimeEnvironment.application));

        Whitebox.setInternalState(spyActivity, "userNameEditText", userNameEditText);

        spyActivity.resetUsernameError();

        Mockito.verify(userNameEditText).setError(null);
    }

    @Test
    public void testGetActivityContextReturnsCorrectInstance() {
        LoginActivity spyActivity = Mockito.spy(loginActivity);

        Assert.assertEquals(spyActivity, spyActivity.getActivityContext());

    }

    protected Activity getActivity() {
        return loginActivity;
    }

    protected ActivityController getActivityController() {
        return controller;
    }
}
