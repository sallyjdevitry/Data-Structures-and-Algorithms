import java.util.*;

public class GraphNode {

    public int nodeID;
    public LinkedList<EdgeInfo> edgeList;
    public int parent;
    public boolean visited;

    public GraphNode() {
        this(0);
    }
    public GraphNode(int nodeID) {
        this.nodeID = nodeID;
        this.edgeList = new LinkedList<EdgeInfo>();
        this.parent = 0;
        this.visited = false;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(nodeID + ": ");
        Iterator<EdgeInfo> itr = edgeList.iterator();
        while (itr.hasNext()) {
            sb.append(itr.next().toString());
        }
        sb.append("\n");
        return sb.toString();
    }

    public void addEdge(int v1, int v2, int capacity) {
        //System.out.println("GraphNode.addEdge " + v1 + "->" + v2 + "(" + capacity + ")");
        edgeList.addFirst(new EdgeInfo(v1, v2, capacity));
    }

    public class EdgeInfo {

        int from;        // source of edge
        int to;          // destination of edge
        int capacity;    // capacity of edge
        public EdgeInfo(int from, int to, int capacity) {
            this.from = from;
            this.to = to;
            this.capacity = capacity;

        }

        public String toString() {
            return "Edge " + from + "->" + to + " (" + capacity + ") ";
        }
    }

}