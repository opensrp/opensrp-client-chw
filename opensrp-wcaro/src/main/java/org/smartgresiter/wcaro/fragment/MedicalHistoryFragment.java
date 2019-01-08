package org.smartgresiter.wcaro.fragment;

import android.app.DialogFragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.smartgresiter.wcaro.R;
import org.smartgresiter.wcaro.adapter.GrowthAdapter;
import org.smartgresiter.wcaro.adapter.VaccineAdapter;
import org.smartgresiter.wcaro.contract.MedicalHistoryContract;
import org.smartgresiter.wcaro.presenter.MedicalHistoryPresenter;
import org.smartgresiter.wcaro.util.Constants;
import org.smartregister.immunization.domain.Vaccine;

import java.util.List;

public class MedicalHistoryFragment extends DialogFragment implements View.OnClickListener,MedicalHistoryContract.View {
    private TextView textViewTitle,textViewLastVisit;
    private RelativeLayout layoutImmunization;
    private LinearLayout layoutGrowthAndNutrition;
    private RecyclerView recyclerViewImmunization,recyclerViewGrowthNutrition;
    private List<Vaccine> vaccineList;
    private String childBaseId,name,lastVisitDays;
    private MedicalHistoryContract.Presenter presenter;
    private VaccineAdapter vaccineAdapter;
    private GrowthAdapter growthAdapter;

    public static MedicalHistoryFragment getInstance(String childBaseEntityId,String childName,String lastVisitDays){
        MedicalHistoryFragment medicalHistoryFragment=new MedicalHistoryFragment();
        Bundle bundle=new Bundle();
        bundle.putString(Constants.INTENT_KEY.CHILD_BASE_ID,childBaseEntityId);
        bundle.putString(Constants.INTENT_KEY.CHILD_NAME,childName);
        bundle.putString(Constants.INTENT_KEY.CHILD_LAST_VISIT_DAYS,lastVisitDays);
        medicalHistoryFragment.setArguments(bundle);

        return medicalHistoryFragment;
    }
    public void setVaccineList(List<Vaccine> vaccines){
        vaccineList=vaccines;

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(android.app.DialogFragment.STYLE_NORMAL, android.R.style.Theme_Holo_Light_NoActionBar);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        return   inflater.inflate(R.layout.fragment_medical_history, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayout toolbar = view.findViewById(R.id.collapsing_toolbar);
        textViewTitle = toolbar.findViewById(R.id.toolbar_title);
        textViewLastVisit=view.findViewById(R.id.home_visit_date);
        layoutImmunization=view.findViewById(R.id.immunization_bar);
        recyclerViewImmunization=view.findViewById(R.id.immunization_recycler_view);
        recyclerViewGrowthNutrition=view.findViewById(R.id.recycler_view_growth);
        recyclerViewImmunization.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewGrowthNutrition.setLayoutManager(new LinearLayoutManager(getActivity()));
        layoutGrowthAndNutrition=view.findViewById(R.id.growth_and_nutrition_list);
        (view.findViewById(R.id.back_btn)).setOnClickListener(this);
        parseBundleANdUpdateView();


    }
    private void parseBundleANdUpdateView(){
        childBaseId=getArguments().getString(Constants.INTENT_KEY.CHILD_BASE_ID,"");
        name=getArguments().getString(Constants.INTENT_KEY.CHILD_NAME,"");
        lastVisitDays=getArguments().getString(Constants.INTENT_KEY.CHILD_LAST_VISIT_DAYS,"");
        if(TextUtils.isEmpty(name)){
            textViewTitle.setVisibility(View.GONE);
        }else{
            textViewTitle.setText(getString(R.string.medical_title,name));
        }
        textViewLastVisit.setText(getString(R.string.medical_last_visit,lastVisitDays));
        initializePresenter();
        setInitialVaccineList();
        fetchGrowthNutrition();
    }

    @Override
    public void onStart() {
        super.onStart();
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                getDialog().getWindow().setLayout(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);

            }
        });

    }
    private void setInitialVaccineList(){
        presenter.setInitialVaccineList(vaccineList);

    }
    private void fetchGrowthNutrition(){
        presenter.fetchGrowthNutrition(childBaseId);

    }


    @Override
    public void updateVaccinationData() {
        if(presenter.getVaccineBaseItem()!=null && presenter.getVaccineBaseItem().size()>0){
            layoutImmunization.setVisibility(View.VISIBLE);
            if(vaccineAdapter ==null){
                vaccineAdapter =new VaccineAdapter();
                vaccineAdapter.addItem(presenter.getVaccineBaseItem());
                recyclerViewImmunization.setAdapter(vaccineAdapter);
            }else{
                vaccineAdapter.notifyDataSetChanged();
            }
        }else{
            layoutImmunization.setVisibility(View.GONE);
        }


    }
    @Override
    public void updateGrowthNutrition() {
        if(presenter.getGrowthNutrition()!=null && presenter.getGrowthNutrition().size()>0){
            layoutGrowthAndNutrition.setVisibility(View.VISIBLE);
            if(growthAdapter==null){
                growthAdapter=new GrowthAdapter();
                growthAdapter.addItem(presenter.getGrowthNutrition());
                recyclerViewGrowthNutrition.setAdapter(growthAdapter);
            }else{
                growthAdapter.notifyDataSetChanged();
            }
        }else{
            layoutGrowthAndNutrition.setVisibility(View.GONE);
        }

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_btn:
                dismiss();
                break;
        }
    }



    @Override
    public MedicalHistoryContract.Presenter initializePresenter() {
        presenter=new MedicalHistoryPresenter(this);
        return presenter;
    }
}
