package org.smartregister.brac.hnpp.widget;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
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
    private static JsonFormFragmentView formFragmentView;
    private static ImageView imageView;
    private static Button button;



    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JsonFormFragment formFragment, JSONObject jsonObject, CommonListener listener, boolean popup) throws Exception {
        List<View> views = super.getViewsFromJson(stepName, context, formFragment, jsonObject, listener, popup);
        formFragmentView = formFragment;
        imageView = (ImageView) views.get(0);
        button = (Button) views.get(1);
        return views;
    }
    public static void showFingerPrintErrorMessage() {
        ValidationStatus validationStatus = HnppFingerPrintFactory.validate(formFragmentView, imageView);
        if (!validationStatus.isValid()) {
            imageView.setImageResource(R.drawable.fingerprint_not_found);
        }
    }
    public static void updateButton(String guid){
        if(!TextUtils.isEmpty(guid)){
            button.setVisibility(View.GONE);
            if(imageView != null) imageView.setOnClickListener(null);
        }
    }

}
