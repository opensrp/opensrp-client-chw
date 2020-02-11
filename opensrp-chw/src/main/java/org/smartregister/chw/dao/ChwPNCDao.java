package org.smartregister.chw.dao;

import android.util.Pair;

import com.google.android.gms.vision.L;

import net.sqlcipher.database.SQLiteDatabase;

import org.jetbrains.annotations.Nullable;
import org.smartregister.chw.anc.domain.VisitDetail;
import org.smartregister.chw.domain.PNCHealthFacilityVisitSummary;
import org.smartregister.dao.AbstractDao;
import org.smartregister.immunization.domain.Vaccine;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class ChwPNCDao extends AbstractDao {

    public static @Nullable PNCHealthFacilityVisitSummary getLastHealthFacilityVisitSummary(String baseEntityID) {
        return getLastHealthFacilityVisitSummary(baseEntityID, null);
    }

    public static @Nullable PNCHealthFacilityVisitSummary getLastHealthFacilityVisitSummary(String baseEntityID, SQLiteDatabase sqLiteDatabase) {
        String sql = "select  last_health_facility_visit_date , confirmed_health_facility_visits, delivery_date from ec_pregnancy_outcome " +
                "where base_entity_id = '" + baseEntityID + "'" + " COLLATE NOCASE ";

        DataMap<PNCHealthFacilityVisitSummary> dataMap = c -> {
            try {
                return new PNCHealthFacilityVisitSummary(
                        getCursorValue(c, "delivery_date"),
                        getCursorValue(c, "last_health_facility_visit_date"),
                        getCursorValue(c, "confirmed_health_facility_visits")
                );
            } catch (ParseException e) {
                Timber.e(e);
            }
            return null;
        };
        List<PNCHealthFacilityVisitSummary> res;
        if (sqLiteDatabase != null) {
            res = AbstractDao.readData(sql, dataMap, sqLiteDatabase);
        } else {
            res = AbstractDao.readData(sql, dataMap);
        }
        return (res != null && res.size() > 0) ? res.get(0) : null;
    }

    public static @Nullable List<VisitDetail> getAllPNCHealthFacilityVisits(String motherBaseEntityId){
        String sql = "SELECT vd.visit_key, vd.details\n" +
                " FROM Visit_details vd  \n" +
                " INNER JOIN visits v \n" +
                " on v.visit_id = vd.visit_id\n" +
                " AND v.visit_type = 'PNC Home Visit'\n" +
                " AND v.base_entity_id   = '" + motherBaseEntityId + "'" +
                " AND vd.visit_key LIKE 'pnc_hf_visit%'";

        List<VisitDetail> details = new ArrayList<>();
        DataMap<VisitDetail> dataMap =  c -> {
            VisitDetail detail = new VisitDetail();
            detail.setVisitKey(getCursorValue(c, "visit_key"));
            detail.setDetails(getCursorValue(c, "details"));
            details.add(detail);
            return detail;
        };
        List<VisitDetail> res = AbstractDao.readData(sql, dataMap);
        if (res == null || res.size() == 0)
            return new ArrayList<>();

        return details;
    }


    public static Map<String,String> getPNCImmunizationAtBirth(String babyBaseEntityId){
        String sql = "SELECT vd.visit_key, vd.details " +
                "FROM Visit_details vd  INNER JOIN visits v on v.visit_id = vd.visit_id " +
                "AND v.visit_type = 'Immunization Visit' " +
                "AND v.base_entity_id = '" + babyBaseEntityId + "'";


        DataMap<Pair<String, String>> dataMap = c -> Pair.create(getCursorValue(c, "visit_key"), getCursorValue(c, "details"));

        Map<String, String> immunizations = new HashMap<>();
        List<Pair<String, String>> pairs = AbstractDao.readData(sql, dataMap);
        if (pairs == null || pairs.size() == 0)
            return immunizations;

        for (Pair<String, String> pair : pairs) {
            immunizations.put(pair.first, pair.second);
        }

        return immunizations;
    }

    public static List<Vaccine> getPncChildVaccines(String babyBaseEntityId){
        String sql = "SELECT name FROM vaccines\n" +
                "WHERE base_entity_id = '" + babyBaseEntityId + "'" +
                "AND ( name LIKE 'bcg%' OR name LIKE 'opv%')";

        List<Vaccine> vaccines = new ArrayList<>();
        DataMap<Vaccine> dataMap =  c -> {
            Vaccine vaccine = new Vaccine();
            vaccine.setName(getCursorValue(c, "name"));
            vaccines.add(vaccine);
            return vaccine;
        };
        List<Vaccine> res = AbstractDao.readData(sql, dataMap);
        if (res == null || res.size() == 0)
            return new ArrayList<>();

        return vaccines;
    }

    public interface Flavor {

    }
}
