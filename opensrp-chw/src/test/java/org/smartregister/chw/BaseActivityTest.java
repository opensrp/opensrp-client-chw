package org.smartregister.chw;

import android.app.Activity;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.robolectric.android.controller.ActivityController;

import timber.log.Timber;

/**
 * @param <T>
 * @author rkodev
 */
public abstract class BaseActivityTest<T extends Activity> extends BaseActivityTestSetUp<T> {

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
            throw e;
//            com.ibm.icu.impl.Assert.fail("Should not have thrown any exception");
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
