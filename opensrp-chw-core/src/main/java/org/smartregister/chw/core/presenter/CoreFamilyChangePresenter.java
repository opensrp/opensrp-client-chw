package org.smartregister.chw.core.presenter;

import android.content.Context;
import android.util.Pair;

import org.smartregister.chw.core.contract.FamilyChangeContract;
import org.smartregister.chw.core.domain.FamilyMember;
import org.smartregister.chw.core.model.FamilyChangeContractModel;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.view.LocationPickerView;

import java.lang.ref.WeakReference;
import java.util.List;

public abstract class CoreFamilyChangePresenter implements FamilyChangeContract.Presenter {

    protected WeakReference<FamilyChangeContract.View> view;
    protected FamilyChangeContract.Model model;
    protected FamilyChangeContract.Interactor interactor;
    protected String familyID;

    public CoreFamilyChangePresenter(FamilyChangeContract.View view, String familyID) {
        this.view = new WeakReference<>(view);
        this.familyID = familyID;
        this.model = new FamilyChangeContractModel();
    }

    @Override
    public void saveCompleted(String familyHeadID, String careGiverID) {
        if (view != null && view.get() != null) {
            view.get().saveComplete(familyHeadID, careGiverID);
        }
    }

    @Override
    public void getAdultMembersExcludePCG() {
        if (view != null && view.get() != null) {
            interactor.getAdultMembersExcludePCG(familyID, this);
        }
    }

    @Override
    public void saveFamilyMember(Context context, Pair<String, FamilyMember> member) {

        LocationPickerView lpv = new LocationPickerView(context);
        lpv.init();
        String lastLocationId = LocationHelper.getInstance().getOpenMrsLocationId(lpv.getSelectedItem());

        interactor.updateFamilyMember(context, member, familyID, lastLocationId, this);
    }

    @Override
    public void renderAdultMembersExcludePCG(List<FamilyMember> clients, String primaryCareID, String headOfHouseID) {
        if (view != null && view.get() != null) {
            List<FamilyMember> res = model.getMembersExcluding(clients, primaryCareID, headOfHouseID, primaryCareID);
            view.get().refreshMembersView(res);
        }
    }

    @Override
    public void getAdultMembersExcludeHOF() {
        if (view != null && view.get() != null) {
            interactor.getAdultMembersExcludeHOF(familyID, this);
        }
    }

    @Override
    public void renderAdultMembersExcludeHOF(List<FamilyMember> clients, String primaryCareID, String headOfHouseID) {
        if (view != null && view.get() != null) {
            List<FamilyMember> res = model.getMembersExcluding(clients, primaryCareID, headOfHouseID, headOfHouseID);
            view.get().refreshMembersView(res);
        }
    }
}

