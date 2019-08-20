package org.smartregister.chw.fragment;

public class ChildHomeVisitFragmentFlv implements ChildHomeVisitFragment.Flavor {
    @Override
    public boolean onTaskVisibility() {
        return true;
    }

    @Override
    public boolean onObsIllnessVisibility() {
        return true;
    }

    @Override
    public boolean onSleepingUnderLLITNVisibility() {
        return true;
    }

    @Override
    public boolean onMUACVisibility() {
        return true;
    }
}
