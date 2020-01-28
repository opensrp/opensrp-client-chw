package org.smartregister.chw.presenter;

import org.smartregister.chw.core.presenter.CoreFamilyProfileMemberPresenter;
import org.smartregister.chw.core.utils.ChildDBConstants;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.family.contract.FamilyProfileMemberContract;
import org.smartregister.family.util.DBConstants;

public class FamilyProfileMemberPresenter extends CoreFamilyProfileMemberPresenter {

    public FamilyProfileMemberPresenter(FamilyProfileMemberContract.View view, FamilyProfileMemberContract.Model model, String viewConfigurationIdentifier, String familyBaseEntityId, String familyHead, String primaryCaregiver) {
        super(view, model, viewConfigurationIdentifier, familyBaseEntityId, familyHead, primaryCaregiver);
    }

    @Override
    public String getDefaultSortQuery() {
        return CoreConstants.TABLE_NAME.FAMILY_MEMBER + "." + DBConstants.KEY.DOD + ", " + CoreConstants.TABLE_NAME.FAMILY_MEMBER + "." + DBConstants.KEY.DOB + " ASC ";
    }

    public String getMainCondition() {
        return String.format(" %s.%s = '%s' and (%s.%s is null or %s.%s is not null ) ",
                CoreConstants.TABLE_NAME.FAMILY_MEMBER, DBConstants.KEY.RELATIONAL_ID, this.familyBaseEntityId,
                CoreConstants.TABLE_NAME.FAMILY_MEMBER, DBConstants.KEY.DATE_REMOVED,
                CoreConstants.TABLE_NAME.FAMILY_MEMBER, DBConstants.KEY.DOD
        );
    }

    public String getChildFilter() {
      return  " and (( ifnull(" + CoreConstants.TABLE_NAME.CHILD + "." + ChildDBConstants.KEY.ENTRY_POINT + ",'') <> 'PNC' ) or (ifnull(" + CoreConstants.TABLE_NAME.CHILD + "." + ChildDBConstants.KEY.ENTRY_POINT + ",'') = 'PNC' and ( date(" + CoreConstants.TABLE_NAME.CHILD + "." +  DBConstants.KEY.DOB + ", '+28 days') <= date() and ((SELECT is_closed FROM ec_family_member WHERE base_entity_id = " + CoreConstants.TABLE_NAME.CHILD + "." + ChildDBConstants.KEY.MOTHER_ENTITY_ID + " ) = 0)))  or (ifnull(ec_child.entry_point,'') = 'PNC'  and (SELECT is_closed FROM ec_family_member WHERE base_entity_id = ec_child.mother_entity_id ) = 1)) " ;
    }
}
