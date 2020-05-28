package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Intent;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.smartregister.chw.application.TestChwApplication;
import org.smartregister.chw.shadows.BaseJobShadow;
import org.smartregister.chw.shadows.ContextShadow;

@RunWith(RobolectricTestRunner.class)
@Config(application = TestChwApplication.class, shadows = {ContextShadow.class, BaseJobShadow.class})
public class PncMemberProfileActivityTest {
    protected PncMemberProfileActivity activity;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        activity = Robolectric.buildActivity(PncMemberProfileActivity.class)
                .create()
                .start()
                .postCreate(null)
                .resume()
                .get();
    }

    @Test
    public void testStartMe(){
        Activity activity = Mockito.mock(Activity.class);
        String baseID = "baseID";
        PncMemberProfileActivity.startMe(activity, baseID);
        Mockito.verify(activity).startActivity(Mockito.any(Intent.class));
    }
}
