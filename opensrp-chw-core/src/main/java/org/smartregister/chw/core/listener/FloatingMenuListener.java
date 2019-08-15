package org.smartregister.chw.core.listener;

import android.app.Activity;

import org.smartregister.chw.core.R;
import org.smartregister.chw.core.fragment.AddMemberFragment;
import org.smartregister.chw.core.fragment.FamilyCallDialogFragment;

import java.lang.ref.WeakReference;

import timber.log.Timber;

public class FloatingMenuListener implements OnClickFloatingMenu {
    private static FloatingMenuListener instance;
    private WeakReference<Activity> context;
    private String familyBaseEntityId;

    private FloatingMenuListener(Activity context, String familyBaseEntityId) {
        this.context = new WeakReference<>(context);
        this.familyBaseEntityId = familyBaseEntityId;
    }

    public static FloatingMenuListener getInstance(Activity context, String familyBaseEntityId) {
        if (instance == null) {
            instance = new FloatingMenuListener(context, familyBaseEntityId);
        } else {
            instance.setFamilyBaseEntityId(familyBaseEntityId);
            if (instance.context.get() != context) {
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

    @Override
    public void onClickMenu(int viewId) {
        if (context.get() != null) {

            if (context.get().isDestroyed()) {
                Timber.d("Activity Destroyed");
                return;
            }

            if (viewId == R.id.call_layout) {
                FamilyCallDialogFragment.launchDialog(context.get(), familyBaseEntityId);
            } else if (viewId == R.id.add_new_member_layout) {
                AddMemberFragment addmemberFragment = AddMemberFragment.newInstance();
                addmemberFragment.setContext(context.get());
                addmemberFragment.show(context.get().getFragmentManager(), AddMemberFragment.DIALOG_TAG);
            }
        }
    }
}
