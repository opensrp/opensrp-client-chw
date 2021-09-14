package org.smartregister.chw.presenter;

import static com.vijay.jsonwizard.constants.JsonFormConstants.FIELDS;
import static com.vijay.jsonwizard.constants.JsonFormConstants.KEYS;
import static com.vijay.jsonwizard.constants.JsonFormConstants.STEP1;
import static com.vijay.jsonwizard.constants.JsonFormConstants.VALUES;
import static com.vijay.jsonwizard.widgets.TimePickerFactory.KEY.VALUE;

import static org.smartregister.chw.core.utils.CoreConstants.TABLE_NAME.FAMILY_LOCATION_COMMUNITY;
import static org.smartregister.chw.core.utils.CoreConstants.TABLE_NAME.FAMILY_LOCATION_LGA;
import static org.smartregister.chw.core.utils.CoreConstants.TABLE_NAME.FAMILY_LOCATION_STATE;
import static org.smartregister.chw.core.utils.CoreConstants.TABLE_NAME.FAMILY_LOCATION_WARD;

import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.google.gson.Gson;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.customviews.MaterialSpinner;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interactors.JsonFormInteractor;
import com.vijay.jsonwizard.presenters.JsonFormFragmentPresenter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.activity.FamilyWizardFormExtendedActivity;
import org.smartregister.chw.util.JsonFormUtils;
import org.smartregister.chw.util.Utils;
import org.smartregister.domain.Location;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import timber.log.Timber;

public class FamilyRegisterJsonFormFragmentPresenter extends JsonFormFragmentPresenter {

    private final JsonFormFragment formFragment;
    private final FamilyWizardFormExtendedActivity familyWizardFormExtendedActivity;

    private final HashSet<String> locationSpinners = new HashSet<String>() {{
        add(FAMILY_LOCATION_STATE);
        add(FAMILY_LOCATION_LGA);
        add(FAMILY_LOCATION_WARD);
        add(FAMILY_LOCATION_COMMUNITY);
    }};

    public FamilyRegisterJsonFormFragmentPresenter(JsonFormFragment formFragment, JsonFormInteractor jsonFormInteractor) {
        super(formFragment, jsonFormInteractor);
        this.formFragment = formFragment;
        familyWizardFormExtendedActivity = (FamilyWizardFormExtendedActivity) formFragment.getActivity();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        super.onItemSelected(parent, view, position, id);

        String key = (String) parent.getTag(R.id.key);
        String isHumanAction = String.valueOf(parent.getTag(R.id.is_human_action));

        try {
            if (locationSpinners.contains(key) && Boolean.parseBoolean(isHumanAction)) {

                JSONObject field = JsonFormUtils.getFieldJSONObject(formFragment.getStep(STEP1).getJSONArray(FIELDS), key);
                String parentLocationId = field.getString(VALUE);

                if (key.equals(FAMILY_LOCATION_STATE)) {
                    populateLocationSpinner(parentLocationId, FAMILY_LOCATION_LGA, Arrays.asList(FAMILY_LOCATION_WARD, FAMILY_LOCATION_COMMUNITY));
                } else if (key.equals(FAMILY_LOCATION_LGA)) {
                    populateLocationSpinner(parentLocationId, FAMILY_LOCATION_WARD, Arrays.asList(FAMILY_LOCATION_COMMUNITY));
                } else if (key.equals(FAMILY_LOCATION_WARD) && position > -1) {
                    populateLocationSpinner(parentLocationId, FAMILY_LOCATION_COMMUNITY, null);
                }
            }
        } catch (JSONException e) {
            Timber.e(e);
        }
    }

    private void populateLocationSpinner(String parentLocationId, String spinnerKey, List<String> controlsToHide) {
        List<Location> locations = Utils.getLocationsByParentId(parentLocationId);
        String selectedLocation = getCurrentLocation(spinnerKey);

        MaterialSpinner spinner = (MaterialSpinner) familyWizardFormExtendedActivity.getFormDataView(STEP1 + ":" + spinnerKey);
        if (spinner != null) {
            if (locations != null && !locations.isEmpty()) {
                Pair<JSONArray, JSONArray> options = populateLocationOptions(locations);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getView().getContext(), R.layout.native_form_simple_list_item_1, new Gson().fromJson(options.second.toString(), String[].class));
                spinner.setAdapter(adapter);
                spinner.setOnItemSelectedListener(formFragment.getCommonListener());
                spinner.setTag(R.id.keys, options.first);
                spinner.setVisibility(View.VISIBLE);
                spinner.setSelection(adapter.getPosition(selectedLocation) + 1);
            } else {
                spinner.setVisibility(View.GONE);
            }

            if (controlsToHide != null && !controlsToHide.isEmpty()) {
                for (String control : controlsToHide) {
                    MaterialSpinner spinnerToHide = (MaterialSpinner) familyWizardFormExtendedActivity.getFormDataView(JsonFormConstants.STEP1 + ":" + control);
                    spinnerToHide.setVisibility(View.GONE);
                }
            }
        }
    }

    public Pair<JSONArray, JSONArray> populateLocationOptions(List<Location> locations) {
        if (locations == null)
            return null;

        JSONObject field = new JSONObject();
        JSONArray codes = new JSONArray();
        JSONArray values = new JSONArray();

        for (int i = 0; i < locations.size(); i++) {
            codes.put(locations.get(i).getId());

            String id = locations.get(i).getProperties().getName().toLowerCase()
                    .replace(" ", "_")
                    .replace("(", "")
                    .replace(")", "")
                    .replace("-", "_")
                    .replace(":", "_");

            int identifier = formFragment.getResources().getIdentifier(id, "string", familyWizardFormExtendedActivity.getApplicationContext().getPackageName());
            String locationName = locations.get(i).getProperties().getName();
            if (identifier != 0) {
                locationName = familyWizardFormExtendedActivity.getResources().getString(identifier);
            }

            // values.put(locations.get(i).getProperties().getName());
            values.put(locationName);
        }

        try {
            field.put(KEYS, codes);
            field.put(VALUES, values);
        } catch (JSONException e) {
            Timber.e(e, "Error populating location options");
        }

        return new Pair<>(codes, values);
    }

    private String getCurrentLocation(String level) {
        String facilityId = Utils.getAllSharedPreferences().fetchUserLocalityId(Utils.getAllSharedPreferences().fetchRegisteredANM());
        String currentLocation = "";

        try {
            JSONObject form = familyWizardFormExtendedActivity.getmJSONObject();
            String fieldValue = JsonFormUtils.getFieldValue(form.getJSONObject(STEP1).getJSONArray(FIELDS), level);
            facilityId = (fieldValue == null || fieldValue.equals("")) ? facilityId : fieldValue;

            Location community = Utils.getLocationById(facilityId);
            String wardId = community != null ? community.getProperties().getParentId() : "";
            Location ward = Utils.getLocationById(wardId);
            Location lga = Utils.getLocationById(ward != null ? ward.getProperties().getParentId() : "");
            Location state = Utils.getLocationById(lga != null ? lga.getProperties().getParentId() : "");
            switch (level) {
                case "state":
                    currentLocation = state != null ? state.getId() : "";
                    break;
                case "lga":
                    currentLocation = lga != null ? lga.getId() : "";
                    break;
                case "community":
                    currentLocation = community != null ? community.getId() : "";
                    break;
                case "ward":
                default:
                    currentLocation = wardId != null ? ward.getId() : "";
                    break;
            }
        } catch (Exception e) {
            Timber.e(e);
        }

        return currentLocation;
    }
}
