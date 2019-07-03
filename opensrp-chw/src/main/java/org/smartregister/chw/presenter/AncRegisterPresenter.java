package org.smartregister.chw.presenter;

import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONObject;
import org.smartregister.chw.anc.contract.BaseAncRegisterContract;
import org.smartregister.chw.anc.presenter.BaseAncRegisterPresenter;
import org.smartregister.chw.interactor.AncRegisterInteractor;
import org.smartregister.chw.model.AncRegisterModel;
import org.smartregister.family.contract.FamilyRegisterContract;
import org.smartregister.family.domain.FamilyEventClient;

import java.util.List;

import timber.log.Timber;

public class AncRegisterPresenter extends BaseAncRegisterPresenter implements FamilyRegisterContract.InteractorCallBack {

    private AncRegisterInteractor interactor;
    private AncRegisterModel model;

    public AncRegisterPresenter(BaseAncRegisterContract.View view, BaseAncRegisterContract.Model model, BaseAncRegisterContract.Interactor interactor) {
        super(view, model, interactor);
        this.interactor = (AncRegisterInteractor) interactor;
        this.model = (AncRegisterModel) model;
    }

    public void startFamilyForm(String formName, String entityId, String metadata, String currentLocationId) throws Exception {

        if (StringUtils.isBlank(entityId)) {
            Triple<String, String, String> triple = Triple.of(formName, metadata, currentLocationId);
            interactor.getNextUniqueId(triple, this);
            return;
        }

        JSONObject form = model.getFormAsJson(formName, entityId, currentLocationId);
        if (getView() != null)
            getView().startFormActivity(form);
    }

    @Override
    public void onNoUniqueId() {
        if (getView() != null)
            getView().displayShortToast(org.smartregister.family.R.string.no_unique_id);
    }

    @Override
    public void onUniqueIdFetched(Triple<String, String, String> triple, String entityId) {
        try {
            startForm(triple.getLeft(), entityId, triple.getMiddle(), triple.getRight());
        } catch (Exception e) {
            Timber.e(Log.getStackTraceString(e));
            if (getView() != null)
                getView().displayToast(org.smartregister.family.R.string.error_unable_to_start_form);
        }
    }

    private BaseAncRegisterContract.View getView() {
        return this.viewReference != null ? this.viewReference.get() : null;
    }

    public void saveForm(String jsonString, boolean isEditMode) {
        try {
            getView().showProgressDialog(org.smartregister.family.R.string.saving_dialog_title);

            List<FamilyEventClient> familyEventClientList = model.processRegistration(jsonString);
            if (familyEventClientList == null || familyEventClientList.isEmpty()) {
                return;
            }
            interactor.saveRegistration(familyEventClientList, jsonString, isEditMode, this);

        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }
}
