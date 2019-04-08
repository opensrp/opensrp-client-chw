package org.smartregister.chw.presenter;

import org.smartregister.chw.contract.ImmunizationEditContract;
import org.smartregister.chw.interactor.ImmunizationEditViewInteractor;
import org.smartregister.chw.util.HomeVisitVaccineGroupDetails;
import org.smartregister.commonregistry.CommonPersonObjectClient;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class ImmunizationEditViewPresenter implements ImmunizationEditContract.Presenter,ImmunizationEditContract.InteractorCallBack {

    private WeakReference<ImmunizationEditContract.View> view;
    private ArrayList<HomeVisitVaccineGroupDetails> homeVisitVaccineGroupDetails;
    private ImmunizationEditContract.Interactor interactor;
    public ImmunizationEditViewPresenter(ImmunizationEditContract.View view){
        this.view = new WeakReference<>(view);
        interactor = new ImmunizationEditViewInteractor();

    }

    public ArrayList<HomeVisitVaccineGroupDetails> getHomeVisitVaccineGroupDetails() {
        return homeVisitVaccineGroupDetails;
    }

    @Override
    public void fetchImmunizationEditData(CommonPersonObjectClient commonPersonObjectClient) {

    }

    @Override
    public void updateEditData(ArrayList<HomeVisitVaccineGroupDetails> homeVisitVaccineGroupDetails) {
        this.homeVisitVaccineGroupDetails = homeVisitVaccineGroupDetails;
        getView().updateAdapter();

    }

    @Override
    public ImmunizationEditContract.View getView() {
        if (view != null) {
            return view.get();
        } else {
            return null;
        }
    }
}
