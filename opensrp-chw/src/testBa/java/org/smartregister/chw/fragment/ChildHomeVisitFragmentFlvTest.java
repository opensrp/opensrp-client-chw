package org.smartregister.chw.fragment;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ChildHomeVisitFragmentFlvTest {

    private ChildHomeVisitFragmentFlv fragmentFlv = new ChildHomeVisitFragmentFlv();

    @Test
    public void onTaskVisibility() {
        assertFalse(fragmentFlv.onTaskVisibility());
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
