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
}
