package org.smartregister.chw.fragment;

import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class ChildHomeVisitFragmentFlvTest {

    private ChildHomeVisitFragmentFlv fragmentFlv = new ChildHomeVisitFragmentFlv();

    @Test
    public void onTaskVisibility() {
        assertTrue(fragmentFlv.onTaskVisibility());
    }

    @Test
    public void onObsIllnessVisibility() {
        assertTrue(fragmentFlv.onObsIllnessVisibility());
    }

    @Test
    public void onSleepingUnderLLITNVisibility() {
        assertFalse(fragmentFlv.onSleepingUnderLLITNVisibility());
    }

    @Test
    public void onMUACVisibility() {
        assertFalse(fragmentFlv.onMUACVisibility());
    }
}
