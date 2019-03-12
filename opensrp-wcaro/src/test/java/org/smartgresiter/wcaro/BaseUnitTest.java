package org.smartgresiter.wcaro;

import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.smartgresiter.wcaro.application.TestWcaroApplication;

/**
 * Created by keyman on 11/03/2019.
 */

@RunWith(RobolectricTestRunner.class)
@Config(application = TestWcaroApplication.class, constants = BuildConfig.class, sdk = 22)
public abstract class BaseUnitTest {

}
