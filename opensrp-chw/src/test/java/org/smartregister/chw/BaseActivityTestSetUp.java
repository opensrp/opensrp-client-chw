package org.smartregister.chw;

import android.app.Activity;
import android.content.Intent;

import org.junit.Before;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.android.controller.ActivityController;

/**
 * @param <T>
 * @author rkodev
 */
public abstract class BaseActivityTestSetUp<T extends Activity> extends BaseUnitTest {

    protected T activity;
    protected ActivityController<T> controller;

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
}
