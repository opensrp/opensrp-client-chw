package org.smartgresiter.wcaro.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.smartgresiter.wcaro.R;
import org.smartgresiter.wcaro.util.GrowthNutrition;

import java.util.ArrayList;

public class GrowthAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<GrowthNutrition> growthNutritionArrayList;
    public GrowthAdapter(){
        growthNutritionArrayList=new ArrayList<>();
    }
    public void addItem(ArrayList<GrowthNutrition> growthNutritions){
        growthNutritionArrayList.addAll(growthNutritions);
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        return new GrowthViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.growth_item_view,null));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        GrowthViewHolder growthViewHolder=(GrowthViewHolder)viewHolder;
        GrowthNutrition growthNutrition=growthNutritionArrayList.get(position);
        growthViewHolder.headerTitle.setText(growthNutrition.getGrowthName()+" : "+growthNutrition.getStatus());

    }

    @Override
    public int getItemCount() {
        return growthNutritionArrayList.size();
    }
    public class GrowthViewHolder extends RecyclerView.ViewHolder {
        public TextView headerTitle;
        private View myView;

        private GrowthViewHolder(View view) {
            super(view);
            headerTitle = view.findViewById(R.id.growth_text);

            myView = view;
        }

        public View getView() {
            return myView;
        }
    }
}
