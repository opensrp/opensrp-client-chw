package org.smartregister.chw.custom_view;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import org.joda.time.DateTime;
import org.smartregister.chw.R;
import org.smartregister.chw.adapter.ImmunizationAdapter;
import org.smartregister.chw.contract.ImmunizationContact;
import org.smartregister.chw.fragment.ChildHomeVisitFragment;
import org.smartregister.chw.fragment.VaccinationDialogFragment;
import org.smartregister.chw.listener.OnClickEditAdapter;
import org.smartregister.chw.presenter.ImmunizationViewPresenter;
import org.smartregister.chw.util.HomeVisitVaccineGroup;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.immunization.db.VaccineRepo;
import org.smartregister.immunization.domain.VaccineWrapper;

import java.util.ArrayList;
import java.util.Date;

import io.reactivex.Observable;

public class ImmunizationView extends LinearLayout implements ImmunizationContact.View {

    private RecyclerView recyclerView;
    private ImmunizationAdapter adapter;
    private ImmunizationViewPresenter presenter;
    private CommonPersonObjectClient childClient;
    private Activity activity;
    private int pressPosition;
    private boolean isEditMode;

    public ImmunizationView(Context context) {
        super(context);
        initUi();
    }

    public ImmunizationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initUi();
    }

    public ImmunizationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initUi();
    }

    private void initUi() {
        inflate(getContext(), R.layout.custom_vaccine_edit, this);
        recyclerView = findViewById(R.id.immunization_recycler_view);
        initializePresenter();
    }

    @Override
    public ImmunizationContact.Presenter initializePresenter() {
        presenter = new ImmunizationViewPresenter(this);
        return presenter;
    }

    public void setChildClient(Activity activity, CommonPersonObjectClient childClient, boolean isEditMode) {
        this.childClient = childClient;
        this.activity = activity;
        this.isEditMode = isEditMode;
        if (isEditMode) {
            presenter.fetchImmunizationEditData(childClient);
        } else {
            presenter.fetchImmunizationData(childClient);
        }

    }

    public Observable undoVaccine() {
        return presenter.undoVaccine(childClient);
    }

    public Observable undoPreviousGivenVaccine() {
        return presenter.undoPreviousGivenVaccine(childClient);
    }

    public Observable saveGivenThisVaccine() {
        return presenter.saveGivenThisVaccine(childClient);
    }

    public ImmunizationViewPresenter getPresenter() {
        return presenter;
    }

    @Override
    public void allDataLoaded() {
        ChildHomeVisitFragment childHomeVisitFragment = (ChildHomeVisitFragment) activity.getFragmentManager().findFragmentByTag(ChildHomeVisitFragment.DIALOG_TAG);

        childHomeVisitFragment.allVaccineDataLoaded = true;
        if (isEditMode) {
            childHomeVisitFragment.forcfullyProgressBarInvisible();
        } else {
            childHomeVisitFragment.allVaccineStateFullfilled = true;
            childHomeVisitFragment.progressBarInvisible();
            childHomeVisitFragment.checkIfSubmitIsToBeEnabled();
        }
    }

    @Override
    public void updateAdapter(int position) {
        ChildHomeVisitFragment childHomeVisitFragment = (ChildHomeVisitFragment) activity.getFragmentManager().findFragmentByTag(ChildHomeVisitFragment.DIALOG_TAG);
        if (isEditMode) {
            childHomeVisitFragment.allVaccineDataLoaded = true;
            childHomeVisitFragment.submitButtonEnableDisable(true);
        }
        if (adapter == null) {
            adapter = new ImmunizationAdapter(getContext(), onClickEditAdapter);
            adapter.addItem(presenter.getHomeVisitVaccineGroupDetails());
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        } else {
            adapter.notifyItemChanged(position);
        }

    }

    private OnClickEditAdapter onClickEditAdapter = new OnClickEditAdapter() {
        @Override
        public void onClick(int position, HomeVisitVaccineGroup homeVisitVaccineGroup) {
            pressPosition = position;
            String dobString = org.smartregister.util.Utils.getValue(childClient.getColumnmaps(), "dob", false);
            DateTime dateTime = new DateTime(dobString);
            Date dob = dateTime.toDate();
            VaccinationDialogFragment customVaccinationDialogFragment = VaccinationDialogFragment.newInstance(dob, presenter.getNotGivenVaccineWrappers(homeVisitVaccineGroup), new ArrayList<VaccineWrapper>(),
                    presenter.getDueVaccineWrappers(homeVisitVaccineGroup), homeVisitVaccineGroup.getGroup());
            customVaccinationDialogFragment.setChildDetails(childClient);
            customVaccinationDialogFragment.setView(ImmunizationView.this);
            FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
            customVaccinationDialogFragment.show(ft, VaccinationDialogFragment.DIALOG_TAG);

        }
    };

    public void updatePosition() {
        ArrayList<VaccineRepo.Vaccine> givenList = presenter.convertGivenVaccineWrapperListToVaccineRepo();
        ArrayList<VaccineRepo.Vaccine> notGiven = presenter.convertNotVaccineWrapperListToVaccineRepo();
        if (givenList.size() == 0 && notGiven.size() == 0) return;
        HomeVisitVaccineGroup selectedGroup = presenter.getHomeVisitVaccineGroupDetails().get(pressPosition);

        selectedGroup.setViewType(HomeVisitVaccineGroup.TYPE_ACTIVE);
        selectedGroup.getGivenVaccines().clear();
        selectedGroup.getGivenVaccines().addAll(givenList);
        selectedGroup.getNotGivenVaccines().clear();
        selectedGroup.getNotGivenVaccines().addAll(notGiven);
        updateAdapter(pressPosition);
        if (!isEditMode && (pressPosition + 1) < presenter.getHomeVisitVaccineGroupDetails().size()) {
            HomeVisitVaccineGroup nextSelectedGroup = presenter.getHomeVisitVaccineGroupDetails().get(pressPosition + 1);
            if (nextSelectedGroup.getViewType() == HomeVisitVaccineGroup.TYPE_INACTIVE) {
                nextSelectedGroup.setViewType(HomeVisitVaccineGroup.TYPE_INITIAL);
            }
            updateAdapter(pressPosition + 1);
        }
    }

    public ArrayList<VaccineWrapper> getNotGivenVaccine() {
        return presenter.getNotGivenVaccines();
    }
}
