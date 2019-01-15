package org.smartgresiter.wcaro.listener;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.widget.Toast;

import org.smartgresiter.wcaro.R;
import org.smartgresiter.wcaro.activity.FamilyProfileActivity;
import org.smartgresiter.wcaro.activity.FamilyProfileMenu;
import org.smartgresiter.wcaro.fragment.AddMemberFragment;
import org.smartgresiter.wcaro.fragment.FamilyCallDialogFragment;

public class FloatingMenuListener implements OnClickFloatingMenu {

    private Activity context;

    public FloatingMenuListener(Activity context) {
        this.context = context;
    }

    @Override
    public void onClickMenu(int viewId) {
        switch (viewId) {
            case R.id.call_layout:
                // Toast.makeText(context, "Go to call screen", Toast.LENGTH_SHORT).show();
                FamilyCallDialogFragment dialog = FamilyCallDialogFragment.showDialog(context, ((FamilyProfileActivity) context).getFamilyBaseEntityId());
                //go to child add form activity
                break;
            case R.id.family_detail_layout:
                ((FamilyProfileActivity) context).startFormForEdit();
                break;
            case R.id.add_new_member_layout:
                FragmentTransaction ft = context.getFragmentManager().beginTransaction();
                AddMemberFragment addmemberFragment = AddMemberFragment.newInstance();
                addmemberFragment.setContext(context);
                addmemberFragment.show(context.getFragmentManager(), AddMemberFragment.DIALOG_TAG);
                break;

            case R.id.remove_member_layout:
                Toast.makeText(context, "Go to remove member", Toast.LENGTH_SHORT).show();
                //go to child add form activity
                break;
            case R.id.change_head_layout:

                Intent intent = new Intent(context, FamilyProfileMenu.class);
                intent.putExtra(org.smartregister.family.util.Constants.INTENT_KEY.BASE_ENTITY_ID,
                        ((FamilyProfileActivity) context).getFamilyBaseEntityId());
                intent.putExtra(FamilyProfileMenu.MENU, FamilyProfileMenu.MenuType.ChangeHead);
                context.startActivity(intent);

                break;
            case R.id.change_primary_layout:
                Toast.makeText(context, "Go to change primary caregiver", Toast.LENGTH_SHORT).show();
                //go to child add form activity
                break;
        }
    }
}
