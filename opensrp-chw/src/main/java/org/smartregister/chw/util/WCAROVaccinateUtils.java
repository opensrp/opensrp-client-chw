package org.smartregister.chw.util;

import org.smartregister.immunization.db.VaccineRepo;


public class WCAROVaccinateUtils  {

    public static String stateKey(VaccineRepo.Vaccine vaccine) {

        switch (vaccine) {
            case opv0:
            case bcg:
                return "at birth";

            case opv1:
            case penta1:
            case pcv1:
            case rota1:
                return "6 weeks";

            case opv2:
            case penta2:
            case pcv2:
            case rota2:
                return "10 weeks";

            case opv3:
            case penta3:
            case pcv3:
            case ipv:
                return "14 weeks";

            case measles1:
            case mr1:
            case opv4:
                return "9 months";

            case measles2:
            case mr2:
                return "15 months";
            default:
                break;
        }

        return "";
    }
}
