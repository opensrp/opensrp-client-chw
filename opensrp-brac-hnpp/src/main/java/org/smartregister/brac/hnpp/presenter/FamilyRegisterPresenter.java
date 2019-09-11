package org.smartregister.brac.hnpp.presenter;

import org.smartregister.brac.hnpp.contract.HnppFamilyRegisterContract;
import org.smartregister.brac.hnpp.interactor.HnppFamilyRegisterInteractor;
import org.smartregister.brac.hnpp.location.SSLocationForm;
import org.smartregister.brac.hnpp.model.HnppFamilyRegisterModel;
import org.smartregister.family.contract.FamilyRegisterContract;
import org.smartregister.family.domain.FamilyEventClient;
import org.smartregister.family.presenter.BaseFamilyRegisterPresenter;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class FamilyRegisterPresenter extends BaseFamilyRegisterPresenter implements HnppFamilyRegisterContract.InteractorCallBack {
    public FamilyRegisterPresenter(FamilyRegisterContract.View view, FamilyRegisterContract.Model model) {
        super(view, model);
        interactor  = new HnppFamilyRegisterInteractor();
    }

    public String fetchHouseHoldId(int index){

        return ((HnppFamilyRegisterInteractor)interactor).getHouseHoldId(getSSLocation().get(index).locations,this);
    }
    public ArrayList<SSLocationForm> getSSLocation(){
        return ((HnppFamilyRegisterModel) model).getSsLocationForms();
    }

    @Override
    public void saveForm(String jsonString, boolean isEditMode) {
        try {

//            if (getView() != null)
//                getView().showProgressDialog(R.string.saving);

            List<FamilyEventClient> familyEventClientList = model.processRegistration(jsonString);
            if (familyEventClientList == null || familyEventClientList.isEmpty()) {
                if (getView() != null) getView().hideProgressDialog();
                return;
            }

            interactor.saveRegistration(familyEventClientList, jsonString, isEditMode, this);

        } catch (Exception e) {
            Timber.e(e);
        }
    }
    private FamilyRegisterContract.View getView() {
        if (viewReference != null)
            return viewReference.get();
        else
            return null;
    }

    @Override
    public void updateHouseHoldId(String houseHoldId) {
        //update householdid in form
        System.out.println(houseHoldId);
    }
}
