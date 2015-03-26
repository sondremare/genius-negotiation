package negotiator.GroupPikeOverskaug;

import java.util.*;

import negotiator.AgentID;
import negotiator.Bid;
import negotiator.DeadlineType;
import negotiator.Timeline;
import negotiator.actions.Accept;
import negotiator.actions.Action;
import negotiator.actions.Offer;
import negotiator.issue.Issue;
import negotiator.issue.IssueDiscrete;
import negotiator.issue.ValueDiscrete;
import negotiator.parties.AbstractNegotiationParty;
import negotiator.utility.UtilitySpace;

/**
 * This is your negotiation party.
 */
public class GroupPikeOverskaug extends AbstractNegotiationParty {
    private MultiTreeMap possibleBids;
    private double lastUtility;
    private double utilityThreshold;
    private double MAX_UTILITY = 1.0;
    private ArrayList<Issue> issues;
    private HashMap<AgentID, FrequencyOpponentModel> opponentModels = new HashMap<AgentID, FrequencyOpponentModel>();

	/**
	 * Please keep this constructor. This is called by genius.
	 *
	 * @param utilitySpace Your utility space.
	 * @param deadlines The deadlines set for this negotiation.
	 * @param timeline Value counting from 0 (start) to 1 (end).
	 * @param randomSeed If you use any randomization, use this seed for it.
	 */
	public GroupPikeOverskaug(UtilitySpace utilitySpace,
                              Map<DeadlineType, Object> deadlines,
                              Timeline timeline,
                              long randomSeed) {
		// Make sure that this constructor calls it's parent.
		super(utilitySpace, deadlines, timeline, randomSeed);

        issues = utilitySpace.getDomain().getIssues();
        UtilityAnalyzer.printUtilitySpace(utilitySpace, "PikeOverskaugAgent");
        try {
            possibleBids = generateBids(issues, 0, issues.size(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	/**
	 * Each round this method gets called and ask you to accept or offer. The first party in
	 * the first round is a bit different, it can only propose an offer.
	 *
	 * @param validActions Either a list containing both accept and offer or only offer.
	 * @return The chosen action.
	 */
	@Override
	public Action chooseAction(List<Class> validActions) {
        utilityThreshold = concede();
        if (!validActions.contains(Accept.class) || lastUtility < utilityThreshold) {
			return new Offer(findBestBid());
		}
		else {
			return new Accept();
		}
	}

	/**
	 * All offers proposed by the other parties will be received as a message.
	 * You can use this information to your advantage, for example to predict their utility.
	 *
	 * @param sender The party that did the action.
	 * @param action The action that party did.
	 */
	@Override
	public void receiveMessage(Object sender, Action action) {
		super.receiveMessage(sender, action);

        if (action instanceof Offer) {
            Offer offer = (Offer) action;
            Bid lastBid = offer.getBid();
            AgentID agentId = offer.getAgent();
            try {
                lastUtility = utilitySpace.getUtility(lastBid);
                if (opponentModels.get(action.getAgent()) == null) {
                    opponentModels.put(action.getAgent(), new FrequencyOpponentModel(utilitySpace, timeline, "PikeOverskaugAgent"));
                }
                opponentModels.get(agentId).updateModel(lastBid);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
	}

    /* Returns the best bid in regards to the opposing agents expected utility */
    public Bid findBestBid() {
        ArrayList<Bid> bids = listBids(possibleBids.getBidsOverThreshold(utilityThreshold));
        Collections.sort(bids, new BidComparator());
        return bids.get(0);
    }

    public ArrayList<Bid> listBids(SortedMap<Double, ArrayList<Bid>> bidMap) {
        ArrayList<Bid> bidList = new ArrayList<Bid>();
        Iterator iterator = bidMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry pair = (Map.Entry) iterator.next();
            ArrayList<Bid> bids = (ArrayList<Bid>) pair.getValue();
            for (Bid bid : bids) {
                bidList.add(bid);
            }
        }
        return bidList;
    }

    public double concede() {
        double remainingTimeRatio = 1 - timeline.getCurrentTime() / timeline.getTotalTime();
        double minUtility = 0;
        if (remainingTimeRatio > 0.5) {
            minUtility = 0.8;
        } else if (remainingTimeRatio > 0.1) {
            minUtility = 0.6;
        } else {
            minUtility = 0.3;
        }
        return boulware(minUtility, MAX_UTILITY, timeline.getCurrentTime(), timeline.getTotalTime());
    }

    public double boulware(double minUtility, double maxUtility, double currentTime, double totalTime) {
        return minUtility + (maxUtility - minUtility) * Math.pow(1 - Math.min(currentTime, totalTime)/totalTime, 1/Math.E);
    }

    /**
     * Generates all possible bids from a set of issues, and returns them in a map with utility as the key.
     */
    public MultiTreeMap generateBids(ArrayList<Issue> issues, int index, int size, Bid bid) throws Exception {
        MultiTreeMap bidList = new MultiTreeMap();
        List<ValueDiscrete> values = ((IssueDiscrete) issues.get(index)).getValues();
        if (bid == null) {
            bid = utilitySpace.getDomain().getRandomBid();
        }
        for (int i = 0; i < values.size(); i++) {
            Bid cloneBid = new Bid(bid);
            cloneBid.setValue(index + 1, values.get(i)); //Bids are 1-indexed, hence the +1
            if (index < size - 1) {
                bidList.putAll(generateBids(issues, index + 1, size, cloneBid));
            } else {
                bidList.put(utilitySpace.getUtility(cloneBid), cloneBid);
            }
        }
        return bidList;
    }

    /* Comparator class responsible for comparing bids based on the opposing agents opponent model.
    *  It compares the combined expected utility for all agents for a given bid */
    private class BidComparator implements Comparator {

        @Override
        public int compare(Object o1, Object o2) {
            Bid bid1 = (Bid) o1;
            Bid bid2 = (Bid) o2;
            double accumulatedUtility1 = 0;
            double accumulatedUtility2 = 0;
            Iterator iterator = opponentModels.entrySet().iterator();
            while(iterator.hasNext()) {
                Map.Entry pair = (Map.Entry) iterator.next();
                FrequencyOpponentModel frequencyOpponentModel = (FrequencyOpponentModel) pair.getValue();
                accumulatedUtility1 += frequencyOpponentModel.getUtility(bid1);
                accumulatedUtility2 += frequencyOpponentModel.getUtility(bid2);
            }
            if (accumulatedUtility1 > accumulatedUtility2) {
                return 1;
            } else if (accumulatedUtility1 < accumulatedUtility2) {
                return -1;
            } else {
                return 0;
            }
        }
    }

}
