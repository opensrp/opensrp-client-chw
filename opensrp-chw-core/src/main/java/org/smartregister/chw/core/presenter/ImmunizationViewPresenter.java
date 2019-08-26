package org.smartregister.chw.core.presenter;

import org.smartregister.chw.core.contract.ImmunizationContact;
import org.smartregister.chw.core.interactor.ImmunizationViewInteractor;
import org.smartregister.chw.core.utils.HomeVisitVaccineGroup;
import org.smartregister.commonregistry.CommonPersonObjectClient;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

public class ImmunizationViewPresenter implements ImmunizationContact.Presenter, ImmunizationContact.InteractorCallBack {

    private WeakReference<ImmunizationContact.View> view;
    private ArrayList<HomeVisitVaccineGroup> homeVisitVaccineGroupDetails = new ArrayList<>();
    private ImmunizationViewInteractor interactor;

    public ImmunizationViewPresenter(ImmunizationContact.View view) {
        this.view = new WeakReference<>(view);
        interactor = new ImmunizationViewInteractor(getView().getMyContext());
    }

    public ArrayList<HomeVisitVaccineGroup> getHomeVisitVaccineGroupDetails() {
        return homeVisitVaccineGroupDetails;
    }

    @Override
    public void fetchImmunizationData(CommonPersonObjectClient commonPersonObjectClient, String groupName) {
        interactor.fetchImmunizationData(commonPersonObjectClient, this);
    }

    @Override
    public ImmunizationContact.View getView() {
        if (view != null) {
            return view.get();
        } else {
            return null;
        }
    }

    @Override
    public void updateData(ArrayList<HomeVisitVaccineGroup> homeVisitVaccineGroupDetails, Map<String, Date> vaccines) {

        this.homeVisitVaccineGroupDetails = homeVisitVaccineGroupDetails;
        //if all due vaccine is same as given vaccine so remove the row.
        // has an issue after 24 hours/next visit all given vaccine showing again
        for (Iterator<HomeVisitVaccineGroup> iterator = this.homeVisitVaccineGroupDetails.iterator(); iterator.hasNext(); ) {
            HomeVisitVaccineGroup homeVisitVaccineGroup = iterator.next();
            if (homeVisitVaccineGroup.getDueVaccines().size() == 0 ||
                    (homeVisitVaccineGroup.getDueVaccines().size() == homeVisitVaccineGroup.getGivenVaccines().size())) {
                iterator.remove();
            }

        }
        for (int i = 0; i < this.homeVisitVaccineGroupDetails.size(); i++) {
            HomeVisitVaccineGroup homeVisitVaccineGroup = this.homeVisitVaccineGroupDetails.get(i);
            if (i == 0) {
                homeVisitVaccineGroup.setViewType(HomeVisitVaccineGroup.TYPE_INITIAL);
            } else {
                homeVisitVaccineGroup.setViewType(HomeVisitVaccineGroup.TYPE_INACTIVE);
            }
        }
        getView().allDataLoaded();
        getView().updateAdapter(0, view.get().getMyContext());


    }

}
