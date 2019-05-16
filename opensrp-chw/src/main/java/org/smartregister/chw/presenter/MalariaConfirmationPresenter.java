package org.smartregister.chw.presenter;

import android.util.Log;

import org.json.JSONObject;
import org.smartregister.chw.contract.MalariaConfirmationContract;
import org.smartregister.chw.util.Constants;
import org.smartregister.util.FormUtils;

import java.lang.ref.WeakReference;


public class MalariaConfirmationPresenter implements MalariaConfirmationContract.Presenter {

    private WeakReference<MalariaConfirmationContract.View> view;
    private FormUtils formUtils = null;

    @Override
    public MalariaConfirmationContract.View getView() {
        if (view != null) {
            return view.get();
        } else {
            return null;
        }
    }

    @Override
    public void startMalariaConfirmationForm() {
        try {

            JSONObject form = getFormUtils().getFormJson(Constants.JSON_FORM.HOME_VISIT_COUNSELLING);
            getView().startFormActivity(form);

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    private FormUtils getFormUtils() {
        if (formUtils == null) {
            try {
                formUtils = FormUtils.getInstance(org.smartregister.family.util.Utils.context().applicationContext());
            } catch (Exception e) {
                Log.e(MalariaConfirmationPresenter.class.getCanonicalName(), e.getMessage(), e);
            }
        }
        return formUtils;
    }
}
