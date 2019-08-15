package org.smartregister.chw.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.smartregister.chw.R;
import org.smartregister.chw.util.BaseVaccine;
import org.smartregister.chw.util.ChildUtils;
import org.smartregister.chw.util.VaccineContent;
import org.smartregister.chw.util.VaccineHeader;

import java.util.ArrayList;

public class VaccineAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<BaseVaccine> baseVaccines;
    private Context context;

    public VaccineAdapter(Context context) {
        this.context = context;
        this.baseVaccines = new ArrayList<>();
    }

    public void addItem(ArrayList<BaseVaccine> baseVaccines) {
        this.baseVaccines.addAll(baseVaccines);

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        switch (viewType) {
            case BaseVaccine.TYPE_HEADER:
                return new HeaderViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.vaccine_header_view, null));
            case BaseVaccine.TYPE_CONTENT:
                return new ContentViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.vaccine_content_view, null));

        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

        switch (viewHolder.getItemViewType()) {
            case BaseVaccine.TYPE_HEADER:
                BaseVaccine baseVaccine = baseVaccines.get(position);
                VaccineHeader vaccineHeader = (VaccineHeader) baseVaccine;
                HeaderViewHolder headerViewHolder = (HeaderViewHolder) viewHolder;
                if (vaccineHeader.getVaccineHeaderName().trim().equalsIgnoreCase(context.getString(R.string.at_birth))) {
                    vaccineHeader.setVaccineHeaderName(context.getString(R.string.birth_cap));
                }
                headerViewHolder.headerTitle.setText(vaccineHeader.getVaccineHeaderName());
                break;
            case BaseVaccine.TYPE_CONTENT:
                BaseVaccine content = baseVaccines.get(position);
                VaccineContent vaccineContent = (VaccineContent) content;
                ContentViewHolder contentViewHolder = (ContentViewHolder) viewHolder;
                contentViewHolder.vaccineName.setText(ChildUtils.fixVaccineCasing(vaccineContent.getVaccineName()) + " - " + context.getString(R.string.done) + vaccineContent.getVaccineDate());
                break;
        }

    }

    @Override
    public int getItemViewType(int position) {

        return baseVaccines.get(position).getType();
    }

    @Override
    public int getItemCount() {
        return baseVaccines.size();
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        public TextView headerTitle;
        private View myView;

        private HeaderViewHolder(View view) {
            super(view);
            headerTitle = view.findViewById(R.id.header_text);

            myView = view;
        }

        public View getView() {
            return myView;
        }
    }

    public class ContentViewHolder extends RecyclerView.ViewHolder {
        public TextView vaccineName;
        private View myView;

        private ContentViewHolder(View view) {
            super(view);
            vaccineName = view.findViewById(R.id.name_date_tv);

            myView = view;
        }

        public View getView() {
            return myView;
        }
    }
}
