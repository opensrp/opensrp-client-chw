package org.smartregister.chw.presenter;

import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.model.WashCheckModel;
import org.smartregister.family.contract.FamilyProfileDueContract;
import org.smartregister.family.presenter.BaseFamilyProfileDuePresenter;

public class FamilyProfileDuePresenter extends BaseFamilyProfileDuePresenter {
    private WashCheckModel washCheckModel;

    public FamilyProfileDuePresenter(FamilyProfileDueContract.View view, FamilyProfileDueContract.Model model, String viewConfigurationIdentifier, String familyBaseEntityId) {
        super(view, model, viewConfigurationIdentifier, familyBaseEntityId);
        washCheckModel = new WashCheckModel(familyBaseEntityId);
    }

    @Override
    public void initializeQueries(String mainCondition) {
        String tableName = CoreConstants.TABLE_NAME.SCHEDULE_SERVICE;

        String selectCondition = " ( ec_family_member.relational_id = '" + this.familyBaseEntityId + "' or ec_family.base_entity_id = '" + this.familyBaseEntityId + "' ) AND "
                + getDueQuery() + getPNCChildQuery();

        String countSelect = model.countSelect(tableName, selectCondition);
        String mainSelect = model.mainSelect(tableName, selectCondition);

        getView().initializeQueryParams(CoreConstants.TABLE_NAME.FAMILY_MEMBER, countSelect, mainSelect);
        getView().initializeAdapter(visibleColumns);

        getView().countExecute();
        getView().filterandSortInInitializeQueries();
    }

    private String getPNCChildQuery() {
        return "AND CASE WHEN ec_family_member.entity_type = 'ec_child' THEN ((date(ec_family_member.dob, '+28 days') <= date()) OR " +
                "  ((date(ec_family_member.dob, '+28 days') >= date()) AND ifnull(ec_child.entry_point,'') <> 'PNC'))" +
                " ELSE true END";
    }

    private String getDueQuery() {
        return " (ifnull(schedule_service.completion_date,'') = '' and schedule_service.expiry_date >= strftime('%Y-%m-%d') and schedule_service.due_date <= strftime('%Y-%m-%d') and ifnull(schedule_service.not_done_date,'') = '' ) ";
    }

    public boolean saveData(String jsonObject) {
        return washCheckModel.saveWashCheckEvent(jsonObject);
    }

}
