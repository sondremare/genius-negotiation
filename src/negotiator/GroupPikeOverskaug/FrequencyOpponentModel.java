package negotiator.GroupPikeOverskaug;

import negotiator.Bid;
import negotiator.Timeline;
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
    private UtilitySpace utilitySpace;
    private String agentName;
    private HashMap<Issue, HashMap<Value , Double>> frequencyMap = new HashMap<Issue, HashMap<Value , Double>>();
    private double totalValuesCount = 0;
    private Timeline timeline;

    public FrequencyOpponentModel(UtilitySpace utilitySpace, Timeline timeline, String agentName) {
        this.utilitySpace = utilitySpace;
        this.timeline = timeline;
        this.agentName = agentName;
        for (Issue issue : utilitySpace.getDomain().getIssues()) {
            List<ValueDiscrete> values = ((IssueDiscrete)issue).getValues();
            HashMap<Value, Double> valueMap = new HashMap<Value, Double>();
            for (Value value : values) {
                valueMap.put(value, 0.0);
            }
            frequencyMap.put(issue, valueMap);
        }
    }

    public void updateModel(Bid bid) {
        for (int issueIndex = 0; issueIndex < bid.getIssues().size(); issueIndex++) {
            Issue issue = bid.getIssues().get(issueIndex);
            Value issueValue = findValue(bid, issueIndex);
            HashMap<Value, Double> valueMap = frequencyMap.get(issue);
            double weight = concedeOverTime();
            valueMap.put(issueValue, valueMap.get(issueValue) + weight);
            totalValuesCount = totalValuesCount + weight;
        }
        UtilityAnalyzer.compareOpponentModelToRealModel(frequencyMap, agentName);
//        UtilityAnalyzer.printBeliefState(frequencyMap);
    }

    private double concedeOverTime() {
        double remainingTimeRatio = timeline.getCurrentTime() / timeline.getTotalTime();
        double concedeValue1 = 1.0;
        double concedeValue2 = Math.pow(1-remainingTimeRatio, Math.E);
        double concedeValue3 = Math.pow(1-remainingTimeRatio, 1.0/Math.E);
        double concedeValue4 = Math.pow(remainingTimeRatio, 1.0/Math.E);
        double concedeValue5 = Math.pow(remainingTimeRatio, Math.E);
        double concedeValue6 = Math.pow(remainingTimeRatio, 4.0);
        double concedeValue7 = Math.pow(remainingTimeRatio, 3.0);
        return concedeValue1;
    }

    public double getUtility(Bid bid) {
        double utility = 0;
        for (int issueIndex = 0; issueIndex < bid.getIssues().size(); issueIndex++) {
            Issue issue = bid.getIssues().get(issueIndex);
            Value issueValue = findValue(bid, issueIndex);

            HashMap<Value, Double> valueMap = frequencyMap.get(issue);
            double weight = valueMap.get(issueValue) / totalValuesCount;
            utility += weight;
        }
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
