package org.smartregister.brac.hnpp.widget;

import android.content.Context;
import android.widget.TextView;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.vijay.jsonwizard.customviews.DatePickerDialog;
import com.vijay.jsonwizard.widgets.DatePickerFactory;

import org.json.JSONException;
import org.json.JSONObject;


public class HnppDatePickerFactory extends DatePickerFactory {
    @Override
    protected DatePickerDialog createDateDialog(Context context, TextView duration, MaterialEditText editText, JSONObject jsonObject) throws JSONException {
        DatePickerDialog datePickerDialog = super.createDateDialog(context, duration, editText, jsonObject);
        datePickerDialog.setYmdOrder(new char[]{'y', 'm', 'd'} );
        return datePickerDialog;
    }
}
