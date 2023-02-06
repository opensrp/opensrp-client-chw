package org.smartregister.chw;

import android.app.Activity;
import android.content.Intent;

import org.junit.Before;
import org.junit.Rule;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.Robolectric;
import org.robolectric.android.controller.ActivityController;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;

import java.util.ArrayList;

/**
 * @param <T>
 * @author rkodev
 */
public abstract class BaseActivityTestSetUp<T extends Activity> extends BaseUnitTest {

    protected T activity;
    protected ActivityController<T> controller;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Context context = Context.getInstance();
        Context.bindtypes = new ArrayList<>();
        CoreLibrary.init(context);

        //Auto login by default
        String password = "pwd";
        context.session().start(context.session().lengthInMilliseconds());
        context.configuration().getDrishtiApplication().setPassword(password.getBytes());
        context.session().setPassword(password.getBytes());

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
