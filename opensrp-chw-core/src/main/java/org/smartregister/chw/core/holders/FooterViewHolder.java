package org.smartregister.chw.core.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class FooterViewHolder extends RecyclerView.ViewHolder {
    public TextView pageInfoView;
    public Button nextPageView;
    public Button previousPageView;

    public FooterViewHolder(View view) {
        super(view);

        nextPageView = view.findViewById(org.smartregister.R.id.btn_next_page);
        previousPageView = view.findViewById(org.smartregister.R.id.btn_previous_page);
        pageInfoView = view.findViewById(org.smartregister.R.id.txt_page_info);
    }
}
