package org.smartregister.brac.hnpp.presenter;

import org.smartregister.chw.core.utils.ChildDBConstants;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.family.contract.FamilyProfileActivityContract;
import org.smartregister.family.presenter.BaseFamilyProfileActivityPresenter;
import org.smartregister.family.util.Constants;
import org.smartregister.family.util.DBConstants;

public class MemberProfileActivityPresenter extends BaseFamilyProfileActivityPresenter {

    public MemberProfileActivityPresenter(FamilyProfileActivityContract.View view, FamilyProfileActivityContract.Model model, String viewConfigurationIdentifier, String familyBaseEntityId) {
        super(view, model, viewConfigurationIdentifier, familyBaseEntityId);
    }

    @Override
    public String getMainCondition() {
        return String.format(" %s = '%s' and %s is null and ( %s is null OR %s != '0') ", CoreConstants.TABLE_NAME.CHILD_ACTIVITY + ".relational_id", this.familyBaseEntityId, CoreConstants.TABLE_NAME.CHILD_ACTIVITY + "." + DBConstants.KEY.DATE_REMOVED, CoreConstants.TABLE_NAME.CHILD_ACTIVITY + "." + DBConstants.KEY.DATE_VISIT_NOT_DONE, CoreConstants.TABLE_NAME.CHILD_ACTIVITY + "." + DBConstants.KEY.DATE_VISIT_NOT_DONE);
    }

    @Override
    public String getDefaultSortQuery() {
        return CoreConstants.TABLE_NAME.CHILD_ACTIVITY + "." + ChildDBConstants.KEY.EVENT_DATE + " DESC";
    }
}
