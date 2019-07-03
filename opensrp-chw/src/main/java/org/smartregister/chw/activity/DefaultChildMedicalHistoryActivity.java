package org.smartregister.chw.activity;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import org.smartregister.chw.R;
import org.smartregister.chw.adapter.BirthAndIllnessAdapter;
import org.smartregister.chw.adapter.GrowthAdapter;
import org.smartregister.chw.adapter.VaccineAdapter;
import org.smartregister.chw.contract.ChildMedicalHistoryContract;
import org.smartregister.chw.presenter.ChildMedicalHistoryPresenter;
import org.smartregister.chw.util.ChildDBConstants;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import java.util.Date;
import java.util.Map;

import static org.smartregister.util.Utils.getValue;

public abstract class DefaultChildMedicalHistoryActivity implements ChildMedicalHistoryContract.View {
    private static final String TAG = DefaultChildMedicalHistoryActivity.class.getCanonicalName();

    private LinearLayout layoutImmunization, layoutGrowthAndNutrition, layoutBirthCert, layoutIllness,layoutVaccineCard;
    private RelativeLayout layoutFullyImmunizationBarAge1, layoutFullyImmunizationBarAge2;
    private RecyclerView recyclerViewImmunization, recyclerViewGrowthNutrition, recyclerViewBirthCert, recyclerViewIllness;
    private TextView textViewVaccineCardText;

    private ChildMedicalHistoryContract.Presenter presenter;
    private VaccineAdapter vaccineAdapter;
    private GrowthAdapter growthAdapter;
    private BirthAndIllnessAdapter birthCertAdapter, illnessAdapter;
    private Activity activity;

    protected void onViewUpdated(Activity activity) {
        this.activity = activity;
        layoutImmunization = activity.findViewById(R.id.immunization_bar);
        layoutFullyImmunizationBarAge1 = activity.findViewById(R.id.immu_bar_age_1);
        layoutFullyImmunizationBarAge2 = activity.findViewById(R.id.immu_bar_age_2);
        layoutBirthCert = activity.findViewById(R.id.birth_cert_list);
        layoutIllness = activity.findViewById(R.id.illness_list);
        layoutVaccineCard = activity.findViewById(R.id.vaccine_card_list);
        textViewVaccineCardText = activity.findViewById(R.id.vaccine_card_text);
        recyclerViewImmunization = activity.findViewById(R.id.immunization_recycler_view);
        recyclerViewGrowthNutrition = activity.findViewById(R.id.recycler_view_growth);
        recyclerViewBirthCert = activity.findViewById(R.id.recycler_view_birth);
        recyclerViewIllness = activity.findViewById(R.id.recycler_view_illness);
        recyclerViewImmunization.setLayoutManager(new LinearLayoutManager(activity));
        recyclerViewGrowthNutrition.setLayoutManager(new LinearLayoutManager(activity));
        recyclerViewIllness.setLayoutManager(new LinearLayoutManager(activity));
        recyclerViewBirthCert.setLayoutManager(new LinearLayoutManager(activity));
        layoutGrowthAndNutrition = activity.findViewById(R.id.growth_and_nutrition_list);
        initializePresenter();
    }

    public void fetchFullYImmunization(String dateOfBirth) {
        presenter.fetchFullyImmunization(dateOfBirth);
    }

    public void generateHomeVisitServiceList(CommonPersonObjectClient childClient) {
        String lastHomeVisitStr = getValue(childClient, ChildDBConstants.KEY.LAST_HOME_VISIT, false);
        long lastHomeVisit= TextUtils.isEmpty(lastHomeVisitStr)?0:Long.parseLong(lastHomeVisitStr);
        presenter.generateHomeVisitServiceList(lastHomeVisit);
    }

    public void setInitialVaccineList(Map<String, Date> vaccineList) {
        presenter.setInitialVaccineList(vaccineList);

    }

    public void fetchGrowthNutrition(CommonPersonObjectClient childClient) {
        presenter.fetchGrowthNutrition(childClient);

    }

    public void fetchBirthCertificateData(CommonPersonObjectClient childClient) {
        presenter.fetchBirthData(childClient);
    }

    public void fetchIllnessData(CommonPersonObjectClient childClient) {
        presenter.fetchIllnessData(childClient);
    }

    public void fetchDietaryData(CommonPersonObjectClient childClient){
        presenter.fetchDietaryData(childClient);
    }

    public void fetchMuacData(CommonPersonObjectClient childClient){
        presenter.fetchMuacData(childClient);
    }

    public void fetchLlitnData(CommonPersonObjectClient childClient){
        presenter.fetchLLitnData(childClient);
    }

    public void fetchEcdData(CommonPersonObjectClient childClient){
        presenter.fetchEcdData(childClient);
    }
    @Override
    public void updateFullyImmunization(String text) {
        if (text.equalsIgnoreCase("2")) {
            layoutFullyImmunizationBarAge1.setVisibility(View.VISIBLE);
            layoutFullyImmunizationBarAge2.setVisibility(View.VISIBLE);
        } else if (text.equalsIgnoreCase("1")) {
            layoutFullyImmunizationBarAge1.setVisibility(View.VISIBLE);
            layoutFullyImmunizationBarAge2.setVisibility(View.GONE);
        } else {
            layoutFullyImmunizationBarAge1.setVisibility(View.GONE);
            layoutFullyImmunizationBarAge2.setVisibility(View.GONE);
        }
    }

    @Override
    public void updateVaccinationData() {
        if (presenter.getVaccineBaseItem() != null && presenter.getVaccineBaseItem().size() > 0) {
            layoutImmunization.setVisibility(View.VISIBLE);
            if (vaccineAdapter == null) {
                vaccineAdapter = new VaccineAdapter();
                vaccineAdapter.addItem(presenter.getVaccineBaseItem());
                recyclerViewImmunization.setAdapter(vaccineAdapter);
            } else {
                vaccineAdapter.notifyDataSetChanged();
            }
        } else {
            layoutImmunization.setVisibility(View.GONE);
        }


    }

    @Override
    public void updateGrowthNutrition() {
        if (presenter.getGrowthNutrition() != null && presenter.getGrowthNutrition().size() > 0) {
            layoutGrowthAndNutrition.setVisibility(View.VISIBLE);
            if (growthAdapter == null) {
                growthAdapter = new GrowthAdapter();
                growthAdapter.addItem(presenter.getGrowthNutrition());
                recyclerViewGrowthNutrition.setAdapter(growthAdapter);
            } else {
                growthAdapter.notifyDataSetChanged();
            }
        } else {
            layoutGrowthAndNutrition.setVisibility(View.GONE);
        }

    }

    @Override
    public void updateBirthCertification() {
        if (presenter.getBirthCertification() != null && presenter.getBirthCertification().size() > 0) {
            layoutBirthCert.setVisibility(View.VISIBLE);
            if (birthCertAdapter == null) {
                birthCertAdapter = new BirthAndIllnessAdapter();
                birthCertAdapter.setData(presenter.getBirthCertification());
                recyclerViewBirthCert.setAdapter(birthCertAdapter);
            } else {
                birthCertAdapter.notifyDataSetChanged();
            }
        } else {
            layoutBirthCert.setVisibility(View.GONE);
        }

    }

    @Override
    public void updateObsIllness() {
        if (presenter.getObsIllness() != null && presenter.getObsIllness().size() > 0) {
            layoutIllness.setVisibility(View.VISIBLE);
            if (illnessAdapter == null) {
                illnessAdapter = new BirthAndIllnessAdapter();
                illnessAdapter.setData(presenter.getObsIllness());
                recyclerViewIllness.setAdapter(illnessAdapter);
            } else {
                illnessAdapter.notifyDataSetChanged();
            }
        } else {
            layoutIllness.setVisibility(View.GONE);
        }
    }

    @Override
    public void updateDietaryData() {
        if (presenter.getDietaryList() != null && presenter.getDietaryList().size() > 0) {
            layoutGrowthAndNutrition.setVisibility(View.VISIBLE);
            if (growthAdapter == null) {
                growthAdapter = new GrowthAdapter();
                growthAdapter.addItem(presenter.getDietaryList());
                recyclerViewGrowthNutrition.setAdapter(growthAdapter);
            } else {
                growthAdapter.addItem(presenter.getDietaryList());
                growthAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void updateMuacData() {

        if (presenter.getMuacList() != null && presenter.getMuacList().size() > 0) {
            layoutGrowthAndNutrition.setVisibility(View.VISIBLE);
            if (growthAdapter == null) {
                growthAdapter = new GrowthAdapter();
                growthAdapter.addItem(presenter.getMuacList());
                recyclerViewGrowthNutrition.setAdapter(growthAdapter);
            } else {
                growthAdapter.addItem(presenter.getMuacList());
                growthAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void updateLLitnData() {

    }

    @Override
    public void updateEcdData() {

    }

    @Override
    public void updateVaccineCard(String value) {
        layoutVaccineCard.setVisibility(View.VISIBLE);
        textViewVaccineCardText.setText(String.format("%s %s",activity.getString(R.string.vaccine_card_text),value));
    }

    @Override
    public ChildMedicalHistoryContract.Presenter initializePresenter() {
        presenter = new ChildMedicalHistoryPresenter(this);
        return presenter;
    }
}
