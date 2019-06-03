package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;
import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.fragment.FamilyCallDialogFragment;
import org.smartregister.chw.listener.OnClickFloatingMenu;
import org.smartregister.chw.presenter.ChildProfilePresenter;
import org.smartregister.family.util.Constants;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.family.util.Utils;

import static android.support.v4.app.ActivityCompat.startActivityForResult;

public class ChildProfileActivityFlv {

    public static OnClickFloatingMenu getOnClickFloatingMenu(final Activity activity, final ChildProfilePresenter presenter) {
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

    public static boolean onCreateOptionsMenu(Menu menu) {
        MenuItem actionMalaria = menu.findItem(R.id.action_malaria_confirmation);
        actionMalaria.setVisible(true);
        return true;
    }

    static Intent startFormActivityForIntent(Context context, JSONObject jsonForm) {
        Intent intent = new Intent(context, Utils.metadata().familyMemberFormActivity);
        intent.putExtra(Constants.JSON_FORM_EXTRA.JSON, jsonForm.toString());

        Form form = new Form();
        form.setActionBarBackground(R.color.family_actionbar);
        form.setWizard(false);
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);

        return intent;
    }

}
