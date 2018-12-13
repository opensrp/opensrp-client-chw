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
import org.smartgresiter.wcaro.model.NavigationOption;

import java.util.List;

public class NavigationAdapter extends RecyclerView.Adapter<NavigationAdapter.MyViewHolder> {

    private List<NavigationOption> navigationOptionList;
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
                    NavigationOption model = navigationOptionList.get(getAdapterPosition());
                    selectedPosition = getAdapterPosition();
                    if (model.getSelectedAction() != null) {
                        model.getSelectedAction().onSelect();
                    }
                    notifyDataSetChanged();
                }
            });
        }
    }

    public NavigationAdapter(List<NavigationOption> navigationOptions, Context context) {
        this.navigationOptionList = navigationOptions;
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
        NavigationOption model = navigationOptionList.get(position);
        holder.tvName.setText(model.getMenuTitle());
        holder.tvCount.setText(Long.toString(model.getRegisterCount()));
        holder.ivIcon.setImageResource(model.getResourceID());

        if (selectedPosition == position) {
            holder.tvCount.setTextColor(context.getResources().getColor(R.color.holo_blue));
            holder.tvName.setTextColor(context.getResources().getColor(R.color.holo_blue));
            holder.ivIcon.setImageResource(model.getResourceActiveID());
        } else {
            holder.tvCount.setTextColor(Color.WHITE);
            holder.tvName.setTextColor(Color.WHITE);
            holder.ivIcon.setImageResource(model.getResourceID());
        }
    }

    @Override
    public int getItemCount() {
        return navigationOptionList.size();
    }

}


