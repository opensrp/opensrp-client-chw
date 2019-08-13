package org.smartregister.chw.provider;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.apache.commons.lang3.text.WordUtils;
import org.jeasy.rules.api.Rules;
import org.smartregister.chw.R;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.interactor.ChildProfileInteractor;
import org.smartregister.chw.util.ChildDBConstants;
import org.smartregister.chw.util.ChildUtils;
import org.smartregister.chw.util.ChildVisit;
import org.smartregister.chw.util.Constants;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.cursoradapter.RecyclerViewProvider;
import org.smartregister.family.fragment.BaseFamilyRegisterFragment;
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

/**
 * Created by keyman on 13/11/2018.
 */

public class ChildRegisterProvider implements RecyclerViewProvider<ChildRegisterProvider.RegisterViewHolder> {

    private final LayoutInflater inflater;
    private Set<org.smartregister.configurableviews.model.View> visibleColumns;

    private View.OnClickListener onClickListener;
    private View.OnClickListener paginationClickListener;

    private Context context;
    private CommonRepository commonRepository;

    public ChildRegisterProvider(Context context, CommonRepository commonRepository, Set visibleColumns, View.OnClickListener onClickListener, View.OnClickListener paginationClickListener) {

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.visibleColumns = visibleColumns;

        this.onClickListener = onClickListener;
        this.paginationClickListener = paginationClickListener;
        this.context = context;
        this.commonRepository = commonRepository;
    }

    public static void fillValue(TextView v, String value) {
        if (v != null)
            v.setText(value);

    }

    @Override
    public void getView(Cursor cursor, SmartRegisterClient client, RegisterViewHolder viewHolder) {
        CommonPersonObjectClient pc = (CommonPersonObjectClient) client;
        if (visibleColumns.isEmpty()) {
            populatePatientColumn(pc, client, viewHolder);
            populateIdentifierColumn(pc, viewHolder);
            populateLastColumn(pc, viewHolder);

            return;
        }
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

    private void populatePatientColumn(CommonPersonObjectClient pc, SmartRegisterClient client, RegisterViewHolder viewHolder) {

        String parentFirstName = Utils.getValue(pc.getColumnmaps(), ChildDBConstants.KEY.FAMILY_FIRST_NAME, true);
        String parentLastName = Utils.getValue(pc.getColumnmaps(), ChildDBConstants.KEY.FAMILY_LAST_NAME, true);
        String parentMiddleName = Utils.getValue(pc.getColumnmaps(), ChildDBConstants.KEY.FAMILY_MIDDLE_NAME, true);

        String parentName = context.getResources().getString(R.string.care_giver_initials) + ": " + org.smartregister.util.Utils.getName(parentFirstName, parentMiddleName + " " + parentLastName);
        String firstName = Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.FIRST_NAME, true);
        String middleName = Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.MIDDLE_NAME, true);
        String lastName = Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.LAST_NAME, true);
        String childName = org.smartregister.util.Utils.getName(firstName, middleName + " " + lastName);

        fillValue(viewHolder.textViewParentName, WordUtils.capitalize(parentName));

        String dobString = Utils.getDuration(Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.DOB, false));
        //dobString = dobString.contains("y") ? dobString.substring(0, dobString.indexOf("y")) : dobString;
        fillValue(viewHolder.textViewChildName, WordUtils.capitalize(childName) + ", " + WordUtils.capitalize(Utils.getTranslatedDate(dobString, context)));
        String address = Utils.getValue(pc.getColumnmaps(), ChildDBConstants.KEY.FAMILY_HOME_ADDRESS, true);
        String gender_key = Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.GENDER, true);
        fillValue(viewHolder.textViewAddressGender, address + " \u00B7 " + org.smartregister.chw.util.Utils.getGenderLanguageSpecific(context,gender_key));

        View patient = viewHolder.childColumn;
        attachPatientOnclickListener(patient, client);


        View dueButton = viewHolder.dueButton;
        attachDosageOnclickListener(dueButton, client);

    }

    private void populateIdentifierColumn(CommonPersonObjectClient pc, RegisterViewHolder viewHolder) {
        String uniqueId = Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.UNIQUE_ID, false);
        //fillValue(viewHolder.ancId, String.format(context.getString(R.string.unique_id_text), uniqueId));
    }

    private void populateLastColumn(CommonPersonObjectClient pc, RegisterViewHolder viewHolder) {
        Utils.startAsyncTask(new UpdateLastAsyncTask(context, commonRepository, viewHolder, pc.entityId()), null);
    }

    private void setVisitNotDone(Context context, Button dueButton) {
        dueButton.setTextColor(context.getResources().getColor(R.color.progress_orange));
        dueButton.setText(context.getString(R.string.visit_not_done));
        dueButton.setBackgroundColor(context.getResources().getColor(R.color.transparent));
        dueButton.setOnClickListener(null);
    }

    private void setVisitAboveTwentyFourView(Context context, Button dueButton) {
        dueButton.setTextColor(context.getResources().getColor(R.color.alert_complete_green));
        dueButton.setText(context.getString(R.string.visit_done));
        dueButton.setBackgroundColor(context.getResources().getColor(R.color.transparent));
        dueButton.setOnClickListener(null);
    }

    private void setVisitLessTwentyFourView(Context context, Button dueButton, String lastVisitMonth) {
        setVisitAboveTwentyFourView(context, dueButton);
    }

    private void setVisitButtonOverdueStatus(Context context, Button dueButton, String lastVisitDays) {
        dueButton.setTextColor(context.getResources().getColor(R.color.white));
        if (TextUtils.isEmpty(lastVisitDays)) {
            dueButton.setText(context.getString(R.string.record_visit));
        } else {
            dueButton.setText(context.getString(R.string.due_visit, lastVisitDays));
        }

        dueButton.setBackgroundResource(R.drawable.overdue_red_btn_selector);
        dueButton.setOnClickListener(onClickListener);
    }

    private void setVisitButtonDueStatus(Context context, Button dueButton) {
        dueButton.setTextColor(context.getResources().getColor(R.color.alert_in_progress_blue));
        dueButton.setText(context.getString(R.string.record_home_visit));
        dueButton.setBackgroundResource(R.drawable.blue_btn_selector);
        dueButton.setOnClickListener(onClickListener);
    }

    private void attachPatientOnclickListener(View view, SmartRegisterClient client) {
        view.setOnClickListener(onClickListener);
        view.setTag(client);
        view.setTag(R.id.VIEW_ID, BaseFamilyRegisterFragment.CLICK_VIEW_NORMAL);
    }

    private void attachDosageOnclickListener(View view, SmartRegisterClient client) {
        view.setOnClickListener(onClickListener);
        view.setTag(client);
        view.setTag(R.id.VIEW_ID, BaseFamilyRegisterFragment.CLICK_VIEW_DOSAGE_STATUS);
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
        View view = inflater.inflate(R.layout.adapter_child_register_list_row, parent, false);

        /*
        ConfigurableViewsHelper helper = ConfigurableViewsLibrary.getInstance().getConfigurableViewsHelper();
        if (helper.isJsonViewsEnabled()) {

            ViewConfiguration viewConfiguration = helper.getViewConfiguration(Constants.CONFIGURATION.HOME_REGISTER_ROW);
            ViewConfiguration commonConfiguration = helper.getViewConfiguration(COMMON_REGISTER_ROW);

            if (viewConfiguration != null) {
                return helper.inflateDynamicView(viewConfiguration, commonConfiguration, view, R.id.register_columns, false);
            }
        }*/

        return new RegisterViewHolder(view);
    }

    @Override
    public RecyclerView.ViewHolder createFooterHolder(ViewGroup parent) {
        View view = inflater.inflate(R.layout.smart_register_pagination, parent, false);
        return new FooterViewHolder(view);
    }

    @Override
    public boolean isFooterViewHolder(RecyclerView.ViewHolder viewHolder) {
        return FooterViewHolder.class.isInstance(viewHolder);
    }

    ////////////////////////////////////////////////////////////////
    // Inner classes
    ////////////////////////////////////////////////////////////////

    public class RegisterViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewParentName;
        public TextView textViewChildName;
        public TextView textViewAddressGender;
        public Button dueButton;
        public View childColumn;

        public RegisterViewHolder(View itemView) {
            super(itemView);

            textViewParentName = itemView.findViewById(R.id.textview_parent_name);
            textViewChildName = itemView.findViewById(R.id.text_view_child_name);
            textViewAddressGender = itemView.findViewById(R.id.text_view_address_gender);
            dueButton = itemView.findViewById(R.id.due_button);

            childColumn = itemView.findViewById(R.id.child_column);
        }
    }

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

    private class UpdateLastAsyncTask extends AsyncTask<Void, Void, Void> {
        private final Context context;
        private final CommonRepository commonRepository;
        private final RegisterViewHolder viewHolder;
        private final String baseEntityId;
        private final Rules rules;
        private String TAG = UpdateLastAsyncTask.class.getCanonicalName();
        private CommonPersonObject commonPersonObject;
        private ChildVisit childVisit;

        private UpdateLastAsyncTask(Context context, CommonRepository commonRepository, RegisterViewHolder viewHolder, String baseEntityId) {
            this.context = context;
            this.commonRepository = commonRepository;
            this.viewHolder = viewHolder;
            this.baseEntityId = baseEntityId;
            this.rules = ChwApplication.getInstance().getRulesEngineHelper().rules(Constants.RULE_FILE.HOME_VISIT);
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (commonRepository != null) {
                commonPersonObject = commonRepository.findByBaseEntityId(baseEntityId);
                if (commonPersonObject != null) {
                    String strDateCreated = Utils.getValue(commonPersonObject.getColumnmaps(), ChildDBConstants.KEY.DATE_CREATED, false);
                    String lastVisitDate = Utils.getValue(commonPersonObject.getColumnmaps(), ChildDBConstants.KEY.LAST_HOME_VISIT, false);
                    String visitNotDone = Utils.getValue(commonPersonObject.getColumnmaps(), ChildDBConstants.KEY.VISIT_NOT_DONE, false);
                    long lastVisit = 0, visitNot = 0, dateCreated = 0;
                    if (!TextUtils.isEmpty(visitNotDone)) {
                        visitNot = Long.parseLong(visitNotDone);
                    }
                    if (!TextUtils.isEmpty(lastVisitDate)) {
                        lastVisit = Long.parseLong(lastVisitDate);
                    }
                    if (!TextUtils.isEmpty(strDateCreated)) {
                        dateCreated = Utils.dobStringToDateTime(strDateCreated).getMillis();
                    }

                    String dobString = Utils.getDuration(Utils.getValue(commonPersonObject.getColumnmaps(), DBConstants.KEY.DOB, false));
                    childVisit = ChildUtils.getChildVisitStatus(context, rules, dobString, lastVisit, visitNot, dateCreated);

                }
                return null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
            if (commonPersonObject != null) {
                viewHolder.dueButton.setVisibility(View.VISIBLE);
                if (childVisit.getVisitStatus().equalsIgnoreCase(ChildProfileInteractor.VisitType.DUE.name())) {
                    setVisitButtonDueStatus(context, viewHolder.dueButton);
                } else if (childVisit.getVisitStatus().equalsIgnoreCase(ChildProfileInteractor.VisitType.OVERDUE.name())) {
                    setVisitButtonOverdueStatus(context, viewHolder.dueButton, childVisit.getNoOfMonthDue());
                } else if (childVisit.getVisitStatus().equalsIgnoreCase(ChildProfileInteractor.VisitType.LESS_TWENTY_FOUR.name())) {
                    setVisitLessTwentyFourView(context, viewHolder.dueButton, childVisit.getLastVisitDays());
                } else if (childVisit.getVisitStatus().equalsIgnoreCase(ChildProfileInteractor.VisitType.VISIT_THIS_MONTH.name())) {
                    setVisitAboveTwentyFourView(context, viewHolder.dueButton);
                } else if (childVisit.getVisitStatus().equalsIgnoreCase(ChildProfileInteractor.VisitType.NOT_VISIT_THIS_MONTH.name())) {
                    setVisitNotDone(context, viewHolder.dueButton);
                }
            } else {
                viewHolder.dueButton.setVisibility(View.GONE);
                //attachSyncOnclickListener(viewHolder.sync, pc);
            }

        }
    }

}
