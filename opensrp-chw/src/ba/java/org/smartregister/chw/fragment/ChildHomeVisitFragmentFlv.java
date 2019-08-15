package org.smartregister.chw.fragment;

import org.smartregister.chw.core.fragment.CoreChildHomeVisitFragment;

public class ChildHomeVisitFragmentFlv implements CoreChildHomeVisitFragment.Flavor {
    @Override
    public boolean onTaskVisibility() {
        return false;
    }

    @Override
    public boolean onObsIllnessVisibility() {
        return true;
    }

    @Override
    public boolean onSleepingUnderLLITNVisibility() {
        return false;
    }

    @Override
    public boolean onMUACVisibility() {
        return false;
    }
}
