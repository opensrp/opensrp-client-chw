package org.smartregister.chw.core.utils;

import org.joda.time.DateTime;
import org.smartregister.chw.core.enums.ImmunizationState;
import org.smartregister.immunization.db.VaccineRepo;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by raihan on 1/15/19.
 */

public class HomeVisitVaccineGroup {
    public static final int TYPE_INACTIVE = 0;//inactive color row
    public static final int TYPE_ACTIVE = 1;//active means row showing with vaccine name like opv,bcg...
    public static final int TYPE_INITIAL = 2;//initial view means row with text like immunization(at birth)
    public static final int TYPE_HIDDEN = 3;//initial view means row with text like immunization(at birth)
    private ArrayList<VaccineRepo.Vaccine> givenVaccines = new ArrayList<VaccineRepo.Vaccine>();
    private ArrayList<VaccineRepo.Vaccine> dueVaccines = new ArrayList<VaccineRepo.Vaccine>();
    private ArrayList<VaccineRepo.Vaccine> notGivenVaccines = new ArrayList<VaccineRepo.Vaccine>();
    private ArrayList<VaccineRepo.Vaccine> notGivenInThisVisitVaccines = new ArrayList<VaccineRepo.Vaccine>();
    private LinkedHashMap<DateTime, ArrayList<VaccineRepo.Vaccine>> groupedByDate = new LinkedHashMap<>();

    private String group = "";
    private int viewType;
    private ImmunizationState alert = ImmunizationState.NO_ALERT;
    private String dueDisplayDate = "";
    private String dueDate = "";

    public LinkedHashMap<DateTime, ArrayList<VaccineRepo.Vaccine>> getGroupedByDate() {
        return groupedByDate;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }


    public ArrayList<VaccineRepo.Vaccine> getNotGivenInThisVisitVaccines() {
        return notGivenInThisVisitVaccines;
    }

    public ArrayList<VaccineRepo.Vaccine> getGivenVaccines() {
        return givenVaccines;
    }


    public ArrayList<VaccineRepo.Vaccine> getDueVaccines() {
        return dueVaccines;
    }


    public ArrayList<VaccineRepo.Vaccine> getNotGivenVaccines() {
        return notGivenVaccines;
    }


    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public ImmunizationState getAlert() {
        return alert;
    }

    public void setAlert(ImmunizationState alert) {
        this.alert = alert;
    }

    public void calculateNotGivenVaccines() {
        for (VaccineRepo.Vaccine vaccine : dueVaccines) {
//            boolean isGiven = false;
//            for (VaccineRepo.Vaccine givenVaccine : givenVaccines) {
//                if (givenVaccine.display().equalsIgnoreCase(vaccine.display())) {
//                    isGiven = true;
//                    break;
//                }
//            }
            if (!notGivenVaccines.contains(vaccine) && !givenVaccines.contains(vaccine)) {
                notGivenVaccines.add(vaccine);
            }
        }
    }

    public String getDueDisplayDate() {
        return dueDisplayDate;
    }

    public void setDueDisplayDate(String dueDate) {
        this.dueDisplayDate = dueDate;
    }
}
