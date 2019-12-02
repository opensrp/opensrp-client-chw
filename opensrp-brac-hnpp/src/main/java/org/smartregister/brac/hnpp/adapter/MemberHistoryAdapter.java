package org.smartregister.brac.hnpp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.smartregister.brac.hnpp.R;
import org.smartregister.brac.hnpp.holder.MemberDueViewHolder;
import org.smartregister.brac.hnpp.utils.HnppConstants;
import org.smartregister.brac.hnpp.utils.MemberHistoryData;
import org.smartregister.chw.core.utils.CoreConstants;

import java.util.ArrayList;

public class MemberHistoryAdapter extends RecyclerView.Adapter<MemberDueViewHolder> {
    private ArrayList<MemberHistoryData> contentList;
    private Context context;
    private OnClickAdapter onClickAdapter;

    public MemberHistoryAdapter(Context context, OnClickAdapter onClickAdapter) {
        this.context = context;
        this.onClickAdapter = onClickAdapter;
        contentList = new ArrayList<>();
    }

    public void setData(ArrayList<MemberHistoryData> contentList) {
        this.contentList.clear();
        this.contentList.addAll(contentList);
    }

    @NonNull
    @Override
    public MemberDueViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        MemberDueViewHolder viewHolder = new MemberDueViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.view_member_due, null));
        viewHolder.statusImage.setVisibility(View.INVISIBLE);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull final MemberDueViewHolder viewHolder, int position) {
        final MemberHistoryData content = contentList.get(position);
        viewHolder.imageView.setImageResource(content.getImageSource());
        viewHolder.textViewTitle.setText(content.getTitle());
        viewHolder.textViewLastVisit.setVisibility(View.VISIBLE);
        viewHolder.textViewLastVisit.setText(HnppConstants.DDMMYY.format(content.getVisitDate()));
        viewHolder.itemView.setOnClickListener(v -> onClickAdapter.onClick(position, content));
    }


    @Override
    public int getItemCount() {
        return contentList.size();
    }

    public interface OnClickAdapter {
        void onClick(int position, MemberHistoryData content);
    }
}
