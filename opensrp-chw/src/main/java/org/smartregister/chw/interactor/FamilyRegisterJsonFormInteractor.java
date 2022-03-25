package org.smartregister.chw.interactor;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.interactors.JsonFormInteractor;

import org.smartregister.chw.factory.FamilyRegisterSpinnerFactory;

public class FamilyRegisterJsonFormInteractor extends JsonFormInteractor {
    private static final FamilyRegisterJsonFormInteractor INTERACTOR_INSTANCE = new FamilyRegisterJsonFormInteractor();

    protected FamilyRegisterJsonFormInteractor() {
        super();
    }

    public static JsonFormInteractor getChildInteractorInstance() {
        return INTERACTOR_INSTANCE;
    }

    @Override
    protected void registerWidgets() {
        super.registerWidgets();
        map.put(JsonFormConstants.SPINNER, new FamilyRegisterSpinnerFactory());
    }
}
