package org.smartregister.chw;

import android.app.Activity;
import android.content.Intent;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.android.controller.ActivityController;

import timber.log.Timber;

public abstract class BaseActivityTest<T extends Activity> extends BaseUnitTest {

    private T activity;
    private ActivityController<T> controller;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        if (getControllerIntent() == null) {
            controller = Robolectric.buildActivity(getActivityClass()).create().start();
        } else {
            controller = Robolectric.buildActivity(getActivityClass(), getControllerIntent()).create().start();
        }
        activity = controller.get();
    }

    protected Intent getControllerIntent() {
        return null;
    }

    protected abstract Class<T> getActivityClass();

    @After
    public void tearDown() {
        try {
            getActivity().finish();
            getActivityController().pause().stop().destroy(); //destroy controller if we can

        } catch (Exception e) {
            Timber.e(e);
        }

        System.gc();
    }

    protected T getActivity() {
        return activity;
    }

    protected ActivityController getActivityController() {
        return controller;
    }

    @Test
    public void testActivityExists() {
        Assert.assertNotNull(getActivity());
    }

    @Test
    public void testNoErrorOnPause() {
        try {
            getActivityController().pause();
        } catch (Exception e) {
            com.ibm.icu.impl.Assert.fail("Should not have thrown any exception");
        }
    }

    @Test
    public void testNoErrorOnResume() {
        try {
            getActivityController().pause();
            getActivityController().resume();
        } catch (Exception e) {
            com.ibm.icu.impl.Assert.fail("Should not have thrown any exception");
        }
    }

    @Test
    public void testNoErrorOnRestart() {
        try {
            getActivityController().restart();
        } catch (Exception e) {
            com.ibm.icu.impl.Assert.fail("Should not have thrown any exception");
        }
    }

    @Test
    public void testNoErrorOnStop() {
        try {
            getActivityController().stop();
        } catch (Exception e) {
            com.ibm.icu.impl.Assert.fail("Should not have thrown any exception");
        }
    }
}
