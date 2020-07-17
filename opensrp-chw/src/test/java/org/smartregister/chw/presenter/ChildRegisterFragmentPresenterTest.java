package org.smartregister.chw.presenter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.smartregister.chw.BaseUnitTest;
import org.smartregister.chw.core.contract.CoreChildRegisterFragmentContract;

public class ChildRegisterFragmentPresenterTest extends BaseUnitTest {

    private ChildRegisterFragmentPresenter presenter;

    @Mock
    private CoreChildRegisterFragmentContract.View view;

    @Mock
    private CoreChildRegisterFragmentContract.Model model;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        presenter = new ChildRegisterFragmentPresenter(view, model, "");
    }

    @Test
    public void testMainCondition() {
        String tableName = "table_a";
        Assert.assertEquals(" table_a.date_removed is null AND  ((( julianday('now') - julianday(ec_child.table_a.dob))/365.25) <5) " +
                "  and (( ifnull(ec_child.table_a.entry_point,'') <> 'PNC' ) or (ifnull(ec_child.table_a.entry_point,'') = 'PNC' " +
                "and ( date(ec_child.table_a.dob, '+28 days') <= date() and ((SELECT is_closed FROM ec_family_member " +
                "WHERE base_entity_id = ec_child.table_a.mother_entity_id ) = 0)))  or (ifnull(ec_child.entry_point,'') = 'PNC' " +
                " and (SELECT is_closed FROM ec_family_member WHERE base_entity_id = ec_child.mother_entity_id ) = 1))  " +
                "and ((( julianday('now') - julianday(ec_child.table_a.dob))/365.25) < 5) ", presenter.getMainCondition(tableName));

    }

    @Test
    public void testMainConditionWithTableName() {
        String tableName = "table_a";
        Assert.assertEquals(" table_a.date_removed is null AND  ((( julianday('now') - julianday(ec_child.table_a.dob))/365.25) <5)   " +
                "and (( ifnull(ec_child.table_a.entry_point,'') <> 'PNC' ) or (ifnull(ec_child.table_a.entry_point,'') = 'PNC' " +
                "and ( date(ec_child.table_a.dob, '+28 days') <= date() and ((SELECT is_closed FROM ec_family_member " +
                "WHERE base_entity_id = ec_child.table_a.mother_entity_id ) = 0)))  or (ifnull(ec_child.entry_point,'') = 'PNC'  " +
                "and (SELECT is_closed FROM ec_family_member WHERE base_entity_id = ec_child.mother_entity_id ) = 1))  " +
                "and ((( julianday('now') - julianday(ec_child.table_a.dob))/365.25) < 5) ", presenter.getMainCondition(tableName));

    }

    @Test
    public void testDefaultSortQuery() {
        Assert.assertEquals(" MAX(ec_child.last_interacted_with , ifnull(VISIT_SUMMARY.visit_date,0)) DESC ", presenter.getDefaultSortQuery());

    }

    @Test
    public void testDueAndFilterCondition() {
        Assert.assertEquals(" ec_child.date_removed is null AND  ((( julianday('now') - julianday(ec_child.dob))/365.25) <5)   and (( ifnull(ec_child.entry_point,'') <> 'PNC' ) or (ifnull(ec_child.entry_point,'') = 'PNC' and ( date(ec_child.dob, '+28 days') <= date() and ((SELECT is_closed FROM ec_family_member WHERE base_entity_id = ec_child.mother_entity_id ) = 0)))  or (ifnull(ec_child.entry_point,'') = 'PNC'  and (SELECT is_closed FROM ec_family_member WHERE base_entity_id = ec_child.mother_entity_id ) = 1))  and ((( julianday('now') - julianday(ec_child.dob))/365.25) < 5)  AND (( IFNULL(STRFTIME('%Y%m%d%H%M%S', datetime((last_home_visit)/1000,'unixepoch')),0) < STRFTIME('%Y%m%d%H%M%S', datetime('now','start of month')) AND IFNULL(STRFTIME('%Y%m%d%H%M%S', datetime((visit_not_done)/1000,'unixepoch')),0) < STRFTIME('%Y%m%d%H%M%S', datetime('now','start of month'))  ))", presenter.getDueFilterCondition());

    }
}
