package org.smartregister.chw.activity;

import android.app.Activity;
import android.text.TextUtils;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.smartregister.chw.R;
import org.smartregister.chw.fragment.FamilyCallDialogFragment;
import org.smartregister.chw.listener.OnClickFloatingMenu;
import org.smartregister.commonregistry.CommonPersonObjectClient;

public abstract class DefaultFamilyOtherMemberProfileActivityFlv implements FamilyOtherMemberProfileActivity.Flavor {

    public OnClickFloatingMenu getOnClickFloatingMenu(final Activity activity, final String familyBaseEntityId) {
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

    @Override
    public boolean isWra(CommonPersonObjectClient commonPersonObject) {
        if (commonPersonObject == null) {
            return false;
        }

        // check age and gender
        String dobString = org.smartregister.util.Utils.getValue(commonPersonObject.getColumnmaps(), "dob", false);
        String gender = org.smartregister.util.Utils.getValue(commonPersonObject.getColumnmaps(), "gender", false);
        if (!TextUtils.isEmpty(dobString) && gender.trim().equalsIgnoreCase("Female")) {
            Period period = new Period(new DateTime(dobString), new DateTime());
            int age = period.getYears();
            return age >= 10 && age <= 49;
        }

        return false;
    }

    @Override
    public boolean showMalariaConfirmationMenu() {
        return false;
    }

}