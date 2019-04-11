package org.smartregister.chw.presenter;

import org.smartregister.chw.contract.ImmunizationEditContract;
import org.smartregister.chw.interactor.ImmunizationEditViewInteractor;
import org.smartregister.chw.util.HomeVisitVaccineGroup;
import org.smartregister.commonregistry.CommonPersonObjectClient;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class ImmunizationEditViewPresenter implements ImmunizationEditContract.Presenter,ImmunizationEditContract.InteractorCallBack {

    private WeakReference<ImmunizationEditContract.View> view;
    private ArrayList<HomeVisitVaccineGroup> homeVisitVaccineGroupDetails;
    private ImmunizationEditContract.Interactor interactor;
    public ImmunizationEditViewPresenter(ImmunizationEditContract.View view){
        this.view = new WeakReference<>(view);
        interactor = new ImmunizationEditViewInteractor();

    }

    public ArrayList<HomeVisitVaccineGroup> getHomeVisitVaccineGroupDetails() {
        return homeVisitVaccineGroupDetails;
    }

    @Override
    public void fetchImmunizationEditData(CommonPersonObjectClient commonPersonObjectClient) {
        interactor.fetchImmunizationEditData(commonPersonObjectClient,this);

    }

    @Override
    public void updateEditData(ArrayList<HomeVisitVaccineGroup> homeVisitVaccineGroupDetails) {
        this.homeVisitVaccineGroupDetails = homeVisitVaccineGroupDetails;
        for (HomeVisitVaccineGroup homeVisitVaccineGroup :this.homeVisitVaccineGroupDetails){
            homeVisitVaccineGroup.setViewType(HomeVisitVaccineGroup.TYPE_ACTIVE);
        }
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
