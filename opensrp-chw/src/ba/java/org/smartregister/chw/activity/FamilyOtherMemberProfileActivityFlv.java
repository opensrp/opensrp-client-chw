package org.smartregister.chw.activity;

import android.app.Activity;
import android.text.TextUtils;
import android.view.Menu;
import android.widget.Toast;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.smartregister.chw.R;
import org.smartregister.chw.fragment.FamilyCallDialogFragment;
import org.smartregister.chw.listener.OnClickFloatingMenu;
import org.smartregister.commonregistry.CommonPersonObjectClient;

public class FamilyOtherMemberProfileActivityFlv implements FamilyOtherMemberProfileActivity.Flavor {

    @Override
    public OnClickFloatingMenu getOnClickFloatingMenu(final Activity activity, final String familyBaseEntityId) {
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
            return age >= 15 && age <= 49;
        }

        return false;
    }


    @Override
    public Boolean onCreateOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_malaria_registration).setVisible(true);
        menu.findItem(R.id.action_malaria_followup_visit).setVisible(false);
        return true;
    }

}
