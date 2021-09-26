package org.smartregister.chw.custom_view;

import android.view.View;

public class DefaultFamilyFloatingMenuFlv implements ChwFamilyFloatingMenu.Flavor {

    @Override
    public void setCallLayoutVisibility(View view) {
      view.setVisibility(View.VISIBLE);
    }
}
