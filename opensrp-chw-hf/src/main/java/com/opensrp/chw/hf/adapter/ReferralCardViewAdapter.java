package com.opensrp.chw.hf.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.opensrp.chw.hf.activity.ChildProfileActivity;
import com.opensrp.chw.hf.holder.ReferralCardViewHolder;
import com.opensrp.chw.hf.listener.ReferralRecyclerClickListener;
import com.opensrp.hf.R;

import org.smartregister.domain.Task;

import java.util.List;

/**
 * Created by wizard on 06/08/19.
 */
public class ReferralCardViewAdapter extends RecyclerView.Adapter<ReferralCardViewHolder> {
    private List<Task> tasks;
    private Activity context;
    private ReferralRecyclerClickListener referralRecyclerClickListener = new ReferralRecyclerClickListener();

    public ReferralCardViewAdapter(List<Task> taskList, ChildProfileActivity childProfileActivity) {
        this.tasks = taskList;
        this.context = childProfileActivity;
    }

    @NonNull
    @Override
    public ReferralCardViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View referralLayout = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.profile_referral_card_row, viewGroup, false);
        return new ReferralCardViewHolder(referralLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull ReferralCardViewHolder referralCardViewHolder, int position) {
        referralCardViewHolder.textViewReferralHeader.setText(String.format(context.getApplicationContext().getResources().getString(R.string.referral_for), tasks.get(position).getFocus()));
        referralCardViewHolder.referralRow.setOnClickListener(referralRecyclerClickListener);
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }
}
