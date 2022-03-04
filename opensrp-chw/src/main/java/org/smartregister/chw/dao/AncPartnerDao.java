package org.smartregister.chw.dao;

import org.smartregister.dao.AbstractDao;

import java.util.List;

public class AncPartnerDao extends AbstractDao {

    public static boolean isPartnerRegistered(String referralFormSubmissionId) {
        DataMap<String> dataMap = cursor -> getCursorValue(cursor, "partner_base_entity_id");

        String sql = String.format(
                "SELECT partner_base_entity_id FROM %s WHERE referral_form_id = '%s' " +
                        "AND partner_base_entity_id is not null " +
                        "AND is_closed = 0",
                "ec_anc_partner_community_feedback",
                referralFormSubmissionId
        );

        List<String> res = readData(sql, dataMap);

        return res.size() == 1;
    }

    public static boolean hasPartnerAgreeForRegistration(String referralFormSubmissionId) {
        DataMap<String> dataMap = cursor -> getCursorValue(cursor, "partner_agree_attending_hf");

        String sql = String.format(
                "SELECT partner_agree_attending_hf FROM %s WHERE referral_form_id = '%s' " +
                        "AND partner_agree_attending_hf is not null " +
                        "AND is_closed = 0",
                "ec_anc_partner_community_feedback",
                referralFormSubmissionId
        );

        List<String> res = readData(sql, dataMap);

        if (res.size() > 0) {
            return res.get(0).equalsIgnoreCase("yes");
        }
        return false;
    }

    public static boolean isPartnerFollowedUp(String referralFormSubmissionId) {
        DataMap<String> dataMap = cursor -> getCursorValue(cursor, "client_found");

        String sql = String.format(
                "SELECT client_found FROM %s WHERE referral_form_id = '%s' " +
                        "AND client_found is not null " +
                        "AND is_closed = 0",
                "ec_anc_partner_community_feedback",
                referralFormSubmissionId
        );

        List<String> res = readData(sql, dataMap);

        return res.size() > 0;
    }

    public static String getFeedbackFormId(String referralFormSubmissionId) {
        DataMap<String> dataMap = cursor -> getCursorValue(cursor, "base_entity_id");

        String sql = String.format(
                "SELECT base_entity_id FROM %s WHERE referral_form_id = '%s' " +
                        "AND base_entity_id is not null " +
                        "AND is_closed = 0",
                "ec_anc_partner_community_feedback",
                referralFormSubmissionId
        );

        List<String> res = readData(sql, dataMap);

        return res.size() > 0 ? res.get(0) : "";
    }

}
