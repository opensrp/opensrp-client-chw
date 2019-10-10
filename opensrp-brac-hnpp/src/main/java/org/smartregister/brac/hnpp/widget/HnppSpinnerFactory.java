package org.smartregister.brac.hnpp.widget;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;

import com.vijay.jsonwizard.customviews.MaterialSpinner;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.widgets.SpinnerFactory;

import org.json.JSONObject;

import java.util.List;

public class HnppSpinnerFactory extends SpinnerFactory {



    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JsonFormFragment formFragment, JSONObject jsonObject, CommonListener listener, boolean popup) throws Exception {
        List<View> views = super.getViewsFromJson(stepName, context, formFragment, jsonObject, listener, popup);
        for(View view : views){
            RelativeLayout relativeLayout = (RelativeLayout)view;
            MaterialSpinner materialSpinner =(MaterialSpinner) relativeLayout.getChildAt(0);
            materialSpinner.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Log.v("SPINNER_CHECK","onTouch>>>");
                    InputMethodManager inputManager = (InputMethodManager) v.getContext().getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    if(inputManager.isAcceptingText()){
                        inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }

                    return false;
                }
            });
        }
        return views;
    }

}
