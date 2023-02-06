package org.smartregister.chw.custom_view;

import android.view.View;

public class FamilyFloatingMenuFlv extends DefaultFamilyFloatingMenuFlv {
    @Override
    public void setCallLayoutVisibility(View view) {
        view.setVisibility(View.GONE);
    }

}
