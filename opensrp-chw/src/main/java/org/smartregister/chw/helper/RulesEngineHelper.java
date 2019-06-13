package org.smartregister.chw.helper;

import android.content.Context;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.jeasy.rules.core.DefaultRulesEngine;
import org.jeasy.rules.core.InferenceRulesEngine;
import org.jeasy.rules.core.RulesEngineParameters;
import org.jeasy.rules.mvel.MVELRuleFactory;
import org.smartregister.chw.rule.AncVisitAlertRule;
import org.smartregister.chw.rule.HomeAlertRule;
import org.smartregister.chw.rule.ICommonRule;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class RulesEngineHelper {
    private Context context;
    private RulesEngine inferentialRulesEngine;
    private RulesEngine defaultRulesEngine;
    private Map<String, Rules> ruleMap;
    private final String RULE_FOLDER_PATH = "rule/";

    public RulesEngineHelper(Context context) {
        this.context = context;
        this.inferentialRulesEngine = new InferenceRulesEngine();
        RulesEngineParameters parameters = new RulesEngineParameters().skipOnFirstAppliedRule(true);
        this.defaultRulesEngine = new DefaultRulesEngine(parameters);
        this.ruleMap = new HashMap<>();

    }

    private Rules getRulesFromAsset(String fileName) {
        try {
            if (!ruleMap.containsKey(fileName)) {

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(context.getAssets().open(fileName)));
                ruleMap.put(fileName, MVELRuleFactory.createRulesFrom(bufferedReader));
            }
            return ruleMap.get(fileName);
        } catch (IOException e) {
            return null;
        }
    }

    protected void processInferentialRules(Rules rules, Facts facts) {

        inferentialRulesEngine.fire(rules, facts);
    }

    protected void processDefaultRules(Rules rules, Facts facts) {

        defaultRulesEngine.fire(rules, facts);
    }

    public String getButtonAlertStatus(ICommonRule alertRule, String rulesFile) {

        Facts facts = new Facts();
        facts.put(alertRule.getRuleKey(), alertRule);

        Rules rules = rules(rulesFile);
        if (rules == null) {
            return null;
        }

        processDefaultRules(rules, facts);

        return alertRule.getButtonStatus();
    }

    public String getButtonAlertStatus(HomeAlertRule alertRule, Rules rules) {

        if (rules == null) {
            return null;
        }

        Facts facts = new Facts();
        facts.put(alertRule.getRuleKey(), alertRule);

        processDefaultRules(rules, facts);

        return alertRule.getButtonStatus();
    }

    public String getButtonAlertStatus(AncVisitAlertRule alertRule, Rules rules) {

        if (rules == null) {
            return null;
        }

        Facts facts = new Facts();
        facts.put(alertRule.getRuleKey(), alertRule);

        processDefaultRules(rules, facts);

        return alertRule.getButtonStatus();
    }


    public Rules rules(String rulesFile) {
        return getRulesFromAsset(RULE_FOLDER_PATH + rulesFile);
    }
}
