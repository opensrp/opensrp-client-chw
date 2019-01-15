package org.smartgresiter.wcaro.listener;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.widget.Toast;

import org.smartgresiter.wcaro.R;
import org.smartgresiter.wcaro.activity.FamilyProfileActivity;
import org.smartgresiter.wcaro.activity.FamilyProfileMenuActivity;
import org.smartgresiter.wcaro.activity.FamilyRemoveMemberActivity;
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

                Intent frm_intent = new Intent(context, FamilyRemoveMemberActivity.class);
                if (context instanceof FamilyProfileActivity) {
                    frm_intent.putExtras(((FamilyProfileActivity) context).getProfileExtras());
                }
                context.startActivity(frm_intent);

                break;
            case R.id.change_head_layout:

                Intent fh_intent = new Intent(context, FamilyProfileMenuActivity.class);
                fh_intent.putExtra(org.smartregister.family.util.Constants.INTENT_KEY.BASE_ENTITY_ID,
                        ((FamilyProfileActivity) context).getFamilyBaseEntityId());
                fh_intent.putExtra(FamilyProfileMenuActivity.MENU, FamilyProfileMenuActivity.MenuType.ChangeHead);
                context.startActivity(fh_intent);

                break;
            case R.id.change_primary_layout:

                Intent pc_intent = new Intent(context, FamilyProfileMenuActivity.class);
                pc_intent.putExtra(org.smartregister.family.util.Constants.INTENT_KEY.BASE_ENTITY_ID,
                        ((FamilyProfileActivity) context).getFamilyBaseEntityId());
                pc_intent.putExtra(FamilyProfileMenuActivity.MENU, FamilyProfileMenuActivity.MenuType.ChangePrimaryCare);
                context.startActivity(pc_intent);

                break;
        }
    }
}
