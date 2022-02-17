package org.smartregister.chw.provider;

import static org.smartregister.AllConstants.CLIENT_TYPE;
import static org.smartregister.chw.core.utils.CoreConstants.FORM_CONSTANTS.REMOVE_MEMBER_FORM.RECEIVED_DEATH_CERTIFICATE;
import static org.smartregister.chw.core.utils.Utils.getDuration;
import static org.smartregister.chw.util.Constants.PRENANCY_OUTCOME;
import static org.smartregister.chw.util.CrvsConstants.NO;
import static org.smartregister.chw.util.CrvsConstants.YES;
import static org.smartregister.chw.util.Utils.getClientName;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.Button;

import org.apache.commons.lang3.text.WordUtils;
import org.smartregister.chw.R;
import org.smartregister.chw.anc.util.Constants;
import org.smartregister.chw.core.holders.RegisterViewHolder;
import org.smartregister.chw.core.provider.CoreCertificationRegisterProvider;
import org.smartregister.chw.core.utils.ChildDBConstants;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.util.DBConstants;
import org.smartregister.family.util.Utils;
import org.smartregister.view.contract.SmartRegisterClient;

import java.util.Set;

import timber.log.Timber;

public class DeathCertificationRegisterProvider extends CoreCertificationRegisterProvider {

    private Set<org.smartregister.configurableviews.model.View> visibleColumns;

    private View.OnClickListener onClickListener;

    private Context context;

    public DeathCertificationRegisterProvider(Context context, Set visibleColumns, View.OnClickListener onClickListener, View.OnClickListener paginationClickListener) {
        super(context, visibleColumns, onClickListener, paginationClickListener);
        this.visibleColumns = visibleColumns;
        this.onClickListener = onClickListener;
        this.context = context;
    }

    @Override
    public void getView(Cursor cursor, SmartRegisterClient client, RegisterViewHolder viewHolder) {
        CommonPersonObjectClient pc = (CommonPersonObjectClient) client;
        if (visibleColumns.isEmpty()) {
            populatePatientColumn(pc, client, viewHolder);
            populateLastColumn(pc, viewHolder);
        }
    }

    protected void populatePatientColumn(CommonPersonObjectClient pc, SmartRegisterClient client, RegisterViewHolder viewHolder) {
        try {
            String parentFirstName = Utils.getValue(pc.getColumnmaps(), ChildDBConstants.KEY.FAMILY_FIRST_NAME, true);
            String parentLastName = Utils.getValue(pc.getColumnmaps(), ChildDBConstants.KEY.FAMILY_LAST_NAME, true);
            String parentMiddleName = Utils.getValue(pc.getColumnmaps(), ChildDBConstants.KEY.FAMILY_MIDDLE_NAME, true);

            String dobString = getDuration(Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.DOB, false));
            String firstName = Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.FIRST_NAME, true);
            String middleName = Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.MIDDLE_NAME, true);
            String lastName = Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.LAST_NAME, true);
            String stillBirth = Utils.getValue(pc.getColumnmaps(), PRENANCY_OUTCOME, true);
            String clientType = Utils.getValue(pc.getColumnmaps(), CLIENT_TYPE, false);
            String childName = getClientName(firstName, middleName, lastName);

            switch (clientType) {
                case Constants.TABLES.PREGNANCY_OUTCOME: {
                    String parentName = context.getResources().getString(R.string.care_giver_initials) + ": " + getClientName(parentFirstName, parentMiddleName, parentLastName) + ", [" + stillBirth + "]";
                    fillValue(viewHolder.textViewParentName, WordUtils.capitalize(parentName));
                    String address = Utils.getValue(pc.getColumnmaps(), ChildDBConstants.KEY.FAMILY_HOME_ADDRESS, true);
                    fillValue(viewHolder.textViewAddressGender, address);
                    break;
                }
                case CoreConstants.TABLE_NAME.FAMILY_MEMBER: {
                    viewHolder.textViewChildName.setVisibility(View.GONE);
                    viewHolder.textViewChildAge.setVisibility(View.GONE);
                    String age = WordUtils.capitalize(Utils.getTranslatedDate(dobString, context));
                    String parentName = getClientName(parentFirstName, parentMiddleName, parentLastName);
                    fillValue(viewHolder.textViewParentName, WordUtils.capitalize(parentName) + ", " + age);
                    String address = Utils.getValue(pc.getColumnmaps(), ChildDBConstants.KEY.FAMILY_HOME_ADDRESS, true);
                    String gender_key = Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.GENDER, true);
                    String gender = "";
                    if (gender_key.equalsIgnoreCase("Male") || gender_key.equalsIgnoreCase("Masculin")) {
                        gender = context.getString(org.smartregister.chw.core.R.string.male);
                    } else if (gender_key.equalsIgnoreCase("Female") || gender_key.equalsIgnoreCase("Feminin")) {
                        gender = context.getString(org.smartregister.chw.core.R.string.female);
                    }
                    fillValue(viewHolder.textViewAddressGender, address + " \u00B7 " + gender);
                    break;
                }
                case Constants.TABLES.EC_CHILD: {
                    String parentName = context.getResources().getString(R.string.care_giver_initials) + ": " + getClientName(parentFirstName, parentMiddleName, parentLastName);
                    fillValue(viewHolder.textViewParentName, WordUtils.capitalize(parentName));
                    String age = WordUtils.capitalize(Utils.getTranslatedDate(dobString, context));
                    fillValue(viewHolder.textViewChildName, WordUtils.capitalize(childName) + ", " + age);
                    String address = Utils.getValue(pc.getColumnmaps(), ChildDBConstants.KEY.FAMILY_HOME_ADDRESS, true);
                    String gender_key = Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.GENDER, true);
                    String gender = "";
                    if (gender_key.equalsIgnoreCase("Male") || gender_key.equalsIgnoreCase("Masculin")) {
                        gender = context.getString(org.smartregister.chw.core.R.string.male);
                    } else if (gender_key.equalsIgnoreCase("Female") || gender_key.equalsIgnoreCase("Feminin")) {
                        gender = context.getString(org.smartregister.chw.core.R.string.female);
                    }
                    fillValue(viewHolder.textViewAddressGender, address + " \u00B7 " + gender);
                    break;
                }
                case CoreConstants.TABLE_NAME.OUT_OF_AREA_DEATH: {
                    viewHolder.textViewChildName.setVisibility(View.GONE);
                    viewHolder.textViewChildAge.setVisibility(View.GONE);
                    String age = WordUtils.capitalize(Utils.getTranslatedDate(dobString, context));
                    String personName = firstName;
                    fillValue(viewHolder.textViewParentName, WordUtils.capitalize(personName) + ", " + age);
                    String address = Utils.getValue(pc.getColumnmaps(), ChildDBConstants.KEY.FAMILY_HOME_ADDRESS, true);
                    String gender_key = Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.GENDER, true);
                    String gender = "";
                    if (gender_key.equalsIgnoreCase("Male") || gender_key.equalsIgnoreCase("Masculin")) {
                        gender = context.getString(org.smartregister.chw.core.R.string.male);
                    } else if (gender_key.equalsIgnoreCase("Female") || gender_key.equalsIgnoreCase("Feminin")) {
                        gender = context.getString(org.smartregister.chw.core.R.string.female);
                    }
                    fillValue(viewHolder.textViewAddressGender, address + " \u00B7 " + gender);
                    break;
                }
                default:
                    Timber.log(0, "Patient column not updated");
                    break;
            }

            addStatusButtonClickListener(client, viewHolder);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void populateLastColumn(CommonPersonObjectClient pc, RegisterViewHolder viewHolder) {
        Button viewHolderDueBtn = viewHolder.dueButton;
        if (pc != null) {
            viewHolder.dueButton.setVisibility(View.VISIBLE);
            String received_death_certificate = Utils.getValue(pc.getColumnmaps(), RECEIVED_DEATH_CERTIFICATE, false);
            if (YES.equalsIgnoreCase(received_death_certificate)) {
                setReceivedButtonColor(context, viewHolderDueBtn);
            } else if (NO.equalsIgnoreCase(received_death_certificate)) {
                setNotReceivedButtonColor(context, viewHolderDueBtn);
            } else {
                setUpdateStatusButtonColor(context, viewHolderDueBtn);
            }
        } else {
            viewHolderDueBtn.setVisibility(View.GONE);
        }
    }

    @Override
    public void setNotReceivedButtonColor(Context context, Button dueButton) {
        updateButton(dueButton, context.getString(org.smartregister.chw.core.R.string.certificate_not_received),
                context.getResources().getColor(org.smartregister.chw.core.R.color.black), 0);
    }
}
