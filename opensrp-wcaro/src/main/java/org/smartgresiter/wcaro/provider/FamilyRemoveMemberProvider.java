package org.smartgresiter.wcaro.provider;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.smartgresiter.wcaro.R;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.family.provider.FamilyMemberRegisterProvider;

import java.util.Set;

public class FamilyRemoveMemberProvider extends FamilyMemberRegisterProvider {

    Context context;
    android.view.View.OnClickListener footerClickListener;

    public FamilyRemoveMemberProvider(Context context, CommonRepository commonRepository, Set visibleColumns, View.OnClickListener onClickListener, View.OnClickListener paginationClickListener, String familyHead, String primaryCaregiver) {
        super(context, commonRepository, visibleColumns, onClickListener, paginationClickListener, familyHead, primaryCaregiver);

        this.context = context;
        this.footerClickListener = paginationClickListener;
    }


    @Override
    public RecyclerView.ViewHolder createFooterHolder(ViewGroup parent) {
        View view = inflater().inflate(R.layout.family_remove_member_footer, parent, false);
        view.findViewById(R.id.top).setVisibility(View.GONE);
        return new RemoveFooterViewHolder(view);
    }

    @Override
    public void getFooterView(RecyclerView.ViewHolder viewHolder, int currentPageCount, int totalPageCount, boolean hasNext, boolean hasPrevious) {
        // do nothing
        RemoveFooterViewHolder footerViewHolder = (RemoveFooterViewHolder) viewHolder;
        footerViewHolder.hint.setText(String.format("%s adults and %s U5 children", "X", "N"));
        footerViewHolder.view.setOnClickListener(footerClickListener);
    }

    public class RemoveFooterViewHolder extends FooterViewHolder {
        public TextView hint;
        public View view;

        public RemoveFooterViewHolder(android.view.View view) {
            super(view);
            this.hint = view.findViewById(R.id.hint);
            this.view = view;
        }
    }
}

