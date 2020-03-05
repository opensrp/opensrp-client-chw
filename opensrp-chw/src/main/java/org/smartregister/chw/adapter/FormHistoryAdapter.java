package org.smartregister.chw.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.smartregister.chw.R;
import org.smartregister.chw.domain.Choice;
import org.smartregister.chw.domain.Question;
import org.smartregister.chw.util.Constants;
import org.smartregister.chw.view_holder.BaseIllnessViewHolder;
import org.smartregister.chw.view_holder.IllnessCheckViewHolder;
import org.smartregister.chw.view_holder.IllnessEditViewHolder;
import org.smartregister.chw.view_holder.IllnessRadioViewHolder;

import java.util.List;

public class FormHistoryAdapter extends RecyclerView.Adapter<BaseIllnessViewHolder> {

    private List<Question> questions;
    private LayoutInflater layoutInflater;


    public FormHistoryAdapter(@NotNull List<Question> questions) {
        this.questions = questions;
    }

    @NonNull
    @Override
    public BaseIllnessViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        this.layoutInflater = LayoutInflater.from(parent.getContext());
        if (Constants.ChildIllnessViewType.RADIO_BUTTON == viewType) {
            view = layoutInflater.inflate(R.layout.fragment_routine_question_radio, parent, false);
            return new IllnessRadioViewHolder(view);
        } else if (Constants.ChildIllnessViewType.CHECK_BOX == viewType) {
            view = layoutInflater.inflate(R.layout.fragment_routine_question_check, parent, false);
            return new IllnessCheckViewHolder(view);
        } else {
            view = layoutInflater.inflate(R.layout.fragment_routine_question, parent, false);
            return new IllnessEditViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseIllnessViewHolder holder, int position) {
        Question question = questions.get(position);

        if (holder.getItemViewType() == Constants.ChildIllnessViewType.RADIO_BUTTON) {
            if (question.getChoices() == null) {
                return;
            }
            IllnessRadioViewHolder radioViewHolder = ((IllnessRadioViewHolder) holder);
            radioViewHolder.tvQuestion.setText(question.getName());
            radioViewHolder.rgOptions.removeAllViews();
            for (Choice choice : question.getChoices()) {
                View view = layoutInflater.inflate(R.layout.fragment_routine_question_choice, null, false);
                RadioButton radioButton = view.findViewById(R.id.radioButtonChoice);
                radioButton.setText(choice.getName());
                radioButton.setChecked(choice.getSelected());
                radioViewHolder.rgOptions.addView(radioButton);
            }
        } else if (holder.getItemViewType() == Constants.ChildIllnessViewType.CHECK_BOX) {
            if (question.getChoices() == null) {
                return;
            }
            IllnessCheckViewHolder checkViewHolder = ((IllnessCheckViewHolder) holder);
            checkViewHolder.tvQuestion.setText(question.getName());
            checkViewHolder.checkboxParentLayout.removeAllViews();
            for (Choice choice : question.getChoices()) {
                View view = layoutInflater.inflate(R.layout.fragment_routine_question_checkbox, null, false);
                CheckBox checkBox = view.findViewById(R.id.checkBoxChoice);
                checkBox.setText(choice.getName());
                checkBox.setChecked(choice.getSelected());
                checkViewHolder.checkboxParentLayout.addView(checkBox);
            }
        } else if (holder.getItemViewType() == Constants.ChildIllnessViewType.EDIT_TEXT) {
            IllnessEditViewHolder editViewHolder = ((IllnessEditViewHolder) holder);
            editViewHolder.tvQuestion.setText(question.getName());
            if (StringUtils.isBlank(question.getValue())) {
                editViewHolder.tvValue.setVisibility(View.GONE);
            } else {
                editViewHolder.tvValue.setText(question.getValue());
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (JsonFormConstants.NATIVE_RADIO_BUTTON.equalsIgnoreCase(questions.get(position).getType())) {
            return Constants.ChildIllnessViewType.RADIO_BUTTON;
        } else if (JsonFormConstants.CHECK_BOX.equalsIgnoreCase(questions.get(position).getType())) {
            return Constants.ChildIllnessViewType.CHECK_BOX;
        } else {
            // Default ViewType for view types that aren't currently handled
            return Constants.ChildIllnessViewType.EDIT_TEXT;
        }
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }


}
