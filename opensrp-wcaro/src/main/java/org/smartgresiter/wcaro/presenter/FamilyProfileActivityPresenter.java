package org.smartgresiter.wcaro.presenter;

import org.smartgresiter.wcaro.util.ChildDBConstants;
import org.smartgresiter.wcaro.util.Constants;
import org.smartregister.family.contract.FamilyProfileActivityContract;
import org.smartregister.family.presenter.BaseFamilyProfileActivityPresenter;
import org.smartregister.family.util.DBConstants;

public class FamilyProfileActivityPresenter extends BaseFamilyProfileActivityPresenter {

    public FamilyProfileActivityPresenter(FamilyProfileActivityContract.View view, FamilyProfileActivityContract.Model model, String viewConfigurationIdentifier, String familyBaseEntityId) {
        super(view, model, viewConfigurationIdentifier, familyBaseEntityId);
    }

    @Override
    public String getMainCondition() {
        return String.format(" %s = '%s' and %s is null ", Constants.TABLE_NAME.CHILD_ACTIVITY + ".relational_id", this.familyBaseEntityId, Constants.TABLE_NAME.CHILD_ACTIVITY + "." + DBConstants.KEY.DATE_REMOVED);
    }

    @Override
    public String getDefaultSortQuery() {
        return Constants.TABLE_NAME.CHILD_ACTIVITY + "." + ChildDBConstants.KEY.EVENT_DATE + " DESC" ;
    }
}
