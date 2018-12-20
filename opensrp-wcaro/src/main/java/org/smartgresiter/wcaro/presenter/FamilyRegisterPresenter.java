package org.smartgresiter.wcaro.presenter;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONObject;
import org.smartgresiter.wcaro.interactor.ChildRegisterInteractor;
import org.smartregister.family.contract.FamilyRegisterContract;
import org.smartregister.family.interactor.FamilyRegisterInteractor;
import org.smartregister.family.presenter.BaseFamilyRegisterPresenter;

public class FamilyRegisterPresenter extends BaseFamilyRegisterPresenter {

    public FamilyRegisterPresenter(FamilyRegisterContract.View view, FamilyRegisterContract.Model model) {
        super(view, model);
    }



}
