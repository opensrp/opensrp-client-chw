package org.smartregister.chw.presenter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.smartregister.chw.BaseUnitTest;
import org.smartregister.family.contract.FamilyProfileActivityContract;

/**
 * Created by keyman on 11/03/2019.
 */
public class FamilyProfileActivityPresenterTest extends BaseUnitTest {

    @Mock
    private FamilyProfileActivityContract.View view;

    @Mock
    private FamilyProfileActivityContract.Model model;

    private FamilyProfileActivityContract.Presenter presenter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        String viewConfigurationIdentifier = "viewConfigurationIdentifier";
        String familyBaseEntityId = "familyBaseEntityId";


        presenter = new FamilyProfileActivityPresenter(view, model, viewConfigurationIdentifier, familyBaseEntityId);
    }

    @Test
    public void testMainCondition() {
        FamilyProfileActivityPresenter familyProfileActivityPresenter = (FamilyProfileActivityPresenter) presenter;
        Assert.assertEquals(" ec_child_activity.relational_id = 'familyBaseEntityId' and ec_child_activity.date_removed is null and ( ec_child_activity.visit_not_done is null OR ec_child_activity.visit_not_done != '0') ", familyProfileActivityPresenter.getMainCondition());
    }

    @Test
    public void testDefaultSort() {
        FamilyProfileActivityPresenter familyProfileActivityPresenter = (FamilyProfileActivityPresenter) presenter;
        Assert.assertEquals("ec_child_activity.event_date DESC", familyProfileActivityPresenter.getDefaultSortQuery());
    }

}
