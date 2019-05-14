package org.smartregister.chw.activity;

import android.app.Activity;
import android.widget.Toast;

import org.smartregister.chw.R;
import org.smartregister.chw.fragment.FamilyCallDialogFragment;
import org.smartregister.chw.listener.OnClickFloatingMenu;

public class FamilyOtherMemberProfileActivityFlv {

    public static OnClickFloatingMenu getOnClickFloatingMenu(final Activity activity, final String familyBaseEntityId) {
        return new OnClickFloatingMenu() {
            @Override
            public void onClickMenu(int viewId) {
                switch (viewId) {
                    case R.id.call_layout:
                        FamilyCallDialogFragment.launchDialog(activity, familyBaseEntityId);
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

}
