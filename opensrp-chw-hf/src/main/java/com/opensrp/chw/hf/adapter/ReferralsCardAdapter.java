package com.opensrp.chw.hf.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.opensrp.chw.hf.holder.ReferralsCardHolder;
import com.opensrp.hf.R;

import org.smartregister.domain.Task;

import java.util.List;

public class ReferralsCardAdapter extends RecyclerView.Adapter<ReferralsCardHolder> {
    private List<Task> taskList;

    public ReferralsCardAdapter(List<Task> taskList) {
        this.taskList = taskList;
    }

    @NonNull
    @Override
    public ReferralsCardHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View referralsCard = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.profile_referral_card_row, viewGroup, false);
        return new ReferralsCardHolder(referralsCard);
    }

    @Override
    public void onBindViewHolder(@NonNull ReferralsCardHolder referralsCardHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }
}
