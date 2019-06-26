package org.smartregister.chw.presenter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.smartregister.chw.BaseUnitTest;
import org.smartregister.chw.contract.ChildRegisterFragmentContract;
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
        Assert.assertEquals(" date_removed is null AND  ((( julianday('now') - julianday(dob))/365.25) <5)", presenter.getMainCondition());

    }

}
