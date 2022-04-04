package org.smartregister.chw.presenter;

import android.content.Context;
import android.util.Pair;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.Months;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.dao.VisitDao;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.VaccineScheduleUtil;
import org.smartregister.chw.core.utils.VisitVaccineUtil;
import org.smartregister.chw.dao.ChwChildDao;
import org.smartregister.chw.model.FamilyKitModel;
import org.smartregister.chw.model.WashCheckModel;
import org.smartregister.chw.util.Constants;
import org.smartregister.dao.AbstractDao;
import org.smartregister.domain.Alert;
import org.smartregister.family.contract.FamilyProfileDueContract;
import org.smartregister.family.presenter.BaseFamilyProfileDuePresenter;
import org.smartregister.immunization.db.VaccineRepo;
import org.smartregister.immunization.domain.jsonmapping.Vaccine;
import org.smartregister.immunization.domain.jsonmapping.VaccineGroup;
import org.smartregister.immunization.util.VaccinatorUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

import timber.log.Timber;

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

    private String getSelectCondition(){
        String condition = " ( ec_family_member.relational_id = '" + this.familyBaseEntityId + "' or ec_family.base_entity_id = '" + this.familyBaseEntityId + "' ) AND ";
        if(ChwApplication.getApplicationFlavor().showChildrenAboveTwoDueStatus()){
            condition += getDefaultChildDueQuery();
//                    + " EXISTS(select * from alerts where caseID = ec_family_member.base_entity_id and status in ('normal','urgent') and expiryDate > date()) AND "
        }
        else {
            condition += getChildDueQueryForChildrenUnderTwoAndGirlsAgeNineToEleven();
//                    + "EXISTS(select * from alerts where caseID = ec_family_member.base_entity_id and status in ('normal','urgent') and expiryDate > date()) AND "
        }

        return condition + (ChwApplication.getApplicationFlavor().checkExtraForDueInFamily() ? String.format(" and ec_family_member.base_entity_id in (%s)", validMembers()) : "");
    }

    String validMembers(){
        List<Pair<String, String>> familyMembers = FamilyMemberDao.getFamilyMembers(this.familyBaseEntityId);
        List<VaccineGroup> childVaccineGroups = VaccineScheduleUtil.getVaccineGroups(ChwApplication.getInstance().getApplicationContext(), CoreConstants.SERVICE_GROUPS.CHILD);
        List<Vaccine> specialVaccines = VaccinatorUtils.getSpecialVaccines(contextSupplier.get());

        StringBuilder joiner = new StringBuilder();
        for (Pair<String, String> familyMemberRepr : familyMembers) {
            MemberObject member = new MemberObject();
            member.setBaseEntityId(familyMemberRepr.first);
            member.setFamilyBaseEntityId(this.familyBaseEntityId);
            member.setDob(familyMemberRepr.second);

            boolean vaccineCardReceived = VisitDao.memberHasVaccineCard(member.getBaseEntityId());

            if (!vaccineCardReceived || pendingImmunization(member, childVaccineGroups, specialVaccines)) {
                joiner.append(String.format("'%s'", member.getBaseEntityId()));
                joiner.append(",");
            }
        }
        if(!joiner.toString().equalsIgnoreCase("")){
            joiner.deleteCharAt(joiner.length() - 1);
        }

        return joiner.toString();
    }

    int immunizationCeiling(MemberObject memberObject) {
        String gender = ChwChildDao.getChildGender(memberObject.getBaseEntityId());

        if (gender != null && gender.equalsIgnoreCase("Female")) {
            if (memberObject.getAge() >= 9 && memberObject.getAge() <= 11) {
                return 132;
            } else {
                return 60;
            }
        }

        return 60;
    }

    boolean pendingImmunization(MemberObject memberObject, List<VaccineGroup> vaccineGroups, List<Vaccine> specialVaccines){
        Date dob = null;
        try {
            dob = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(memberObject.getDob());
        } catch (ParseException e) {
            Timber.e(e);
        }

        int ageInMonths = Months.monthsBetween(new LocalDate(dob), new LocalDate()).getMonths();
        if (ageInMonths >= immunizationCeiling(memberObject)) return false;

        List<org.smartregister.immunization.domain.Vaccine> vaccines = CoreChwApplication.getInstance().vaccineRepository()
                .findByEntityId(memberObject.getBaseEntityId());

        String vaccineCategory = memberObject.getAge() > 5 ? Constants.CHILD_OVER_5 : CoreConstants.SERVICE_GROUPS.CHILD;
        List<VaccineRepo.Vaccine> allVacs = VaccineRepo.getVaccines(vaccineCategory);

        Map<String, VaccineRepo.Vaccine> vaccinesRepo = new HashMap<>();
        for (VaccineRepo.Vaccine vaccine : allVacs) {
            vaccinesRepo.put(vaccine.display().toLowerCase().replace(" ", ""), vaccine);
        }

        Map<VaccineGroup, List<Pair<VaccineRepo.Vaccine, Alert>>> pendingVaccines = VisitVaccineUtil.generateVisitVaccines(
                memberObject.getBaseEntityId(),
                vaccinesRepo,
                new DateTime(dob),
                vaccineGroups,
                specialVaccines,
                vaccines,
                null
        );

        return !pendingVaccines.isEmpty();
    }

    public boolean saveData(String jsonObject) {
        return washCheckModel.saveWashCheckEvent(jsonObject);
    }

    public boolean saveDataFamilyKit(String jsonObject) {
        return familyKitModel.saveFamilyKitEvent(jsonObject);
    }

    static class FamilyMemberDao extends AbstractDao{
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
