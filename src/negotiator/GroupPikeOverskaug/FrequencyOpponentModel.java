package negotiator.GroupPikeOverskaug;

import negotiator.Bid;
import negotiator.issue.Issue;
import negotiator.issue.IssueDiscrete;
import negotiator.issue.Value;
import negotiator.issue.ValueDiscrete;
import negotiator.utility.UtilitySpace;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Markus on 23.03.2015.
 */
public class FrequencyOpponentModel
{
    UtilitySpace utilitySpace;
    HashMap<Issue, HashMap<Value , Integer>> frequencyMap = new HashMap<Issue, HashMap<Value , Integer>>();
    double totalCount = 0;

    public FrequencyOpponentModel(UtilitySpace utilitySpace) {
        this.utilitySpace = utilitySpace;
        for (Issue issue : utilitySpace.getDomain().getIssues()) {
            List<ValueDiscrete> values = ((IssueDiscrete)issue).getValues();
            HashMap<Value, Integer> valueMap = new HashMap<Value, Integer>();
            for (Value value : values) {
                valueMap.put(value, 0);
            }
            frequencyMap.put(issue, valueMap);
        }
    }

    public void updateModel(Bid bid) {
        for (int issueIndex = 0; issueIndex < bid.getIssues().size(); issueIndex++) {
            Issue issue = bid.getIssues().get(issueIndex);
            Value issueValue = findValue(bid, issueIndex);

            HashMap<Value, Integer> valueMap = frequencyMap.get(issue);valueMap.put(issueValue, valueMap.get(issueValue) + 1);
            totalCount++;
        }
    }

    public double getUtility(Bid bid) {
        double utility = 0;
        for (int issueIndex = 0; issueIndex < bid.getIssues().size(); issueIndex++) {
            Issue issue = bid.getIssues().get(issueIndex);
            Value issueValue = findValue(bid, issueIndex);

            HashMap<Value, Integer> valueMap = frequencyMap.get(issue);
            double weight = valueMap.get(issueValue) / totalCount;
            utility += weight;
        }
        System.out.println(utility);
        return utility;
    }

    private Value findValue(Bid bid, int issueIndex) {
        Value issueValue = null;
        try {
            issueValue = bid.getValue(issueIndex+1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return issueValue;
    }


}
