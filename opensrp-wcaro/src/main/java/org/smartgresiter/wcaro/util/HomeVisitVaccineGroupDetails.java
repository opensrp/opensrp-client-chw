package org.smartgresiter.wcaro.util;

import org.smartregister.immunization.db.VaccineRepo;

import java.util.ArrayList;

/**
 * Created by raihan on 1/15/19.
 */

public class HomeVisitVaccineGroupDetails {
    ArrayList<VaccineRepo.Vaccine> givenVaccines = new ArrayList<VaccineRepo.Vaccine>();
    ;
    ArrayList<VaccineRepo.Vaccine> dueVaccines = new ArrayList<VaccineRepo.Vaccine>();
    ;
    ArrayList<VaccineRepo.Vaccine> notGivenVaccines = new ArrayList<VaccineRepo.Vaccine>();
    ArrayList<VaccineRepo.Vaccine> notGivenInThisVisitVaccines = new ArrayList<VaccineRepo.Vaccine>();
    String group = "";
    ImmunizationState alert = ImmunizationState.NO_ALERT;
    private String dueDate = "";


    public ArrayList<VaccineRepo.Vaccine> getNotGivenInThisVisitVaccines() {
        return notGivenInThisVisitVaccines;
    }

    public void setNotGivenInThisVisitVaccines(ArrayList<VaccineRepo.Vaccine> notGivenInThisVisitVaccines) {
        this.notGivenInThisVisitVaccines = notGivenInThisVisitVaccines;
    }




    public ArrayList<VaccineRepo.Vaccine> getGivenVaccines() {
        return givenVaccines;
    }

    public void setGivenVaccines(ArrayList<VaccineRepo.Vaccine> givenVaccines) {
        this.givenVaccines = givenVaccines;
    }

    public ArrayList<VaccineRepo.Vaccine> getDueVaccines() {
        return dueVaccines;
    }

    public void setDueVaccines(ArrayList<VaccineRepo.Vaccine> dueVaccines) {
        this.dueVaccines = dueVaccines;
    }

    public ArrayList<VaccineRepo.Vaccine> getNotGivenVaccines() {
        return notGivenVaccines;
    }

    public void setNotGivenVaccines(ArrayList<VaccineRepo.Vaccine> notGivenVaccines) {
        this.notGivenVaccines = notGivenVaccines;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public ImmunizationState getAlert() {
        return alert;
    }

    public void setAlert(ImmunizationState alert) {
        this.alert = alert;
    }

    public void calculateNotGivenVaccines() {
        for (VaccineRepo.Vaccine vaccine : dueVaccines) {
            boolean isGiven = false;
            for (VaccineRepo.Vaccine givenVaccine : givenVaccines) {
                if (givenVaccine.display().equalsIgnoreCase(vaccine.display())) {
                    isGiven = true;
                }
            }
            if (!isGiven) {
                notGivenVaccines.add(vaccine);
            }
        }
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
       this.dueDate = dueDate;
    }
}
