package org.smartregister.chw.custom_view;

import android.content.Context;
import android.util.AttributeSet;

import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.core.custom_views.CoreMalariaFloatingMenu;
import org.smartregister.chw.malaria.domain.MemberObject;

public class MalariaFloatingMenu extends CoreMalariaFloatingMenu {
    public MalariaFloatingMenu(Context context, MemberObject MEMBER_OBJECT) {
        super(context, MEMBER_OBJECT);
    }

}
