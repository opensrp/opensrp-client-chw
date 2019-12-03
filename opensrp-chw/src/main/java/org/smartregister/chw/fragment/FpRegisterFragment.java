package org.smartregister.chw.fragment;

import org.smartregister.chw.core.fragment.CoreFpRegisterFragment;
import org.smartregister.chw.fp.model.BaseFpRegisterFragmentModel;
import org.smartregister.chw.presenter.FpRegisterFragmentPresenter;

public class FpRegisterFragment extends CoreFpRegisterFragment {

    @Override
    protected void initializePresenter() {
        if (getActivity() == null) {
            return;
        }
        presenter = new FpRegisterFragmentPresenter(this, new BaseFpRegisterFragmentModel());
    }

}


