package org.smartregister.chw.activity;

import android.app.Activity;

import org.smartregister.chw.R;
import org.smartregister.chw.fragment.FamilyCallDialogFragment;
import org.smartregister.chw.listener.OnClickFloatingMenu;
import org.smartregister.chw.presenter.ChildProfilePresenter;

public class ChildProfileActivityFlv implements ChildProfileActivity.Flavor {

    @Override
    public OnClickFloatingMenu getOnClickFloatingMenu(final Activity activity, final ChildProfilePresenter presenter) {
        return new OnClickFloatingMenu() {
            @Override
            public void onClickMenu(int viewId) {
                if(presenter != null) {
                    switch (viewId) {
                        case R.id.call_layout:
                            FamilyCallDialogFragment.launchDialog(activity, presenter.getFamilyId());
                            break;
                        case R.id.refer_to_facility_layout:
                            presenter.startSickChildReferralForm();
                            break;
                        default:
                            break;
                    }
                }
            }
        };
    }

    @Override
    public boolean showMalariaConfirmationMenu() {
        return true;
    }
}
