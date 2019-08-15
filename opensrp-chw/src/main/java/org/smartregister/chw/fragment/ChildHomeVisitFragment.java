package org.smartregister.chw.fragment;

import org.smartregister.chw.core.fragment.CoreChildHomeVisitFragment;

public class ChildHomeVisitFragment extends CoreChildHomeVisitFragment {
    public static ChildHomeVisitFragment newInstance() {
        ChildHomeVisitFragment childHomeVisitFragment = new ChildHomeVisitFragment();
        childHomeVisitFragment.setFlavor(new ChildHomeVisitFragmentFlv());
        return childHomeVisitFragment;
    }

}
