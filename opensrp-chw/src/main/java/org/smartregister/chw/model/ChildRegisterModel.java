package org.smartregister.chw.model;

import android.util.Pair;

import org.json.JSONObject;
import com.opensrp.chw.core.contract.ChildRegisterContract;
import org.smartregister.chw.util.JsonFormUtils;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.configurableviews.ConfigurableViewsLibrary;
import org.smartregister.family.util.Utils;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.util.FormUtils;

import java.util.List;

import timber.log.Timber;

public class ChildRegisterModel implements ChildRegisterContract.Model {
    private FormUtils formUtils;

    @Override
    public void registerViewConfigurations(List<String> viewIdentifiers) {
        ConfigurableViewsLibrary.getInstance().getConfigurableViewsHelper().registerViewConfigurations(viewIdentifiers);
    }

    @Override
    public void unregisterViewConfiguration(List<String> viewIdentifiers) {
        ConfigurableViewsLibrary.getInstance().getConfigurableViewsHelper().unregisterViewConfiguration(viewIdentifiers);
    }

    @Override
    public void saveLanguage(String language) {
        // TODO Save Language
        //Map<String, String> langs = getAvailableLanguagesMap();
        //Utils.saveLanguage(Utils.getKeyByValue(langs, language));
    }

    /*private Map<String, String> getAvailableLanguagesMap() {
        return null;
        //return AncApplication.getJsonSpecHelper().getAvailableLanguagesMap();
    }*/

    @Override
    public String getLocationId(String locationName) {
        return LocationHelper.getInstance().getOpenMrsLocationId(locationName);
    }

    @Override
    public Pair<Client, Event> processRegistration(String jsonString) {
        return JsonFormUtils.processChildRegistrationForm(Utils.context().allSharedPreferences(), jsonString);
    }

    @Override
    public JSONObject getFormAsJson(String formName, String entityId, String currentLocationId, String familyID) throws Exception {
        JSONObject form = getFormUtils().getFormJson(formName);
        if (form == null) {
            return null;
        }
        return JsonFormUtils.getFormAsJson(form, formName, entityId, currentLocationId, familyID);
    }

    private FormUtils getFormUtils() {
        if (formUtils == null) {
            try {
                formUtils = FormUtils.getInstance(Utils.context().applicationContext());
            } catch (Exception e) {
                Timber.e(e);
            }
        }
        return formUtils;
    }

    public void setFormUtils(FormUtils formUtils) {
        this.formUtils = formUtils;
    }


    @Override
    public String getInitials() {
        return Utils.getUserInitials();
    }
}
