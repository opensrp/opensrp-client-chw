package org.smartregister.chw.util;

import static org.smartregister.chw.util.Constants.Events.CBHS_CLOSE_VISITS;
import static org.smartregister.chw.util.Constants.TableName.CBHS_REGISTER;
import static org.smartregister.util.JsonFormUtils.FIELDS;

import android.content.Context;

import com.vijay.jsonwizard.utils.FormUtils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.chw.BuildConfig;
import org.smartregister.chw.anc.AncLibrary;
import org.smartregister.chw.anc.util.NCUtils;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.dao.ChwCBHSDao;
import org.smartregister.chw.hiv.domain.HivMemberObject;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.clientandeventmodel.Obs;
import org.smartregister.repository.AllSharedPreferences;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import timber.log.Timber;

public class CbhsUtils {
    public static void createCloseCbhsEvent(HivMemberObject hivMemberObject) {
        AllSharedPreferences allSharedPreferences = AncLibrary.getInstance().context().allSharedPreferences();
        Event closeCbhsEvent = (Event) new Event()
                .withBaseEntityId(hivMemberObject.getBaseEntityId())
                .withEventDate(new Date())
                .withEventType(CBHS_CLOSE_VISITS)
                .withFormSubmissionId(UUID.randomUUID().toString())
                .withEntityType(CBHS_REGISTER)
                .withProviderId(allSharedPreferences.fetchRegisteredANM())
                .withTeamId(allSharedPreferences.fetchDefaultTeamId(allSharedPreferences.fetchRegisteredANM()))
                .withTeam(allSharedPreferences.fetchDefaultTeam(allSharedPreferences.fetchRegisteredANM()))
                .withClientDatabaseVersion(BuildConfig.DATABASE_VERSION)
                .withClientApplicationVersion(BuildConfig.VERSION_CODE)
                .withDateCreated(new Date());

        closeCbhsEvent.setFormSubmissionId(UUID.randomUUID().toString());
        closeCbhsEvent.setEventDate(new Date());

        closeCbhsEvent.addObs(
                (new Obs())
                        .withFormSubmissionField("cbhs_close_visit_date")
                        .withValue(new Date().toString())
                        .withFieldCode("cbhs_close_visit_date")
                        .withFieldType("formsubmissionField")
                        .withFieldDataType("text")
                        .withParentCode("")
                        .withHumanReadableValues(new ArrayList<>()));

        try {
            NCUtils.addEvent(allSharedPreferences, closeCbhsEvent);
            NCUtils.startClientProcessing();
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    public static void removeDeceasedClients(HivMemberObject hivMemberObject, Context context) throws Exception {
        JSONObject removeFamilyMemberForm = null;
        if (ChwCBHSDao.isDeceased(hivMemberObject.getBaseEntityId())) {
            try {
                removeFamilyMemberForm = (new FormUtils()).getFormJsonFromRepositoryOrAssets(context, CoreConstants.JSON_FORM.FAMILY_DETAILS_REMOVE_MEMBER);
                org.smartregister.chw.anc.util.JsonFormUtils.getRegistrationForm(removeFamilyMemberForm, hivMemberObject.getBaseEntityId(), org.smartregister.Context.getInstance().allSharedPreferences().getPreference(AllConstants.CURRENT_LOCATION_ID));
            } catch (Exception e) {
                Timber.e(e);
            }

            if (removeFamilyMemberForm != null) {
                JSONObject stepOne = removeFamilyMemberForm.getJSONObject(org.smartregister.chw.anc.util.JsonFormUtils.STEP1);
                JSONArray jsonArray = stepOne.getJSONArray(FIELDS);

                org.smartregister.chw.anc.util.JsonFormUtils.updateFormField(jsonArray, "remove_reason", "Death");

                org.smartregister.chw.anc.util.JsonFormUtils.updateFormField(jsonArray, "dob", hivMemberObject.getAge());
                org.smartregister.chw.anc.util.JsonFormUtils.updateFormField(jsonArray, "date_died", new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date()));
                org.smartregister.chw.anc.util.JsonFormUtils.updateFormField(jsonArray, "age_at_death", org.smartregister.chw.util.Utils.getAgeFromDate(hivMemberObject.getAge()) + "y");

                org.smartregister.chw.util.Utils.removeUser(null, removeFamilyMemberForm, org.smartregister.chw.util.Utils.context().allSharedPreferences().fetchRegisteredANM());
            }
        }
    }

}
