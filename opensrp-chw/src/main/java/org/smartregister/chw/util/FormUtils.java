package org.smartregister.chw.util;

import android.util.Pair;

import org.smartregister.chw.activity.FamilyWizardFormExtendedActivity;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.family.domain.FamilyMetadata;
import org.smartregister.view.activity.BaseProfileActivity;

import java.util.ArrayList;

public class FormUtils extends org.smartregister.chw.core.utils.FormUtils {


    public static FamilyMetadata getFamilyMetadata(BaseProfileActivity baseProfileActivity, String defaultLocation, ArrayList<String> locationHierarchy, ArrayList<Pair<String, String>> locationFields) {
        FamilyMetadata metadata = new FamilyMetadata(FamilyWizardFormExtendedActivity.class, FamilyWizardFormExtendedActivity.class,
                baseProfileActivity.getClass(), CoreConstants.IDENTIFIER.UNIQUE_IDENTIFIER_KEY, false);
        metadata.updateFamilyRegister(CoreConstants.JSON_FORM.getFamilyRegister(), CoreConstants.TABLE_NAME.FAMILY,
                CoreConstants.EventType.FAMILY_REGISTRATION, CoreConstants.EventType.UPDATE_FAMILY_REGISTRATION,
                CoreConstants.CONFIGURATION.FAMILY_REGISTER, CoreConstants.RELATIONSHIP.FAMILY_HEAD, CoreConstants.RELATIONSHIP.PRIMARY_CAREGIVER);
        metadata.updateFamilyMemberRegister(CoreConstants.JSON_FORM.getFamilyMemberRegister(),
                CoreConstants.TABLE_NAME.FAMILY_MEMBER, CoreConstants.EventType.FAMILY_MEMBER_REGISTRATION,
                CoreConstants.EventType.UPDATE_FAMILY_MEMBER_REGISTRATION, CoreConstants.CONFIGURATION.FAMILY_MEMBER_REGISTER, CoreConstants.RELATIONSHIP.FAMILY);

        metadata.updateFamilyDueRegister(CoreConstants.TABLE_NAME.CHILD, Integer.MAX_VALUE, false);
        metadata.updateFamilyActivityRegister(CoreConstants.TABLE_NAME.CHILD_ACTIVITY, Integer.MAX_VALUE, false);
        metadata.updateFamilyOtherMemberRegister(CoreConstants.TABLE_NAME.FAMILY_MEMBER, Integer.MAX_VALUE, false);
        metadata.setDefaultLocation(defaultLocation);
        metadata.setLocationHierarchy(locationHierarchy);
        metadata.setLocationFields(locationFields);
        return metadata;
    }
}
