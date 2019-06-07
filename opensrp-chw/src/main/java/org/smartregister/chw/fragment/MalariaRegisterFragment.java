package org.smartregister.chw.fragment;

import android.view.View;

import org.smartregister.chw.malaria.fragment.BaseMalariaRegisterFragment;
import org.smartregister.chw.model.MalariaRegisterFragmentModel;
import org.smartregister.chw.presenter.MalariaRegisterFragmentPresenter;

public class MalariaRegisterFragment extends BaseMalariaRegisterFragment {

    private View view;

    @Override
    public void setupViews(View view) {
        super.setupViews(view);
        this.view = view;
    }

    @Override
    protected void initializePresenter() {
        if (getActivity() == null) {
            return;
        }
        presenter = new MalariaRegisterFragmentPresenter(this, new MalariaRegisterFragmentModel(), null);
    }


}


