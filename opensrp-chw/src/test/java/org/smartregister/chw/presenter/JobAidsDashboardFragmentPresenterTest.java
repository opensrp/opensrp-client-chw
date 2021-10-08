package org.smartregister.chw.presenter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.Context;
import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.reporting.ReportingLibrary;
import org.smartregister.reporting.contract.ReportContract;
import org.smartregister.reporting.domain.IndicatorQuery;
import org.smartregister.reporting.domain.ReportIndicator;
import org.smartregister.repository.Repository;

import java.util.ArrayList;
import java.util.List;

public class JobAidsDashboardFragmentPresenterTest {

    private JobAidsDashboardFragmentPresenter presenter;

    @Mock
    ReportContract.View view;

    @Mock
    ReportContract.Model model;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ReportingLibrary.init(Mockito.mock(Context.class), Mockito.mock(Repository.class),
                Mockito.mock(CommonFtsObject.class), 1, 1);
        presenter = new JobAidsDashboardFragmentPresenter(view);
        ReflectionHelpers.setField(presenter, "model", model);
    }

    @Test
    public void fetchDailyTalliesCallsModelGetDailyTallies() {
        presenter.fetchIndicatorsDailytallies();
        Mockito.verify(model).getIndicatorsDailyTallies();
    }

    @Test
    public void canAddAListOfIndicators() {
        List<ReportIndicator> indicators = new ArrayList<>();
        indicators.add(Mockito.mock(ReportIndicator.class));
        presenter.addIndicators(indicators);
        Mockito.verify(model, Mockito.atLeast(1)).addIndicator(Mockito.any(ReportIndicator.class));
    }

    @Test
    public void canAddIndicatorQueries() {
        List<IndicatorQuery> indicatorQueries = new ArrayList<>();
        indicatorQueries.add(Mockito.mock(IndicatorQuery.class));
        presenter.addIndicatorQueries(indicatorQueries);
        Mockito.verify(model, Mockito.atLeast(1)).addIndicatorQuery(Mockito.any(IndicatorQuery.class));
    }

    @Test
    public void canGetView() {
        Assert.assertTrue(presenter.getView() instanceof ReportContract.View);
    }
}
