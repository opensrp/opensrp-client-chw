package org.smartregister.chw.presenter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.smartregister.chw.model.FamilyProfileDueModel;
import org.smartregister.family.contract.FamilyProfileDueContract;

public class FamilyProfileDuePresenterTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    private FamilyProfileDuePresenter presenter;

    @Mock
    private FamilyProfileDueContract.View view;

    @Spy
    private FamilyProfileDueContract.Model model;

    private String familyBaseEntityId = "familyBaseEntityId";

    @Before
    public void setUp() {
        String viewConfigurationIdentifier = "viewConfigurationIdentifier";
        presenter = new FamilyProfileDuePresenter(view, model, viewConfigurationIdentifier, familyBaseEntityId);
    }

    @Test
    public void testGetMainCondition() {
        Assert.assertEquals(presenter.getPNCChildQuery(),
                "AND CASE WHEN ec_family_member.entity_type = 'ec_child' THEN ((date(ec_family_member.dob, '+28 days') <= date()) OR ((date(ec_family_member.dob, '+28 days') >= date()) AND ifnull(ec_child.entry_point,'') <> 'PNC')) ELSE true END");
      }

    @Test
    public void testgetDueQuery() {
        Assert.assertEquals(presenter.getDueQuery(),
                " (ifnull(schedule_service.completion_date,'') = '' and schedule_service.expiry_date >= strftime('%Y-%m-%d') and schedule_service.due_date <= strftime('%Y-%m-%d') and ifnull(schedule_service.not_done_date,'') = '' ) ");
    }


    @Test
    public void testMainSelectCondition() {
        String selectCondition = " ( ec_family_member.relational_id = '" + this.familyBaseEntityId + "' or ec_family.base_entity_id = '" + this.familyBaseEntityId + "' ) AND "
                + presenter.getDueQuery() + presenter.getPNCChildQuery();

        FamilyProfileDueModel familyProfileDueModel = Mockito.spy(FamilyProfileDueModel.class);
        String mainSelect = familyProfileDueModel.mainSelect("a_table",selectCondition);
        Assert.assertEquals(mainSelect,"Select a_table.id as _id , ec_child.entry_point , ec_family_member.relationalid , ec_family_member.base_entity_id AS _id , ec_family_member.last_interacted_with , ec_family_member.base_entity_id , ec_family_member.first_name , ec_family_member.middle_name , ec_family_member.last_name , ec_family_member.unique_id , ec_family_member.gender , ec_family_member.dob , ec_family_member.dod , ec_family_member.entity_type , schedule_service.schedule_name , schedule_service.due_date , schedule_service.over_due_date , schedule_service.not_done_date , schedule_service.expiry_date , schedule_service.completion_date , ec_family.first_name AS family_first_name FROM a_table LEFT JOIN ec_family_member ON  ec_family_member.base_entity_id = a_table.base_entity_id COLLATE NOCASE  LEFT JOIN ec_family ON  ec_family.base_entity_id = a_table.base_entity_id COLLATE NOCASE  LEFT JOIN ec_child ON  ec_child.base_entity_id = a_table.base_entity_id COLLATE NOCASE  WHERE  ( ec_family_member.relational_id = 'familyBaseEntityId' or ec_family.base_entity_id = 'familyBaseEntityId' ) AND  (ifnull(schedule_service.completion_date,'') = '' and schedule_service.expiry_date >= strftime('%Y-%m-%d') and schedule_service.due_date <= strftime('%Y-%m-%d') and ifnull(schedule_service.not_done_date,'') = '' ) AND CASE WHEN ec_family_member.entity_type = 'ec_child' THEN ((date(ec_family_member.dob, '+28 days') <= date()) OR ((date(ec_family_member.dob, '+28 days') >= date()) AND ifnull(ec_child.entry_point,'') <> 'PNC')) ELSE true END ");
    }
    @Test
    public void testCountSelectCondition() {
        String selectCondition = " ( ec_family_member.relational_id = '" + this.familyBaseEntityId + "' or ec_family.base_entity_id = '" + this.familyBaseEntityId + "' ) AND "
                + presenter.getDueQuery() + presenter.getPNCChildQuery();

        FamilyProfileDueModel familyProfileDueModel = Mockito.spy(FamilyProfileDueModel.class);
        String countSelect = familyProfileDueModel.countSelect("a_table",selectCondition);
        Assert.assertEquals(countSelect,"SELECT COUNT(*) FROM a_table LEFT JOIN ec_family_member ON  ec_family_member.base_entity_id = a_table.base_entity_id COLLATE NOCASE  LEFT JOIN ec_family ON  ec_family.base_entity_id = a_table.base_entity_id COLLATE NOCASE  LEFT JOIN ec_child ON  ec_child.base_entity_id = a_table.base_entity_id COLLATE NOCASE  WHERE  ( ec_family_member.relational_id = 'familyBaseEntityId' or ec_family.base_entity_id = 'familyBaseEntityId' ) AND  (ifnull(schedule_service.completion_date,'') = '' and schedule_service.expiry_date >= strftime('%Y-%m-%d') and schedule_service.due_date <= strftime('%Y-%m-%d') and ifnull(schedule_service.not_done_date,'') = '' ) AND CASE WHEN ec_family_member.entity_type = 'ec_child' THEN ((date(ec_family_member.dob, '+28 days') <= date()) OR ((date(ec_family_member.dob, '+28 days') >= date()) AND ifnull(ec_child.entry_point,'') <> 'PNC')) ELSE true END ");
    }


}
