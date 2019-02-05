package org.smartgresiter.wcaro.presenter;

import org.json.JSONObject;
import org.smartgresiter.wcaro.contract.ChildRemoveContract;
import org.smartgresiter.wcaro.interactor.FamilyRemoveMemberInteractor;
import org.smartgresiter.wcaro.model.FamilyRemoveMemberModel;
import org.smartgresiter.wcaro.util.Constants;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.view.LocationPickerView;

import java.lang.ref.WeakReference;

public class ChildRemovePresenter implements ChildRemoveContract.Presenter {
    WeakReference<ChildRemoveContract.View> viewReference;
    private String familyBaseEntityId;

    public ChildRemovePresenter(String familyBaseEntityId, ChildRemoveContract.View view) {
        this.viewReference = new WeakReference<>(view);
        this.familyBaseEntityId = familyBaseEntityId;
    }

    @Override
    public void removeMember(CommonPersonObjectClient client) {
        FamilyRemoveMemberModel model = new FamilyRemoveMemberModel();

        JSONObject form = model.prepareJsonForm(client, Constants.JSON_FORM.FAMILY_DETAILS_REMOVE_CHILD);
        if (form != null) {
            viewReference.get().startJsonActivity(form);
        }

    }

    @Override
    public void processRemoveForm(JSONObject jsonObject) {
        LocationPickerView lpv = new LocationPickerView(viewReference.get().getContext());
        lpv.init();
        String lastLocationId = LocationHelper.getInstance().getOpenMrsLocationId(lpv.getSelectedItem());
        FamilyRemoveMemberInteractor.getInstance().removeChild(familyBaseEntityId, lastLocationId, jsonObject, this);
    }

    @Override
    public void onChildRemove() {
        if (viewReference.get() != null) {
            viewReference.get().onChildRemove();
        }
    }
}
