package org.smartregister.chw.activity;

import android.app.ProgressDialog;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.reflect.Whitebox;
import org.robolectric.Robolectric;
import org.robolectric.android.controller.ActivityController;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.chw.BaseUnitTest;
import org.smartregister.chw.R;
import org.smartregister.domain.FetchStatus;

public class FamilyRegisterActivityTest extends BaseUnitTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();
    private FamilyRegisterActivity activity;
    private ActivityController<FamilyRegisterActivity> controller;

    @Mock
    private ProgressDialog progressDialog;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        Context context = Context.getInstance();
        CoreLibrary.init(context);

        //Auto login by default
        String password = "pwd";
        context.session().start(context.session().lengthInMilliseconds());
        context.configuration().getDrishtiApplication().setPassword(password.getBytes());
        context.session().setPassword(password.getBytes());

        controller = Robolectric.buildActivity(FamilyRegisterActivity.class).create().start();
        activity = controller.get();
    }

    @After
    public void tearDown() {
        try {
            activity.finish();
            controller.pause().stop().destroy(); //destroy controller if we can
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void testRegisterBottomNavigation() {
        BottomNavigationView bottomNavigationView = activity.findViewById(R.id.bottom_navigation);
        Assert.assertNotNull(bottomNavigationView);
        Assert.assertEquals(3, bottomNavigationView.getMenu().size());
    }

    @Test
    public void testShowProgressShouldShowProgressDialog() {
        FamilyRegisterActivity spyActivity = Mockito.spy(activity);
//        Whitebox.setInternalState(spyActivity, "progressDialog", progressDialog);
        spyActivity.showProgressDialog();
        ProgressDialog progressDialog_ = Mockito.spy((ProgressDialog) Whitebox.getInternalState(spyActivity, "progressDialog"));
        Assert.assertTrue(progressDialog_.isShowing());
    }

    @Test
    public void testHideProgressDialog() {
        FamilyRegisterActivity spyActivity = Mockito.spy(activity);
        Whitebox.setInternalState(spyActivity, "progressDialog", progressDialog);
        spyActivity.hideProgressDialog();
        Mockito.verify(progressDialog).dismiss();
    }


  /*  @Test
    public void testStartFamilyRegisterFormReturnsCorrectIntent() {
        FamilyRegisterActivity spyActivity = Mockito.spy(activity);

        ArgumentCaptor<Intent> intentArgumentCaptor = ArgumentCaptor.forClass(Intent.class);

        Assert.assertNotNull(intentArgumentCaptor);
        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        Assert.assertNotNull(stringArgumentCaptor);
        Mockito.verify(spyActivity).startFamilyRegisterForm(activity);
    }*/

    @Test
    public void testOnSyncStart() {
        FamilyRegisterActivity spyActivity = Mockito.spy(activity);
        spyActivity.onSyncStart();
        Mockito.verify(spyActivity).showProgressDialog();
    }

    @Test
    public void testOnSyncInProgress() {
        FamilyRegisterActivity spyActivity = Mockito.spy(activity);
        spyActivity.onSyncInProgress(FetchStatus.fetched);
        Mockito.verify(spyActivity).showProgressDialog();
    }

    @Test
    public void testOnSyncComplete() {
        FamilyRegisterActivity spyActivity = Mockito.spy(activity);
        spyActivity.onSyncComplete(FetchStatus.fetched);
        Mockito.verify(spyActivity).hideProgressDialog();
    }

}