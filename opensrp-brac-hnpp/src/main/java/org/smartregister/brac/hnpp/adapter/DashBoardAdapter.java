package org.smartregister.brac.hnpp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import org.smartregister.brac.hnpp.R;
import org.smartregister.brac.hnpp.holder.DashBoardViewHolder;
import org.smartregister.brac.hnpp.model.DashBoardData;
import java.util.ArrayList;

public class DashBoardAdapter extends RecyclerView.Adapter<DashBoardViewHolder> {
    private ArrayList<DashBoardData> contentList;
    private Context context;
    private OnClickAdapter onClickAdapter;

    public DashBoardAdapter(Context context, OnClickAdapter onClickAdapter) {
        this.context = context;
        this.onClickAdapter = onClickAdapter;
        contentList = new ArrayList<>();
    }

    public void setData(ArrayList<DashBoardData> contentList) {
        this.contentList.clear();
        this.contentList.addAll(contentList);
    }

    @NonNull
    @Override
    public DashBoardViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new DashBoardViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.view_dashboard_item, null));

    }

    @Override
    public void onBindViewHolder(@NonNull final DashBoardViewHolder viewHolder, int position) {
        final DashBoardData content = contentList.get(position);
        viewHolder.imageView.setImageResource(content.getImageSource());
        viewHolder.textViewTitle.setText(content.getTitle());
        viewHolder.textViewCount.setText(content.getCount());
        viewHolder.itemView.setOnClickListener(v -> onClickAdapter.onClick(viewHolder.getAdapterPosition(), content));
    }


    @Override
    public int getItemCount() {
        return contentList.size();
    }

    public interface OnClickAdapter {
        void onClick(int position, DashBoardData content);
    }
}
