package search;

import java.util.HashMap;
import java.util.PriorityQueue;

/**
 * Created by Nicolas on 21/10/2016
 *
 * This class extends the pre-implemented class PriorityQueue from the Java API.
 * The comparator uses getTotalEvaluation() (cost + heuristic) to order the nodes in the queue.
 *
 * We extend the class so that we can use the PriorityQueue in parallel with a HashMap.
 * This is useful when we want to check if a node is already present in the PriorityQueue:
 * The complexity is O(1) with the HashMap and would be O(n) with only a PriorityQueue.
 *
 * When we add a node to the PriorityQueue, we want to verify whether the node is already contained or not
 * because if it is, and the new found node has a lesser cost, it would be better to replace it so the solution is more optimal.
 * So, if we are in that case, the node is replaced in the parallel HashMap and re-inserted in the PriorityQueue.
 *
 * But we removed this functionality. After doing some tests, we noticed that it was uselessly increasing the number of
 * nodes visited and inserted in the A* closed list, whereas the solution wasn't less optimal without this replacement.
 * We assume that replacing the nodes would be useful in searches with very big search trees. But this is not needed for our kind of levels.
 */

public class RobotPriorityQueue extends PriorityQueue<Node> {

    private HashMap<Integer, Node> hashMap;

    public RobotPriorityQueue() {
        //Declaration of the comparator
        super((Node node1, Node node2) -> (int) (node1.getTotalEvaluation() - node2.getTotalEvaluation()));
        hashMap = new HashMap<>();
    }

    @Override
    public boolean add(Node node) {
        //If the node is not already contained: add it
        if (!hashMap.containsKey(node.hashCode())) {
            hashMap.put(node.hashCode(), node);
            return super.add(node);
        }
        /*Node presentNode = hashMap.get(node.hashCode());
        //If the node is already contained, but more optimistic: delete the old one from the HashMap, and new request to add the node
        if (presentNode.getPredecessorHashCode() != null && node.getCost() < presentNode.getCost()) {
            hashMap.remove(presentNode.hashCode());
            this.add(node);
        }
        //If the node is already contained and less optimistic: do nothing*/
        return false;
    }

    @Override
    public Node poll() {
        hashMap.remove(super.peek().hashCode());
        return super.poll();
    }

    @Override
    public void clear() {
        super.clear();
        hashMap.clear();
    }

    @Override
    public boolean isEmpty() {
        if (super.isEmpty() != hashMap.isEmpty()) {
            System.out.println("Error in RobotPriorityQueue.isEmpty(): HashMap and PriorityQueue are not synchronous.");
        }
        return super.isEmpty();
    }

    @Override
    public int size() {
        if (hashMap.size() != super.size())
            System.out.println("Error in RobotPriorityQueue.size(): sizes of HashMap and PriorityQueue are not synchronous");
        return hashMap.size();
    }
}
