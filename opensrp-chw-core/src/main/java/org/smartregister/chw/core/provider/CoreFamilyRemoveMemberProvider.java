package org.smartregister.chw.core.provider;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import org.smartregister.chw.core.R;
import org.smartregister.chw.core.contract.FamilyRemoveMemberContract;
import org.smartregister.chw.core.interactor.CoreFamilyRemoveMemberInteractor;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.family.provider.FamilyMemberRegisterProvider;
import org.smartregister.view.customcontrols.CustomFontTextView;
import org.smartregister.view.customcontrols.FontVariant;

import java.util.HashMap;
import java.util.Set;

public abstract class CoreFamilyRemoveMemberProvider extends FamilyMemberRegisterProvider {

    private Context context;
    private View.OnClickListener footerClickListener;
    private String familyID;

    public CoreFamilyRemoveMemberProvider(String familyID, Context context, CommonRepository commonRepository, Set visibleColumns, View.OnClickListener onClickListener, View.OnClickListener paginationClickListener, String familyHead, String primaryCaregiver) {
        super(context, commonRepository, visibleColumns, onClickListener, paginationClickListener, familyHead, primaryCaregiver);
        this.familyID = familyID;
        this.context = context;
        this.footerClickListener = paginationClickListener;
    }

    @Override
    public void getFooterView(RecyclerView.ViewHolder viewHolder, final int currentPageCount, final int totalPageCount, boolean hasNext, boolean hasPrevious) {
        // do nothing
        CoreFamilyRemoveMemberInteractor familyRemoveMemberInteractor = getFamilyRemoveMemberInteractor();
        final RemoveFooterViewHolder footerViewHolder = (RemoveFooterViewHolder) viewHolder;
        familyRemoveMemberInteractor.getFamilySummary(familyID, new FamilyRemoveMemberContract.InteractorCallback<HashMap<String, String>>() {
            @Override
            public void onResult(HashMap<String, String> result) {
                Integer children = Integer.valueOf(result.get(CoreConstants.TABLE_NAME.CHILD));
                Integer members = Integer.valueOf(result.get(CoreConstants.TABLE_NAME.FAMILY_MEMBER));

                int adults = members - children;

                HashMap<String, String> payload = new HashMap<>();
                payload.put(CoreConstants.GLOBAL.MESSAGE, String.format(context.getString(R.string.remove_family_count), String.valueOf(adults), String.valueOf(children)));
                payload.put(CoreConstants.GLOBAL.NAME, result.get(CoreConstants.GLOBAL.NAME));

                footerViewHolder.instructions.setFontVariant(FontVariant.REGULAR);
                footerViewHolder.instructions.setTextColor(Color.BLACK);

                footerViewHolder.hint.setText(payload.get(CoreConstants.GLOBAL.MESSAGE));
                footerViewHolder.hint.setFontVariant(FontVariant.LIGHT);
                footerViewHolder.hint.setTextColor(Color.GRAY);
                footerViewHolder.hint.setTypeface(footerViewHolder.hint.getTypeface(), Typeface.NORMAL);

                footerViewHolder.view.setTag(payload);
            }

            @Override
            public void onError(Exception e) {
                //// TODO: 15/08/19
            }
        });

        footerViewHolder.view.setOnClickListener(footerClickListener);
    }

    @Override
    public RecyclerView.ViewHolder createFooterHolder(ViewGroup parent) {
        View view = inflater().inflate(R.layout.family_remove_member_footer, parent, false);
        view.findViewById(R.id.top).setVisibility(View.GONE);
        return new RemoveFooterViewHolder(view);
    }

    protected abstract CoreFamilyRemoveMemberInteractor getFamilyRemoveMemberInteractor();

    public class RemoveFooterViewHolder extends FooterViewHolder {
        public CustomFontTextView hint;
        public CustomFontTextView instructions;
        public View view;

        public RemoveFooterViewHolder(View view) {
            super(view);
            this.hint = view.findViewById(R.id.hint);
            this.instructions = view.findViewById(R.id.instructions);
            this.view = view;

            hint.setFontVariant(FontVariant.REGULAR);
        }
    }
}

