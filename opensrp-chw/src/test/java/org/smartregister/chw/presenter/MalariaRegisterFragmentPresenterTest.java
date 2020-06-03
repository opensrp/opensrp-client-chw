package org.smartregister.chw.presenter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.smartregister.chw.BaseUnitTest;
import org.smartregister.chw.malaria.contract.MalariaRegisterFragmentContract;

public class MalariaRegisterFragmentPresenterTest extends BaseUnitTest {

    private MalariaRegisterFragmentPresenter presenter;

    @Mock
    private MalariaRegisterFragmentContract.View view;

    @Mock
    private MalariaRegisterFragmentContract.Model model;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        presenter = new MalariaRegisterFragmentPresenter(view, model, "");
    }

    @Test
    public void testMainTable() {
        String table_name = "ec_malaria_confirmation";
        Assert.assertEquals(table_name, presenter.getMainTable());
    }

    @Test
    public void testMainCondition() {
        Assert.assertEquals(" ec_family_member.date_removed is null AND ec_malaria_confirmation.malaria  = 1 AND datetime('NOW') <= datetime(ec_malaria_confirmation.last_interacted_with/1000, 'unixepoch', 'localtime','+15 days') AND ec_malaria_confirmation.is_closed = 0",
                presenter.getMainCondition());

    }

}
