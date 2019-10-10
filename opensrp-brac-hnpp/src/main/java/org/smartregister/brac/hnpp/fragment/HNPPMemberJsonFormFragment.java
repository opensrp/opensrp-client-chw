package org.smartregister.brac.hnpp.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import com.vijay.jsonwizard.fragments.JsonWizardFormFragment;
import com.vijay.jsonwizard.presenters.JsonFormFragmentPresenter;
import com.vijay.jsonwizard.viewstates.JsonFormFragmentViewState;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.brac.hnpp.R;
import org.smartregister.brac.hnpp.interactor.HnppJsonFormInteractor;

import java.util.ArrayList;

import static com.vijay.jsonwizard.utils.FormUtils.getFieldJSONObject;

public class HNPPMemberJsonFormFragment extends JsonWizardFormFragment {
    public HNPPMemberJsonFormFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected JsonFormFragmentPresenter createPresenter() {
        return new JsonFormFragmentPresenter(this, HnppJsonFormInteractor.getInstance());
    }

    public static HNPPMemberJsonFormFragment getFormFragment(String stepName) {
        HNPPMemberJsonFormFragment jsonFormFragment = new HNPPMemberJsonFormFragment();
        Bundle bundle = new Bundle();
        bundle.putString("stepName", stepName);
        jsonFormFragment.setArguments(bundle);
        return jsonFormFragment;
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        super.onItemSelected(parent, view, position, id);

       // hideKeyBoard();
    }


    @Override
    protected JsonFormFragmentViewState createViewState() {
        return super.createViewState();
    }


    @Override
    public JSONObject getStep(String stepName) {
        return super.getStep(stepName);
    }

    public void updateGuid(String guid){
            try {
                JSONObject guIdField = getFieldJSONObject(getStep("step1").getJSONArray("fields"), "gu_id");
                guIdField.put("value",guid);
                JSONObject fingerPrint = getFieldJSONObject(getStep("step1").getJSONArray("fields"), "finger_print");
                fingerPrint.put("image_path", guid);
                JSONObject fingerPrintValue = getFieldJSONObject(getStep("step1").getJSONArray("fields"), "finger_print");
                fingerPrintValue.put("value",guid);
                ArrayList<View> formdataviews = new ArrayList<>(getJsonApi().getFormDataViews());
                for(View view : formdataviews){
                    if (view instanceof ImageView) {
                        ImageView imageView = (ImageView) view;
                        String key = (String) imageView.getTag(com.vijay.jsonwizard.R.id.key);
                        if (key.equals("finger_print")) {
                            imageView.setImageResource(R.drawable.finger_print_done);
                            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            imageView.setTag(R.id.imagePath, guid);
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

}
