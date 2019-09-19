package org.smartregister.chw.hf.provider;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nineoldandroids.view.ViewHelper;

import org.smartregister.chw.core.provider.ChwPncRegisterProvider;
import org.smartregister.chw.hf.R;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.view.contract.SmartRegisterClient;

import java.util.Set;

import provider.PncRegisterProvider;

public class HfPncRegisterProvider extends ChwPncRegisterProvider {
    private final LayoutInflater inflater;

    public HfPncRegisterProvider(Context context, CommonRepository commonRepository, Set visibleColumns, View.OnClickListener onClickListener, View.OnClickListener paginationClickListener) {
        super(context, commonRepository, visibleColumns, onClickListener, paginationClickListener);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public void getView(Cursor cursor, SmartRegisterClient client, RegisterViewHolder viewHolder) {
        super.getView(cursor, client, viewHolder);
        viewHolder.dueWrapper.setVisibility(View.GONE);
    }

    @Override
    public RegisterViewHolder createViewHolder(ViewGroup parent) {
        View view = inflater.inflate(R.layout.pnc_register_list_row, parent, false);
        return new HfPncRegisterViewHolder(view);
    }

    public class HfPncRegisterViewHolder extends PncRegisterProvider.RegisterViewHolder {

        public TextView textViewReferralDay;

        public HfPncRegisterViewHolder(View itemView) {
            super(itemView);
            textViewReferralDay = itemView.findViewById(R.id.text_view_referral_day);
        }
    }
}
