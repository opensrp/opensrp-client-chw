package org.smartregister.brac.hnpp.interactor;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.interactors.JsonFormInteractor;

import org.smartregister.brac.hnpp.widget.HnppSpinnerFactory;

public class HnppJsonFormInteractor extends JsonFormInteractor {

    private static final JsonFormInteractor INSTANCE = new HnppJsonFormInteractor();
    private HnppJsonFormInteractor(){
        super();
    }

    @Override
    protected void registerWidgets() {
        super.registerWidgets();
        map.put(JsonFormConstants.SPINNER, new HnppSpinnerFactory());
    }

    public static JsonFormInteractor getInstance() {
        return INSTANCE;
    }
}
