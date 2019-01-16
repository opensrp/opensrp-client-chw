package org.smartgresiter.wcaro.presenter;

import android.content.Context;

import org.smartgresiter.wcaro.contract.FamilyChangeContract;
import org.smartgresiter.wcaro.interactor.FamilyChangeContractInteractor;
import org.smartgresiter.wcaro.model.FamilyChangeContractModel;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;

public class FamilyChangeContractPresenter implements FamilyChangeContract.Presenter {

    protected WeakReference<FamilyChangeContract.View> view;
    protected FamilyChangeContract.Model model;
    protected FamilyChangeContract.Interactor interactor;
    protected String familyID;

    public FamilyChangeContractPresenter(FamilyChangeContract.View view, String familyID) {
        this.view = new WeakReference<>(view);
        this.familyID = familyID;
        this.model = new FamilyChangeContractModel();
        this.interactor = new FamilyChangeContractInteractor();
    }

    @Override
    public void getAdultMembersExcludePCG() {
        if (view != null && view.get() != null) {
            interactor.getAdultMembersExcludePCG(familyID, this);
        }
    }

    @Override
    public void saveFamilyMember(Context context, HashMap<String,String> member) {
        interactor.updateFamilyMember(context , member, familyID, this);
    }

    @Override
    public void renderAdultMembersExcludePCG(List<HashMap<String, String>> clients, String primaryCareID, String headOfHouseID) {
        if (view != null && view.get() != null) {
            List<HashMap<String,String>> res = model.getMembersExcluding(clients, primaryCareID, headOfHouseID, primaryCareID);
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
    public void renderAdultMembersExcludeHOF(List<HashMap<String, String>> clients, String primaryCareID, String headOfHouseID) {
        if (view != null && view.get() != null) {
            List<HashMap<String,String>> res = model.getMembersExcluding(clients, primaryCareID, headOfHouseID, headOfHouseID);
            view.get().refreshMembersView(res);
        }
    }


    @Override
    public void saveCompleted() {
        if (view != null && view.get() != null) {
            view.get().saveComplete();
        }
    }

    @Override
    public void getMembers(String familyID) {

    }
}

