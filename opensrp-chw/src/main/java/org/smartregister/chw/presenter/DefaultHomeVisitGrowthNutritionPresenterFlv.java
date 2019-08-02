package org.smartregister.chw.presenter;

import com.opensrp.chw.core.presenter.CoreHomeVisitGrowthNutritionPresenter;

public abstract class DefaultHomeVisitGrowthNutritionPresenterFlv implements HomeVisitGrowthNutritionPresenter.Flavor {
    @Override
    public boolean hasMNP() {
        return true;
    }
}
