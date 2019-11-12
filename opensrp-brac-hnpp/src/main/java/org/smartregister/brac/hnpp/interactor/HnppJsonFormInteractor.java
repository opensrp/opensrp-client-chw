package org.smartregister.brac.hnpp.interactor;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.interactors.JsonFormInteractor;

import org.smartregister.brac.hnpp.widget.HnppDatePickerFactory;
import org.smartregister.brac.hnpp.widget.HnppFingerPrintFactory;
import org.smartregister.brac.hnpp.widget.HnppSectionFactory;
import org.smartregister.brac.hnpp.widget.HnppSpinnerFactory;

public class HnppJsonFormInteractor extends JsonFormInteractor {

    private static final JsonFormInteractor INSTANCE = new HnppJsonFormInteractor();
    private HnppJsonFormInteractor(){
        super();
    }

    @Override
    protected void registerWidgets() {
        super.registerWidgets();
        map.put(JsonFormConstants.DATE_PICKER, new HnppDatePickerFactory());
        map.put(JsonFormConstants.SECTION_LABEL, new HnppSectionFactory());
        map.put(JsonFormConstants.SPINNER, new HnppSpinnerFactory());
        map.put(JsonFormConstants.FINGER_PRINT,new HnppFingerPrintFactory());
    }

    public static JsonFormInteractor getInstance() {
        return INSTANCE;
    }
}
