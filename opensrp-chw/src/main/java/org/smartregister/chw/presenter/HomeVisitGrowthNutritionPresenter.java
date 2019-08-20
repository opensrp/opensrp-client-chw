package org.smartregister.chw.presenter;

import org.smartregister.chw.core.contract.HomeVisitGrowthNutritionContract;
import org.smartregister.chw.core.presenter.CoreHomeVisitGrowthNutritionPresenter;

public class HomeVisitGrowthNutritionPresenter extends CoreHomeVisitGrowthNutritionPresenter {
    public HomeVisitGrowthNutritionPresenter(HomeVisitGrowthNutritionContract.View view) {
        super(view);
        CoreHomeVisitGrowthNutritionPresenter.setHomeVisitGrowthNutritionPresenterFlv(new HomeVisitGrowthNutritionPresenterFlv());
    }
}
