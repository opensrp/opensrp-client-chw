package org.smartregister.chw.listener;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import org.smartregister.chw.R;
import org.smartregister.chw.activity.FamilyProfileActivity;
import org.smartregister.chw.activity.FamilyProfileMenuActivity;
import org.smartregister.chw.activity.FamilyRemoveMemberActivity;
import org.smartregister.chw.fragment.AddMemberFragment;
import org.smartregister.chw.fragment.FamilyCallDialogFragment;
import org.smartregister.chw.util.Constants;

import java.lang.ref.WeakReference;

public class FloatingMenuListener implements OnClickFloatingMenu {
    static String TAG = FloatingMenuListener.class.getCanonicalName();
    private WeakReference<Activity> context;
    private String familyBaseEntityId;
    private String familyHead;
    private String primaryCareGiver;

    private FloatingMenuListener(Activity context, String familyBaseEntityId) {
        this.context = new WeakReference<>(context);
        this.familyBaseEntityId = familyBaseEntityId;
    }

    private static FloatingMenuListener instance;

    public static FloatingMenuListener getInstance(Activity context, String familyBaseEntityId) {
        if (instance == null) {
            instance = new FloatingMenuListener(context, familyBaseEntityId);
        } else {
            instance.setFamilyBaseEntityId(familyBaseEntityId);
            if(instance.context.get() != context){
                instance.context = new WeakReference<>(context);
            }
        }
        return instance;
    }

    public String getFamilyBaseEntityId() {
        return familyBaseEntityId;
    }

    public FloatingMenuListener setFamilyBaseEntityId(String familyBaseEntityId) {
        this.familyBaseEntityId = familyBaseEntityId;
        return this;
    }

    public String getFamilyHead() {
        return familyHead;
    }

    public FloatingMenuListener setFamilyHead(String familyHead) {
        this.familyHead = familyHead;
        return this;
    }

    public String getPrimaryCareGiver() {
        return primaryCareGiver;
    }

    public FloatingMenuListener setPrimaryCareGiver(String primaryCareGiver) {
        this.primaryCareGiver = primaryCareGiver;
        return this;
    }

    @Override
    public void onClickMenu(int viewId) {
        if (context.get() != null) {

            if(context.get().isDestroyed()){
                Log.d(TAG, "Activity Destroyed");
                return;
            }

            switch (viewId) {
                case R.id.call_layout:
                    // Toast.makeText(context, "Go to call screen", Toast.LENGTH_SHORT).show();
                    FamilyCallDialogFragment.launchDialog(context.get(), familyBaseEntityId);
                    //go to child add form activity
                    break;

                case R.id.add_new_member_layout:

                    AddMemberFragment addmemberFragment = AddMemberFragment.newInstance();
                    addmemberFragment.setContext(context.get());
                    addmemberFragment.show(context.get().getFragmentManager(), AddMemberFragment.DIALOG_TAG);

                    break;
            }
        }
    }
}
