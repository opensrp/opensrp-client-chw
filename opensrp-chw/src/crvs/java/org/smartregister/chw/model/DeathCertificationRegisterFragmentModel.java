package org.smartregister.chw.model;

import static org.smartregister.AllConstants.CLIENT_TYPE;
import static org.smartregister.chw.anc.util.Constants.TABLES.EC_CHILD;
import static org.smartregister.chw.anc.util.Constants.TABLES.PREGNANCY_OUTCOME;
import static org.smartregister.chw.core.utils.CoreConstants.TABLE_NAME.EC_OUT_OF_AREA_DEATH;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.chw.core.model.CoreCertificationRegisterFragmentModel;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;

import java.text.MessageFormat;

public class DeathCertificationRegisterFragmentModel extends CoreCertificationRegisterFragmentModel {
    @Override
    public String countSelect(String tableName, String mainCondition, String familyMemberTableName) {
        return mainSelect(tableName, mainCondition, familyMemberTableName, mainCondition);
    }

    @Override
    public String mainSelect(String tableName, String familyTableName, String familyMemberTableName, String mainCondition) {
        SmartRegisterQueryBuilder removedFamilyMembersBuilder = new SmartRegisterQueryBuilder(getRemovedFamilyMembersQueryString());
        SmartRegisterQueryBuilder removedChildrenBuilder = new SmartRegisterQueryBuilder(getRemovedEcChildrenQueryString());
        SmartRegisterQueryBuilder stillBirthsBuilder = new SmartRegisterQueryBuilder(getStillBirthsPregnancyOutcomeQueryString());
        SmartRegisterQueryBuilder outOfAreaBuilder = new SmartRegisterQueryBuilder(getOutOfAreaDeathsQueryString());

        removedFamilyMembersBuilder.mainCondition(mainCondition);
        removedFamilyMembersBuilder.customJoin("UNION " + removedChildrenBuilder.mainCondition(mainCondition));
        removedFamilyMembersBuilder.customJoin("UNION " + stillBirthsBuilder.mainCondition(mainCondition));
        removedFamilyMembersBuilder.customJoin("UNION " + outOfAreaBuilder.mainCondition(mainCondition));


        return removedFamilyMembersBuilder.toString();
    }

    public String getRemovedFamilyMembersQueryString() {
        return "Select ec_family_member.id as _id , ec_family_member.relational_id as relationalid , ec_family_member.last_interacted_with , ec_family_member.base_entity_id , " +
                "'' as first_name , '' as middle_name , ec_family_member.first_name as family_first_name , ec_family_member.last_name as family_last_name , " +
                "ec_family_member.middle_name as family_middle_name , ec_family_member.phone_number as family_member_phone_number , ec_family_member.other_phone_number as " +
                "family_member_phone_number_other , ec_family.village_town as family_home_address ,'' as last_name, ec_family_member.unique_id, ec_family_member.gender, ec_family_member.dob, " +
                "ec_family_member.dob_unknown, '' as last_home_visit, '' as visit_not_done, '' as early_bf_1hr, '' as physically_challenged, '' as birth_cert, '' as birth_cert_issue_date, " +
                "'' as birth_cert_num, '' as birth_notification, '' as date_of_illness, '' as illness_description, '' as date_created, '' as action_taken, '' as vaccine_card, " +
                "'' as preg_outcome, ec_family_member.received_death_certificate, ec_family_member.death_certificate_issue_date, ec_family_member.death_notification_done, " +
                "ec_family_member.death_certificate_number, ec_family_member.official_id, ec_family_member.official_name, ec_family_member.official_position, ec_family_member.official_address, " +
                "ec_family_member.official_number, ec_family_member.informant_name, ec_family_member.informant_relationship, ec_family_member.informant_address, ec_family_member.informant_phone, " +
                "'" + CoreConstants.TABLE_NAME.FAMILY_MEMBER + "' as '" + CLIENT_TYPE + "' from ec_family_member LEFT JOIN ec_family ON ec_family_member.base_entity_id = ec_family.primary_caregiver COLLATE NOCASE WHERE ec_family_member.is_closed = 1 AND ec_family_member.dod IS NOT NULL ";
    }

    public String getRemovedEcChildrenQueryString() {
        return "Select ec_child.id as _id , ec_child.relational_id as relationalid , ec_child.last_interacted_with , ec_child.base_entity_id , ec_child.first_name , ec_child.middle_name , " +
                "ec_family_member.first_name as family_first_name , ec_family_member.last_name as family_last_name , ec_family_member.middle_name as family_middle_name , ec_family_member.phone_number as family_member_phone_number , " +
                "ec_family_member.other_phone_number as family_member_phone_number_other , ec_family.village_town as family_home_address , ec_child.last_name , ec_child.unique_id , ec_child.gender , " +
                "ec_child.dob , ec_child.dob_unknown , ec_child.last_home_visit , ec_child.visit_not_done , ec_child.early_bf_1hr , ec_child.physically_challenged , ec_child.birth_cert , ec_child.birth_cert_issue_date , " +
                "ec_child.birth_cert_num , ec_child.birth_notification , ec_child.date_of_illness , ec_child.illness_description , ec_child.date_created , ec_child.action_taken , ec_child.vaccine_card, '' as preg_outcome, " +
                "ec_child.received_death_certificate, ec_child.death_certificate_issue_date, ec_child.death_notification_done, ec_child.death_certificate_number, ec_child.official_id , ec_child.official_name, ec_child.official_position, " +
                "ec_child.official_address, ec_child.official_number, ec_child.informant_name, ec_child.informant_relationship, ec_child.informant_address, ec_child.informant_phone, '" + EC_CHILD + "' as '" + CLIENT_TYPE + "' " +
                "FROM ec_child LEFT JOIN ec_family ON  ec_child.relational_id = ec_family.id COLLATE NOCASE LEFT JOIN ec_family_member ON  ec_family_member.base_entity_id = ec_family.primary_caregiver COLLATE NOCASE  " +
                "LEFT JOIN (select base_entity_id , max(visit_date) visit_date from visits GROUP by base_entity_id) VISIT_SUMMARY ON VISIT_SUMMARY.base_entity_id = ec_child.base_entity_id WHERE  ec_child.is_closed is 1 and ec_child.dod IS NOT NULL";
    }


    public String getStillBirthsPregnancyOutcomeQueryString() {
        return "Select ec_pregnancy_outcome.id as _id, ec_pregnancy_outcome.relational_id as relationalid, ec_pregnancy_outcome.last_interacted_with, ec_pregnancy_outcome.base_entity_id, '' as first_name, '' as middle_name, " +
                "ec_family_member.first_name as family_first_name , ec_family_member.last_name as family_last_name , ec_family_member.middle_name as family_middle_name , ec_family_member.phone_number as family_member_phone_number , " +
                "ec_family_member.other_phone_number as family_member_phone_number_other , ec_family.village_town as family_home_address ,'' as last_name, ec_family_member.unique_id, ec_family_member.gender, ec_family_member.dob, " +
                "ec_family_member.dob_unknown, '' as last_home_visit, '' as visit_not_done, '' as early_bf_1hr, '' as physically_challenged, '' as birth_cert, '' as birth_cert_issue_date, '' as birth_cert_num, " +
                "'' as birth_notification, '' as date_of_illness, '' as illness_description, '' as date_created, '' as action_taken, '' as vaccine_card, ec_pregnancy_outcome.preg_outcome, ec_family_member.received_death_certificate, " +
                "ec_family_member.death_certificate_issue_date, ec_family_member.death_notification_done, ec_family_member.death_certificate_number, ec_family_member.official_id , ec_family_member.official_name, " +
                "ec_family_member.official_position, ec_family_member.official_address, ec_family_member.official_number, ec_family_member.informant_name, ec_family_member.informant_relationship, ec_family_member.informant_address, " +
                "ec_family_member.informant_phone, '" + PREGNANCY_OUTCOME + "' as '" + CLIENT_TYPE + "' from ec_pregnancy_outcome LEFT JOIN ec_family_member ON ec_pregnancy_outcome.base_entity_id = ec_family_member.base_entity_id LEFT JOIN ec_family ON " +
                "ec_pregnancy_outcome.relational_id = ec_family.id COLLATE NOCASE WHERE ec_pregnancy_outcome.preg_outcome = 'Stillbirth'";
    }


    private String getOutOfAreaDeathsQueryString() {
        return "Select ec_out_of_area_death.id as _id , ec_out_of_area_death.relationalid as relationalid , ec_out_of_area_death.last_interacted_with , ec_out_of_area_death.base_entity_id , ec_out_of_area_death.name as first_name , " +
                "'' as middle_name , '' as family_first_name , '' as family_last_name , '' as family_middle_name , ec_out_of_area_death.official_number as family_member_phone_number , '' as family_member_phone_number_other , " +
                "ec_out_of_area_death.death_place as family_home_address , '' as last_name, ec_out_of_area_death.unique_id, ec_out_of_area_death.sex as gender, ec_out_of_area_death.dob, ec_out_of_area_death.dob_unknown, '' as last_home_visit, " +
                "'' as visit_not_done, '' as early_bf_1hr, '' as physically_challenged, '' as birth_cert, '' as birth_cert_issue_date, '' as birth_cert_num, '' as birth_notification, '' as date_of_illness, '' as illness_description, " +
                "ec_out_of_area_death.date_created, '' as action_taken, '' as vaccine_card, '' as preg_outcome, ec_out_of_area_death.received_death_certificate, ec_out_of_area_death.death_certificate_issue_date, ec_out_of_area_death.death_notification_done, " +
                "ec_out_of_area_death.death_certificate_number, ec_out_of_area_death.official_id , ec_out_of_area_death.official_name, ec_out_of_area_death.official_position, ec_out_of_area_death.official_address, ec_out_of_area_death.official_number, " +
                "ec_out_of_area_death.informant_name, ec_out_of_area_death.informant_relationship, ec_out_of_area_death.informant_address, ec_out_of_area_death.informant_phone, '" + EC_OUT_OF_AREA_DEATH + "' as '" + CLIENT_TYPE + "' from ec_out_of_area_death";
    }

    public String getCustomSelectString(String condition, String filters, String SortQueries, boolean isDueActive) {

        SmartRegisterQueryBuilder removedFamilyMembersBuilder = new SmartRegisterQueryBuilder(getRemovedFamilyMembersQueryString());
        removedFamilyMembersBuilder.mainCondition(condition);

        SmartRegisterQueryBuilder removedChildrenBuilder = new SmartRegisterQueryBuilder(getRemovedEcChildrenQueryString());
        removedChildrenBuilder.mainCondition(condition);

        SmartRegisterQueryBuilder stillBirthsBuilder = new SmartRegisterQueryBuilder(getStillBirthsPregnancyOutcomeQueryString());
        stillBirthsBuilder.mainCondition(condition);

        SmartRegisterQueryBuilder outOfAreaBuilder = new SmartRegisterQueryBuilder(getOutOfAreaDeathsQueryString());
        outOfAreaBuilder.mainCondition(condition);

        if (StringUtils.isNotBlank(filters)) {
            removedFamilyMembersBuilder.addCondition(getFamilyMemberFilterString(filters));
            removedChildrenBuilder.addCondition(getRemovedChildFilterString(filters));
            stillBirthsBuilder.addCondition(getFamilyMemberFilterString(filters));
            outOfAreaBuilder.mainCondition(getOutAreaFilterString(filters));
        }

        if (isDueActive) {
            removedFamilyMembersBuilder.addCondition(" and " + CoreConstants.TABLE_NAME.FAMILY_MEMBER + "." + getDueCondition());
            removedChildrenBuilder.addCondition(" and " + EC_CHILD + "." + getDueCondition());
            stillBirthsBuilder.addCondition(" and " + CoreConstants.TABLE_NAME.FAMILY_MEMBER + "." + getDueCondition());
            if (StringUtils.isBlank(filters)) {
                outOfAreaBuilder.mainCondition(EC_OUT_OF_AREA_DEATH + ".received_death_certificate = 'Yes'");
            } else {
                outOfAreaBuilder.addCondition(" and " + EC_OUT_OF_AREA_DEATH + "." + getDueCondition());
            }
        }
        removedFamilyMembersBuilder.customJoin("UNION " + removedChildrenBuilder.toString());
        removedFamilyMembersBuilder.customJoin("UNION " + stillBirthsBuilder.toString());
        removedFamilyMembersBuilder.customJoin("UNION " + outOfAreaBuilder.orderbyCondition(SortQueries));

        return removedFamilyMembersBuilder.orderbyCondition(SortQueries);
    }

    public String getDueCondition() {
        return "received_death_certificate = 'Yes'";
    }

    public String getFamilyMemberFilterString(String filters) {
        StringBuilder customFilter = new StringBuilder();
        if (StringUtils.isNotBlank(filters)) {
            customFilter.append(" and ( ");
            customFilter.append(MessageFormat.format(" {0}.{1} like ''%{2}%'' ", CoreConstants.TABLE_NAME.FAMILY_MEMBER, org.smartregister.chw.anc.util.DBConstants.KEY.FIRST_NAME, filters));
            customFilter.append(MessageFormat.format(" or {0}.{1} like ''%{2}%'' ", CoreConstants.TABLE_NAME.FAMILY_MEMBER, org.smartregister.chw.anc.util.DBConstants.KEY.LAST_NAME, filters));
            customFilter.append(MessageFormat.format(" or {0}.{1} like ''%{2}%'' ", CoreConstants.TABLE_NAME.FAMILY_MEMBER, org.smartregister.chw.anc.util.DBConstants.KEY.MIDDLE_NAME, filters));
            customFilter.append(MessageFormat.format(" or {0}.{1} like ''%{2}%'' ", CoreConstants.TABLE_NAME.FAMILY_MEMBER, org.smartregister.chw.anc.util.DBConstants.KEY.UNIQUE_ID, filters));

            customFilter.append(" ) ");
        }

        return customFilter.toString();
    }

    public String getRemovedChildFilterString(String filters) {
        StringBuilder customFilter = new StringBuilder();
        if (StringUtils.isNotBlank(filters)) {
            customFilter.append(" and ( ");
            customFilter.append(MessageFormat.format(" {0}.{1} like ''%{2}%'' ", CoreConstants.TABLE_NAME.CHILD, org.smartregister.chw.anc.util.DBConstants.KEY.FIRST_NAME, filters));
            customFilter.append(MessageFormat.format(" or {0}.{1} like ''%{2}%'' ", CoreConstants.TABLE_NAME.CHILD, org.smartregister.chw.anc.util.DBConstants.KEY.LAST_NAME, filters));
            customFilter.append(MessageFormat.format(" or {0}.{1} like ''%{2}%'' ", CoreConstants.TABLE_NAME.CHILD, org.smartregister.chw.anc.util.DBConstants.KEY.MIDDLE_NAME, filters));
            customFilter.append(MessageFormat.format(" or {0}.{1} like ''%{2}%'' ", CoreConstants.TABLE_NAME.CHILD, org.smartregister.chw.anc.util.DBConstants.KEY.UNIQUE_ID, filters));

            customFilter.append(" ) ");
        }

        return customFilter.toString();
    }

    public String getOutAreaFilterString(String filters) {
        StringBuilder customFilter = new StringBuilder();
        if (StringUtils.isNotBlank(filters)) {
            customFilter.append(" ( ");
            customFilter.append(MessageFormat.format(" {0}.{1} like ''%{2}%'' ", CoreConstants.TABLE_NAME.EC_OUT_OF_AREA_DEATH, CoreConstants.DB_CONSTANTS.NAME, filters));
            customFilter.append(MessageFormat.format(" or {0}.{1} like ''%{2}%'' ", CoreConstants.TABLE_NAME.EC_OUT_OF_AREA_DEATH, org.smartregister.chw.anc.util.DBConstants.KEY.UNIQUE_ID, filters));

            customFilter.append(" ) ");
        }

        return customFilter.toString();
    }
}
