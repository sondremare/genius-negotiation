package negotiator.GroupPikeOverskaug;

import negotiator.Bid;

import java.util.*;

public class MultiTreeMap {
    private TreeMap<Double, ArrayList<Bid>> treeMap;

    public MultiTreeMap() {
        this.treeMap = new TreeMap<Double, ArrayList<Bid>>();
    }

    public void put(Double util, Bid bid) {
        ArrayList<Bid> bids = treeMap.get(util);
        if (bids == null) {
            bids = new ArrayList<Bid>();
        }
        bids.add(bid);
        treeMap.put(util, bids);
    }

    public void putAll(MultiTreeMap multiTreeMap) {
        TreeMap<Double, ArrayList<Bid>> map = multiTreeMap.getTreeMap();
        for (Map.Entry<Double, ArrayList<Bid>> entry : map.entrySet()) {
            for (Bid bid : entry.getValue()) {
                put(entry.getKey(), bid);
            }
        }
    }

    public ArrayList<Bid> get(Double key) {
        return treeMap.get(key);
    }

    public TreeMap<Double,ArrayList<Bid>> getTreeMap() {
        return treeMap;
    }

    public SortedMap<Double, ArrayList<Bid>> getBidsOverThreshold(double threshold) {
        return treeMap.tailMap(threshold);
    }
}
