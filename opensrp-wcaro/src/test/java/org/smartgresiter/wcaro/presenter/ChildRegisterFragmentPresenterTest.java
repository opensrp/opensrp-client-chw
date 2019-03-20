package org.smartgresiter.wcaro.presenter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartgresiter.wcaro.BaseUnitTest;
import org.smartgresiter.wcaro.contract.ChildRegisterFragmentContract;
import org.smartgresiter.wcaro.model.ChildRegisterFragmentModel;

public class ChildRegisterFragmentPresenterTest extends BaseUnitTest {

    private ChildRegisterFragmentPresenter presenter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        presenter = new ChildRegisterFragmentPresenter(Mockito.any(ChildRegisterFragmentContract.View.class)
                ,new ChildRegisterFragmentModel(),"");
    }
    @Test
    public void getMainConditionTrue(){
        Assert.assertEquals(" date_removed is null AND  (( strftime('%Y','now') - strftime('%Y',dob))<5)",presenter.getMainCondition());

    }
}
