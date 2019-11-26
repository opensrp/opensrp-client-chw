package org.smartregister.brac.hnpp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.smartregister.brac.hnpp.R;
import org.smartregister.brac.hnpp.holder.MemberDueViewHolder;
import org.smartregister.brac.hnpp.utils.OtherServiceData;

import java.util.ArrayList;

public class OtherServiceAdapter extends RecyclerView.Adapter<MemberDueViewHolder> {
    private ArrayList<OtherServiceData> contentList;
    private Context context;
    private OnClickAdapter onClickAdapter;

    public OtherServiceAdapter(Context context, OnClickAdapter onClickAdapter) {
        this.context = context;
        this.onClickAdapter = onClickAdapter;
        contentList = new ArrayList<>();
    }

    public void setData(ArrayList<OtherServiceData> contentList) {
        this.contentList.clear();
        this.contentList.addAll(contentList);
    }

    @NonNull
    @Override
    public MemberDueViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MemberDueViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.view_member_due, null));

    }

    @Override
    public void onBindViewHolder(@NonNull final MemberDueViewHolder viewHolder, int position) {
        final OtherServiceData content = contentList.get(position);
        viewHolder.imageView.setImageResource(content.getImageSource());
        viewHolder.textViewTitle.setText(content.getTitle());
        viewHolder.textViewLastVisit.setVisibility(View.INVISIBLE);
        viewHolder.itemView.setOnClickListener(v -> onClickAdapter.onClick(viewHolder.getAdapterPosition(), content));
    }


    @Override
    public int getItemCount() {
        return contentList.size();
    }

    public interface OnClickAdapter {
        void onClick(int position, OtherServiceData content);
    }
}
