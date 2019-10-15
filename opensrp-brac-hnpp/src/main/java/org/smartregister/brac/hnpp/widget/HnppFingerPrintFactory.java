package org.smartregister.brac.hnpp.widget;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.utils.ValidationStatus;
import com.vijay.jsonwizard.views.JsonFormFragmentView;
import com.vijay.jsonwizard.widgets.FingerPrintFactory;

import org.json.JSONObject;
import org.smartregister.brac.hnpp.R;

import java.util.List;

public class HnppFingerPrintFactory extends FingerPrintFactory {
    static JsonFormFragmentView formFragmentView;
    static ImageView imageView;

    public static void showFingerPrintErrorMessage() {
        ValidationStatus validationStatus = HnppFingerPrintFactory.validate(formFragmentView, imageView);
        if (!validationStatus.isValid()) {
            imageView.setImageResource(R.drawable.fingerprint_not_found);
        }
    }

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JsonFormFragment formFragment, JSONObject jsonObject, CommonListener listener, boolean popup) throws Exception {
        List<View> views = super.getViewsFromJson(stepName, context, formFragment, jsonObject, listener, popup);
        String errMessage = (String) views.get(0).getTag(com.vijay.jsonwizard.R.id.error);
        Boolean isRequired = Boolean.valueOf((String) views.get(0).getTag(com.vijay.jsonwizard.R.id.v_required));
//        ViewGroup layout = (ViewGroup) views.get(0).getParent();
        formFragmentView = formFragment;
        imageView = (ImageView) views.get(0);
        return views;
    }

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JsonFormFragment formFragment, JSONObject jsonObject, CommonListener listener) throws Exception {
        List<View> views = super.getViewsFromJson(stepName, context, formFragment, jsonObject, listener);
        return views;
    }

}
