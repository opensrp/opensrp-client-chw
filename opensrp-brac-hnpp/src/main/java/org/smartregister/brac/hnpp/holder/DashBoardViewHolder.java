package org.smartregister.brac.hnpp.holder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.smartregister.brac.hnpp.R;
import org.smartregister.view.customcontrols.CustomFontTextView;

/**
 * Created by wizard on 06/08/19.
 */
public class DashBoardViewHolder extends RecyclerView.ViewHolder {
    public RelativeLayout itemBg;
    public ImageView imageView;
    public CustomFontTextView textViewTitle;
    public CustomFontTextView textViewCount;

    public DashBoardViewHolder(@NonNull View itemView) {
        super(itemView);
        itemBg = itemView.findViewById(R.id.item_bg);
        imageView = itemView.findViewById(R.id.image_view);
        textViewTitle = itemView.findViewById(R.id.title_txt);
        textViewCount = itemView.findViewById(R.id.count_txt);
    }
}
