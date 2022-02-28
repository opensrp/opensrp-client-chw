package org.smartregister.chw.activity;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import org.json.JSONException;
import org.json.JSONObject;
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
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.chw.BaseUnitTest;
import org.smartregister.chw.core.adapter.NavigationAdapter;
import org.smartregister.chw.core.custom_views.NavigationMenu;
import org.smartregister.chw.presenter.CoreOutOfAreaChildRegisterPresenter;
import org.smartregister.chw.util.CrvsConstants;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class OutOfAreaChildActivityTest extends BaseUnitTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private CoreOutOfAreaChildRegisterPresenter presenter;

    private OutOfAreaChildActivity activity;
    private ActivityController<OutOfAreaChildActivity> controller;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Context context = Context.getInstance();
        CoreLibrary.init(context);

        //Auto login by default
        context.session().start(context.session().lengthInMilliseconds());

        MockitoAnnotations.initMocks(this);
        controller = Robolectric.buildActivity(OutOfAreaChildActivity.class);
        activity = controller.get();
        activity = Mockito.spy(activity);
        // mute this presenter
        Mockito.doNothing().when(activity).initializePresenter();
        ReflectionHelpers.setField(activity, "presenter", presenter);
    }

    @Test
    public void testOnResumption() {
        NavigationMenu menu = Mockito.mock(NavigationMenu.class);
        NavigationAdapter adapter = Mockito.mock(NavigationAdapter.class);

        Mockito.doReturn(adapter).when(menu).getNavigationAdapter();
        ReflectionHelpers.setStaticField(NavigationMenu.class, "instance", menu);

        ReflectionHelpers.setField(activity, "presenter", presenter);
        activity.onResumption();

        Mockito.verify(adapter).setSelectedView(Mockito.anyString());
    }

    @Test
    public void testGetPresenter(){
        Assert.assertEquals(presenter, activity.presenter());
    }

    @Test
    public void testActivityLoaded() {
        Assert.assertNotNull(activity);
    }

    @Test
    public void testStartFormActivity() {
        JSONObject json = new JSONObject();
        activity.startFormActivity(json);
        Mockito.verify(activity).startActivityForResult(Mockito.any(Intent.class), Mockito.anyInt());
    }

    @Test
    public void testOnActivityResultVerifyJsonReceived() {
        OutOfAreaChildActivity spyActivity = Mockito.spy(activity);
        JSONObject form = getFormJson(RuntimeEnvironment.application, CrvsConstants.OUT_OF_AREA_CHILD_FORM);

        CoreOutOfAreaChildRegisterPresenter presenter = Mockito.mock(CoreOutOfAreaChildRegisterPresenter.class);
        ReflectionHelpers.setField(spyActivity, "presenter", presenter);

        int resultCode = Activity.RESULT_OK;
        int requestCode = org.smartregister.family.util.JsonFormUtils.REQUEST_CODE_GET_JSON;
        Intent data = new Intent();
        data.putExtra(org.smartregister.family.util.Constants.JSON_FORM_EXTRA.JSON, form.toString());
        spyActivity.onActivityResultExtended(requestCode, resultCode, data);
    }

    @Test
    public void testPresenterIsSetUp() {
        CoreOutOfAreaChildRegisterPresenter presenter = ReflectionHelpers.getField(activity, "presenter");
        Assert.assertTrue(presenter != null);
    }

    public JSONObject getFormJson(Application mContext, String formIdentity) {
        if (mContext != null) {
            try {
                InputStream inputStream = mContext.getApplicationContext().getAssets()
                        .open("json" + ".form/" + formIdentity + ".json");
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(inputStream, StandardCharsets.UTF_8));
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

    @After
    public void tearDown() {
        try {
            activity.finish();
            controller.pause().stop().destroy(); //destroy controller if we can
        } catch (Exception e) {
            e.printStackTrace();
        }

        //logout
        Context.getInstance().session().expire();
        System.gc();
    }

}