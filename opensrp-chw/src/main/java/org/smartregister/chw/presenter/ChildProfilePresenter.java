package org.smartregister.chw.presenter;

import android.app.Activity;
import android.util.Pair;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.joda.time.LocalDate;
import org.joda.time.Months;
import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.activity.ChildProfileActivity;
import org.smartregister.chw.core.contract.CoreChildProfileContract;
import org.smartregister.chw.core.presenter.CoreChildProfilePresenter;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.interactor.ChildProfileInteractor;
import org.smartregister.chw.interactor.FamilyProfileInteractor;
import org.smartregister.chw.model.ChildRegisterModel;
import org.smartregister.chw.model.ReferralTypeModel;
import org.smartregister.chw.util.Constants;
import org.smartregister.chw.util.Utils;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.util.DBConstants;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

public class ChildProfilePresenter extends CoreChildProfilePresenter {

    public ChildProfilePresenter(CoreChildProfileContract.View childView, CoreChildProfileContract.Model model, String childBaseEntityId) {
        super(childView, model, childBaseEntityId);
        setView(new WeakReference<>(childView));
        setInteractor(new ChildProfileInteractor());
        getInteractor().setChildBaseEntityId(childBaseEntityId);
        setModel(model);
    }

    @Override
    public void verifyHasPhone() {
        new FamilyProfileInteractor().verifyHasPhone(familyID, this);
    }

    @Override
    public void notifyHasPhone(boolean hasPhone) {
        if (getView() != null) {
            getView().updateHasPhone(hasPhone);
        }
    }

    @Override
    public void updateChildProfile(String jsonString) {
        getView().showProgressDialog(R.string.updating);
        Pair<Client, Event> pair = new ChildRegisterModel().processRegistration(jsonString);
        if (pair == null) {
            return;
        }

        getInteractor().saveRegistration(pair, jsonString, true, this);
    }

    @Override
    public void startSickChildReferralForm() {
        try {
            getView().startFormActivity(getFormUtils().getFormJson(Constants.JSON_FORM.getChildReferralForm()));
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    @Override
    public void startSickChildForm(CommonPersonObjectClient client) {
        try {
            JSONObject jsonObject = this.getFormUtils().getFormJson(CoreConstants.JSON_FORM.getChildSickForm());

            String dobStr = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.DOB, true);
            Date dobDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(dobStr);

            LocalDate date1 = LocalDate.fromDateFields(dobDate);
            LocalDate date2 = LocalDate.now();
            int months = Months.monthsBetween(date1, date2).getMonths();

            JSONArray array = jsonObject.getJSONObject(JsonFormConstants.STEP1).getJSONArray(JsonFormConstants.FIELDS);
            int x = 0;
            while (x < array.length()) {
                JSONObject object = array.getJSONObject(x);
                if (object.getString(JsonFormConstants.KEY).equalsIgnoreCase("age_in_months")) {
                    object.put(JsonFormConstants.VALUE, String.valueOf(months));
                    break;
                }
                x++;
            }

            this.getView().startFormActivity(jsonObject);
        } catch (Exception var3) {
            Timber.e(var3);
        }
    }

    public void referToFacility() {
        List<ReferralTypeModel> referralTypeModels = ((ChildProfileActivity) getView()).getReferralTypeModels();
        if (referralTypeModels.size() == 1) {
            startSickChildReferralForm();
        } else {
            Utils.launchClientReferralActivity((Activity) getView(), referralTypeModels, childBaseEntityId);
        }
    }

}
