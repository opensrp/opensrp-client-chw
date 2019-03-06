package org.smartgresiter.wcaro;

import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.smartgresiter.wcaro.application.WcaroApplication;

@RunWith(RobolectricTestRunner.class)
@Config(application = WcaroApplication.class, constants = BuildConfig.class, sdk = 22)
public class BaseTest {
}
