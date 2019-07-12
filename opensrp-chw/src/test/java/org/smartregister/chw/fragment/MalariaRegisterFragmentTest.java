package org.smartregister.chw.fragment;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.smartregister.chw.BuildConfig;
import org.smartregister.chw.application.ChwApplication;

@RunWith(RobolectricTestRunner.class)
@Config(application = ChwApplication.class, constants = BuildConfig.class, sdk = 22)
public class MalariaRegisterFragmentTest {
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }
}
