package org.smartregister.brac.hnpp.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.brac.hnpp.R;
import org.smartregister.brac.hnpp.location.SSLocationForm;
import org.smartregister.brac.hnpp.location.SSLocationHelper;
import org.smartregister.brac.hnpp.model.HnppFamilyRegisterFragmentModel;
import org.smartregister.brac.hnpp.presenter.HnppFamilyRegisterFragmentPresenter;
import org.smartregister.brac.hnpp.provider.HHRegisterProvider;
import org.smartregister.brac.hnpp.utils.HnppConstants;
import org.smartregister.chw.core.fragment.CoreFamilyRegisterFragment;
import org.smartregister.chw.core.provider.CoreRegisterProvider;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;
import org.smartregister.family.util.Constants;
import org.smartregister.family.util.DBConstants;
import org.smartregister.family.util.Utils;

import java.util.ArrayList;
import java.util.Set;

public class FamilyRegisterFragment extends CoreFamilyRegisterFragment {

    private final String DEFAULT_MAIN_CONDITION = "date_removed is null";
    String filterString = "";
    private String selected_village_name = "";
    private String selected_claster_name = "";

    @Override
    public void initializeAdapter(Set<org.smartregister.configurableviews.model.View> visibleColumns) {
        CoreRegisterProvider chwRegisterProvider = new HHRegisterProvider(getActivity(), commonRepository(), visibleColumns, registerActionHandler, paginationViewHandler);
        clientAdapter = new RecyclerViewPaginatedAdapter(null, chwRegisterProvider, context().commonrepository(this.tablename));
        clientAdapter.setCurrentlimit(20);
        clientsView.setAdapter(clientAdapter);
    }

    @Override
    protected void initializePresenter() {
        if (getActivity() == null) {
            return;
        }
        presenter = new HnppFamilyRegisterFragmentPresenter(this, new HnppFamilyRegisterFragmentModel(), null);
    }

    @Override
    protected void goToPatientDetailActivity(CommonPersonObjectClient patient, boolean goToDuePage) {
        Intent intent = new Intent(getActivity(), Utils.metadata().profileActivity);
        intent.putExtra(Constants.INTENT_KEY.FAMILY_BASE_ENTITY_ID, patient.getCaseId());
        intent.putExtra(Constants.INTENT_KEY.FAMILY_HEAD, Utils.getValue(patient.getColumnmaps(), DBConstants.KEY.FAMILY_HEAD, false));
        intent.putExtra(Constants.INTENT_KEY.PRIMARY_CAREGIVER, Utils.getValue(patient.getColumnmaps(), DBConstants.KEY.PRIMARY_CAREGIVER, false));
        intent.putExtra(Constants.INTENT_KEY.VILLAGE_TOWN, Utils.getValue(patient.getColumnmaps(), DBConstants.KEY.VILLAGE_TOWN, false));
        intent.putExtra(Constants.INTENT_KEY.FAMILY_NAME, Utils.getValue(patient.getColumnmaps(), DBConstants.KEY.FIRST_NAME, false));
        intent.putExtra(Constants.INTENT_KEY.GO_TO_DUE_PAGE, goToDuePage);
        intent.putExtra(DBConstants.KEY.UNIQUE_ID, Utils.getValue(patient.getColumnmaps(), DBConstants.KEY.UNIQUE_ID, false));
        intent.putExtra(HnppConstants.KEY.MODULE_ID, Utils.getValue(patient.getColumnmaps(), HnppConstants.KEY.MODULE_ID, false));
        startActivity(intent);
    }

    @Override
    public void setupViews(View view) {
        super.setupViews(view);
        ((TextView) view.findViewById(org.smartregister.chw.core.R.id.filter_text_view)).setText("");
        view.findViewById(org.smartregister.chw.core.R.id.filter_sort_layout).setVisibility(View.VISIBLE);
        dueOnlyLayout.setVisibility(View.GONE);
        view.findViewById(org.smartregister.chw.core.R.id.filter_sort_layout).setOnClickListener(registerActionHandler);
        ViewGroup clients_header_layout = view.findViewById(org.smartregister.chw.core.R.id.clients_header_layout);
        clients_header_layout.setVisibility(View.VISIBLE);
        TextView tv = new TextView(getActivity());
        tv.setBackgroundColor(getActivity().getApplicationContext().getResources().getColor(android.R.color.holo_orange_light));
        tv.setText("Filter Selected");
        clients_header_layout.addView(tv);
//        TextView dueOnly = ((TextView)view.findViewById(org.smartregister.chw.core.R.id.due_only_text_view));
//        dueOnly.setVisibility(View.VISIBLE);
    }

    @Override
    public void filter(String filterString, String joinTableString, String mainConditionString, boolean qrCode) {
        super.filter(filterString, joinTableString, mainConditionString, qrCode);

    }

    @Override
    public void onViewClicked(View view) {
        super.onViewClicked(view);
        if (view.getId() == R.id.filter_sort_layout) {


            ArrayList<String> villageSpinnerArray = new ArrayList<>();
            ArrayList<String> clusterSpinnerArray = new ArrayList<>();

            ArrayList<SSLocationForm> ssLocationForms = SSLocationHelper.getInstance().getSsLocationForms();
            for (int i = 0; i < ssLocationForms.size(); i++) {
                villageSpinnerArray.add(ssLocationForms.get(i).locations.village.name);
            }

            clusterSpinnerArray.add("ক্লাস্টার ১");
            clusterSpinnerArray.add("ক্লাস্টার ২");
            clusterSpinnerArray.add("ক্লাস্টার ৩");
            clusterSpinnerArray.add("ক্লাস্টার ৪");
            clusterSpinnerArray.add("ক্লাস্টার ৫");

            ArrayAdapter<String> villageSpinnerArrayAdapter = new ArrayAdapter<String>
                    (getActivity(), android.R.layout.simple_spinner_item,
                            villageSpinnerArray);

            ArrayAdapter<String> clusterSpinnerArrayAdapter = new ArrayAdapter<String>
                    (getActivity(), android.R.layout.simple_spinner_item,
                            clusterSpinnerArray);

            Dialog dialog = new Dialog(getActivity(),android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT);
            dialog.setContentView(R.layout.filter_options_dialog);

            Spinner village_spinner = dialog.findViewById(R.id.village_filter_spinner);
            Spinner cluster_spinner = dialog.findViewById(R.id.klaster_filter_spinner);
            village_spinner.setAdapter(villageSpinnerArrayAdapter);
            cluster_spinner.setAdapter(clusterSpinnerArrayAdapter);


            village_spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (position != -1) {
                        selected_village_name = villageSpinnerArray.get(position);
                    }
                }
            });
            cluster_spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (position != -1) {
                        selected_claster_name = clusterSpinnerArray.get(position);
                    }
                }
            });
            Button proceed = dialog.findViewById(R.id.filter_apply_button);
            proceed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String filterString = getFilterString();
                    //filter(" village_name like '%AYUBPUR:WARD 1:GA 1%' AND claster like '%ক্লাস্টার ১%' ","","",false);
                    filter(filterString, "", DEFAULT_MAIN_CONDITION, false);
                    dialog.dismiss();
                }
            });
            dialog.show();
            Toast.makeText(getContext(), "sdfdafd", Toast.LENGTH_SHORT).show();
        }
    }

    public String getFilterString() {
        return StringUtils.isEmpty(selected_village_name) ?
                (StringUtils.isEmpty(selected_claster_name) ?
                        "" : selected_claster_name) : (StringUtils.isEmpty(selected_claster_name) ?
                selected_village_name : " " + selected_village_name + " AND " + selected_claster_name + " ");

    }

}
