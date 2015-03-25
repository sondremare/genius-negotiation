package negotiator.GroupPikeOverskaug;

import negotiator.Bid;
import negotiator.issue.Issue;
import negotiator.issue.IssueDiscrete;
import negotiator.issue.Value;
import negotiator.issue.ValueDiscrete;
import negotiator.utility.UtilitySpace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Markus on 24.03.2015.
 */
public class UtilityAnalyzer
{
    private static HashMap<String, UtilitySpace> utilitySpaceList = new HashMap<String, UtilitySpace>();


    public static void printUtilitySpace(UtilitySpace utilitySpace, String agentName) {
        utilitySpaceList.put(agentName, utilitySpace);
        System.out.println("Here are the preferences " + agentName);
        try {
            for (int i = 0; i < utilitySpace.getEvaluators().size(); i++) {
                System.out.println(utilitySpace.getDomain().getIssue(i) + ": Weight=" + utilitySpace.getEvaluator(i+1).getWeight() + " " +  utilitySpace.getEvaluator(i+1));
            }
            System.out.println();
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            Bid bid = utilitySpace.getDomain().getRandomBid();
            System.out.println(bid);
            System.out.println(utilitySpace.getEvaluation(1, bid));
            ArrayList<Issue> issues = utilitySpace.getDomain().getIssues();
            for (int issueIndex = 0; issueIndex < issues.size(); issueIndex++) {
                Issue currentIssue = issues.get(issueIndex);
                System.out.println(utilitySpace.getDomain().getIssue(issueIndex).getNumber());
                List<ValueDiscrete> values = ((IssueDiscrete) issues.get(issueIndex)).getValues();
                for (int valueIndex = 0; valueIndex < values.size(); valueIndex++) {
                    bid.setValue(issueIndex+1, values.get(valueIndex));
                    System.out.println(bid);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void printBeliefState(HashMap<Issue, HashMap<Value , Double>> frequencyMap) {
//        for (Issue issue : frequencyMap.keySet()) {
//            System.out.print(issue + "= [");
//            for (Value value : frequencyMap.get(issue).keySet()) {
//                double highestIssueValue = findHighestIssueValue(frequencyMap, issue);
//                System.out.print((frequencyMap.get(issue).get(value)/highestIssueValue) + ", ");
//            }
//            System.out.println("]");
//        }
//        System.out.println();
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

    private static void compareOpponentModelToRealModel(HashMap<Issue, HashMap<Value , Double>> frequencyMap, String agentName) {

    }

}
