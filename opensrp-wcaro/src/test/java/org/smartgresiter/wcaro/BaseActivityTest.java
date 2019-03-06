package org.smartgresiter.wcaro;

import android.support.v7.app.AppCompatActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.smartgresiter.wcaro.application.WcaroApplication;

public abstract class BaseActivityTest<T extends AppCompatActivity> extends BaseTest {

    private T activity;
    private ActivityController<T> controller;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        controller = Robolectric.buildActivity(getType()).create().start();
        activity = controller.get();
    }

    @After
    public void tearDown() {
        getActivity().finish();

        if(getActivityController() != null){
            if(controller.get() != null){
                getActivityController()
                        .pause()
                        .stop()
                        .destroy();
            }
        }
 //destroy controller if we can

        System.gc();
    }

    protected abstract Class<T> getType();

    protected T getActivity() {
        return activity;
    }

    protected ActivityController getActivityController() {
        return controller;
    }
}
