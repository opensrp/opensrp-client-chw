package org.smartgresiter.wcaro.listener;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import org.smartgresiter.wcaro.R;
import org.smartgresiter.wcaro.activity.ChildRegisterActivity;
import org.smartgresiter.wcaro.activity.FamilyRegisterActivity;
import org.smartgresiter.wcaro.adapter.NavigationAdapter;
import org.smartgresiter.wcaro.util.Constants;

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
                    default:
                        break;
                }


                navigationAdapter.setSelectedView(tag);
            }
        }
    }

    private void startRegisterActivity(Class registerClass){
        Intent intent = new Intent(activity, registerClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
        activity.finish();
    }
}
