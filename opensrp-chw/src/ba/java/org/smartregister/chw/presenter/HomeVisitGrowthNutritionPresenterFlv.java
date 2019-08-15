package org.smartregister.chw.presenter;

import org.smartregister.chw.core.presenter.CoreHomeVisitGrowthNutritionPresenter;

public class HomeVisitGrowthNutritionPresenterFlv implements CoreHomeVisitGrowthNutritionPresenter.Flavor {
    @Override
    public boolean hasMNP() {
        return false;
    }
}
