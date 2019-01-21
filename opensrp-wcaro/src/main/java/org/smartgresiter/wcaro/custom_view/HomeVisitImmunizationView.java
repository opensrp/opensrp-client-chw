package org.smartgresiter.wcaro.custom_view;

import android.app.FragmentManager;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.smartgresiter.wcaro.R;
import org.smartgresiter.wcaro.contract.HomeVisitGrowthNutritionContract;
import org.smartgresiter.wcaro.contract.HomeVisitImmunizationContract;
import org.smartgresiter.wcaro.fragment.ChildHomeVisitFragment;
import org.smartgresiter.wcaro.fragment.GrowthNutritionInputFragment;
import org.smartgresiter.wcaro.presenter.HomeVisitGrowthNutritionPresenter;
import org.smartgresiter.wcaro.presenter.HomeVisitImmunizationPresenter;
import org.smartgresiter.wcaro.util.ChildUtils;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.Alert;
import org.smartregister.immunization.domain.ServiceWrapper;
import org.smartregister.immunization.domain.Vaccine;
import org.smartregister.immunization.domain.VaccineWrapper;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeVisitImmunizationView extends LinearLayout implements View.OnClickListener, HomeVisitImmunizationContract.View {
    public static final String TAG = "HomeVisitImmunization";
   private HomeVisitImmunizationContract.Presenter presenter;
    private CommonPersonObjectClient commonPersonObjectClient;
    private FragmentManager fragmentManager;
    private ChildHomeVisitFragment childHomeVisitFragment;
    private TextView textview_group_immunization_primary_text;
    private TextView textview_group_immunization_secondary_text;
    private TextView textview_immunization_primary_text;
    private TextView textview_immunization_secondary_text;
    private CircleImageView immunization_status_circle;
    private CircleImageView immunization_group_status_circle;
    private LinearLayout multiple_immunization_group;
    private LinearLayout single_immunization_group;


    public HomeVisitImmunizationView(Context context) {
        super(context);
        initUi();
    }

    public HomeVisitImmunizationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initUi();
    }

    public HomeVisitImmunizationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initUi();
    }

    private void initUi() {
        inflate(getContext(), R.layout.view_immunization, this);
        textview_group_immunization_primary_text = (TextView) findViewById(R.id.textview_group_immunization);
        textview_group_immunization_secondary_text = (TextView) findViewById(R.id.textview_immunization_group_secondary_text);
        textview_immunization_primary_text = (TextView) findViewById(R.id.textview_immunization);
        textview_immunization_secondary_text = (TextView) findViewById(R.id.textview_immunization_secondary_text);
        immunization_status_circle = ((CircleImageView) findViewById(R.id.immunization_status_circle));
        immunization_group_status_circle = ((CircleImageView)findViewById(R.id.immunization_group_status_circle));
        single_immunization_group = ((LinearLayout)findViewById(R.id.immunization_name_group));
        multiple_immunization_group = ((LinearLayout) findViewById(R.id.immunization_group));
        initializePresenter();

    }

    public void refreshPresenter(List<Alert> alerts, List<Vaccine> vaccines) {
        presenter.createAllVaccineGroups(alerts,vaccines);
        presenter.getVaccinesNotGivenLastVisit();
        presenter.calculateCurrentActiveGroup();
        if(presenter.isPartiallyComplete()){
            textview_group_immunization_primary_text.setText("Immunizations" + "(" + presenter.getCurrentActiveGroup().getGroup() + ")");
            immunization_group_status_circle.setImageResource(R.drawable.ic_checked);
            immunization_group_status_circle.setColorFilter(getResources().getColor(R.color.white));
            immunization_group_status_circle.setCircleBackgroundColor(getResources().getColor(R.color.pnc_circle_yellow));
            multiple_immunization_group.setOnClickListener(null);
        }else if(presenter.isComplete()){
            textview_group_immunization_primary_text.setText("Immunizations" + "(" + presenter.getCurrentActiveGroup().getGroup() + ")");
            immunization_group_status_circle.setImageResource(R.drawable.ic_checked);
            immunization_group_status_circle.setColorFilter(getResources().getColor(R.color.white));
            immunization_group_status_circle.setCircleBackgroundColor(getResources().getColor(R.color.alert_complete_green));
            multiple_immunization_group.setOnClickListener(null);
        }else if (presenter.groupIsDue()){
            textview_group_immunization_primary_text.setText("Immunizations" + "(" + presenter.getCurrentActiveGroup().getGroup() + ")");
            multiple_immunization_group.setTag(R.id.nextduevaccinelist, presenter.getCurrentActiveGroup());
            multiple_immunization_group.setTag(R.id.vaccinelist, vaccines);
            multiple_immunization_group.setOnClickListener(this);
        }
        if(presenter.getVaccinesDueFromLastVisit().size()>0){
            String vaccinesDueLastVisit = "";
            for(int i = 0;i<presenter.getVaccinesDueFromLastVisit().size();i++){
                vaccinesDueLastVisit = vaccinesDueLastVisit+presenter.getVaccinesDueFromLastVisit().get(i).display().toUpperCase()+",";
            }
            if(vaccinesDueLastVisit.endsWith(",")){
                vaccinesDueLastVisit = vaccinesDueLastVisit.substring(0,vaccinesDueLastVisit.length()-1);
            }
            textview_immunization_primary_text.setText(vaccinesDueLastVisit);
        }else{
            single_immunization_group.setVisibility(View.GONE);
        }
    }


    @Override
    public void onClick(View v) {

    }

    @Override
    public HomeVisitImmunizationContract.Presenter initializePresenter() {
        presenter = new HomeVisitImmunizationPresenter(this);
        return presenter;
    }
}
