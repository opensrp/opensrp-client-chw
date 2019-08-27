package org.smartregister.chw.hf.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.smartregister.chw.hf.R;
import org.smartregister.chw.hf.holder.ReferralCardViewHolder;
import org.smartregister.chw.hf.listener.ReferralRecyclerClickListener;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by wizard on 06/08/19.
 */
public class ReferralCardViewAdapter extends RecyclerView.Adapter<ReferralCardViewHolder> {
    private List<Task> tasks;
    private CommonPersonObjectClient personObjectClient;
    private Activity context;
    private ReferralRecyclerClickListener referralRecyclerClickListener = new ReferralRecyclerClickListener();
    private String startingActivity;

    public ReferralCardViewAdapter(Set<Task> taskList, Activity activity, CommonPersonObjectClient personObjectClient, String startingActivity) {
        this.tasks = new ArrayList<>(taskList);
        this.context = activity;
        this.personObjectClient = personObjectClient;
        this.startingActivity = startingActivity;
    }

    @NonNull
    @Override
    public ReferralCardViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View referralLayout = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.profile_referral_card_row, viewGroup, false);
        return new ReferralCardViewHolder(referralLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull ReferralCardViewHolder referralCardViewHolder, int position) {
        referralRecyclerClickListener.setTask(tasks.get(position));
        referralRecyclerClickListener.setCommonPersonObjectClient(personObjectClient);
        referralRecyclerClickListener.setActivity(context);
        referralRecyclerClickListener.setStartingActivity(startingActivity);
        referralCardViewHolder.textViewReferralHeader.setText(String.format(context.getApplicationContext().getResources().getString(R.string.referral_for), tasks.get(position).getFocus()));
        referralCardViewHolder.referralRow.setOnClickListener(referralRecyclerClickListener);
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }
}
