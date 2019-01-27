package org.smartgresiter.wcaro.custom_view;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.smartgresiter.wcaro.R;
import org.smartgresiter.wcaro.contract.HomeVisitImmunizationContract;
import org.smartgresiter.wcaro.presenter.HomeVisitImmunizationPresenter;
import org.smartgresiter.wcaro.util.HomeVisitVaccineGroupDetails;
import org.smartgresiter.wcaro.util.ImmunizationState;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.Alert;
import org.smartregister.immunization.db.VaccineRepo;
import org.smartregister.immunization.domain.Vaccine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class UpcomingServicesFragmentView extends LinearLayout implements View.OnClickListener, HomeVisitImmunizationContract.View {


    private HomeVisitImmunizationPresenter presenter;
    private Activity context;
    private ArrayList<HomeVisitVaccineGroupDetails> homeVisitVaccineGroupDetailsList;

    public UpcomingServicesFragmentView(Context context) {
        super(context);
    }

    public UpcomingServicesFragmentView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initUi();

    }

    public UpcomingServicesFragmentView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initUi();

    }

    private void initUi() {
        inflate(getContext(), R.layout.view_upcoming_service, this);
        setOrientation(VERTICAL);
        initializePresenter();

    }

    @Override
    public void setActivity(Activity activity) {

        this.context = activity;
    }


    @Override
    public void setChildClient(CommonPersonObjectClient childClient) {
        presenter.setChildClient(childClient);
    }

    @Override
    public void refreshPresenter(List<Alert> alerts, List<Vaccine> vaccines, List<Map<String, Object>> sch) {
        presenter.createAllVaccineGroups(alerts, vaccines, sch);
        presenter.getVaccinesNotGivenLastVisit();
        presenter.calculateCurrentActiveGroup();
        removeAllViews();
        homeVisitVaccineGroupDetailsList = presenter.getAllgroups();
        for (HomeVisitVaccineGroupDetails homeVisitVaccineGroupDetail : homeVisitVaccineGroupDetailsList) {
            if (homeVisitVaccineGroupDetail.getAlert().equals(ImmunizationState.DUE) || homeVisitVaccineGroupDetail.getAlert().equals(ImmunizationState.OVERDUE)) {
                if (homeVisitVaccineGroupDetail.getNotGivenVaccines().size() > 0) {
                    addView(createUpcomingServicesCard(homeVisitVaccineGroupDetail));
                }
            }
        }
    }

    private View createUpcomingServicesCard(HomeVisitVaccineGroupDetails homeVisitVaccineGroupDetail) {
        View view = context.getLayoutInflater().inflate(R.layout.upcoming_service_row, null);
        TextView groupDateTitle = (TextView) view.findViewById(R.id.grou_date_title);
        TextView groupNameTitle = (TextView) view.findViewById(R.id.grou_name_title);
        TextView groupVaccineTitle = (TextView) view.findViewById(R.id.grou_vaccines_title);
        groupVaccineTitle.setText("");

        groupDateTitle.setText(homeVisitVaccineGroupDetail.getDueDate());
        groupNameTitle.setText("Immunizations (" + homeVisitVaccineGroupDetail.getGroup() + ")");
        for (VaccineRepo.Vaccine vaccine : homeVisitVaccineGroupDetail.getNotGivenVaccines()) {
            if (isBlank(groupVaccineTitle.getText().toString())) {
                groupVaccineTitle.append(vaccine.display().toUpperCase());
            } else {
                groupVaccineTitle.append("\n" + vaccine.display().toUpperCase());
            }
        }

        return view;
    }

    @Override
    public void undoVaccines() {

    }

    @Override
    public HomeVisitImmunizationContract.Presenter initializePresenter() {
        presenter = new HomeVisitImmunizationPresenter(this);
        return presenter;
    }

    @Override
    public HomeVisitImmunizationContract.Presenter getPresenter() {
        return presenter;
    }

    @Override
    public void updateImmunizationState() {
        presenter.updateImmunizationState(this);
    }

    @Override
    public void immunizationState(List<Alert> alerts, List<Vaccine> vaccines, List<Map<String, Object>> sch) {
        refreshPresenter(alerts, vaccines, sch);

    }


    @Override
    public void onClick(View v) {

    }
}
