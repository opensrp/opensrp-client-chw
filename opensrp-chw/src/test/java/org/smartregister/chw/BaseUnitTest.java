package org.smartregister.chw;

import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.smartregister.chw.application.TestChwApplication;
import org.smartregister.chw.shadows.ContextShadow;

/**
 * Created by keyman on 11/03/2019.
 */

@RunWith(RobolectricTestRunner.class)
@Config(application = TestChwApplication.class, sdk = 22, shadows = {ContextShadow.class})
public abstract class BaseUnitTest {

}
