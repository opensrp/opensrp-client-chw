package org.smartregister.chw.activity;

import android.app.Activity;
import android.widget.Toast;

import org.smartregister.chw.R;
import com.opensrp.chw.core.fragment.FamilyCallDialogFragment;
import com.opensrp.chw.core.listener.OnClickFloatingMenu;
import org.smartregister.chw.presenter.ChildProfilePresenter;

public class ChildProfileActivityFlv implements ChildProfileActivity.Flavor {

    @Override
    public OnClickFloatingMenu getOnClickFloatingMenu(final Activity activity, final ChildProfilePresenter presenter) {
        return new OnClickFloatingMenu() {
            @Override
            public void onClickMenu(int viewId) {
                switch (viewId) {
                    case R.id.call_layout:
                        FamilyCallDialogFragment.launchDialog(activity, presenter.getFamilyId());
                        break;
                    case R.id.refer_to_facility_fab:
                        Toast.makeText(activity, "Refer to facility", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }
        };
    }

    @Override
    public boolean showMalariaConfirmationMenu(){
        return true;
    }
}
