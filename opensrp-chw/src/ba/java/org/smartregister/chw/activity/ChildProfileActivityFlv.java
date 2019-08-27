package org.smartregister.chw.activity;

import android.app.Activity;

import org.smartregister.chw.R;
import org.smartregister.chw.core.fragment.FamilyCallDialogFragment;
import org.smartregister.chw.core.listener.OnClickFloatingMenu;
import org.smartregister.chw.presenter.ChildProfilePresenter;

public class ChildProfileActivityFlv implements ChildProfileActivity.Flavor {

    @Override
    public OnClickFloatingMenu getOnClickFloatingMenu(final Activity activity, final ChildProfilePresenter presenter) {
        return viewId -> {
            if (presenter != null) {
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
        };
    }


    @Override
    public boolean showMalariaConfirmationMenu() {
        return true;
    }

    @Override
    public boolean showFollowUpVisit() {
        return true;
    }
}
