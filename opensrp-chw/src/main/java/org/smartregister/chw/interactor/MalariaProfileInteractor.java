package org.smartregister.chw.interactor;

import android.content.Context;
import android.support.annotation.VisibleForTesting;
import android.util.Pair;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.smartregister.chw.contract.FamilyProfileExtendedContract;
import org.smartregister.chw.contract.MalariaProfileContract;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.util.AppExecutors;
import org.smartregister.family.util.DBConstants;
import org.smartregister.family.util.Utils;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class MalariaProfileInteractor implements MalariaProfileContract.Interactor {

    private AppExecutors appExecutors;
    private CommonPersonObjectClient pClient;

    @VisibleForTesting
    MalariaProfileInteractor(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
    }

    public MalariaProfileInteractor() {
        this(new AppExecutors());
    }


    public CommonPersonObjectClient getpClient() {
        return pClient;
    }

    public String getValue(Map<String, String> map, String field) {
        return Utils.getValue(map, field, false);
    }

    @Override
    public void updateVisitNotDone(long value, MalariaProfileContract.InteractorCallBack callback) {

    }

    @Override
    public void refreshChildVisitBar(Context context, String baseEntityId, MalariaProfileContract.InteractorCallBack callback) {

    }

    @Override
    public void refreshUpcomingServiceAndFamilyDue(Context context, String familyId, String baseEntityId, MalariaProfileContract.InteractorCallBack callback) {

    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {

    }

    @Override
    public void updateChildCommonPerson(String baseEntityId) {

    }

    @Override
    public void refreshProfileView(String baseEntityId, boolean isForEdit, MalariaProfileContract.InteractorCallBack callback) {

    }

    @Override
    public void saveRegistration(Pair<Client, Event> pair, String jsonString, boolean isEditMode, MalariaProfileContract.InteractorCallBack callBack) {

    }

    @Override
    public JSONObject getAutoPopulatedJsonEditFormString(String formName, String title, Context context, CommonPersonObjectClient client) {
        return null;
    }
}
