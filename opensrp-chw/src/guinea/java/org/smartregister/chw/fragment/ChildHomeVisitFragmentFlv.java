package org.smartregister.chw.fragment;

public class ChildHomeVisitFragmentFlv implements ChildHomeVisitFragment.Flavor {
    @Override
    public boolean onTaskVisibility() {
        return true;
    }

    @Override
    public boolean onObsIllnessVisibility() {
        return false;
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
