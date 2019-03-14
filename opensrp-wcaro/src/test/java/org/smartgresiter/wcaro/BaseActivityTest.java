package org.smartgresiter.wcaro;

import android.app.Activity;
import android.content.Intent;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.android.controller.ActivityController;

public abstract class BaseActivityTest<T extends Activity> extends BaseUnitTest {

    private T activity;
    private ActivityController<T> controller;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        if(getControllerIntent() == null){
            controller = Robolectric.buildActivity(getActivityClass()).create().start();
        }else{
            controller = Robolectric.buildActivity(getActivityClass(), getControllerIntent()).create().start();
        }
        activity = controller.get();
    }

    @After
    public void tearDown() {
        try {
            getActivity().finish();
            getActivityController().pause().stop().destroy(); //destroy controller if we can

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.gc();
    }

    @Test
    public void testActivityExists(){
        Assert.assertNotNull(getActivity());
    }

    protected abstract Class<T> getActivityClass();

    protected Activity getActivity() {
        return activity;
    }

    protected ActivityController getActivityController() {
        return controller;
    }

    protected Intent getControllerIntent(){
        return null;
    }
}
