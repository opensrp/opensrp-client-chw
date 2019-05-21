package org.smartregister.chw.activity;

import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;

import org.smartregister.chw.R;
import org.smartregister.chw.fragment.FamilyCallDialogFragment;
import org.smartregister.chw.listener.OnClickFloatingMenu;

public class FamilyOtherMemberProfileActivityFlv {

    public static OnClickFloatingMenu getOnClickFloatingMenu(final Activity activity, final String familyBaseEntityId) {
        return new OnClickFloatingMenu() {
            @Override
            public void onClickMenu(int viewId) {
                switch (viewId) {
                    case R.id.fab:
                        FamilyCallDialogFragment.launchDialog(activity, familyBaseEntityId);
                        break;
                    default:
                        break;
                }
            }
        };
    }

    public static boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

}
