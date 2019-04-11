package org.smartregister.chw.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.smartregister.chw.R;
import org.smartregister.chw.util.HomeVisitVaccineGroup;
import org.smartregister.immunization.db.VaccineRepo;
import org.smartregister.util.DateUtil;

import java.text.MessageFormat;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static org.smartregister.chw.util.ChildUtils.fixVaccineCasing;

public class HomeVisitImmunizationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<HomeVisitVaccineGroup> homeVisitVaccineGroupDetailsArrayList;
    private Context context;

    public HomeVisitImmunizationAdapter(Context context) {
        this.homeVisitVaccineGroupDetailsArrayList = new ArrayList<>();
        this.context = context;
    }

    public void addItem(ArrayList<HomeVisitVaccineGroup> homeVisitVaccineGroupDetailsArrayList) {
        this.homeVisitVaccineGroupDetailsArrayList.addAll(homeVisitVaccineGroupDetailsArrayList);

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        switch (viewType) {
            case HomeVisitVaccineGroup.TYPE_INACTIVE:
                return new InactiveViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_immunization_inactive, null));
            case HomeVisitVaccineGroup.TYPE_ACTIVE:
                return new ContentViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_immunization_active, null));

        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

        switch (viewHolder.getItemViewType()) {
            case HomeVisitVaccineGroup.TYPE_INACTIVE:
                HomeVisitVaccineGroup baseVaccine = homeVisitVaccineGroupDetailsArrayList.get(position);
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
                break;
            case  HomeVisitVaccineGroup.TYPE_ACTIVE:
                HomeVisitVaccineGroup contentImmunization = homeVisitVaccineGroupDetailsArrayList.get(position);
                ContentViewHolder contentViewHolder = (ContentViewHolder) viewHolder;
                String cImmunization;
                String cValue = contentImmunization.getGroup();
                if (cValue.contains("birth")) {
                    cImmunization = MessageFormat.format(context.getString(R.string.immunizations_count), cValue);

                } else {
                    cImmunization = MessageFormat.format(context.getString(R.string.immunizations_count), cValue.replace("weeks", "w").replace("months", "m").replace(" ", ""));

                }
                contentViewHolder.titleText.setText(cImmunization);
                contentViewHolder.descriptionText.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
                contentViewHolder.descriptionText.setText(getVaccineNames(contentImmunization));
                contentViewHolder.circleImageView.setImageResource(R.drawable.ic_checked);
                contentViewHolder.circleImageView.setColorFilter(context.getResources().getColor(R.color.white));

                int color_res = isComplete(contentImmunization) ? R.color.pnc_circle_yellow : R.color.alert_complete_green;

                contentViewHolder.circleImageView.setCircleBackgroundColor(context.getResources().getColor(color_res));
                contentViewHolder.circleImageView.setBorderColor(context.getResources().getColor(color_res));


                break;
        }

    }

    /**
     * need to display like this opv1,penta1 provided at date or opv1 provided at 20-03-2019 or pcv1,rota1 not given
     * @param contentImmunization
     * @return
     */
    private StringBuilder getVaccineNames(HomeVisitVaccineGroup contentImmunization ){
        StringBuilder givenText = new StringBuilder();
        StringBuilder notGivenText = new StringBuilder();
        DateTime givenDateTime =null;
        ArrayList<VaccineRepo.Vaccine> dueVaccine = contentImmunization.getDueVaccines();
        ArrayList<VaccineRepo.Vaccine> receivedVaccine = contentImmunization.getGivenVaccines();
        for (VaccineRepo.Vaccine due : dueVaccine) {
            if(receivedVaccine.contains(due)){
                if(givenDateTime!=null && givenDateTime.equals(contentImmunization.getDateByVaccine(due))){
                    givenText.append(",").append(fixVaccineCasing(due.display()));
                }else{
                    givenDateTime = contentImmunization.getDateByVaccine(due);
                    givenText = new StringBuilder(fixVaccineCasing(due.display()));
                    givenText.append(context.getString(R.string.given_on_with_spaces)).append(DateUtil.formatDate(givenDateTime.toLocalDate(), "dd MMM yyyy"));

                }

            }else {
                givenText.append(" \u00B7 ");
                if(TextUtils.isEmpty(notGivenText)){
                    notGivenText = new StringBuilder(fixVaccineCasing(due.display()));
                }else{
                    notGivenText = notGivenText.append(",").append(fixVaccineCasing(due.display())) ;
                }
            }

        }
        return givenText.append(notGivenText);

    }
    private boolean isComplete(HomeVisitVaccineGroup contentImmunization){

        return contentImmunization.getDueVaccines().size() == contentImmunization.getGivenVaccines().size();

    }

    @Override
    public int getItemViewType(int position) {

        return homeVisitVaccineGroupDetailsArrayList.get(position).getViewType();
    }

    @Override
    public int getItemCount() {
        return homeVisitVaccineGroupDetailsArrayList.size();
    }

    public class InactiveViewHolder extends RecyclerView.ViewHolder {
        public TextView titleText,descriptionText;
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

    public class ContentViewHolder extends RecyclerView.ViewHolder {
        public TextView titleText,descriptionText;
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
}
