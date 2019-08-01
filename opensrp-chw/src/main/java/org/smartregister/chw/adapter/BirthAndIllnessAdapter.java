package org.smartregister.chw.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.smartregister.chw.R;

import java.util.ArrayList;

public class BirthAndIllnessAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<String> contentList;

    public BirthAndIllnessAdapter() {
        contentList = new ArrayList<>();
    }

    public void setData(ArrayList<String> contentList) {
        this.contentList.addAll(contentList);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ContentViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.content_view, null));

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        String content = contentList.get(position);
        ContentViewHolder contentViewHolder = (ContentViewHolder) viewHolder;
        contentViewHolder.vaccineName.setText(content);

    }

    @Override
    public int getItemCount() {
        return contentList.size();
    }

    public static class ContentViewHolder extends RecyclerView.ViewHolder {
        public TextView vaccineName;
        private View myView;

        public ContentViewHolder(View view) {
            super(view);
            vaccineName = view.findViewById(R.id.name_date_tv);
            myView = view;
        }

        public View getView() {
            return myView;
        }
    }
}
