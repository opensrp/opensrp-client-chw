package org.smartregister.brac.hnpp.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.brac.hnpp.R;
import org.smartregister.brac.hnpp.activity.HnppFamilyOtherMemberProfileActivity;
import org.smartregister.brac.hnpp.location.SSLocationHelper;
import org.smartregister.brac.hnpp.location.SSLocations;
import org.smartregister.brac.hnpp.location.SSModel;
import org.smartregister.brac.hnpp.model.HnppAllMemberRegisterFragmentModel;
import org.smartregister.brac.hnpp.presenter.HnppAllMemberRegisterFragmentPresenter;
import org.smartregister.brac.hnpp.provider.HnppAllMemberRegisterProvider;
import org.smartregister.brac.hnpp.utils.HnppConstants;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.core.activity.CoreChildHomeVisitActivity;
import org.smartregister.chw.core.fragment.CoreChildRegisterFragment;
import org.smartregister.chw.core.utils.ChildDBConstants;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.configurableviews.model.View;
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.family.util.Constants;
import org.smartregister.family.util.DBConstants;
import org.smartregister.util.Utils;
import org.smartregister.view.activity.BaseRegisterActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import timber.log.Timber;

import static android.view.View.inflate;
import static org.smartregister.chw.core.utils.ChildDBConstants.limitClause;
import static org.smartregister.chw.core.utils.ChildDBConstants.matchPhrase;
import static org.smartregister.chw.core.utils.ChildDBConstants.orderByClause;
import static org.smartregister.chw.core.utils.ChildDBConstants.tableColConcat;

public class HnppAllMemberRegisterFragment extends CoreChildRegisterFragment implements android.view.View.OnClickListener {
    private final String DEFAULT_MAIN_CONDITION = "date_removed is null";
    private String mSelectedVillageName,mSelectedClasterName;
    private TextView textViewVillageNameFilter,textViewClasterNameFilter;
    private ImageView imageViewVillageNameFilter,imageViewClasterNameFilter;
    private ViewGroup clients_header_layout;

    @Override
    protected void initializePresenter() {
        if (getActivity() == null) {
            return;
        }

        String viewConfigurationIdentifier = ((BaseRegisterActivity) getActivity()).getViewIdentifiers().get(0);
        presenter = new HnppAllMemberRegisterFragmentPresenter(this, new HnppAllMemberRegisterFragmentModel(), viewConfigurationIdentifier);

    }

    @Override
    public void goToChildDetailActivity(CommonPersonObjectClient patient, boolean launchDialog) {


        String familyId = Utils.getValue(patient.getColumnmaps(), ChildDBConstants.KEY.RELATIONAL_ID, false);
       // HnppChildProfileActivity.startMe(getActivity(), houseHoldId,false, new MemberObject(patient), HnppChildProfileActivity.class);
        String houseHoldHead = org.smartregister.family.util.Utils.getValue(patient.getColumnmaps(), HnppConstants.KEY.HOUSE_HOLD_NAME, true);
        String address = org.smartregister.family.util.Utils.getValue(patient.getColumnmaps(), HnppConstants.KEY.VILLAGE_NAME, true);

        Intent intent = new Intent(getActivity(), HnppFamilyOtherMemberProfileActivity.class);
        intent.putExtras(getArguments());
        intent.putExtra(Constants.INTENT_KEY.BASE_ENTITY_ID, patient.getCaseId());
        intent.putExtra(Constants.INTENT_KEY.FAMILY_BASE_ENTITY_ID, familyId);
        intent.putExtra(CoreConstants.INTENT_KEY.CHILD_COMMON_PERSON, patient);
        intent.putExtra(Constants.INTENT_KEY.FAMILY_HEAD, familyId);
        intent.putExtra(Constants.INTENT_KEY.PRIMARY_CAREGIVER, familyId);
        intent.putExtra(Constants.INTENT_KEY.VILLAGE_TOWN,address);
        intent.putExtra(Constants.INTENT_KEY.FAMILY_NAME,houseHoldHead);
        startActivity(intent);
    }


    @Override
    public void initializeAdapter(Set<View> visibleColumns) {
        HnppAllMemberRegisterProvider childRegisterProvider = new HnppAllMemberRegisterProvider(getActivity(), commonRepository(), visibleColumns, registerActionHandler, paginationViewHandler);
        clientAdapter = new RecyclerViewPaginatedAdapter(null, childRegisterProvider, context().commonrepository(CoreConstants.TABLE_NAME.FAMILY_MEMBER));
        clientAdapter.setCurrentlimit(20);
        clientsView.setAdapter(clientAdapter);
    }



    @Override
    public void setupViews(android.view.View view) {
        super.setupViews(view);
        ((TextView) view.findViewById(org.smartregister.chw.core.R.id.filter_text_view)).setText("");
        view.findViewById(org.smartregister.chw.core.R.id.filter_sort_layout).setVisibility(android.view.View.VISIBLE);
        android.view.View searchBarLayout = view.findViewById(org.smartregister.family.R.id.search_bar_layout);
        searchBarLayout.setBackgroundResource(org.smartregister.family.R.color.customAppThemeBlue);
        if (getSearchView() != null) {
            getSearchView().setBackgroundResource(org.smartregister.family.R.color.white);
            getSearchView().setCompoundDrawablesWithIntrinsicBounds(org.smartregister.family.R.drawable.ic_action_search, 0, 0, 0);
        }
        dueOnlyLayout.setVisibility(android.view.View.GONE);
        view.findViewById(org.smartregister.chw.core.R.id.filter_sort_layout).setOnClickListener(registerActionHandler);
        clients_header_layout = view.findViewById(org.smartregister.chw.core.R.id.clients_header_layout);
        android.view.View filterView =inflate(getContext(),R.layout.filter_top_view,clients_header_layout);
        textViewVillageNameFilter = filterView.findViewById(R.id.village_name_filter);
        textViewClasterNameFilter = filterView.findViewById(R.id.claster_name_filter);
        imageViewVillageNameFilter = filterView.findViewById(R.id.village_filter_img);
        imageViewClasterNameFilter = filterView.findViewById(R.id.claster_filter_img);
        imageViewVillageNameFilter.setOnClickListener(this);
        imageViewClasterNameFilter.setOnClickListener(this);
        clients_header_layout.getLayoutParams().height = 100;
        clients_header_layout.setVisibility(android.view.View.GONE);

//        TextView dueOnly = ((TextView)view.findViewById(org.smartregister.chw.core.R.id.due_only_text_view));
//        dueOnly.setVisibility(View.VISIBLE);
    }

    @Override
    public void filter(String filterString, String joinTableString, String mainConditionString, boolean qrCode) {
        this.joinTables = new String[]{CoreConstants.TABLE_NAME.FAMILY};
        super.filter(filterString, joinTableString, mainConditionString, qrCode);

    }

    @Override
    public void onClick(android.view.View v) {
        super.onViewClicked(v);
        switch (v.getId()){
            case R.id.village_filter_img:
                mSelectedVillageName ="";
                updateFilterView();
                break;
            case R.id.claster_filter_img:
                mSelectedClasterName = "";
                updateFilterView();
                break;
        }
    }

    @Override
    public void onViewClicked(android.view.View view) {
        super.onViewClicked(view);
        if (view.getTag() != null && view.getTag(R.id.VIEW_ID) == CLICK_VIEW_DOSAGE_STATUS && view.getTag() instanceof CommonPersonObjectClient) {
            CommonPersonObjectClient client = (CommonPersonObjectClient) view.getTag();
            String baseEntityId = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.BASE_ENTITY_ID, true);
            if (StringUtils.isNotBlank(baseEntityId)) {
                CoreChildHomeVisitActivity.startMe(getActivity(), new MemberObject(client), false);
            }
        }
        else if (view.getId() == R.id.filter_sort_layout) {


            ArrayList<String> ssSpinnerArray = new ArrayList<>();


            ArrayList<String> villageSpinnerArray = new ArrayList<>();


            ArrayList<SSModel> ssLocationForms = SSLocationHelper.getInstance().getSsModels();
            for(SSModel ssModel : ssLocationForms){
                ssSpinnerArray.add(ssModel.username);
            }



            ArrayAdapter<String> ssSpinnerArrayAdapter = new ArrayAdapter<String>
                    (getActivity(), android.R.layout.simple_spinner_item,
                            ssSpinnerArray);

            ArrayAdapter<String> villageSpinnerArrayAdapter = new ArrayAdapter<String>
                    (getActivity(), android.R.layout.simple_spinner_item,
                            villageSpinnerArray);

            ArrayAdapter<String> clusterSpinnerArrayAdapter = new ArrayAdapter<String>
                    (getActivity(), android.R.layout.simple_spinner_item,
                            HnppConstants.getClasterSpinnerArray());

            Dialog dialog = new Dialog(getActivity(),android.R.style.Theme_NoTitleBar_Fullscreen);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(org.smartregister.family.R.color.customAppThemeBlue)));
            dialog.setContentView(R.layout.filter_options_dialog);
            Spinner ss_spinner = dialog.findViewById(R.id.ss_filter_spinner);
            Spinner village_spinner = dialog.findViewById(R.id.village_filter_spinner);
            Spinner cluster_spinner = dialog.findViewById(R.id.klaster_filter_spinner);
            village_spinner.setAdapter(villageSpinnerArrayAdapter);
            cluster_spinner.setAdapter(clusterSpinnerArrayAdapter);
            ss_spinner.setAdapter(ssSpinnerArrayAdapter);
            ss_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                    if (position != -1) {
                        villageSpinnerArray.clear();
                        ArrayList<SSLocations> ssLocations = SSLocationHelper.getInstance().getSsModels().get(position).locations;
                        for(SSLocations ssLocations1 : ssLocations){
                            villageSpinnerArray.add(ssLocations1.village.name);
                        }
                        villageSpinnerArrayAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            village_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                    if (position != -1) {
                        mSelectedVillageName = villageSpinnerArray.get(position);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            cluster_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                    if (position != -1) {
                        mSelectedClasterName = HnppConstants.getClasterNames().get(HnppConstants.getClasterSpinnerArray().get(position));
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            Button proceed = dialog.findViewById(R.id.filter_apply_button);
            proceed.setOnClickListener(new android.view.View.OnClickListener() {
                @Override
                public void onClick(android.view.View v) {

                    updateFilterView();
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    }
    protected String filterandSortQuery() {
        SmartRegisterQueryBuilder sqb = new SmartRegisterQueryBuilder(mainSelect);

        String query = "";
        try {
            if (isValidFilterForFts(commonRepository())) {
                String sql = mainFilter(mainCondition, presenter().getMainCondition(CommonFtsObject.searchTableName(CoreConstants.TABLE_NAME.FAMILY_MEMBER)), filters, Sortqueries, clientAdapter.getCurrentlimit(), clientAdapter.getCurrentoffset());
                List<String> ids = commonRepository().findSearchIds(sql);
                query = sqb.toStringFts(ids, tablename, CommonRepository.ID_COLUMN,
                        Sortqueries);
                query = sqb.Endquery(query);
            } else {
                sqb.addCondition(filters);
                query = sqb.orderbyCondition(Sortqueries);
                query = sqb.Endquery(sqb.addlimitandOffset(query, clientAdapter.getCurrentlimit(), clientAdapter.getCurrentoffset()));

            }
        } catch (Exception e) {
            Timber.e(e);
        }

        return query;
    }
    public static String mainFilter(String mainCondition, String mainMemberCondition, String filters, String sort, int limit, int offset) {
        return "SELECT " + CommonFtsObject.idColumn + " FROM " + CommonFtsObject.searchTableName(CoreConstants.TABLE_NAME.FAMILY_MEMBER) + " WHERE " + CommonFtsObject.idColumn + " IN " +
                " ( SELECT " + CommonFtsObject.idColumn + " FROM " + CommonFtsObject.searchTableName(CoreConstants.TABLE_NAME.FAMILY_MEMBER) + " WHERE  " + mainCondition + "  AND " + CommonFtsObject.phraseColumn + matchPhrase(filters) +
                " UNION " +
                " SELECT " + tableColConcat(CommonFtsObject.searchTableName(CoreConstants.TABLE_NAME.FAMILY_MEMBER), CommonFtsObject.idColumn) + " FROM " + CommonFtsObject.searchTableName(CoreConstants.TABLE_NAME.FAMILY_MEMBER) +
                " JOIN " + CommonFtsObject.searchTableName(CoreConstants.TABLE_NAME.FAMILY) + " on " + tableColConcat(CommonFtsObject.searchTableName(CoreConstants.TABLE_NAME.FAMILY_MEMBER), CommonFtsObject.relationalIdColumn) + " = " + tableColConcat(CommonFtsObject.searchTableName(CoreConstants.TABLE_NAME.FAMILY), CommonFtsObject.idColumn) +
                " WHERE  " + mainMemberCondition.trim() + " AND " + tableColConcat(CommonFtsObject.searchTableName(CoreConstants.TABLE_NAME.FAMILY), CommonFtsObject.phraseColumn + matchPhrase(filters)) +
                ")  " + orderByClause(sort) + limitClause(limit, offset);
    }
    public void updateFilterView(){
        if(StringUtils.isEmpty(mSelectedVillageName) && StringUtils.isEmpty(mSelectedClasterName)){
            clients_header_layout.setVisibility(android.view.View.GONE);
        }else {
            clients_header_layout.setVisibility(android.view.View.VISIBLE);
        }
        textViewVillageNameFilter.setText(getString(R.string.filter_village_name,mSelectedVillageName));
        textViewClasterNameFilter.setText(getString(R.string.claster_village_name,HnppConstants.getClusterNameFromValue(mSelectedClasterName)));
        String filterString = getFilterString();
        filter(filterString, "", DEFAULT_MAIN_CONDITION, false);


    }

    public String getFilterString() {
        return StringUtils.isEmpty(mSelectedVillageName) ?
                (StringUtils.isEmpty(mSelectedClasterName) ?
                        "" : mSelectedClasterName) : (StringUtils.isEmpty(mSelectedClasterName) ?
                mSelectedVillageName : " " + mSelectedVillageName + " AND " + mSelectedClasterName + " ");

    }
    @Override
    protected int getToolBarTitle() {
        return R.string.menu_all_member;
    }
}
