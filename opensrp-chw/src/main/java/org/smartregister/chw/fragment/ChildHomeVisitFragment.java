package org.smartregister.chw.fragment;

public class ChildHomeVisitFragment extends com.opensrp.chw.core.fragment.CoreChildHomeVisitFragment {
    public static ChildHomeVisitFragment newInstance() {
        ChildHomeVisitFragment childHomeVisitFragment = new ChildHomeVisitFragment();
        childHomeVisitFragment.setFlavor(new ChildHomeVisitFragmentFlv());
        return childHomeVisitFragment;
    }

}
