package org.smartregister.chw.view_holder;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.smartregister.chw.R;

public class BaseIllnessViewHolder extends RecyclerView.ViewHolder {
    public TextView tvQuestion;

    public BaseIllnessViewHolder(View view) {
        super(view);
        tvQuestion = view.findViewById(R.id.tvQuestion);
    }
}
