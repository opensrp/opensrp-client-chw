package org.smartregister.chw.activity;

import android.app.Activity;

import org.smartregister.chw.R;
import org.smartregister.chw.core.fragment.FamilyCallDialogFragment;
import org.smartregister.chw.core.listener.OnClickFloatingMenu;
import org.smartregister.chw.presenter.ChildProfilePresenter;

public abstract class DefaultChildProfileActivityFlv implements ChildProfileActivity.Flavor {

    @Override
    public OnClickFloatingMenu getOnClickFloatingMenu(final Activity activity, final ChildProfilePresenter presenter) {
        return viewId -> {
            if (viewId == R.id.fab) {
                FamilyCallDialogFragment.launchDialog(activity, presenter.getFamilyId());
            }
        };
    }

    @Override
    public boolean showMalariaConfirmationMenu() {
        return false;
    }

    @Override
    public boolean showFollowUpVisit() {
        return false;
    }
}
