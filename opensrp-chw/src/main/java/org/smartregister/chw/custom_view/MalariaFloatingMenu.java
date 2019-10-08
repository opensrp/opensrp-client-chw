package org.smartregister.chw.custom_view;

import android.content.Context;
import android.util.AttributeSet;

import org.smartregister.chw.core.custom_views.CoreMalariaFloatingMenu;

public class MalariaFloatingMenu extends CoreMalariaFloatingMenu {
    public MalariaFloatingMenu(Context context, String clientName, String clientPhone, String clientFamilyHeadName, String clientFamilyHeadPhone) {
        super(context, clientName, clientPhone, clientFamilyHeadName, clientFamilyHeadPhone);
    }

    public MalariaFloatingMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
}
