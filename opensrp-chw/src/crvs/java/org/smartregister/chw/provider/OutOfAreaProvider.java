package org.smartregister.chw.provider;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import org.apache.commons.lang3.text.WordUtils;
import org.smartregister.chw.R;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.core.holders.FooterViewHolder;
import org.smartregister.chw.core.holders.RegisterViewHolder;
import org.smartregister.chw.task.OutOfAreaChildAsyncTask;
import org.smartregister.chw.util.CrvsConstants;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.cursoradapter.RecyclerViewProvider;
import org.smartregister.family.util.DBConstants;
import org.smartregister.family.util.Utils;
import org.smartregister.view.contract.SmartRegisterClient;
import org.smartregister.view.contract.SmartRegisterClients;
import org.smartregister.view.dialog.FilterOption;
import org.smartregister.view.dialog.ServiceModeOption;
import org.smartregister.view.dialog.SortOption;
import org.smartregister.view.viewholder.OnClickFormLauncher;
import java.text.MessageFormat;
import java.util.Set;
import static org.smartregister.chw.core.utils.Utils.getDuration;
import static org.smartregister.chw.util.Utils.getClientName;

public class OutOfAreaProvider implements RecyclerViewProvider<RegisterViewHolder> {
    public final LayoutInflater inflater;
    private Set<org.smartregister.configurableviews.model.View> visibleColumns;
    private View.OnClickListener onClickListener;
    private View.OnClickListener paginationClickListener;
    private Context context;
    private CommonRepository commonRepository;

    public OutOfAreaProvider(Context context, CommonRepository commonRepository, Set visibleColumns, View.OnClickListener onClickListener, View.OnClickListener paginationClickListener) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.visibleColumns = visibleColumns;
        this.onClickListener = onClickListener;
        this.paginationClickListener = paginationClickListener;
        this.context = context;
        this.commonRepository = commonRepository;
    }

    protected static void fillValue(TextView v, String value) {
        if (v != null) {
            v.setText(value);
        }
    }

    @Override
    public void getView(Cursor cursor, SmartRegisterClient client, RegisterViewHolder viewHolder) {
        CommonPersonObjectClient pc = (CommonPersonObjectClient) client;
        if (visibleColumns.isEmpty()) {
            populatePatientColumn(pc, viewHolder);
            populateLastColumn(pc, viewHolder);
        }
    }

    private void populateLastColumn(CommonPersonObjectClient pc, RegisterViewHolder viewHolder) {
        Utils.startAsyncTask(new OutOfAreaChildAsyncTask(context, commonRepository, viewHolder, pc, onClickListener), null);
    }

    private int getChildRegisterLayout() {
        return !ChwApplication.getApplicationFlavor().prioritizeChildNameOnChildRegister() ? R.layout.adapter_child_register_list_row : R.layout.adapter_prioritize_child_register_list_row;
    }

    @Override
    public void getFooterView(RecyclerView.ViewHolder viewHolder, int currentPageCount, int totalPageCount, boolean hasNext, boolean hasPrevious) {
        FooterViewHolder footerViewHolder = (FooterViewHolder) viewHolder;
        footerViewHolder.pageInfoView.setText(
                MessageFormat.format(context.getString(org.smartregister.R.string.str_page_info), currentPageCount,
                        totalPageCount));

        footerViewHolder.nextPageView.setVisibility(hasNext ? View.VISIBLE : View.INVISIBLE);
        footerViewHolder.previousPageView.setVisibility(hasPrevious ? View.VISIBLE : View.INVISIBLE);

        footerViewHolder.nextPageView.setOnClickListener(paginationClickListener);
        footerViewHolder.previousPageView.setOnClickListener(paginationClickListener);
    }

    @Override
    public SmartRegisterClients updateClients(FilterOption villageFilter, ServiceModeOption serviceModeOption, FilterOption searchFilter, SortOption sortOption) {
        return null;
    }

    @Override
    public void onServiceModeSelected(ServiceModeOption serviceModeOption) {//Implement Abstract Method
    }

    @Override
    public OnClickFormLauncher newFormLauncher(String formName, String entityId, String metaData) {
        return null;
    }

    @Override
    public LayoutInflater inflater() {
        return inflater;
    }

    @Override
    public RegisterViewHolder createViewHolder(ViewGroup parent) {
        View view = inflater.inflate(getChildRegisterLayout(), parent, false);
        return new RegisterViewHolder(view);
    }

    @Override
    public RecyclerView.ViewHolder createFooterHolder(ViewGroup parent) {
        View view = inflater.inflate(org.smartregister.chw.core.R.layout.smart_register_pagination, parent, false);
        return new FooterViewHolder(view);
    }

    @Override
    public boolean isFooterViewHolder(RecyclerView.ViewHolder viewHolder) {
        return viewHolder instanceof FooterViewHolder;
    }

    protected void populatePatientColumn(CommonPersonObjectClient pc, RegisterViewHolder viewHolder) {
        try{
            String motherName = Utils.getValue(pc.getColumnmaps(), CrvsConstants.MOTHER_NAME, true);
            String parentName = context.getResources().getString(R.string.care_giver_initials) + ": " + motherName;
            fillValue(viewHolder.textViewParentName, WordUtils.capitalize(parentName));
            String firstName = Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.FIRST_NAME, true);
            String middleName = Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.MIDDLE_NAME, true);
            String childName = getClientName(firstName, middleName);
            String dobString = getDuration(Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.DOB, false));
            String age = WordUtils.capitalize(Utils.getTranslatedDate(dobString, context));
            fillValue(viewHolder.textViewChildName, WordUtils.capitalize(childName)+", "+age);
            String gender_key = Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.GENDER, true);
            fillValue(viewHolder.textViewAddressGender, "Gender: "+gender_key);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public LayoutInflater getInflater() {
        return inflater;
    }

    public View.OnClickListener getOnClickListener() {
        return onClickListener;
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public View.OnClickListener getPaginationClickListener() {
        return paginationClickListener;
    }

    public void setPaginationClickListener(View.OnClickListener paginationClickListener) {
        this.paginationClickListener = paginationClickListener;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
