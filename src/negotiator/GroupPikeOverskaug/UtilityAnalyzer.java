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

    public static void compareOpponentModelToRealModel(HashMap<Issue, HashMap<Value , Double>> frequencyMap, String agentName) {
        if (agentName.equals("RandomAgent")) {
            return;
        }
        UtilitySpace staticUtilitySpace = utilitySpaceList.get(agentName);
        try {
            System.out.println("***********************" + agentName + "***********************");
            Bid bid = staticUtilitySpace.getDomain().getRandomBid();
            ArrayList<Issue> issues = staticUtilitySpace.getDomain().getIssues();
            HashMap<String, HashMap<Value , Double>> nameMap = createNameKeyedMap(frequencyMap);
            for (int issueIndex = 0; issueIndex < issues.size(); issueIndex++) {
                Issue issue = issues.get(issueIndex);
                List<ValueDiscrete> values = ((IssueDiscrete) issue).getValues();
                ArrayList<Value> valueList = new ArrayList<Value>();
                valueList.addAll(nameMap.get(issue.getName()).keySet());
                for (int valueIndex = 0; valueIndex < values.size(); valueIndex++) {
                    double highestIssueValue = findHighestIssueValue(frequencyMap, issue);
                    bid.setValue(issueIndex+1, values.get(valueIndex));
                    System.out.print(issue.getName() + " - " + bid.getValue(issueIndex+1) +"=" + valueList.get(valueIndex).toString() +" -> ");

                    System.out.print(staticUtilitySpace.getEvaluation(issueIndex + 1, bid) + " : ");
                    System.out.print(nameMap.get(issue.getName()).get(valueList.get(valueIndex)) / highestIssueValue);
                    System.out.println();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static HashMap<String, HashMap<Value , Double>> createNameKeyedMap(HashMap<Issue, HashMap<Value , Double>> frequencyMap) {
        HashMap<String, HashMap<Value , Double>> nameMap = new HashMap<String, HashMap<Value , Double>>();
        for (Issue issue : frequencyMap.keySet()) {
            nameMap.put(issue.getName(), frequencyMap.get(issue));
        }
        return nameMap;
    }

}
