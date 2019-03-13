package org.smartgresiter.wcaro.activity;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
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
import org.smartgresiter.wcaro.BuildConfig;
import org.smartgresiter.wcaro.application.WcaroApplication;
import org.smartgresiter.wcaro.presenter.FamilyProfilePresenter;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.domain.FetchStatus;
import org.smartregister.family.fragment.BaseFamilyProfileMemberFragment;
import org.smartregister.family.util.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.verifyPrivate;

@RunWith(RobolectricTestRunner.class)
@Config(application = WcaroApplication.class, constants = BuildConfig.class, sdk = 22)
public class FamilyProfileActivityTest {

    private FamilyProfileActivity activity;
    private ActivityController<FamilyProfileActivity> controller;


    private final String TEST_CARE_GIVER = "45645sdfs64564544";
    private final String TEST_FAMILY_HEAD = "hsdf34453";

    @Mock
    private FamilyProfilePresenter presenter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        controller = Robolectric.buildActivity(FamilyProfileActivity.class).create().start();
        activity = controller.get();


        Context context = Context.getInstance();
        CoreLibrary.init(context);

        //Auto login by default
        String password = "pwd";
        context.session().start(context.session().lengthInMilliseconds());
        context.configuration().getDrishtiApplication().setPassword(password);
        context.session().setPassword(password);

        MockitoAnnotations.initMocks(this);
        Intent testIntent = new Intent();
        testIntent.putExtra(Constants.INTENT_KEY.FAMILY_HEAD, TEST_CARE_GIVER);
        testIntent.putExtra(Constants.INTENT_KEY.PRIMARY_CAREGIVER, TEST_FAMILY_HEAD);
        controller = Robolectric.buildActivity(FamilyProfileActivity.class, testIntent).create().start();

        activity = controller.get();
        Whitebox.setInternalState(activity, "presenter", presenter);

    }

    @After
    public void tearDown() {
        try {
            activity.finish();
            controller.pause().stop().destroy(); //destroy controller if we can
        } catch (Exception e) {
            e.printStackTrace();
        }

        //logout
        Context context = Context.getInstance();
        context.session().expire();

        System.gc();
    }

    @Test
    public void refreshPresenter() throws Exception {

        FamilyProfileActivity spyActivity = Mockito.spy(activity);
        FamilyProfilePresenter presenter = mock(FamilyProfilePresenter.class);

        Whitebox.setInternalState(spyActivity, presenter);
        // verify current presenter
        Assert.assertEquals(spyActivity.presenter(), presenter);

        Whitebox.invokeMethod(spyActivity, "refreshPresenter");

        // verify new presenter
        Assert.assertNotEquals(spyActivity.presenter(), presenter);
    }

    @Test
    public void testOnActivityResultVerifyJsonReceived() throws Exception {

        FamilyProfileActivity spyActivity = Mockito.spy(activity);
        JSONObject form = getFormJson(RuntimeEnvironment.application, org.smartgresiter.wcaro.util.Constants.JSON_FORM.FAMILY_MEMBER_REGISTER);

        FamilyProfilePresenter presenter = mock(FamilyProfilePresenter.class);
        Whitebox.setInternalState(spyActivity, "presenter", presenter);

        int resultCode = Activity.RESULT_OK;
        int requestCode = org.smartregister.family.util.JsonFormUtils.REQUEST_CODE_GET_JSON;
        Intent data = new Intent();
        data.putExtra(org.smartregister.family.util.Constants.JSON_FORM_EXTRA.JSON, form.toString());

        spyActivity.onActivityResult(requestCode, resultCode, data);

        verify(presenter).updatePrimaryCareGiver(spyActivity.getApplicationContext(), form.toString(), null, null);
    }

    public JSONObject getFormJson(Application mContext, String formIdentity) {
        if (mContext != null) {
            try {
                InputStream inputStream = mContext.getApplicationContext().getAssets()
                        .open("json" + ".form/" + formIdentity + ".json");
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(inputStream, "UTF-8"));
                String jsonString;
                StringBuilder stringBuilder = new StringBuilder();

                while ((jsonString = reader.readLine()) != null) {
                    stringBuilder.append(jsonString);
                }
                inputStream.close();

                return new JSONObject(stringBuilder.toString());
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    @Test
    public void testOnActivityResultChangeComplete() throws Exception {

        FamilyProfileActivity spyActivity = Mockito.spy(activity);

        int resultCode = Activity.RESULT_OK;
        int requestCode = org.smartgresiter.wcaro.util.Constants.ProfileActivityResults.CHANGE_COMPLETED;
        Intent data = new Intent();
        data.putExtra(Constants.INTENT_KEY.FAMILY_HEAD, TEST_CARE_GIVER);
        data.putExtra(Constants.INTENT_KEY.PRIMARY_CAREGIVER, TEST_FAMILY_HEAD);


        BaseFamilyProfileMemberFragment profileMemberFragment = mock(BaseFamilyProfileMemberFragment.class);
        when(spyActivity.getProfileMemberFragment()).thenReturn(profileMemberFragment);

        // execute
        spyActivity.onActivityResult(requestCode, resultCode, data);

        // verify
        verifyPrivate(spyActivity).invoke("refreshMemberFragment", TEST_CARE_GIVER, TEST_FAMILY_HEAD);
    }

    @Test
    public void testActivityLoaded() {
        Assert.assertNotNull(activity);
    }

    @Test
    public void testRefreshMemberFragment() throws Exception {

        FamilyProfileActivity spyActivity = Mockito.spy(activity);
        BaseFamilyProfileMemberFragment fragment = mock(BaseFamilyProfileMemberFragment.class);
        when(spyActivity.getProfileMemberFragment()).thenReturn(fragment);

        String careGiverID = "123456";
        String familyHeadID = "7234556";

        // test that updates are sent to the member fragment
        Whitebox.invokeMethod(spyActivity, "refreshMemberFragment", careGiverID, familyHeadID);

        verify(fragment).setPrimaryCaregiver(careGiverID);
        verify(fragment).setFamilyHead(familyHeadID);

        // test that updates are sent to the refresh member list

        verify(spyActivity).refreshMemberList(FetchStatus.fetched);
    }
}
