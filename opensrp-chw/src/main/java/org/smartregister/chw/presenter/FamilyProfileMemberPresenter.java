package org.smartregister.chw.presenter;

import org.smartregister.chw.core.dao.NavigationDao;
import org.smartregister.chw.core.presenter.CoreFamilyProfileMemberPresenter;
import org.smartregister.chw.core.utils.ChildDBConstants;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.family.contract.FamilyProfileMemberContract;
import org.smartregister.family.util.DBConstants;
import org.smartregister.family.util.Utils;

public class FamilyProfileMemberPresenter extends CoreFamilyProfileMemberPresenter {



    public FamilyProfileMemberPresenter(FamilyProfileMemberContract.View view, FamilyProfileMemberContract.Model model, String viewConfigurationIdentifier, String familyBaseEntityId, String familyHead, String primaryCaregiver) {
        super(view, model, viewConfigurationIdentifier, familyBaseEntityId, familyHead, primaryCaregiver);
    }

    @Override
    public void initializeQueries(String mainCondition) {
        String tableName = Utils.metadata().familyMemberRegister.tableName;

        String countSelect = model.countSelect(tableName, mainCondition);
        String mainSelect = model.mainSelect(tableName," ec_family_member.date_removed is Null AND ec_family_member.relational_id = '" + this.familyBaseEntityId + "' AND " + getDueQuery());

        getView().initializeQueryParams(tableName, countSelect, mainSelect);
        getView().initializeAdapter(visibleColumns, familyHead, primaryCaregiver);

        getView().countExecute();
        getView().filterandSortInInitializeQueries();
    }

    @Override
    public String getDefaultSortQuery() {
        return CoreConstants.TABLE_NAME.FAMILY_MEMBER + "." + DBConstants.KEY.DOD + ", " + CoreConstants.TABLE_NAME.FAMILY_MEMBER + "." + DBConstants.KEY.DOB + " ASC ";
    }

    public String getMainCondition() {
        return String.format(" %s.%s = '%s' and %s.%s is null ", CoreConstants.TABLE_NAME.FAMILY_MEMBER, DBConstants.KEY.RELATIONAL_ID, this.familyBaseEntityId, CoreConstants.TABLE_NAME.FAMILY_MEMBER, DBConstants.KEY.DATE_REMOVED);
    }

    public String getChildFilter() {
        return " and (( ifnull( " + CoreConstants.TABLE_NAME.CHILD + "." + ChildDBConstants.KEY.ENTRY_POINT + ",'') <> 'PNC' ) or (ifnull(" + CoreConstants.TABLE_NAME.CHILD + "." + ChildDBConstants.KEY.ENTRY_POINT + ",'') = 'PNC' and date(" + CoreConstants.TABLE_NAME.CHILD + "." + DBConstants.KEY.DOB + ", '+28 days') < date())) ";
    }

    private String getDueQuery() {
        return " (ifnull(schedule_service.completion_date,'') = '' and schedule_service.expiry_date >= strftime('%Y-%m-%d') and schedule_service.due_date <= strftime('%Y-%m-%d') and ifnull(schedule_service.not_done_date,'') = '' ) ";
    }

    public Integer getDueCount() {
        String sql = "select count(*) from schedule_service where " + getDueQuery() +
                " and schedule_service.base_entity_id in (select object_id from ec_family_member_search  where object_relational_id = '" + this.familyBaseEntityId + "' and date_removed is null ) ";

        return NavigationDao.getQueryCount(sql);
    }
}
