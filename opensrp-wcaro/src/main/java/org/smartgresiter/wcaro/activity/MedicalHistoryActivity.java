package org.smartgresiter.wcaro.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.smartgresiter.wcaro.R;
import org.smartgresiter.wcaro.adapter.GrowthAdapter;
import org.smartgresiter.wcaro.adapter.VaccineAdapter;
import org.smartgresiter.wcaro.contract.MedicalHistoryContract;
import org.smartgresiter.wcaro.presenter.MedicalHistoryPresenter;
import org.smartgresiter.wcaro.util.Constants;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MedicalHistoryActivity extends AppCompatActivity implements MedicalHistoryContract.View {
    private TextView textViewTitle,textViewLastVisit,textViewFullyImmunization;
    private LinearLayout layoutImmunization;
    private LinearLayout layoutGrowthAndNutrition;
    private RelativeLayout layoutFullyImmunizationBarAge1,layoutFullyImmunizationBarAge2;
    private RecyclerView recyclerViewImmunization,recyclerViewGrowthNutrition;
    private Map<String, Date> vaccineList;
    private String childBaseId,name,lastVisitDays,dateOfBirth;
    private MedicalHistoryContract.Presenter presenter;
    private VaccineAdapter vaccineAdapter;
    private GrowthAdapter growthAdapter;

    public static void startMedicalHistoryActivity(Activity activity, String childBaseEntityId, String childName, String lastVisitDays, String dateOfirth,
                                                   LinkedHashMap<String, Date> receivedVaccine){
        Intent intent=new Intent(activity,MedicalHistoryActivity.class);
        intent.putExtra(Constants.INTENT_KEY.CHILD_BASE_ID,childBaseEntityId);
        intent.putExtra(Constants.INTENT_KEY.CHILD_NAME,childName);
        intent.putExtra(Constants.INTENT_KEY.CHILD_DATE_OF_BIRTH,dateOfirth);
        intent.putExtra(Constants.INTENT_KEY.CHILD_LAST_VISIT_DAYS,lastVisitDays);
        intent.putExtra(Constants.INTENT_KEY.CHILD_VACCINE_LIST,receivedVaccine);

        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_history);
        setUpActionBar();
        textViewLastVisit=findViewById(R.id.home_visit_date);
        layoutImmunization=findViewById(R.id.immunization_bar);
        layoutFullyImmunizationBarAge1=findViewById(R.id.immu_bar_age_1);
        layoutFullyImmunizationBarAge2=findViewById(R.id.immu_bar_age_2);
        textViewFullyImmunization=findViewById(R.id.fully_immunized);
        recyclerViewImmunization=findViewById(R.id.immunization_recycler_view);
        recyclerViewGrowthNutrition=findViewById(R.id.recycler_view_growth);
        recyclerViewImmunization.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewGrowthNutrition.setLayoutManager(new LinearLayoutManager(this));
        layoutGrowthAndNutrition=findViewById(R.id.growth_and_nutrition_list);
        parseBundleANdUpdateView();
    }

    private void setUpActionBar(){
        Toolbar toolbar = findViewById(R.id.collapsing_toolbar);
        textViewTitle = toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            final Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp);
            upArrow.setColorFilter(getResources().getColor(R.color.text_blue), PorterDuff.Mode.SRC_ATOP);
            actionBar.setHomeAsUpIndicator(upArrow);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
    private void parseBundleANdUpdateView(){
        childBaseId=getIntent().getStringExtra(Constants.INTENT_KEY.CHILD_BASE_ID);
        name=getIntent().getStringExtra(Constants.INTENT_KEY.CHILD_NAME);
        lastVisitDays=getIntent().getStringExtra(Constants.INTENT_KEY.CHILD_LAST_VISIT_DAYS);
        dateOfBirth=getIntent().getStringExtra(Constants.INTENT_KEY.CHILD_DATE_OF_BIRTH);
        vaccineList=(Map<String, Date>)getIntent().getSerializableExtra(Constants.INTENT_KEY.CHILD_VACCINE_LIST);
        if(TextUtils.isEmpty(name)){
            textViewTitle.setVisibility(View.GONE);
        }else{
            textViewTitle.setText(getString(R.string.medical_title,name));
        }
        textViewLastVisit.setText(getString(R.string.medical_last_visit,lastVisitDays));
        initializePresenter();
        setInitialVaccineList();
        fetchFullYImmunization();
        fetchGrowthNutrition();
    }


    private void fetchFullYImmunization(){
        presenter.fetchFullyImmunization(dateOfBirth);
    }
    private void setInitialVaccineList(){
        presenter.setInitialVaccineList(vaccineList);

    }
    private void fetchGrowthNutrition(){
        presenter.fetchGrowthNutrition(childBaseId);

    }

    @Override
    public void updateFullyImmunization(String text) {
        if(text.equalsIgnoreCase("2")){
            layoutFullyImmunizationBarAge1.setVisibility(View.VISIBLE);
            layoutFullyImmunizationBarAge2.setVisibility(View.VISIBLE);
        }else if(text.equalsIgnoreCase("1")){
            layoutFullyImmunizationBarAge1.setVisibility(View.VISIBLE);
            layoutFullyImmunizationBarAge2.setVisibility(View.GONE);
        }else{
            layoutFullyImmunizationBarAge1.setVisibility(View.GONE);
            layoutFullyImmunizationBarAge2.setVisibility(View.GONE);
        }
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
    public MedicalHistoryContract.Presenter initializePresenter() {
        presenter=new MedicalHistoryPresenter(this);
        return presenter;
    }
}
