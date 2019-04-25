//package org.smartregister.chw.custom_view;
//
//import android.app.Activity;
//import android.app.FragmentTransaction;
//import android.content.Context;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.util.AttributeSet;
//import android.widget.LinearLayout;
//
//import org.joda.time.DateTime;
//import org.smartregister.chw.R;
//import org.smartregister.chw.adapter.ImmunizationAdapter;
//import org.smartregister.chw.contract.ImmunizationEditContract;
//import org.smartregister.chw.fragment.ChildHomeVisitFragment;
//import org.smartregister.chw.fragment.ChildImmunizationFragment;
//import org.smartregister.chw.fragment.VaccinationDialogFragment;
//import org.smartregister.chw.listener.OnClickEditAdapter;
//import org.smartregister.chw.presenter.ImmunizationEditViewPresenter;
//import org.smartregister.chw.util.HomeVisitVaccineGroup;
//import org.smartregister.commonregistry.CommonPersonObjectClient;
//import org.smartregister.immunization.db.VaccineRepo;
//import org.smartregister.immunization.domain.VaccineWrapper;
//
//import java.util.ArrayList;
//import java.util.Date;
//
//import io.reactivex.Observable;
//
//public class ImmunizationEditView extends LinearLayout implements ImmunizationEditContract.View {
//
//    private RecyclerView recyclerView;
//    private ImmunizationAdapter adapter;
//    private ImmunizationEditViewPresenter presenter;
//    private CommonPersonObjectClient childClient;
//    private Activity activity;
//    private int pressPosition;
//    public ImmunizationEditView(Context context) {
//        super(context);
//        initUi();
//    }
//
//    public ImmunizationEditView(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        initUi();
//    }
//
//    public ImmunizationEditView(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        initUi();
//    }
//    private void initUi(){
//        inflate(getContext(), R.layout.custom_vaccine_edit,this);
//        recyclerView = findViewById(R.id.immunization_recycler_view);
//        initializePresenter();
//    }
//
//    @Override
//    public ImmunizationEditContract.Presenter initializePresenter() {
//        presenter = new ImmunizationEditViewPresenter(this);
//        return presenter;
//    }
//    public void setChildClient(Activity activity,CommonPersonObjectClient childClient){
//        this.childClient = childClient;
//        this.activity = activity;
//        presenter.fetchImmunizationEditData(childClient);
//    }
//    public Observable undoVaccine() {
//        return presenter.undoVaccine(childClient);
//    }
//    public Observable undoPreviousGivenVaccine(){
//        return presenter.undoPreviousGivenVaccine(childClient);
//    }
//    public Observable saveGivenThisVaccine(){
//        return presenter.saveGivenThisVaccine(childClient);
//    }
//    public ImmunizationEditViewPresenter getPresenter(){
//        return presenter;
//    }
//
//    @Override
//    public void allDataLoaded() {
//        ChildHomeVisitFragment childHomeVisitFragment = (ChildHomeVisitFragment) activity.getFragmentManager().findFragmentByTag(ChildHomeVisitFragment.DIALOG_TAG);
//
//        childHomeVisitFragment.allVaccineDataLoaded = true;
//        childHomeVisitFragment.forcfullyProgressBarInvisible();
//    }
//
//    @Override
//    public void updateAdapter() {
//        ChildHomeVisitFragment childHomeVisitFragment = (ChildHomeVisitFragment) activity.getFragmentManager().findFragmentByTag(ChildHomeVisitFragment.DIALOG_TAG);
//        childHomeVisitFragment.allVaccineDataLoaded = true;
//        childHomeVisitFragment.submitButtonEnableDisable(true);
//        if(adapter==null){
//            adapter = new ImmunizationAdapter(getContext(),onClickEditAdapter);
//            adapter.addItem(presenter.getHomeVisitVaccineGroupDetails());
//            recyclerView.setAdapter(adapter);
//            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        }else{
//            adapter.notifyItemChanged(pressPosition);
//        }
//
//    }
//    OnClickEditAdapter onClickEditAdapter = new OnClickEditAdapter() {
//        @Override
//        public void onClick(int position,HomeVisitVaccineGroup homeVisitVaccineGroup) {
//            pressPosition = position;
//            String dobString = org.smartregister.util.Utils.getValue(childClient.getColumnmaps(), "dob", false);
//            DateTime dateTime = new DateTime(dobString);
//            Date dob = dateTime.toDate();
//            VaccinationDialogFragment customVaccinationDialogFragment = VaccinationDialogFragment.newInstance(dob,presenter.getNotGivenVaccineWrappers(homeVisitVaccineGroup),new ArrayList<VaccineWrapper>(),
//                    presenter.getDueVaccineWrappers(homeVisitVaccineGroup));
//            customVaccinationDialogFragment.setChildDetails(childClient);
//            customVaccinationDialogFragment.setView(ImmunizationEditView.this);
//            FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
//            customVaccinationDialogFragment.show(ft, ChildImmunizationFragment.TAG);
//
//        }
//    };
//    public void updatePosition(){
//        ArrayList<VaccineRepo.Vaccine> givenList = presenter.convertGivenVaccineWrapperListToVaccineRepo();
//        ArrayList<VaccineRepo.Vaccine> notGiven = presenter.convertNotVaccineWrapperListToVaccineRepo();
//        if(givenList.size() == 0 && notGiven.size() == 0) return;
//        HomeVisitVaccineGroup selectedGroup = presenter.getHomeVisitVaccineGroupDetails().get(pressPosition);
//        selectedGroup.getGivenVaccines().clear();
//        selectedGroup.getGivenVaccines().addAll(givenList);
//        selectedGroup.getNotGivenVaccines().clear();
//        selectedGroup.getNotGivenVaccines().addAll(notGiven);
//        updateAdapter();
//    }
//
//    public ArrayList<VaccineWrapper> getNotGivenVaccine(){
//        return presenter.getNotGivenVaccines();
//    }
//}
