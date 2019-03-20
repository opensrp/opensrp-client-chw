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
import org.smartgresiter.wcaro.domain.FamilyMember;
import org.smartregister.family.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.MyViewHolder> {

    public String TAG = MemberAdapter.class.getCanonicalName();
    private List<FamilyMember> familyMembers;
    private MyViewHolder currentViewHolder;
    private Context context;

    private String selected = null;

    Animation slideUp;
    Animation slideDown;
    List<String> eduOptions;

    public MemberAdapter(Context context, List<FamilyMember> myDataset) {
        familyMembers = myDataset;
        this.context = context;

        slideUp = AnimationUtils.loadAnimation(context, R.anim.slide_up);
        slideDown = AnimationUtils.loadAnimation(context, R.anim.slide_up);
    }

    public String getSelected() {
        return selected;
    }

    public void setSelected(MyViewHolder view, String selected) {
        currentViewHolder = view;
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
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

        final FamilyMember model = familyMembers.get(position);

        String dobString = Utils.getDuration(model.getDob());
        dobString = dobString.contains("y") ? dobString.substring(0, dobString.indexOf("y")) : dobString;

        if (StringUtils.isNotBlank(model.getDod())) {
            dobString = Utils.getDuration(model.getDod(), model.getDob());
            dobString = dobString.contains("y") ? dobString.substring(0, dobString.indexOf("y")) : dobString;
        }

        holder.tvName.setText(String.format("%s , %s", model.getFullNames(), dobString));
        holder.llQuestions.setVisibility(model.getMemberID().equals(selected) ? View.VISIBLE : View.GONE);
        holder.radioButton.setChecked(model.getMemberID().equals(selected));

        holder.radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    redrawView(holder, model);
                }
            }
        });
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redrawView(holder, model);
            }
        });
        renderViews(holder, model);
    }

    private void redrawView(MyViewHolder holder, FamilyMember model) {
        if (currentViewHolder != null) {
            currentViewHolder.radioButton.setChecked(false);
            currentViewHolder.llQuestions.setVisibility(View.GONE);
            currentViewHolder.llQuestions.startAnimation(slideUp);
        }

        setSelected(holder, model.getMemberID());

        boolean isVisible = (holder.llQuestions.getVisibility() == View.VISIBLE);
        if (model.getMemberID().equals(selected) && !isVisible) {
            holder.llQuestions.setVisibility(View.VISIBLE);
            holder.llQuestions.startAnimation(slideDown);
        }

        if (model.getMemberID().equals(selected) && isVisible) {
            holder.llQuestions.setVisibility(View.GONE);
            holder.llQuestions.startAnimation(slideUp);
        }

        holder.radioButton.setChecked(model.getMemberID().equals(selected));
    }

    private void renderViews(final MyViewHolder holder, FamilyMember model) {
        holder.etPhone.setText(model.getPhone());
        holder.etAlternatePhone.setText(model.getOtherPhone());
        if (StringUtils.isNotBlank(model.getEduLevel())) {
            switch (model.getEduLevel()) {
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

    public boolean validateSave() {
        if (currentViewHolder == null) {
            return false;
        }

        boolean res = validateTextView(currentViewHolder.etPhone);
        res = (res && validateTextView(currentViewHolder.etAlternatePhone));

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
        return res;
    }

    private boolean validateTextView(TextView textView){
        String text = textView.getText().toString().trim();
        if (text.length() > 0 && !text.substring(0, 1).equals("0")) {
            textView.setError("Must start with 0");
            return false;
        }

        if (text.length() > 0 && text.length() != 10) {
            textView.setError("Length must be equal to 10");
            return false;
        }
        return true;
    }

    public FamilyMember getSelectedResults() {

        for (FamilyMember m : familyMembers) {
            if (m.getMemberID().equals(getSelected())) {

                m.setPhone(currentViewHolder.etPhone.getText().toString());
                m.setOtherPhone(currentViewHolder.etAlternatePhone.getText().toString());
                m.setEduLevel(currentViewHolder.spEduLevel.getSelectedItem().toString());
                return m;
            }
        }

        return null;
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