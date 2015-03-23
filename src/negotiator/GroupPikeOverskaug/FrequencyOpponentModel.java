package negotiator.GroupPikeOverskaug;

import negotiator.Bid;
import negotiator.issue.Issue;
import negotiator.utility.UtilitySpace;

import java.util.HashMap;

/**
 * Created by Markus on 23.03.2015.
 */
public class FrequencyOpponentModel
{
    UtilitySpace utilitySpace;
    HashMap<Issue, Integer> frequencyMap = new HashMap<Issue, Integer>();
    double totalCount = 0;

    public FrequencyOpponentModel(UtilitySpace utilitySpace) {
        this.utilitySpace = utilitySpace;
        for (Issue issues : utilitySpace.getDomain().getIssues()) {
            frequencyMap.put(issues, 0);
        }

    }

    public void updateModel(Bid bid) {
        for (Issue issue : bid.getIssues()) {
            frequencyMap.put(issue, frequencyMap.get(issue) + 1);
            totalCount++;
        }
    }

    public double getUtility(Bid bid)
    {
        double utility = 0;
        for (Issue issue : bid.getIssues()) {
            double weight = frequencyMap.get(issue) / totalCount;
            utility += weight;
        }
    }


}
