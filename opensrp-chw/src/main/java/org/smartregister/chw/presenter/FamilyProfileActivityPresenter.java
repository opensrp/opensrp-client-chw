package org.smartregister.chw.presenter;

import org.smartregister.chw.core.utils.ChildDBConstants;
import org.smartregister.chw.fragment.FamilyProfileActivityFragment;
import org.smartregister.chw.model.WashCheckModel;
import org.smartregister.chw.util.Constants;
import org.smartregister.family.contract.FamilyProfileActivityContract;
import org.smartregister.family.presenter.BaseFamilyProfileActivityPresenter;
import org.smartregister.family.util.DBConstants;

public class FamilyProfileActivityPresenter extends BaseFamilyProfileActivityPresenter {
    private WashCheckModel washCheckModel;

    public FamilyProfileActivityPresenter(FamilyProfileActivityContract.View view, FamilyProfileActivityContract.Model model, String viewConfigurationIdentifier, String familyBaseEntityId) {
        super(view, model, viewConfigurationIdentifier, familyBaseEntityId);
        washCheckModel = new WashCheckModel(familyBaseEntityId);
    }

    @Override
    public String getMainCondition() {
        return String.format(" %s = '%s' and %s is null and ( %s is null OR %s != '0') ", Constants.TABLE_NAME.CHILD_ACTIVITY + ".relational_id", this.familyBaseEntityId, Constants.TABLE_NAME.CHILD_ACTIVITY + "." + DBConstants.KEY.DATE_REMOVED, Constants.TABLE_NAME.CHILD_ACTIVITY + "." + DBConstants.KEY.DATE_VISIT_NOT_DONE, Constants.TABLE_NAME.CHILD_ACTIVITY + "." + DBConstants.KEY.DATE_VISIT_NOT_DONE);
    }

    @Override
    public String getDefaultSortQuery() {
        return Constants.TABLE_NAME.CHILD_ACTIVITY + "." + ChildDBConstants.KEY.EVENT_DATE + " DESC";
    }

    public void fetchLastWashCheck() {

        if (getView() instanceof FamilyProfileActivityFragment) {
            FamilyProfileActivityFragment familyProfileActivityFragment = (FamilyProfileActivityFragment) getView();
            familyProfileActivityFragment.updateWashCheckBar(washCheckModel.getAllWashCheckList());
        }
    }
}
