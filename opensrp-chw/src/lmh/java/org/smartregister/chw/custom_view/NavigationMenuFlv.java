package org.smartregister.chw.custom_view;

public class NavigationMenuFlv extends DefaultNavigationMenuFlv {
    @Override
    public boolean hasCommunityResponders() {
        return false;
    }

    @Override
    public String childNavigationMenuCountString() {
        return "Select count(*)\n" +
                " FROM ec_child\n" +
                " LEFT JOIN ec_family ON  ec_child.relational_id = ec_family.id COLLATE NOCASE  \n" +
                " LEFT JOIN ec_family_member ON  ec_family_member.base_entity_id = ec_family.primary_caregiver COLLATE NOCASE  \n" +
                " LEFT JOIN (select base_entity_id , max(visit_date) visit_date from visits GROUP by base_entity_id) VISIT_SUMMARY ON VISIT_SUMMARY.base_entity_id = ec_child.base_entity_id \n" +
                " WHERE  ec_child.date_removed is null\n" +
                " AND  (( ifnull(ec_child.entry_point,'') <> 'PNC' )  or (ifnull(ec_child.entry_point,'') = 'PNC' and ( date(ec_child.dob, '+28 days') <= date() \n" +
                " and ((SELECT is_closed FROM ec_family_member WHERE base_entity_id = ec_child.mother_entity_id ) = 0))) \n" +
                " or (ifnull(ec_child.entry_point,'') = 'PNC'  \n" +
                " and (SELECT is_closed FROM ec_family_member WHERE base_entity_id = ec_child.mother_entity_id ) = 1)) \n" +
                " and CASE WHEN ec_child.gender = 'Male' \n" +
                " THEN (\n" +
                " ((( julianday('now') - julianday(ec_child.dob))/365.25) < +1.8) OR ( (((( julianday('now') - julianday(ec_child.dob))/365.25) >= +1.8) AND ((( julianday('now') - julianday(ec_child.dob))/365.25) <= +2)) AND\n" +
                "  ((SELECT count(*) FROM alerts WHERE alerts.caseID = ec_child.base_entity_id and alerts.status in ('normal','urgent')) > 0))\n" +
                "  )\n" +
                "  WHEN ec_child.gender = 'Female' \n" +
                " THEN (\n" +
                " ((( julianday('now') - julianday(ec_child.dob))/365.25) < +1.8) OR ( (((( julianday('now') - julianday(ec_child.dob))/365.25) >= +1.8) AND ((( julianday('now') - julianday(ec_child.dob))/365.25) <= +2)) AND\n" +
                "  ((SELECT count(*) FROM alerts WHERE alerts.caseID = ec_child.base_entity_id and alerts.status in ('normal','urgent')) > 0))  OR (((julianday('now') - julianday(ec_child.dob))/365.25) BETWEEN 9 AND 11)\n" +
                "  ) END";
    }

}
