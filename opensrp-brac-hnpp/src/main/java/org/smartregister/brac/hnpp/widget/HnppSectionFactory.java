package org.smartregister.brac.hnpp.widget;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.widgets.SectionFactory;

import org.json.JSONObject;

import java.util.List;

public class HnppSectionFactory extends SectionFactory {

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JsonFormFragment formFragment, JSONObject jsonObject, CommonListener listener, boolean popup) throws Exception {
        List<View> views =  super.getViewsFromJson(stepName, context, formFragment, jsonObject, listener, popup);
        for(View v : views){
            LinearLayout linearLayout = (LinearLayout)v;
            TextView textView = (TextView) linearLayout.getChildAt(0);
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
        }
        return views;
    }
}
