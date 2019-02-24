package org.smartgresiter.wcaro.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.smartgresiter.wcaro.R;
import org.smartgresiter.wcaro.util.BirthIllnessData;

import java.util.ArrayList;

public class HomeVisitBirthAndIllnessDataAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<BirthIllnessData> contentList;

    public HomeVisitBirthAndIllnessDataAdapter() {
        contentList = new ArrayList<>();
    }

    public void setData(ArrayList<BirthIllnessData> contentList) {
        this.contentList.clear();
        this.contentList.addAll(contentList);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ContentViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_growth_illness_data_view, null));

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        BirthIllnessData content = contentList.get(position);
       ContentViewHolder contentViewHolder = (ContentViewHolder) viewHolder;
        contentViewHolder.questionText.setText(content.getQuestion());
        contentViewHolder.answerText.setText(content.getAnswer());

    }

    @Override
    public int getItemCount() {
        return contentList.size();
    }
    public static class ContentViewHolder extends RecyclerView.ViewHolder {
        public TextView questionText,answerText;
        private View myView;

        public ContentViewHolder(View view) {
            super(view);
            questionText = view.findViewById(R.id.question_tv);
            answerText= view.findViewById(R.id.answer_tv);
            myView = view;
        }

        public View getView() {
            return myView;
        }
    }
}
