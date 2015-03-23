package negotiator.GroupPikeOverskaug;

import negotiator.Bid;
import negotiator.issue.Issue;
import negotiator.issue.IssueDiscrete;
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
    HashMap<Issue, HashMap<Integer , Integer>> frequencyMap = new HashMap<Issue, HashMap<Integer , Integer>>();
    double totalCount = 0;

    public FrequencyOpponentModel(UtilitySpace utilitySpace) {
        this.utilitySpace = utilitySpace;
        for (Issue issue : utilitySpace.getDomain().getIssues()) {
            List<ValueDiscrete> values = ((IssueDiscrete)issue).getValues();
            HashMap<Integer, Integer> valueMap = new HashMap<Integer, Integer>();
            for (int valueIndex = 0; valueIndex < values.size(); valueIndex++) {
                valueMap.put(valueIndex, 0);
            }
            frequencyMap.put(issue, valueMap);
        }

    }

    public void updateModel(Bid bid) {
        for (Issue issue : bid.getIssues()) {
            List<ValueDiscrete> values = ((IssueDiscrete)issue).getValues();
            HashMap<Integer, Integer> valueMap = frequencyMap.get(issue);
            for (int valueIndex = 0; valueIndex < values.size(); valueIndex++) {
                valueMap.put(valueIndex, valueMap.get(valueIndex) + 1);
                totalCount++;
            }
        }
    }

    public double getUtility(Bid bid)
    {
        double utility = 0;
        for (Issue issue : bid.getIssues()) {
            List<ValueDiscrete> values = ((IssueDiscrete)issue).getValues();
            HashMap<Integer, Integer> valueMap = frequencyMap.get(issue);
            for (int valueIndex = 0; valueIndex < values.size(); valueIndex++) {
                double weight = valueMap.get(valueIndex) / totalCount;
                utility += weight;
            }

        }
        return utility;
    }


}
