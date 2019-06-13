package org.smartregister.chw.provider;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.smartregister.chw.R;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.cursoradapter.RecyclerViewProvider;
import org.smartregister.util.Utils;
import org.smartregister.view.contract.SmartRegisterClient;

import java.util.Set;

public class MalariaRegisterProvider extends org.smartregister.chw.malaria.provider.MalariaRegisterProvider implements RecyclerViewProvider<org.smartregister.chw.malaria.provider.MalariaRegisterProvider.RegisterViewHolder> {
    public static final String CLICK_VIEW_NORMAL = "click_view_normal";
    public static final String CLICK_VIEW_DOSAGE_STATUS = "click_view_dosage_status";

    private LayoutInflater inflater;
    private View.OnClickListener onClickListener;
    private View.OnClickListener paginationClickListener;
    private Set<org.smartregister.configurableviews.model.View> visibleColumns;
    private CommonRepository commonRepository;
    private Context context;


    public MalariaRegisterProvider(Context context, View.OnClickListener paginationClickListener, Set visibleColumns, CommonRepository commonRepository, LayoutInflater inflater) {
        super(context, paginationClickListener, visibleColumns, commonRepository);
    }

    public MalariaRegisterProvider(Context context, View.OnClickListener paginationClickListener, View.OnClickListener onClickListener, Set visibleColumns, CommonRepository commonRepository) {
        super(context, paginationClickListener, visibleColumns, commonRepository);
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.paginationClickListener = paginationClickListener;
        this.onClickListener = onClickListener;
        this.visibleColumns = visibleColumns;
        this.context = context;
        this.commonRepository = commonRepository;
    }


    @Override
    public void getView(Cursor cursor, SmartRegisterClient smartRegisterClient, org.smartregister.chw.malaria.provider.MalariaRegisterProvider.RegisterViewHolder registerViewHolder) {
        CommonPersonObjectClient pc = (CommonPersonObjectClient) smartRegisterClient;
        if (this.visibleColumns.isEmpty()) {
            this.populatePatientColumn(pc, smartRegisterClient, registerViewHolder);
            this.populateLastColumn(pc, registerViewHolder);
        }
    }


    private void populatePatientColumn(CommonPersonObjectClient pc, SmartRegisterClient smartRegisterClient, org.smartregister.chw.malaria.provider.MalariaRegisterProvider.RegisterViewHolder viewHolder) {
        String fname = Utils.getName(Utils.getValue(pc.getColumnmaps(), "first_name", true), Utils.getValue(pc.getColumnmaps(), "middle_name", true));
        String dobString = Utils.getValue(pc.getColumnmaps(), "dob", false);
        int age = (new Period(new DateTime(dobString), new DateTime())).getYears();
        String patientName = Utils.getName(fname, Utils.getValue(pc.getColumnmaps(), "last_name", true));
        viewHolder.patientName.setText(patientName + ", " + age);
        viewHolder.textViewGender.setText(Utils.getValue(pc.getColumnmaps(), "gender", true));
        viewHolder.textViewVillage.setText(Utils.getValue(pc.getColumnmaps(), "village_town", true));

        View patient = viewHolder.patientColumn;
        attachPatientOnclickListener(patient, smartRegisterClient);
    }

    private void populateLastColumn(CommonPersonObjectClient pc, org.smartregister.chw.malaria.provider.MalariaRegisterProvider.RegisterViewHolder viewHolder) {
        if (this.commonRepository != null) {
            CommonPersonObject commonPersonObject = this.commonRepository.findByBaseEntityId(pc.entityId());
            if (commonPersonObject != null) {
//                viewHolder.dueButton.setVisibility(0);
//                viewHolder.dueButton.setText(org.smartregister.malaria.R.string.malaria_followup_visit);
//                viewHolder.dueButton.setAllCaps(true);
            } else {
//                viewHolder.dueButton.setVisibility(8);
            }
        }

    }

    private void attachPatientOnclickListener(View view, SmartRegisterClient client) {
        view.setOnClickListener(onClickListener);
        view.setTag(client);
        view.setTag(R.id.VIEW_ID, CLICK_VIEW_NORMAL);
    }
}
