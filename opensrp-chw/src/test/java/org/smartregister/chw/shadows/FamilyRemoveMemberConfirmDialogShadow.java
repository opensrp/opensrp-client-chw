package org.smartregister.chw.shadows;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.smartregister.chw.core.fragment.FamilyRemoveMemberConfirmDialog;

/**
 * @author rkodev
 */

@Implements(FamilyRemoveMemberConfirmDialog.class)
public class FamilyRemoveMemberConfirmDialogShadow {

    private static FamilyRemoveMemberConfirmDialog instance;

    @Implementation
    public static FamilyRemoveMemberConfirmDialog newInstance(String message) {
        if (instance == null) {
            instance = new FamilyRemoveMemberConfirmDialog();
        }
        return instance;
    }

    public static void setInstance(FamilyRemoveMemberConfirmDialog dialog) {
        instance = dialog;
    }
}
