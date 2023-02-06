package org.smartregister.chw.presenter;

import android.content.Intent;

import org.smartregister.chw.application.ChwApplication;
import org.smartregister.family.contract.FamilyRegisterContract;
import org.smartregister.family.domain.FamilyEventClient;
import org.smartregister.family.presenter.BaseFamilyRegisterPresenter;
import org.smartregister.family.util.Constants;
import org.smartregister.family.util.Utils;
import org.smartregister.view.contract.BaseRegisterContract;

import java.util.List;

public class FamilyRegisterPresenter extends BaseFamilyRegisterPresenter {
    public FamilyRegisterPresenter(FamilyRegisterContract.View view, FamilyRegisterContract.Model model) {
        super(view, model);
    }

    @Override
    public void onRegistrationSaved(boolean isEditMode, boolean isSaved, List<FamilyEventClient> familyEventClientList) {
        if (ChwApplication.getApplicationFlavor().onFamilySaveGoToProfile()){
            BaseRegisterContract.View view = viewReference == null? null : viewReference.get();
            if (view != null && view.getContext() != null) {
//                view.refreshList(FetchStatus.fetched);
                view.hideProgressDialog();
                FamilyEventClient familyEventClient = familyEventClientList.get(0);
//                FamilyEventClient headEventClient = familyEventClientList.get(1);
                Intent intent = new Intent(view.getContext(), Utils.metadata().profileActivity);
                intent.putExtra(Constants.INTENT_KEY.FAMILY_BASE_ENTITY_ID, familyEventClient.getClient().getBaseEntityId());
//                intent.putExtra("family_head", Utils.getValue(patient.getColumnmaps(), "family_head", false));
//                intent.putExtra("primary_caregiver", Utils.getValue(patient.getColumnmaps(), "primary_caregiver", false));
//                intent.putExtra("village_town", Utils.getValue(patient.getColumnmaps(), "village_town", false));
//                intent.putExtra("family_name", Utils.getValue(patient.getColumnmaps(), "first_name", false));
//                intent.putExtra("go_to_due_page", goToDuePage);

                view.getContext().startActivity(intent);
            }
        }else{
            super.onRegistrationSaved(isEditMode, isSaved, familyEventClientList);
        }
    }
}
