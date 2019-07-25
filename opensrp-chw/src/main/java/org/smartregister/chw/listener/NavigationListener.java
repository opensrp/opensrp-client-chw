package org.smartregister.chw.listener;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;
import org.smartregister.chw.R;
import org.smartregister.chw.activity.AncRegisterActivity;
import org.smartregister.chw.activity.ChildRegisterActivity;
import org.smartregister.chw.activity.FamilyRegisterActivity;
import org.smartregister.chw.activity.MalariaRegisterActivity;
import org.smartregister.chw.activity.PncRegisterActivity;
import org.smartregister.chw.adapter.NavigationAdapter;
import org.smartregister.chw.util.Constants;

public class NavigationListener implements View.OnClickListener {

    private Activity activity;
    private NavigationAdapter navigationAdapter;

    public NavigationListener(Activity activity, NavigationAdapter adapter) {
        this.activity = activity;
        this.navigationAdapter = adapter;
    }

    @Override
    public void onClick(View v) {
        if (v.getTag() != null) {
            if (v.getTag() instanceof String) {
                String tag = (String) v.getTag();

                switch (tag) {
                    case Constants.DrawerMenu.CHILD_CLIENTS:
                        startRegisterActivity(ChildRegisterActivity.class);
                        break;
                    case Constants.DrawerMenu.ALL_FAMILIES:
                        startRegisterActivity(FamilyRegisterActivity.class);
                        break;
                    case Constants.DrawerMenu.ANC:
                        startRegisterActivity(AncRegisterActivity.class);
                        break;
                    case Constants.DrawerMenu.LD:
                        Toast.makeText(activity.getApplicationContext(), Constants.DrawerMenu.LD, Toast.LENGTH_SHORT).show();
                        break;
                    case Constants.DrawerMenu.PNC:
                        startRegisterActivity(PncRegisterActivity.class);
                        break;
                    case Constants.DrawerMenu.FAMILY_PLANNING:
                        Toast.makeText(activity.getApplicationContext(), Constants.DrawerMenu.FAMILY_PLANNING, Toast.LENGTH_SHORT).show();
                        break;
                    case Constants.DrawerMenu.MALARIA:
                        startRegisterActivity(MalariaRegisterActivity.class);
                        break;
                    default:
                        break;
                }
                navigationAdapter.setSelectedView(tag);
            }
        }
    }

    private void startRegisterActivity(Class registerClass) {
        Intent intent = new Intent(activity, registerClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
        activity.finish();
    }
}
