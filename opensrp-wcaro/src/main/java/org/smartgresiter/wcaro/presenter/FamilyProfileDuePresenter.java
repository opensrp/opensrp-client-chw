package org.smartgresiter.wcaro.presenter;

import org.smartgresiter.wcaro.util.ChildDBConstants;
import org.smartgresiter.wcaro.util.Constants;
import org.smartregister.family.contract.FamilyProfileDueContract;
import org.smartregister.family.presenter.BaseFamilyProfileDuePresenter;

public class FamilyProfileDuePresenter extends BaseFamilyProfileDuePresenter {

    public FamilyProfileDuePresenter(FamilyProfileDueContract.View view, FamilyProfileDueContract.Model model, String viewConfigurationIdentifier, String familyBaseEntityId) {
        super(view, model, viewConfigurationIdentifier, familyBaseEntityId);
    }

    @Override
    public String getMainCondition() {
        return super.getMainCondition() + " AND " + getChildEntityCondition() + " AND " + ChildDBConstants.childDueFilter();
    }

    private String getChildEntityCondition() {
        return String.format(" %s = '%s' ", ChildDBConstants.KEY.ENTITY_TYPE, Constants.TABLE_NAME.CHILD);
    }
}
