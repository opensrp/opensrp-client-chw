package org.smartregister.chw.fragment;

import org.junit.Test;

import static junit.framework.TestCase.assertTrue;

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
        assertTrue(fragmentFlv.onSleepingUnderLLITNVisibility());
    }

    @Test
    public void onMUACVisibility() {
        assertTrue(fragmentFlv.onMUACVisibility());
    }
}
