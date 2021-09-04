package org.smartregister.chw.custom_view;

import android.content.Context;

import org.smartregister.chw.core.custom_views.CoreHivIndexContactFloatingMenu;
import org.smartregister.chw.hiv.domain.HivIndexContactObject;

public class HivIndexContactFloatingMenu extends CoreHivIndexContactFloatingMenu {
    public HivIndexContactFloatingMenu(Context context, HivIndexContactObject hivIndexContactObject) {
        super(context, hivIndexContactObject);
        referLayout.setVisibility(GONE);
    }
}
