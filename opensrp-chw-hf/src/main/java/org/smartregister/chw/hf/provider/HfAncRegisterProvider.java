package org.smartregister.chw.hf.provider;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.joda.time.Years;
import org.smartregister.chw.anc.fragment.BaseAncRegisterFragment;
import org.smartregister.chw.anc.provider.AncRegisterProvider;
import org.smartregister.chw.anc.util.DBConstants;
import org.smartregister.chw.anc.util.NCUtils;
import org.smartregister.chw.core.provider.ChwAncRegisterProvider;
import org.smartregister.chw.hf.R;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.util.Utils;
import org.smartregister.view.contract.SmartRegisterClient;

import java.text.MessageFormat;
import java.util.Set;

public class HfAncRegisterProvider extends ChwAncRegisterProvider {
    private final LayoutInflater inflater;
    private View.OnClickListener onClickListener;
    private Context context;

    public HfAncRegisterProvider(Context context, CommonRepository commonRepository, Set visibleColumns, View.OnClickListener onClickListener, View.OnClickListener paginationClickListener) {
        super(context, commonRepository, visibleColumns, onClickListener, paginationClickListener);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.onClickListener = onClickListener;
        this.context = context;
    }

    @Override
    protected void populatePatientColumn(@NotNull CommonPersonObjectClient pc, SmartRegisterClient client, @NotNull final AncRegisterProvider.RegisterViewHolder viewHolder) {
        String fname = Utils.getName(
                Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.FIRST_NAME, true),
                Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.MIDDLE_NAME, true)
        );

        String patientName = Utils.getName(fname, Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.LAST_NAME, true));

        // calculate LMP
        String dobString = Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.DOB, false);
        String lmpString = Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.LAST_MENSTRUAL_PERIOD, false);
        if (StringUtils.isNotBlank(dobString) && StringUtils.isNotBlank(lmpString)) {
            int age = Years.yearsBetween(new DateTime(dobString), new DateTime()).getYears();

            String gaLocation = MessageFormat.format("{0}: {1} {2} {3} {4}",
                    context.getString(R.string.gestation_age_initial),
                    NCUtils.gestationAgeString(lmpString, context, false),
                    context.getString(R.string.abbrv_weeks),
                    context.getString(R.string.interpunct),
                    Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.VILLAGE_TOWN, true));

            String patientNameAge = MessageFormat.format("{0}, {1}", patientName, age);
            viewHolder.patientName.setText(patientNameAge);
            viewHolder.patientAge.setText(gaLocation);
        }

        // add patient listener
        viewHolder.patientColumn.setOnClickListener(onClickListener);
        viewHolder.patientColumn.setTag(client);
        viewHolder.patientColumn.setTag(R.id.VIEW_ID, BaseAncRegisterFragment.CLICK_VIEW_NORMAL);


        // add due listener
        viewHolder.dueButton.setOnClickListener(onClickListener);
        viewHolder.dueButton.setTag(client);
        viewHolder.dueButton.setTag(R.id.VIEW_ID, BaseAncRegisterFragment.CLICK_VIEW_DOSAGE_STATUS);

        viewHolder.registerColumns.setOnClickListener(v -> viewHolder.patientColumn.performClick());
        viewHolder.dueWrapper.setOnClickListener(v -> viewHolder.dueButton.performClick());
    }

    @Override
    public AncRegisterProvider.RegisterViewHolder createViewHolder(ViewGroup parent) {
        View view = inflater.inflate(R.layout.anc_register_list_row, parent, false);
        return new AncRegisterProvider.RegisterViewHolder(view);
    }
}
