package org.smartregister.chw.fragment;

public class ChildHomeVisitFragmentFlv implements ChildHomeVisitFragment.flavor {
    @Override
    public boolean onTaskVisibility() {
        return false;
    }

    @Override
    public boolean onObsIllnessVisibility() {
        return true;
    }
}
