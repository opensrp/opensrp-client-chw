package org.smartregister.chw.presenter;

import org.smartregister.chw.core.dao.NavigationDao;
import org.smartregister.chw.core.enums.ImmunizationState;
import org.smartregister.chw.core.rule.WashCheckAlertRule;
import org.smartregister.chw.core.utils.ChildDBConstants;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.WashCheck;
import org.smartregister.chw.fragment.FamilyProfileDueFragment;
import org.smartregister.chw.interactor.ChildProfileInteractor;
import org.smartregister.chw.model.WashCheckModel;
import org.smartregister.family.contract.FamilyProfileDueContract;
import org.smartregister.family.presenter.BaseFamilyProfileDuePresenter;
import org.smartregister.family.util.DBConstants;

public class FamilyProfileDuePresenter extends BaseFamilyProfileDuePresenter {
    private WashCheckModel washCheckModel;

    public FamilyProfileDuePresenter(FamilyProfileDueContract.View view, FamilyProfileDueContract.Model model, String viewConfigurationIdentifier, String familyBaseEntityId) {
        super(view, model, viewConfigurationIdentifier, familyBaseEntityId);
        washCheckModel = new WashCheckModel(familyBaseEntityId);
    }

/*
    @Override
    public String getMainCondition() {
        return String.format(" %s AND %s ", super.getMainCondition(), getDueQuery());
    }

*/
    private String getDueQuery() {
        return " (ifnull(schedule_service.completion_date,'') = '' and schedule_service.expiry_date >= strftime('%Y-%m-%d') and schedule_service.due_date <= strftime('%Y-%m-%d')) ";
    }

    public String getMailTableFilter() {
        return String.format(" %s = '%s' and %s is null ", DBConstants.KEY.BASE_ENTITY_ID, familyBaseEntityId, DBConstants.KEY.DATE_REMOVED);
    }

    @Override
    public String getDefaultSortQuery() {
        return ChildDBConstants.KEY.LAST_HOME_VISIT + ", " + ChildDBConstants.KEY.VISIT_NOT_DONE + " ASC ";
    }

    public boolean saveData(String jsonObject) {
        return washCheckModel.saveWashCheckEvent(jsonObject);
    }

    public void fetchLastWashCheck(long dateCreatedFamily) {
        WashCheck washCheck = washCheckModel.getLatestWashCheck();
        if (washCheck != null) {
            WashCheckAlertRule washCheckAlertRule = new WashCheckAlertRule(getView().getContext(), washCheck.getLastVisit(), dateCreatedFamily);
            if (washCheckAlertRule.isOverdueWithinMonth(1)) {
                washCheck.setStatus(ChildProfileInteractor.VisitType.OVERDUE.name());
            } else if (washCheckAlertRule.isDueWithinMonth()) {
                washCheck.setStatus(ChildProfileInteractor.VisitType.DUE.name());
            } else {
                washCheck.setStatus(ImmunizationState.NO_ALERT.name());
            }
            washCheck.setLastVisitDate(washCheckAlertRule.noOfDayDue);
        }
        if (getView() instanceof FamilyProfileDueFragment) {
            FamilyProfileDueFragment familyProfileDueFragment = (FamilyProfileDueFragment) getView();
            familyProfileDueFragment.updateWashCheckBar(washCheck);
        }
    }

    public Integer getDueCount() {
        String sql = "select count(*) from schedule_service where " + getDueQuery() +
                " and schedule_service.base_entity_id in (select object_id from ec_family_member_search  where object_relational_id = '" + this.familyBaseEntityId + "' and date_removed is null ) ";

        return NavigationDao.getQueryCount(sql);
    }
}
