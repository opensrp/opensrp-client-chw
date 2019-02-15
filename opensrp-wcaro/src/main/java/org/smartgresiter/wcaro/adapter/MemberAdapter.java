package org.smartgresiter.wcaro.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.smartgresiter.wcaro.R;
import org.smartgresiter.wcaro.util.Constants;
import org.smartregister.family.util.DBConstants;
import org.smartregister.family.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.MyViewHolder> {

    private List<HashMap<String, String>> familyMembers;
    private View.OnClickListener clickListener;
    private Context context;

    private Integer selected = -1;

    Animation slideUp;
    Animation slideDown;
    List<String> eduOptions;

    public MemberAdapter(Context context, List<HashMap<String, String>> myDataset, View.OnClickListener clickListener) {
        familyMembers = myDataset;
        this.clickListener = clickListener;
        this.context = context;

        slideUp = AnimationUtils.loadAnimation(context, R.anim.slide_up);
        slideDown = AnimationUtils.loadAnimation(context, R.anim.slide_up);
    }

    public Integer getSelected() {
        return selected;
    }

    public void setSelected(Integer selected) {
        this.selected = selected;
    }

    @NonNull
    @Override
    public MemberAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.change_member_list, parent, false);
        return new MemberAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        final HashMap<String, String> model = familyMembers.get(position);

        String dob = ((!model.containsKey(DBConstants.KEY.DOB) || model.get(DBConstants.KEY.DOB) == "null") ? "" : model.get(DBConstants.KEY.DOB));
        String dobString = Utils.getDuration(dob);
        dobString = dobString.contains("y") ? dobString.substring(0, dobString.indexOf("y")) : dobString;

        String dod = ((!model.containsKey(DBConstants.KEY.DOD) || model.get(DBConstants.KEY.DOD) == "null") ? "" : model.get(DBConstants.KEY.DOD));

        if (StringUtils.isNotBlank(dod)) {
            dobString = Utils.getDuration(dod, dob);
            dobString = dobString.contains("y") ? dobString.substring(0, dobString.indexOf("y")) : dobString;
        }

        holder.tvName.setText(
                String.format("%s %s %s, %s",
                        ((!model.containsKey(DBConstants.KEY.FIRST_NAME) || model.get(DBConstants.KEY.FIRST_NAME).equals("null")) ? "" : model.get(DBConstants.KEY.FIRST_NAME)),
                        ((!model.containsKey(DBConstants.KEY.MIDDLE_NAME) || model.get(DBConstants.KEY.MIDDLE_NAME).equals("null")) ? "" : model.get(DBConstants.KEY.MIDDLE_NAME)),
                        ((!model.containsKey(DBConstants.KEY.LAST_NAME) || model.get(DBConstants.KEY.LAST_NAME).equals("null")) ? "" : model.get(DBConstants.KEY.LAST_NAME)),
                        dobString
                )
        );
        holder.llQuestions.setVisibility((selected == position) ? View.VISIBLE : View.GONE);

        holder.radioButton.setChecked((selected == position));

        holder.radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    setSelected(position);
                    onBindViewHolder(holder, position);

                    try {
                        notifyDataSetChanged();
                    } catch (Exception e) {

                    }
                }
            }
        });


        Boolean isVisible = (holder.llQuestions.getVisibility() == View.VISIBLE);
        if ((selected == position) && !isVisible) {
            holder.llQuestions.setVisibility(View.VISIBLE);
            holder.llQuestions.startAnimation(slideDown);
        }

        if ((selected != position) && isVisible) {
            holder.llQuestions.setVisibility(View.GONE);
            holder.llQuestions.startAnimation(slideUp);
        }


        holder.view.setOnClickListener(clickListener);

        renderViews(holder, model);
    }

    private void renderViews(final MyViewHolder holder, HashMap<String, String> model) {
        String phoneNumber = model.get(DBConstants.KEY.PHONE_NUMBER);
        phoneNumber = (StringUtils.equalsIgnoreCase(phoneNumber, "null") ? "" : phoneNumber);

        String otherPhoneNumber = model.get(DBConstants.KEY.OTHER_PHONE_NUMBER);
        otherPhoneNumber = (StringUtils.equalsIgnoreCase(otherPhoneNumber, "null") ? "" : otherPhoneNumber);

        holder.etPhone.setText(phoneNumber);
        holder.etAlternatePhone.setText(otherPhoneNumber);

        String highestEduLevel = model.get(DBConstants.KEY.HIGHEST_EDU_LEVEL);
        if (StringUtils.isNotBlank(highestEduLevel)) {
            switch (highestEduLevel) {
                case "None":
                    holder.spEduLevel.setSelection(0);
                    break;
                case "Primary":
                    holder.spEduLevel.setSelection(1);
                    break;
                case "Secondary":
                    holder.spEduLevel.setSelection(2);
                    break;
                case "Post-secondary":
                    holder.spEduLevel.setSelection(3);
                    break;
                default:

                    holder.spEduLevel.setSelection(0);
                    break;
            }
        }
    }

    public boolean validateSave(MyViewHolder holder) {
        boolean res = true;

        if (holder != null) {

            String text = holder.etPhone.getText().toString().trim();
            if (text.length() > 0 && !text.substring(0, 1).equals("0")) {
                holder.etPhone.setError("Must start with 0");
                res = false;
            }

            if (text.length() != 10) {
                holder.etPhone.setError("Length must be equal to 10");
                res = false;
            }

            text = holder.etAlternatePhone.getText().toString().trim();
            if (text.length() > 0 && !text.substring(0, 1).equals("0")) {
                holder.etAlternatePhone.setError("Must start with 0");
                res = false;
            }

            if (text.length() > 0 && text.length() != 10) {
                holder.etAlternatePhone.setError("Length must be equal to 10");
                res = false;
            }

            if (!res) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                builder1.setMessage("Kindly complete the form before submitting");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Dismiss",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
        }
        return res;
    }

    public HashMap<String, String> getSelectedResults(MyViewHolder holder, int Position) {
        HashMap<String, String> res = new HashMap<>();

        res.put(DBConstants.KEY.BASE_ENTITY_ID, familyMembers.get(Position).get(DBConstants.KEY.BASE_ENTITY_ID));
        res.put(Constants.JsonAssets.FAMILY_MEMBER.PHONE_NUMBER, holder.etPhone.getText().toString());
        res.put(Constants.JsonAssets.FAMILY_MEMBER.OTHER_PHONE_NUMBER, holder.etAlternatePhone.getText().toString());
        res.put(Constants.JsonAssets.FAMILY_MEMBER.HIGHEST_EDUCATION_LEVEL, holder.spEduLevel.getSelectedItem().toString());

        return res;
    }

    private List<String> getOptions() {
        if (eduOptions == null) {
            eduOptions = new ArrayList<>();
            eduOptions.add("None");
            eduOptions.add("Primary");
            eduOptions.add("Secondary");
            eduOptions.add("Post-secondary");
        }
        return eduOptions;
    }

    @Override
    public int getItemCount() {
        return familyMembers.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName, tvGender;
        public RadioButton radioButton;
        public LinearLayout llQuestions, llNewPhone, llAltPhone, llHighestEduLevel;
        public View view;
        public EditText etPhone, etAlternatePhone;
        public Spinner spEduLevel;

        private MyViewHolder(View view) {
            super(view);
            this.view = view;

            tvName = view.findViewById(R.id.tvName);
            tvGender = view.findViewById(R.id.tvGender);
            radioButton = view.findViewById(R.id.rbButton);
            llQuestions = view.findViewById(R.id.llQuestions);

            llNewPhone = view.findViewById(R.id.llNewNumber);
            llAltPhone = view.findViewById(R.id.llOtherNumber);
            llHighestEduLevel = view.findViewById(R.id.llHighestEduLevel);

            etPhone = view.findViewById(R.id.etPhoneNumber);
            etAlternatePhone = view.findViewById(R.id.etOtherNumber);
            spEduLevel = view.findViewById(R.id.spEducationLevel);

            ArrayAdapter<String> adp1 = new ArrayAdapter<>(context,
                    android.R.layout.simple_list_item_1, getOptions());
            adp1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spEduLevel.setAdapter(adp1);

            setLengthErrorMessage(etPhone);
            setLengthErrorMessage(etAlternatePhone);

        }

        private void setLengthErrorMessage(final EditText et) {
            String error = "Length must be equal to 10";

            TextWatcher tw = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    String text = et.getText().toString().trim();
                    if (text.length() > 0 && text.length() != 10) {
                        et.setError("Length must be equal to 10");
                    }
                    if (text.length() > 0 && !text.substring(0, 1).equals("0")) {
                        et.setError("Must start with 0");
                    }
                }
            };

            et.addTextChangedListener(tw);
        }

    }

}