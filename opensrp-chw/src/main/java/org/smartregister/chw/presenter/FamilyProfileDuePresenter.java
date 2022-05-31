package org.smartregister.chw.presenter;

import android.content.Context;
import android.util.Pair;

import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.core.dao.VisitDao;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.dao.ChwChildDao;
import org.smartregister.chw.model.FamilyKitModel;
import org.smartregister.chw.model.WashCheckModel;
import org.smartregister.chw.util.UpcomingServicesUtil;
import org.smartregister.dao.AbstractDao;
import org.smartregister.family.contract.FamilyProfileDueContract;
import org.smartregister.family.presenter.BaseFamilyProfileDuePresenter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class FamilyProfileDuePresenter extends BaseFamilyProfileDuePresenter {
    private WashCheckModel washCheckModel;
    private FamilyKitModel familyKitModel;
    private String childBaseEntityId;
    private final Supplier<Context> contextSupplier;

    public FamilyProfileDuePresenter(FamilyProfileDueContract.View view, FamilyProfileDueContract.Model model, String viewConfigurationIdentifier, String familyBaseEntityId, String childBaseEntityId, Supplier<Context> contextSupplier) {
        super(view, model, viewConfigurationIdentifier, familyBaseEntityId);
        washCheckModel = new WashCheckModel(familyBaseEntityId);
        familyKitModel = new FamilyKitModel(familyBaseEntityId);
        this.childBaseEntityId = childBaseEntityId;
        this.contextSupplier = contextSupplier;
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

    private String getChildDueQueryForChildrenUnderTwoAndGirlsAgeNineToEleven() {
        String ageFilter = "(((julianday('now') - julianday(ec_child.dob))/365.25) < 2 or (ec_child.gender = 'Female' and (((julianday('now') - julianday(ec_child.dob))/365.25) BETWEEN 9 AND 11)))\n";
        return (ChwApplication.getApplicationFlavor().checkExtraForDueInFamily() ? ageFilter :
                " (ifnull(schedule_service.completion_date,'') = '' and schedule_service.expiry_date >= strftime('%Y-%m-%d') " +
                        "and schedule_service.due_date <= strftime('%Y-%m-%d') and ifnull(schedule_service.not_done_date,'') = '' ) " +
                        "and " + ageFilter);
    }

    private String getSelectCondition() {
        String condition = " ( ec_family_member.relational_id = '" + this.familyBaseEntityId + "' or ec_family.base_entity_id = '" + this.familyBaseEntityId + "' ) AND ";
        if (ChwApplication.getApplicationFlavor().showChildrenAboveTwoDueStatus()) {
            condition += getDefaultChildDueQuery();
//                    + " EXISTS(select * from alerts where caseID = ec_family_member.base_entity_id and status in ('normal','urgent') and expiryDate > date()) AND "
        } else {
            condition += getChildDueQueryForChildrenUnderTwoAndGirlsAgeNineToEleven();
//                    + "EXISTS(select * from alerts where caseID = ec_family_member.base_entity_id and status in ('normal','urgent') and expiryDate > date()) AND "
        }

        return condition + (ChwApplication.getApplicationFlavor().checkExtraForDueInFamily() ? String.format(" and ec_family_member.base_entity_id in (%s)", validMembers()) : "");
    }

    String validMembers() {
        List<Pair<String, String>> familyMembers = FamilyMemberDao.getFamilyMembers(this.familyBaseEntityId);

        StringBuilder joiner = new StringBuilder();
        for (Pair<String, String> familyMemberRepr : familyMembers) {
            MemberObject member = new MemberObject();
            member.setBaseEntityId(familyMemberRepr.first);
            member.setFamilyBaseEntityId(this.familyBaseEntityId);
            member.setDob(familyMemberRepr.second);

//            boolean vaccineCardReceived = VisitDao.memberHasVaccineCard(member.getBaseEntityId());
            String childGender = ChwChildDao.getChildGender(member.getBaseEntityId());

            if (/*!vaccineCardReceived || */UpcomingServicesUtil.showStatusForChild(member, childGender)
                    && UpcomingServicesUtil.hasUpcomingDueServices(member, contextSupplier.get())) {
                joiner.append(String.format("'%s'", member.getBaseEntityId()));
                joiner.append(",");
            }
        }
        if (!joiner.toString().equalsIgnoreCase("")) {
            joiner.deleteCharAt(joiner.length() - 1);
        }

        return joiner.toString();
    }

    public boolean saveData(String jsonObject) {
        return washCheckModel.saveWashCheckEvent(jsonObject);
    }

    public boolean saveDataFamilyKit(String jsonObject) {
        return familyKitModel.saveFamilyKitEvent(jsonObject);
    }

    static class FamilyMemberDao extends AbstractDao {
        public static List<Pair<String, String>> getFamilyMembers(String baseEntityId) {
            String sql = "SELECT base_entity_id, dob from ec_family_member" +
                    " where relational_id = '" + baseEntityId + "'" +
                    " and is_closed = 0";

            DataMap<Pair<String, String>> dataMap = cursor -> Pair.create(getCursorValue(cursor, "base_entity_id"), getCursorValue(cursor, "dob"));

            List<Pair<String, String>> values = readData(sql, dataMap);
            if (values == null || values.size() == 0)
                return new ArrayList<>();

            return values;
        }
    }
}
