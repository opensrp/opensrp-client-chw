package org.smartregister.chw.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.smartregister.chw.R;
import org.smartregister.chw.listener.OnClickEditAdapter;
import org.smartregister.chw.presenter.ImmunizationViewPresenter;
import org.smartregister.chw.util.HomeVisitVaccineGroup;
import org.smartregister.chw.util.ImmunizationState;
import org.smartregister.immunization.db.VaccineRepo;
import org.smartregister.util.DateUtil;
import org.smartregister.util.Log;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static org.smartregister.chw.util.ChildUtils.fixVaccineCasing;

public class ImmunizationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ImmunizationViewPresenter presenter;
    private Context context;
    private OnClickEditAdapter onClickEditAdapter;

    public ImmunizationAdapter(Context context, OnClickEditAdapter onClickEditAdapter, ImmunizationViewPresenter presenter) {
        this.presenter = presenter;
        this.context = context;
        this.onClickEditAdapter = onClickEditAdapter;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        switch (viewType) {
            case HomeVisitVaccineGroup.TYPE_INITIAL:
                return new InitialViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_immunization_inactive, null));
            case HomeVisitVaccineGroup.TYPE_INACTIVE:
                return new InactiveViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_immunization_inactive, null));
            case HomeVisitVaccineGroup.TYPE_ACTIVE:
                return new ContentViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_immunization_active, null));
            case HomeVisitVaccineGroup.TYPE_HIDDEN:
                return new HiddenViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_immunization_hidden, null));
            default:
                return new InitialViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_immunization_inactive, null));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder,int pos) {

        switch (viewHolder.getItemViewType()) {
            case HomeVisitVaccineGroup.TYPE_INITIAL: {
                ArrayList<HomeVisitVaccineGroup> list =  presenter.getHomeVisitVaccineGroupDetails();
                final HomeVisitVaccineGroup baseVaccine = list.get(viewHolder.getAdapterPosition());
                InitialViewHolder inactiveViewHolder = (InitialViewHolder) viewHolder;
                String immunizations;
                String value = baseVaccine.getGroup();
                if (value.contains("birth")) {
                    immunizations = MessageFormat.format(context.getString(R.string.immunizations_count), value);

                } else {
                    immunizations = MessageFormat.format(context.getString(R.string.immunizations_count), value.replace("weeks", "w").replace("months", "m").replace(" ", ""));

                }
                inactiveViewHolder.circleImageView.setCircleBackgroundColor(context.getResources().getColor(R.color.white));
                inactiveViewHolder.circleImageView.setBorderColor(context.getResources().getColor(R.color.dark_grey));
                inactiveViewHolder.titleText.setText(immunizations);
                String message = MessageFormat.format("{0}{1}",
                        ((baseVaccine.getAlert().equals(ImmunizationState.OVERDUE)) ? context.getResources().getString(R.string.overdue) : context.getResources().getString(R.string.due)),
                        baseVaccine.getDueDisplayDate());
                int color_res = ((baseVaccine.getAlert().equals(ImmunizationState.OVERDUE)) ? R.color.alert_urgent_red : R.color.grey);
                inactiveViewHolder.descriptionText.setTextColor(context.getResources().getColor(color_res));
                inactiveViewHolder.descriptionText.setText(message);
                inactiveViewHolder.getView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onClickEditAdapter.onClick(viewHolder.getAdapterPosition(), baseVaccine);
                    }
                });
            }
            break;
            case HomeVisitVaccineGroup.TYPE_INACTIVE:
                HomeVisitVaccineGroup baseVaccine = presenter.getHomeVisitVaccineGroupDetails().get(viewHolder.getAdapterPosition());
                InactiveViewHolder inactiveViewHolder = (InactiveViewHolder) viewHolder;
                String immunizations;
                String value = baseVaccine.getGroup();
                if (value.contains("birth")) {
                    immunizations = MessageFormat.format(context.getString(R.string.immunizations_count), value);

                } else {
                    immunizations = MessageFormat.format(context.getString(R.string.immunizations_count), value.replace("weeks", "w").replace("months", "m").replace(" ", ""));

                }
                inactiveViewHolder.titleText.setText(immunizations);
                inactiveViewHolder.descriptionText.setText(Html.fromHtml(context.getString(R.string.fill_earler_immunization)));
                inactiveViewHolder.getView().setOnClickListener(null);
                break;
            case HomeVisitVaccineGroup.TYPE_ACTIVE:
                final HomeVisitVaccineGroup contentImmunization = presenter.getHomeVisitVaccineGroupDetails().get(viewHolder.getAdapterPosition());
                ContentViewHolder contentViewHolder = (ContentViewHolder) viewHolder;
                String cImmunization;
                String cValue = contentImmunization.getGroup();
                if (cValue.contains("birth")) {
                    cImmunization = MessageFormat.format(context.getString(R.string.immunizations_count), cValue);

                } else {
                    cImmunization = MessageFormat.format(context.getString(R.string.immunizations_count), cValue.replace("weeks", "w").replace("months", "m").replace(" ", ""));

                }
                contentViewHolder.titleText.setText(cImmunization);
                contentViewHolder.descriptionText.setTextColor(context.getResources().getColor(R.color.grey));
                contentViewHolder.descriptionText.setText(getVaccineWithDateText(contentImmunization));
                contentViewHolder.circleImageView.setImageResource(R.drawable.ic_checked);
                contentViewHolder.circleImageView.setColorFilter(context.getResources().getColor(R.color.white));

                int color_res = isComplete(contentImmunization) ? R.color.alert_complete_green : R.color.pnc_circle_yellow;

                contentViewHolder.circleImageView.setCircleBackgroundColor(context.getResources().getColor(color_res));
                contentViewHolder.circleImageView.setBorderColor(context.getResources().getColor(color_res));
                contentViewHolder.getView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onClickEditAdapter.onClick(viewHolder.getAdapterPosition(), contentImmunization);
                    }
                });

                break;
            case HomeVisitVaccineGroup.TYPE_HIDDEN:
                Log.logError("HIDDEN_VIEW","HomeVisitVaccineGroup.TYPE_HIDDEN");
                HiddenViewHolder hiddenViewHolder = (HiddenViewHolder) viewHolder;
                hiddenViewHolder.hiddenView.setVisibility(View.GONE);
                break;
            default:
                break;
        }

    }

    /**
     * need to display like this opv1,penta1 provided at date or opv1 provided at 20-03-2019 or pcv1,rota1 not given'
     * it'll iterate on dueVaccines with date map.and showing only given vaccines name with date.
     *
     * @param contentImmunization
     * @return
     * @link count variable using not to display the text ".given on <date>".as it's iterate on other due
     * vaccines.
     */
    private StringBuilder getVaccineWithDateText(HomeVisitVaccineGroup contentImmunization) {
        StringBuilder groupSecondaryText = new StringBuilder();
        Iterator<Map.Entry<DateTime, ArrayList<VaccineRepo.Vaccine>>> iterator = contentImmunization.getGroupedByDate().entrySet().iterator();
        int count;
        while (iterator.hasNext()) {
            count = 0;
            Map.Entry<DateTime, ArrayList<VaccineRepo.Vaccine>> entry = iterator.next();
            DateTime dueDate = entry.getKey();
            ArrayList<VaccineRepo.Vaccine> vaccines = entry.getValue();
            for (VaccineRepo.Vaccine vaccineGiven : vaccines) {
                if (isExistInGivenVaccine(contentImmunization, vaccineGiven.display())) {
                    groupSecondaryText.append(fixVaccineCasing(vaccineGiven.display())).append(", ");
                    count++;
                }

            }

            if (groupSecondaryText.toString().endsWith(", ")) {
                groupSecondaryText = new StringBuilder(groupSecondaryText.toString().trim());
                groupSecondaryText = new StringBuilder(groupSecondaryText.substring(0, groupSecondaryText.length() - 1));
            }

            if (!TextUtils.isEmpty(groupSecondaryText) && count > 0) {
                groupSecondaryText.append(context.getString(R.string.given_on_with_spaces)).append(DateUtil.formatDate(dueDate.toLocalDate(), "dd MMM yyyy"));
                if (contentImmunization.getNotGivenVaccines().size() > 0 || iterator.hasNext()) {
                    groupSecondaryText.append(" \u00B7 ");
                }
            }

        }
        groupSecondaryText.append(getNotGivenVaccineName(contentImmunization));
        return groupSecondaryText;
    }

    /**
     * This method return the text like as "bcg,op1 not give"
     *
     * @param contentImmunization
     * @return
     */

    private StringBuilder getNotGivenVaccineName(HomeVisitVaccineGroup contentImmunization) {
        StringBuilder groupSecondaryText = new StringBuilder();
        for (VaccineRepo.Vaccine notGiven : contentImmunization.getNotGivenVaccines()) {
           if(isExistInDueVaccine(contentImmunization,notGiven.display())){
               groupSecondaryText.append(fixVaccineCasing(notGiven.display())).append(", ");
           }
        }
        if (groupSecondaryText.toString().endsWith(", ")) {
            groupSecondaryText = new StringBuilder(groupSecondaryText.toString().trim());
            groupSecondaryText = new StringBuilder(groupSecondaryText.substring(0, groupSecondaryText.length() - 1));
        }
        if (!TextUtils.isEmpty(groupSecondaryText))
            groupSecondaryText.append(context.getString(R.string.not_given_with_spaces));
        return groupSecondaryText;
    }

    private boolean isExistInGivenVaccine(HomeVisitVaccineGroup contentImmunization, String name) {
        for (VaccineRepo.Vaccine vaccineGiven : contentImmunization.getGivenVaccines()) {
            if (vaccineGiven.display().equalsIgnoreCase(name)
                    && isExistInDueVaccine(contentImmunization,vaccineGiven.display())
                    && !isExistInNotGiven(contentImmunization,vaccineGiven.display())) {
                return true;
            }
        }
        return false;
    }
    private boolean isExistInDueVaccine(HomeVisitVaccineGroup contentImmunization,String name){
        for(VaccineRepo.Vaccine dueList:contentImmunization.getDueVaccines()){
            if(dueList.display().equalsIgnoreCase(name))
                return true;
        }
        return false;
    }
    private boolean isExistInNotGiven(HomeVisitVaccineGroup contentImmunization,String name){
        for(VaccineRepo.Vaccine dueList:contentImmunization.getNotGivenVaccines()){
            if(dueList.display().equalsIgnoreCase(name))
                return true;
        }
        return false;
    }

    private boolean isComplete(HomeVisitVaccineGroup contentImmunization) {

        return contentImmunization.getNotGivenVaccines().size() == 0;

    }

    @Override
    public int getItemViewType(int position) {

        return presenter.getHomeVisitVaccineGroupDetails().get(position).getViewType();
    }

    @Override
    public int getItemCount() {
        return presenter.getHomeVisitVaccineGroupDetails().size();
    }

    public class InactiveViewHolder extends RecyclerView.ViewHolder {
        public TextView titleText, descriptionText;
        private View myView;

        private InactiveViewHolder(View view) {
            super(view);
            titleText = view.findViewById(R.id.textview_group_immunization);
            descriptionText = view.findViewById(R.id.textview_immunization_group_secondary_text);

            myView = view;
        }

        public View getView() {
            return myView;
        }
    }

    public class InitialViewHolder extends RecyclerView.ViewHolder {
        public TextView titleText, descriptionText;
        public CircleImageView circleImageView;
        private View myView;

        private InitialViewHolder(View view) {
            super(view);
            titleText = view.findViewById(R.id.textview_group_immunization);
            titleText.setTextColor(context.getResources().getColor(R.color.black));
            descriptionText = view.findViewById(R.id.textview_immunization_group_secondary_text);
            circleImageView = view.findViewById(R.id.immunization_group_status_circle);
            myView = view;
        }

        public View getView() {
            return myView;
        }
    }

    public class ContentViewHolder extends RecyclerView.ViewHolder {
        public TextView titleText, descriptionText;
        public CircleImageView circleImageView;
        private View myView;

        private ContentViewHolder(View view) {
            super(view);
            titleText = view.findViewById(R.id.textview_group_immunization);
            descriptionText = view.findViewById(R.id.textview_immunization_group_secondary_text);
            circleImageView = view.findViewById(R.id.immunization_group_status_circle);
            myView = view;
        }

        public View getView() {
            return myView;
        }
    }
    public class HiddenViewHolder extends RecyclerView.ViewHolder {
        private View myView;
        public LinearLayout hiddenView;
        private HiddenViewHolder(View view) {
            super(view);
            hiddenView = view.findViewById(R.id.parent_view);
            myView = view;
        }

        public View getView() {
            return myView;
        }
    }
}
