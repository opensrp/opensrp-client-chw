package org.smartregister.chw.factory;

import static android.provider.Settings.NameValueTable.VALUE;
import static com.vijay.jsonwizard.constants.JsonFormConstants.FIELDS;
import static com.vijay.jsonwizard.constants.JsonFormConstants.HIDDEN;
import static com.vijay.jsonwizard.constants.JsonFormConstants.STEP1;
import static org.smartregister.chw.core.repository.ContactInfoRepository.KEY;
import static org.smartregister.chw.core.utils.CoreConstants.LOCATIONS.FAMILY_LOCATION_COMMUNITY;
import static org.smartregister.chw.core.utils.CoreConstants.LOCATIONS.FAMILY_LOCATION_LGA;
import static org.smartregister.chw.core.utils.CoreConstants.LOCATIONS.FAMILY_LOCATION_STATE;
import static org.smartregister.chw.core.utils.CoreConstants.LOCATIONS.FAMILY_LOCATION_WARD;
import static org.smartregister.chw.core.utils.CoreConstants.TEXT;
import static org.smartregister.chw.util.Constants.LOCATION_SUB_TYPE;
import static org.smartregister.chw.util.Constants.SUB_TYPE;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;

import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.widgets.SpinnerFactory;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.BuildConfig;
import org.smartregister.chw.R;
import org.smartregister.chw.activity.FamilyWizardFormExtendedActivity;
import org.smartregister.chw.util.JsonFormUtils;
import org.smartregister.chw.util.Utils;
import org.smartregister.domain.Location;
import org.smartregister.domain.LocationTag;
import org.smartregister.family.util.Constants;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class FamilyRegisterSpinnerFactory extends SpinnerFactory {

    private JsonFormFragment formFragment;

    private FamilyWizardFormExtendedActivity familyWizardFormExtendedActivity;

    private final Map<String, List<String>> descendants = new HashMap<String, List<String>>() {{
        put(FAMILY_LOCATION_STATE, Arrays.asList(FAMILY_LOCATION_LGA, FAMILY_LOCATION_WARD, FAMILY_LOCATION_COMMUNITY));
        put(FAMILY_LOCATION_LGA, Arrays.asList(FAMILY_LOCATION_WARD, FAMILY_LOCATION_COMMUNITY));
        put(FAMILY_LOCATION_WARD, Arrays.asList(FAMILY_LOCATION_COMMUNITY));
        put(FAMILY_LOCATION_COMMUNITY, null);
    }};

    private final Map<String, String> parents = new HashMap<String, String>() {{
        put(FAMILY_LOCATION_LGA, FAMILY_LOCATION_STATE);
        put(FAMILY_LOCATION_WARD, FAMILY_LOCATION_LGA);
        put(FAMILY_LOCATION_COMMUNITY, FAMILY_LOCATION_WARD);
    }};

    public FamilyRegisterSpinnerFactory() {
        super();
    }

    @Override
    public void genericWidgetLayoutHookback(View view, JSONObject jsonObject, JsonFormFragment formFragment) {
        super.genericWidgetLayoutHookback(view, jsonObject, formFragment);
        View materialView = ((RelativeLayout) view).getChildAt(0);
        materialView.setOnTouchListener((v, event) -> {
            materialView.setTag(R.id.is_human_action, true);
            return false;
        });
    }

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JsonFormFragment formFragment, JSONObject jsonObject, CommonListener listener, boolean popup) throws Exception {
        this.formFragment = formFragment;
        familyWizardFormExtendedActivity = (FamilyWizardFormExtendedActivity) formFragment.getActivity();

        try {
            if (jsonObject.has(SUB_TYPE)
                    && jsonObject.getString(SUB_TYPE).equalsIgnoreCase(LOCATION_SUB_TYPE)
                    && jsonObject.has(Constants.JSON_FORM_KEY.OPTIONS)) {
                jsonObject.getJSONArray(Constants.JSON_FORM_KEY.OPTIONS);
                if (jsonObject.getJSONArray(Constants.JSON_FORM_KEY.OPTIONS).length() <= 0) {

                    if ("state".equals(jsonObject.getString(KEY))) {
                        populateState(jsonObject);
                    } else {
                        populateDescendants(jsonObject);
                    }
                }
            }
        } catch (JSONException e) {
            Timber.e(e);
        }

        return super.getViewsFromJson(stepName, context, formFragment, jsonObject, listener, popup);
    }

    private void populateState(JSONObject jsonObject) {
        List<LocationTag> tags = Utils.getLocationTagsByTagName(BuildConfig.ROOT_LOCATION_TAG);
        String countryId = (tags != null && tags.size() > 0) ? tags.get(0).getLocationId() : "";

        try {
            populateLocationSpinner(jsonObject, countryId, jsonObject.getString(KEY), descendants.get(jsonObject.getString(KEY)));
        } catch (JSONException e) {
            Timber.e(e);
        }
    }


    private void populateDescendants(JSONObject jsonObject) {
        try {
            JSONObject parentField = JsonFormUtils.getFieldJSONObject(getFormStep().getJSONArray(FIELDS), parents.get(jsonObject.getString(KEY)));
            String parentId = parentField.getString(VALUE);

            populateLocationSpinner(jsonObject, parentId, jsonObject.getString(KEY), descendants.get(jsonObject.getString(KEY)));
        } catch (JSONException e) {
            Timber.e(e);
        }
    }

    private void populateLocationSpinner(JSONObject jsonObject, String parentLocationId, String spinnerKey, List<String> controlsToHide) {
        List<Location> locations = Utils.getLocationsByParentId(parentLocationId);
        String selectedLocation = getCurrentLocation(spinnerKey);

        try {
            JSONArray spinnerOptions = jsonObject.getJSONArray("options");

            for (Location entry : locations) {
                String id = entry.getProperties().getName().toLowerCase()
                        .replace(" ", "_")
                        .replace("(", "")
                        .replace(")", "")
                        .replace("-", "_")
                        .replace(":", "_");

                int identifier = formFragment.getResources().getIdentifier(id, "string", familyWizardFormExtendedActivity.getApplicationContext().getPackageName());
                String locationName = entry.getProperties().getName();
                if (identifier != 0) {
                    locationName = familyWizardFormExtendedActivity.getResources().getString(identifier);
                }

                JSONObject option = new JSONObject();
                option.put(KEY, entry.getId());
                // option.put(TEXT, entry.getProperties().getName());
                option.put(TEXT, locationName);

                spinnerOptions.put(option);

                jsonObject.put(VALUE, selectedLocation);
            }

            JSONObject field = JsonFormUtils.getFieldJSONObject(getFormStep().getJSONArray(FIELDS), jsonObject.getString(KEY));
            field.put(VALUE, selectedLocation);
        } catch (JSONException e) {
            Timber.e(e);
        }

        if (controlsToHide != null && !controlsToHide.isEmpty() && StringUtils.isEmpty(parentLocationId)) {
            for (String control : controlsToHide) {
                try {
                    JSONObject formField = JsonFormUtils.getFieldJSONObject(getFormStep().getJSONArray(FIELDS), control);

                    formField.put(HIDDEN, true);
                } catch (JSONException e) {
                    Timber.e(e);
                }
            }
        }
    }

    private JSONObject getFormStep() {
        return formFragment.getStep(STEP1);
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
                    if (fieldValue != null) state = Utils.getLocationById(fieldValue);
                    currentLocation = state != null ? state.getId() : "";
                    break;
                case "lga":
                    if (fieldValue != null) lga = Utils.getLocationById(fieldValue);
                    currentLocation = lga != null ? lga.getId() : "";
                    break;
                case "community":
                    if (fieldValue != null) community = Utils.getLocationById(fieldValue);
                    currentLocation = community != null ? community.getId() : "";
                    break;
                case "ward":
                default:
                    if (fieldValue != null) ward = Utils.getLocationById(fieldValue);
                    currentLocation = wardId != null ? ward.getId() : "";
                    break;
            }
        } catch (Exception e) {
            Timber.e(e);
        }

        return currentLocation;
    }

}
