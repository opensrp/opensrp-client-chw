package org.smartregister.chw.presenter;

public abstract class DefaultHomeVisitGrowthNutritionPresenterFlv implements HomeVisitGrowthNutritionPresenter.Flavor {
    @Override
    public boolean hasMNP() {
        return true;
    }
}
