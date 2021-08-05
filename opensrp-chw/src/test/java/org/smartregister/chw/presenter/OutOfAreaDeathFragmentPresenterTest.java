package org.smartregister.chw.presenter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.core.contract.CoreChildRegisterFragmentContract;
import org.smartregister.chw.model.CoreOutOfAreaDeathFragmentModel;

public class OutOfAreaDeathFragmentPresenterTest {

    private OutOfAreaDeathFragmentPresenter presenter;

    @Mock
    private CoreChildRegisterFragmentContract.View view;

    @Mock
    private CoreOutOfAreaDeathFragmentModel model;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        presenter = new OutOfAreaDeathFragmentPresenter(view, model, "");
    }

    @Test
    public void testMainConditionWithTableName() {
        String tableName = "table_a";
        if (ChwApplication.getApplicationFlavor().dueVaccinesFilterInChildRegister()) {
            Assert.assertEquals(" table_a.date_removed is null AND  (( ifnull(ec_child.table_a.entry_point,'') <> 'PNC' ) or (ifnull(ec_child.table_a.entry_point,'') = 'PNC' and ( date(ec_child.table_a.dob, '+28 days') <= date() and ((SELECT is_closed FROM ec_family_member WHERE base_entity_id = ec_child.table_a.mother_entity_id ) = 0)))  or (ifnull(ec_child.entry_point,'') = 'PNC'  and (SELECT is_closed FROM ec_family_member WHERE base_entity_id = ec_child.mother_entity_id ) = 1))  and CASE WHEN ec_child.gender = 'Male' THEN (((( julianday('now') - julianday(ec_child.dob))/365.25) < +5) and ((SELECT alerts.expiryDate FROM alerts WHERE alerts.caseID = ec_child.base_entity_id and alerts.status in ('normal','urgent')) > date()))  WHEN ec_child.gender = 'Female' THEN (((( julianday('now') - julianday(ec_child.dob))/365.25) < +5) and ((SELECT alerts.expiryDate FROM alerts WHERE alerts.caseID = ec_child.base_entity_id and alerts.status in ('normal','urgent')) > date())) OR (SELECT ( ((julianday('now') - julianday(ec_child.dob))/365.25) BETWEEN 9 AND 11) AND ((SELECT alerts.expiryDate FROM alerts WHERE alerts.caseID = ec_child.base_entity_id and alerts.status in ('normal','urgent') and alerts.scheduleName = 'HPV') > date())) END",
                    presenter.getMainCondition(tableName));
        } else {
            Assert.assertEquals(" table_a.date_removed is null AND  ((( julianday('now') - julianday(ec_child.table_a.dob))/365.25) <5)   and (( ifnull(ec_child.table_a.entry_point,'') <> 'PNC' ) or (ifnull(ec_child.table_a.entry_point,'') = 'PNC' and ( date(ec_child.table_a.dob, '+28 days') <= date() and ((SELECT is_closed FROM ec_family_member WHERE base_entity_id = ec_child.table_a.mother_entity_id ) = 0)))  or (ifnull(ec_child.entry_point,'') = 'PNC'  and (SELECT is_closed FROM ec_family_member WHERE base_entity_id = ec_child.mother_entity_id ) = 1))  and ((( julianday('now') - julianday(ec_child.table_a.dob))/365.25) < 5) ",
                    presenter.getMainCondition(tableName));
        }
    }

    @Test
    public void testDefaultSortQuery() {
        Assert.assertEquals(" MAX(ec_out_of_area_death.last_interacted_with , 0 DESC ", presenter.getDefaultSortQuery());
    }

}
