package org.smartgresiter.wcaro.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.smartgresiter.wcaro.R;
import org.smartgresiter.wcaro.model.NavigationModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NavigationAdapter extends RecyclerView.Adapter<NavigationAdapter.MyViewHolder> {

    private List<NavigationModel> navigationModelList;
    private Map<String, SelectedAction> actions = new HashMap<>();
    private int selectedPosition = 0;
    Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName, tvCount;
        public ImageView ivIcon;

        private MyViewHolder(View view) {
            super(view);
            tvName = view.findViewById(R.id.tvName);
            tvCount = view.findViewById(R.id.tvCount);
            ivIcon = view.findViewById(R.id.ivIcon);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NavigationModel model = navigationModelList.get(getAdapterPosition());
                    selectedPosition = getAdapterPosition();
                    SelectedAction sa = actions.get(model.getMenuTitle());
                    if (sa != null) {
                        sa.onSelect();
                    }
                    notifyDataSetChanged();
                }
            });
        }
    }

    public NavigationAdapter(List<NavigationModel> navigationModels, Context context) {
        this.navigationModelList = navigationModels;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.navigation_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        NavigationModel model = navigationModelList.get(position);
        holder.tvName.setText(model.getMenuTitle());
        holder.tvCount.setText(Long.toString(model.getRegisterCount()));
        holder.ivIcon.setImageResource(model.getResourceID());

        if (selectedPosition == position) {
            holder.tvCount.setTextColor(context.getResources().getColor(R.color.holo_blue));
            holder.tvName.setTextColor(context.getResources().getColor(R.color.holo_blue));
        } else {
            holder.tvCount.setTextColor(Color.WHITE);
            holder.tvName.setTextColor(Color.WHITE);
        }
    }

    @Override
    public int getItemCount() {
        return navigationModelList.size();
    }

    public void addAction(String menuKey, SelectedAction selectedAction) {
        actions.put(menuKey, selectedAction);
    }

    public interface SelectedAction {
        void onSelect();
    }
}


