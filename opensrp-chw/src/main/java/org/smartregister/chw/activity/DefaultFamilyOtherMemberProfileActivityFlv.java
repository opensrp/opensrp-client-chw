package org.smartregister.chw.activity;

import android.app.Activity;
import android.view.Menu;

import org.smartregister.chw.R;
import org.smartregister.chw.core.fragment.FamilyCallDialogFragment;
import org.smartregister.chw.core.listener.OnClickFloatingMenu;
import org.smartregister.commonregistry.CommonPersonObjectClient;

import static org.smartregister.chw.core.utils.Utils.isWomanOfReproductiveAge;

public abstract class DefaultFamilyOtherMemberProfileActivityFlv implements FamilyOtherMemberProfileActivity.Flavor {

    public OnClickFloatingMenu getOnClickFloatingMenu(final Activity activity, final String familyBaseEntityId) {
        return viewId -> {
            if (viewId == R.id.fab) {
                FamilyCallDialogFragment.launchDialog(activity, familyBaseEntityId);
            }
        };
    }

    @Override
    public boolean isWra(CommonPersonObjectClient commonPersonObject) {
        return isWomanOfReproductiveAge(commonPersonObject, 10, 49);
    }

    @Override
    public void updateFpMenuItems(String baseEntityId, Menu menu) {
//        TODO implement if wcaro would need fp module
    }

    @Override
    public void updateMalariaMenuItems(String baseEntityId, Menu menu) {
//        TODO implement if wcaro would need malaria module
    }

    @Override
    public boolean hasANC() {
        return true;
    }
}