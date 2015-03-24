package negotiator.GroupPikeOverskaug;

import negotiator.issue.Issue;
import negotiator.issue.Value;
import negotiator.utility.UtilitySpace;

import java.util.HashMap;

/**
 * Created by Markus on 24.03.2015.
 */
public class UtilityAnalyzer
{

    public static void printUtilitySpace(UtilitySpace utilitySpace, String agentName) {
        System.out.println("Here are the preferences " + agentName);
        try {
            for (int i = 0; i < utilitySpace.getEvaluators().size(); i++) {
                System.out.println(utilitySpace.getDomain().getIssue(i) + ": Weight=" + utilitySpace.getEvaluator(i+1).getWeight() + " " +  utilitySpace.getEvaluator(i+1));
            }
            System.out.println();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void printBeliefState(HashMap<Issue, HashMap<Value , Double>> frequencyMap) {
        for (Issue issue : frequencyMap.keySet()) {
            System.out.print(issue + "= [");
            for (Value value : frequencyMap.get(issue).keySet()) {
                double highestIssueValue = findHighestIssueValue(frequencyMap, issue);
                System.out.print((frequencyMap.get(issue).get(value)/highestIssueValue) + ", ");
            }
            System.out.println("]");
        }
        System.out.println();
    }

    private static double findHighestIssueValue(HashMap<Issue, HashMap<Value , Double>> frequencyMap, Issue issue) {
        double highest = 0;
        for (Value value : frequencyMap.get(issue).keySet()) {
            if (frequencyMap.get(issue).get(value) > highest){
                highest = frequencyMap.get(issue).get(value);
            }
        }
        return highest;
    }

}
