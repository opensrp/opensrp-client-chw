package org.smartregister.chw.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.smartregister.chw.R;
import org.smartregister.chw.domain.Choice;
import org.smartregister.chw.domain.Question;

import java.util.List;

public class FormHistoryAdapter extends RecyclerView.Adapter<FormHistoryAdapter.MyViewHolder> {

    private List<Question> questions;
    private LayoutInflater layoutInflater;


    public FormHistoryAdapter(@NotNull List<Question> questions) {
        this.questions = questions;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.layoutInflater = LayoutInflater.from(parent.getContext());
        View v = layoutInflater.inflate(R.layout.fragment_routine_question, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Question question = questions.get(position);

        holder.tvQuestion.setText(question.getName());
        if (StringUtils.isBlank(question.getValue())) {
            holder.tvValue.setVisibility(View.GONE);
        } else {
            holder.tvValue.setText(question.getValue());
        }

        if (question.getChoices() == null){
            holder.rgOptions.setVisibility(View.GONE);
            return;
        }
        for (Choice choice : question.getChoices()) {
            View view = layoutInflater.inflate(R.layout.fragment_routine_question_choice, null, false);
            RadioButton radioButton = view.findViewById(R.id.radioButtonChoice);
            radioButton.setText(choice.getName());
            radioButton.setChecked(choice.getSelected());
            holder.rgOptions.addView(radioButton);
        }
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvQuestion;
        private TextView tvValue;
        private RadioGroup rgOptions;

        private MyViewHolder(View view) {
            super(view);
            tvQuestion = view.findViewById(R.id.tvQuestion);
            tvValue = view.findViewById(R.id.tvValue);
            rgOptions = view.findViewById(R.id.rgOptions);
        }
    }

}
