package org.smartregister.chw.custom_view;

import android.content.Context;
import android.util.AttributeSet;

import org.smartregister.chw.core.custom_views.CoreAncFloatingMenu;

public class AncFloatingMenu extends CoreAncFloatingMenu {
    private Flavor flavor;

    public AncFloatingMenu(Context context, String ancWomanName, String ancWomanPhone, String ancFamilyHeadName, String ancFamilyHeadPhone, String profileType) {
        super(context, ancWomanName, ancWomanPhone, ancFamilyHeadName, ancFamilyHeadPhone, profileType);
    }

    public AncFloatingMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initUi() {
        super.initUi();
        this.referLayout.setVisibility(getFlavor().hasReferral() ? VISIBLE : GONE);
    }

    private Flavor getFlavor() {
        if (flavor == null) {
            flavor = new AncFloatingMenuFlv();
        }
        return flavor;
    }

    public interface Flavor {
        boolean hasReferral();
    }
}
