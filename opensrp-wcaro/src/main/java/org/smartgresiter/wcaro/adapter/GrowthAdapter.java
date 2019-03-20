package org.smartgresiter.wcaro.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.smartgresiter.wcaro.R;
import org.smartgresiter.wcaro.util.BaseService;
import org.smartgresiter.wcaro.util.BaseVaccine;
import org.smartgresiter.wcaro.util.ServiceContent;
import org.smartgresiter.wcaro.util.ServiceHeader;

import java.util.ArrayList;

public class GrowthAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<BaseService> baseServices;

    public GrowthAdapter() {
        this.baseServices = new ArrayList<>();
    }

    public void addItem(ArrayList<BaseService> baseVaccines) {
        this.baseServices.addAll(baseVaccines);

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
                BaseService baseService = baseServices.get(position);
                ServiceHeader serviceHeader = (ServiceHeader) baseService;
                HeaderViewHolder headerViewHolder = (HeaderViewHolder) viewHolder;
                headerViewHolder.headerTitle.setText(serviceHeader.getServiceHeaderName());
                break;
            case BaseVaccine.TYPE_CONTENT:
                BaseService content = baseServices.get(position);
                ServiceContent serviceContent = (ServiceContent) content;
                ContentViewHolder contentViewHolder = (ContentViewHolder) viewHolder;
                contentViewHolder.vaccineName.setText(serviceContent.getServiceName());
                break;
            default:
                break;
        }

    }

    @Override
    public int getItemViewType(int position) {

        return baseServices.get(position).getType();
    }

    @Override
    public int getItemCount() {
        return baseServices.size();
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        private TextView headerTitle;
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

    public static class ContentViewHolder extends RecyclerView.ViewHolder {
        private TextView vaccineName;
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
