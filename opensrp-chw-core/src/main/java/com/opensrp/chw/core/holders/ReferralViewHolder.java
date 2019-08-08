package com.opensrp.chw.core.holders;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.opensrp.chw.core.R;

public class ReferralViewHolder extends RecyclerView.ViewHolder {

    private TextView nameTextView;

    private TextView reasonTextView;

    private TextView referredByTextView;

    private TextView executionStartTextView;

    public ReferralViewHolder(@NonNull View itemView) {
        super(itemView);
        nameTextView = itemView.findViewById(R.id.patient_name);
        reasonTextView = itemView.findViewById(R.id.referral_reason);
        referredByTextView = itemView.findViewById(R.id.referred_by);
        executionStartTextView = itemView.findViewById(R.id.referral_start);
    }


    public void setName(String name) {
        nameTextView.setText(name);
    }

    public void setReason(String reason) {
        reasonTextView.setText(reason);
    }

    public void setReferredBy(String referredBy) {
        referredByTextView.setText(itemView.getContext().getString(R.string.referred_by, referredBy));
    }

    public void setReferralStart(String referStart) {
        executionStartTextView.setText(referStart);
    }
}
