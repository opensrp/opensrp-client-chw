package org.smartregister.chw.custom_view;

public class NavigationMenuFlv extends DefaultNavigationMenuFlv {
    @Override
    public boolean hasCommunityResponders() {
        return false;
    }

    @Override
    public String childNavigationMenuCountString() {
        return "select count(*) " +
                "from ( " +
                " select ec_child.base_entity_id , ec_child.gender , (julianday('now') - julianday(ec_child.dob))/365.25 child_age " +
                " from ec_child " +
                " left join ec_family_member on ec_family_member.base_entity_id = ec_child.base_entity_id " +
                " where ec_child.date_removed is null and ec_child.is_closed = 0  " +
                " and ec_child.base_entity_id not in (select ec_family_member.base_entity_id from ec_family_member where ec_family_member.date_removed is not null or ec_family_member.is_closed = 1) " +
                " and ec_family_member.relational_id not in (select ec_family.base_entity_id from ec_family where ec_family.date_removed is not null or ec_family.is_closed = 1 ) " +
                ") x  " +
                "where child_age < 2 or (child_age = 'Female' and child_age BETWEEN 9 AND 11)";
    }
}
