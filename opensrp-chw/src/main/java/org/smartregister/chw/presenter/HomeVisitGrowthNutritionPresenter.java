package org.smartregister.chw.presenter;

import com.opensrp.chw.core.contract.HomeVisitGrowthNutritionContract;
import com.opensrp.chw.core.presenter.CoreHomeVisitGrowthNutritionPresenter;

public class HomeVisitGrowthNutritionPresenter extends CoreHomeVisitGrowthNutritionPresenter {
    public HomeVisitGrowthNutritionPresenter(HomeVisitGrowthNutritionContract.View view) {
        super(view);
        CoreHomeVisitGrowthNutritionPresenter.setHomeVisitGrowthNutritionPresenterFlv(new HomeVisitGrowthNutritionPresenterFlv());
    }
}
