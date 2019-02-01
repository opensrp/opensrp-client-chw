package org.smartgresiter.wcaro.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.smartgresiter.wcaro.R;

import java.util.ArrayList;

public class BirthAndIllnessAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<String> contentList;

    public BirthAndIllnessAdapter() {
        contentList = new ArrayList<>();
    }

    public void setData(ArrayList<String> contentList) {
        this.contentList = contentList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new GrowthAdapter.ContentViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.vaccine_content_view, null));

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        String content = contentList.get(position);
        GrowthAdapter.ContentViewHolder contentViewHolder = (GrowthAdapter.ContentViewHolder) viewHolder;
        contentViewHolder.vaccineName.setText(content);

    }

    @Override
    public int getItemCount() {
        return contentList.size();
    }
}
