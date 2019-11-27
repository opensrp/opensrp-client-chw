package org.smartregister.chw.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.smartregister.chw.R;
import org.smartregister.chw.util.BaseService;
import org.smartregister.chw.util.ServiceContent;
import org.smartregister.chw.util.ServiceHeader;

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
            case BaseService.TYPE_HEADER:
                return new HeaderViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.growth_header_view, null));
            case BaseService.TYPE_CONTENT:
                return new ContentViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.growth_content_view, null));
            case BaseService.TYPE_LINE:
                return new HeaderViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.view_line, null));
            default:
                return new HeaderViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.growth_header_view, null));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

        switch (viewHolder.getItemViewType()) {
            case BaseService.TYPE_HEADER:
                BaseService baseService = baseServices.get(position);
                ServiceHeader serviceHeader = (ServiceHeader) baseService;
                HeaderViewHolder headerViewHolder = (HeaderViewHolder) viewHolder;
                headerViewHolder.headerTitle.setText(serviceHeader.getServiceHeaderName());
                break;
            case BaseService.TYPE_CONTENT:
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
}
