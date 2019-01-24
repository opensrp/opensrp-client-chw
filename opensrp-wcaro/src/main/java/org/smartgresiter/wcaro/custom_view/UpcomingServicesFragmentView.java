package org.smartgresiter.wcaro.custom_view;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.smartgresiter.wcaro.R;
import org.smartgresiter.wcaro.contract.HomeVisitImmunizationContract;
import org.smartgresiter.wcaro.fragment.ChildHomeVisitFragment;
import org.smartgresiter.wcaro.fragment.ChildImmunizationFragment;
import org.smartgresiter.wcaro.fragment.CustomMultipleVaccinationDialogFragment;
import org.smartgresiter.wcaro.fragment.CustomVaccinationDialogFragment;
import org.smartgresiter.wcaro.presenter.HomeVisitImmunizationPresenter;
import org.smartgresiter.wcaro.util.HomeVisitVaccineGroupDetails;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.Alert;
import org.smartregister.immunization.db.VaccineRepo;
import org.smartregister.immunization.domain.Vaccine;
import org.smartregister.immunization.domain.VaccineWrapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class UpcomingServicesFragmentView extends LinearLayout implements View.OnClickListener, HomeVisitImmunizationContract.View{


    public UpcomingServicesFragmentView(Context context) {
        super(context);
    }

    public UpcomingServicesFragmentView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public UpcomingServicesFragmentView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setActivity(Activity activity) {

    }

    @Override
    public void setChildClient(CommonPersonObjectClient childClient) {

    }

    @Override
    public void refreshPresenter(List<Alert> alerts, List<Vaccine> vaccines, List<Map<String, Object>> sch) {

    }

    @Override
    public void undoVaccines() {

    }

    @Override
    public HomeVisitImmunizationContract.Presenter initializePresenter() {
        return null;
    }

    @Override
    public HomeVisitImmunizationContract.Presenter getPresenter() {
        return null;
    }

    @Override
    public void updateImmunizationState() {

    }

    @Override
    public void immunizationState(List<Alert> alerts, List<Vaccine> vaccines, List<Map<String, Object>> sch) {

    }

    @Override
    public void onClick(View v) {

    }
}
