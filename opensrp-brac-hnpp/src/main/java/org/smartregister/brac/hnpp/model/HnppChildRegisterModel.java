package org.smartregister.brac.hnpp.model;

import android.database.Cursor;
import android.util.Pair;

import org.json.JSONObject;
import org.smartregister.brac.hnpp.utils.HnppJsonFormUtils;
import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.model.CoreChildRegisterModel;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.family.util.Utils;

import java.util.ArrayList;

import timber.log.Timber;

public class HnppChildRegisterModel extends CoreChildRegisterModel {
    private String houseHoldId;
    private String familyBaseEntityId;
    public HnppChildRegisterModel( String houseHoldId, String familyBaseEntityId) {
        this.houseHoldId = houseHoldId;
        this.familyBaseEntityId = familyBaseEntityId;
    }
    @Override
    public Pair<Client, Event> processRegistration(String jsonString) {
        return HnppJsonFormUtils.processChildRegistrationForm(Utils.context().allSharedPreferences(), jsonString);
    }

    @Override
    public JSONObject getFormAsJson(String formName, String entityId, String currentLocationId, String familyID) throws Exception {
        JSONObject form = getFormUtils().getFormJson(formName);
        if (form == null) {
            return null;
        }
        ArrayList<String> womenList = getAllWomenInHouseHold();
        HnppJsonFormUtils.updateFormWithMotherName(form,womenList);
        HnppJsonFormUtils.updateFormWithMemberId(form,houseHoldId,familyBaseEntityId);
        return HnppJsonFormUtils.updateChildFormWithMetaData(form, houseHoldId,familyBaseEntityId);
    }
    public ArrayList<String> getAllWomenInHouseHold(){
        String query = "select first_name from ec_family_member where gender = 'নারী' and (marital_status != 'অবিবাহিত' and marital_status IS NOT NULL) and relational_id = '"+familyBaseEntityId+"'";
        Cursor cursor = null;
        ArrayList<String> womenList = new ArrayList<>();
        try {
            cursor = CoreChwApplication.getInstance().getRepository().getReadableDatabase().rawQuery(query, new String[]{});
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                String name = cursor.getString(0);
                womenList.add(name);
                cursor.moveToNext();
            }
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return womenList;
    }

}
