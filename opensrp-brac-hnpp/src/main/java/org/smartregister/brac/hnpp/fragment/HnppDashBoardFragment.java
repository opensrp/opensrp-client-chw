package org.smartregister.brac.hnpp.fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import org.smartregister.brac.hnpp.R;

import java.util.Calendar;

public class HnppDashBoardFragment extends Fragment implements View.OnClickListener {

    private Button fromBtn,toBtn;
    private RecyclerView recyclerView;
    private int date, month, year;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard,null);
        fromBtn = view.findViewById(R.id.from);
        toBtn = view.findViewById(R.id.to);
        fromBtn.setOnClickListener(this);
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH)+1;
        date = calendar.get(Calendar.DAY_OF_MONTH);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.from:
                DatePickerDialog fromDialog = new DatePickerDialog(getActivity(), R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                    }
                },year,month,date);
                fromDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                fromDialog.show();
                break;
            case R.id.to:
                break;
        }
    }
}
