package org.smartregister.chw.presenter;

import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.model.WashCheckModel;
import org.smartregister.family.contract.FamilyProfileDueContract;
import org.smartregister.family.presenter.BaseFamilyProfileDuePresenter;

public class FamilyProfileDuePresenter extends BaseFamilyProfileDuePresenter {
    private WashCheckModel washCheckModel;
    private String childBaseEntityId;

    public FamilyProfileDuePresenter(FamilyProfileDueContract.View view, FamilyProfileDueContract.Model model, String viewConfigurationIdentifier, String familyBaseEntityId, String childBaseEntityId) {
        super(view, model, viewConfigurationIdentifier, familyBaseEntityId);
        washCheckModel = new WashCheckModel(familyBaseEntityId);
        this.childBaseEntityId = childBaseEntityId;
    }

    @Override
    public void initializeQueries(String mainCondition) {
        String tableName = CoreConstants.TABLE_NAME.SCHEDULE_SERVICE;

        String selectCondition = getSelectCondition();


        String countSelect = model.countSelect(tableName, selectCondition);
        String mainSelect = model.mainSelect(tableName, selectCondition);

        getView().initializeQueryParams(CoreConstants.TABLE_NAME.FAMILY_MEMBER, countSelect, mainSelect);
        getView().initializeAdapter(visibleColumns);

        getView().countExecute();
        getView().filterandSortInInitializeQueries();
    }

    private String getDefaultChildDueQuery() {
        return " (ifnull(schedule_service.completion_date,'') = '' and schedule_service.expiry_date >= strftime('%Y-%m-%d') and schedule_service.due_date <= strftime('%Y-%m-%d') and ifnull(schedule_service.not_done_date,'') = '' ) ";
    }

    private String getSelectCondition() {
        return " ( ec_family_member.relational_id = '" + this.familyBaseEntityId + "' or ec_family.base_entity_id = '" + this.familyBaseEntityId + "' ) AND "
                + getDefaultChildDueQuery();
    }

    public boolean saveData(String jsonObject) {
        return washCheckModel.saveWashCheckEvent(jsonObject);
    }

}
