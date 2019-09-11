package org.smartregister.brac.hnpp.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.vijay.jsonwizard.customviews.MaterialSpinner;
import com.vijay.jsonwizard.fragments.JsonWizardFormFragment;
import com.vijay.jsonwizard.viewstates.JsonFormFragmentViewState;
import org.smartregister.brac.hnpp.activity.HNPPJsonFormActivity;
import org.smartregister.brac.hnpp.location.SSLocationForm;
import org.smartregister.brac.hnpp.location.SSLocationHelper;
import org.smartregister.brac.hnpp.location.SSLocations;
import org.smartregister.util.Utils;

import java.util.ArrayList;

public class HNPPJsonFormFragment extends JsonWizardFormFragment {
    public HNPPJsonFormFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public static HNPPJsonFormFragment getFormFragment(String stepName) {
        HNPPJsonFormFragment jsonFormFragment = new HNPPJsonFormFragment();
        Bundle bundle = new Bundle();
        bundle.putString("stepName", stepName);
        jsonFormFragment.setArguments(bundle);
        return jsonFormFragment;
    }

    @Override
    protected JsonFormFragmentViewState createViewState() {
        return super.createViewState();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//        super.onItemSelected(parent, view, position, id);
        if (position != -1 && parent instanceof MaterialSpinner) {
            if (((MaterialSpinner) parent).getFloatingLabelText().toString().equalsIgnoreCase("স্বাস্থ্য সেবিকার/গ্রামের নামঃ")) {

                processUniqueId(position);
            }
        }


    }

    public void processUniqueId(final int index) {


        Utils.startAsyncTask(new AsyncTask() {

            String unique_id = "";

            @Override
            protected Object doInBackground(Object[] objects) {
                if (getActivity() instanceof HNPPJsonFormActivity) {
                    HNPPJsonFormActivity jsonFormActivity = (HNPPJsonFormActivity) getActivity();
                    ArrayList<SSLocationForm> locationFormList = jsonFormActivity.getSsLocationForms();
                    SSLocations ssLocations = locationFormList.get(index).locations;
                    unique_id = SSLocationHelper.getInstance().generateHouseHoldId(ssLocations, ssLocations.village.id + "");
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                ArrayList<View> formdataviews = getJsonApi().getFormDataViews();
                for (int i = 0; i < formdataviews.size(); i++) {
                    if (formdataviews.get(i) instanceof MaterialEditText) {
                        if (((MaterialEditText) formdataviews.get(i)).getFloatingLabelText().toString().trim().equalsIgnoreCase("খানা নাম্বার")) {
                            ((MaterialEditText) formdataviews.get(i)).setText(unique_id);
                            break;
                        }
                    }
                }
            }
        }, null);
    }

}
