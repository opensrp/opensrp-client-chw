package org.smartgresiter.wcaro.listener;

import android.content.Context;
import android.widget.Toast;

import org.smartgresiter.wcaro.R;
import org.smartgresiter.wcaro.util.OnClickFloatingMenu;

public class FloatingMenuListener implements OnClickFloatingMenu {

    private Context context;

    public FloatingMenuListener(Context context) {
        this.context = context;
    }

    @Override
    public void onClickMenu(int viewId) {
        switch (viewId) {
            case R.id.call_layout:
                Toast.makeText(context, "Go to call screen", Toast.LENGTH_SHORT).show();
                //go to child add form activity
                break;
            case R.id.family_detail_layout:
                Toast.makeText(context, "Go to family details", Toast.LENGTH_SHORT).show();
                //go to child add form activity
                break;
            case R.id.add_new_member_layout:
                Toast.makeText(context, "Go to new family member add", Toast.LENGTH_SHORT).show();
                //go to child add form activity
                break;

            case R.id.remove_member_layout:
                Toast.makeText(context, "Go to remove member", Toast.LENGTH_SHORT).show();
                //go to child add form activity
                break;
            case R.id.change_head_layout:
                Toast.makeText(context, "Go to change family head", Toast.LENGTH_SHORT).show();
                //go to child add form activity
                break;
            case R.id.change_primary_layout:
                Toast.makeText(context, "Go to change primary caregiver", Toast.LENGTH_SHORT).show();
                //go to child add form activity
                break;
        }
    }
}
