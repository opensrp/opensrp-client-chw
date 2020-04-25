package org.smartregister.chw.presenter;

import android.app.Activity;

import androidx.annotation.Nullable;

import org.apache.commons.lang3.tuple.Triple;
import org.smartregister.chw.activity.FamilyPlanningMemberProfileActivity;
import org.smartregister.chw.contract.PathfinderFamilyPlanningMemberProfileContract;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.fp_pathfinder.contract.BaseFpProfileContract;
import org.smartregister.chw.fp_pathfinder.domain.FpMemberObject;
import org.smartregister.chw.fp_pathfinder.presenter.BaseFpProfilePresenter;
import org.smartregister.chw.model.ReferralTypeModel;
import org.smartregister.chw.util.Utils;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.contract.FamilyProfileContract;
import org.smartregister.family.domain.FamilyEventClient;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.util.FormUtils;

import java.lang.ref.WeakReference;
import java.util.List;

import timber.log.Timber;

public class PathfinderFamilyPlanningMemberProfilePresenter extends BaseFpProfilePresenter implements PathfinderFamilyPlanningMemberProfileContract.Presenter, FamilyProfileContract.InteractorCallBack, org.smartregister.chw.contract.AncMemberProfileContract.Presenter {
    private org.smartregister.chw.fp_pathfinder.contract.BaseFpProfileContract.Interactor interactor;
    private WeakReference<org.smartregister.chw.fp_pathfinder.contract.BaseFpProfileContract.View> view;
    private FormUtils formUtils;
    private FpMemberObject fpMemberObject;

    public PathfinderFamilyPlanningMemberProfilePresenter(BaseFpProfileContract.View view, BaseFpProfileContract.Interactor interactor, org.smartregister.chw.fp_pathfinder.domain.FpMemberObject fpMemberObject) {
        super(view, interactor, fpMemberObject);
        this.interactor = interactor;
        this.view = new WeakReference<>(view);
        this.fpMemberObject = fpMemberObject;
    }


    @Override
    public void referToFacility() {
        List<ReferralTypeModel> referralTypeModels = ((FamilyPlanningMemberProfileActivity) getView()).getReferralTypeModels();
        if (referralTypeModels.size() == 1) {
            //TODO fix this
//            startFamilyPlanningReferral();
        } else {
            Utils.launchClientReferralActivity((Activity) getView(), referralTypeModels, fpMemberObject.getBaseEntityId());
        }
    }

    @Override
    public void createReferralEvent(AllSharedPreferences allSharedPreferences, String jsonString) throws Exception {
        ((PathfinderFamilyPlanningMemberProfileContract.Interactor) interactor).createReferralEvent(allSharedPreferences, jsonString, fpMemberObject.getBaseEntityId());
    }

    @Override
    @Nullable
    public PathfinderFamilyPlanningMemberProfileContract.View getView() {
        if (view != null) {
            return (PathfinderFamilyPlanningMemberProfileContract.View) view.get();
        } else {
            return null;
        }
    }

    @Override
    public void startFamilyPlanningReferral() {
        try {
            getView().startFormActivity(getFormUtils().getFormJson(CoreConstants.JSON_FORM.getFamilyPlanningReferralForm(fpMemberObject.getGender())), fpMemberObject);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private FormUtils getFormUtils() {

        if (formUtils == null) {
            try {
                formUtils = FormUtils.getInstance(org.smartregister.family.util.Utils.context().applicationContext());
            } catch (Exception e) {
                Timber.e(e);
            }
        }
        return formUtils;
    }

    @Override
    public void startFormForEdit(CommonPersonObjectClient client) {
        // TODO
    }

    @Override
    public void refreshProfileTopSection(CommonPersonObjectClient client) {
        // TODO
    }

    @Override
    public void onUniqueIdFetched(Triple<String, String, String> triple, String entityId) {
        // TODO
    }

    @Override
    public void onNoUniqueId() {
        // TODO
    }

    @Override
    public void onRegistrationSaved(boolean editMode, boolean isSaved, FamilyEventClient familyEventClient) {
        if (isSaved) {
            refreshProfileData();
            Timber.d("On member profile registration saved");
        }
    }
}
