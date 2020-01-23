import java.io.File;
import java.util.Queue;
import java.util.Scanner;
import java.util.LinkedList;

public class Graph {
    int vertexCt;  // Number of vertices in the graph.
    GraphNode[] nodes;  // Adjacency list for graph.
    String graphName;  //The file from which the graph was created.
    int[][] rGraph;
    int demand;

    public Graph() {
        this.vertexCt = 0;
        this.graphName = "";
    }


    public int getVertexCt() {
        return vertexCt;
    }

    public boolean addEdge(int source, int destination, int cap) {
        //System.out.println("addEdge " + source + "->" + destination + "(" + cap + ")");
        if (source < 0 || source >= vertexCt) return false;
        if (destination < 0 || destination >= vertexCt) return false;
        //add edge
        nodes[source].addEdge(source, destination, cap);
        return true;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("The Graph " + graphName + " \n");

        for (int i = 0; i < vertexCt; i++) {
            sb.append(nodes[i].toString());
        }
        return sb.toString();
    }

    public void makeGraph(String filename) {
        try {
            graphName = filename;
            Scanner reader = new Scanner(new File(filename));
            vertexCt = reader.nextInt();
            rGraph = new int[vertexCt][vertexCt];
            nodes = new GraphNode[vertexCt];
            for (int i = 0; i < vertexCt; i++) {
                nodes[i] = new GraphNode(i);
            }
            while (reader.hasNextInt()) {
                int v1 = reader.nextInt();
                int v2 = reader.nextInt();
                int cap = reader.nextInt();
                if (!addEdge(v1, v2, cap))
                    throw new Exception();
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean bfPathExists(Graph graph, int source, int dest) {

        for (GraphNode node : nodes) {
            node.visited = false;
        }

        Queue<Integer> q = new LinkedList<>();
        q.add(source);
        nodes[source].visited = true;

        while (!q.isEmpty()) {
            int head = q.peek();
//            System.out.println("head node: " + head);
            for (int i = 1; i < graph.vertexCt; i++) {
//                System.out.println("has head to " + i + " been visited? : " + nodes[i].visited);
//                System.out.println("capacity in rMatrix at row " + i + " column 'head' is " + rGraph[i][head]);
//                System.out.println("here is resid capacity at " + i + "and " + head + ": " + rGraph[i][head]);
                if (nodes[i].visited == false && rGraph[i][head] > 0) {
                    q.add(i);
//                    System.out.println("a kid just got added to the queue. the kid is: " + i);
                    nodes[i].parent = head;
                    nodes[i].visited = true;
                }

            }
//            System.out.println("the current queue is : " + q.toString());
            int justRemoved = q.poll();


//            System.out.println("a head just got popped off the queue. the head removed is: " + justRemoved);
        }
        return nodes[dest].visited;
    }


    public int findFlow(Graph graph, int source, int dest) {
        int maxFlow = 0;

        while (bfPathExists(graph, source, dest)) {
            String pathStart = " 0 ";
            StringBuilder pathRest = new StringBuilder();
            int currPathFlow = 100000;

            //find max flow thru each edge and update it to be the min of all edges
            int tempDest1 = dest;
            int tempDest2 = dest;
            while (tempDest1 != source) {

                int parent = nodes[tempDest1].parent;
                pathRest.append(tempDest1 + ">--");

                currPathFlow = Math.min(currPathFlow, rGraph[tempDest1][parent]);
                tempDest1 = nodes[tempDest1].parent;
            }

            //update resid matrix with new front and back flow
            while (tempDest2 != source) {
                int parent = nodes[tempDest2].parent;
                rGraph[tempDest2][parent] -= currPathFlow;
                rGraph[parent][tempDest2] += currPathFlow;
                tempDest2 = nodes[tempDest2].parent;
            }
            System.out.println();
            System.out.println("Found flow of " + currPathFlow + ".  " + pathStart + pathRest.reverse());
            maxFlow += currPathFlow;

        }
        System.out.println();
        System.out.println("the maximum flow on this graph is: " + maxFlow);

        if(maxFlow==demand){
            System.out.println("SUCCESS! Produced: " + maxFlow + ". " + "Demand: " + this.demand + ". ");
        }
        else{
            System.out.println("The demand is " + this.demand + ". Only " + maxFlow + " was produced. The flow did not meet the demand :(");
        }
        System.out.println();
        return maxFlow;
    }

    //method to put the original capacities into the residual matrix
    public void makeRgraph() {
        for (GraphNode node : nodes) {
            LinkedList<GraphNode.EdgeInfo> edges = node.edgeList;
            for (GraphNode.EdgeInfo edge : edges) {
                int toNode = edge.to;
                int fromNode = edge.from;
                int cap = edge.capacity;
                rGraph[toNode][fromNode] = cap;
            }
        }
    }

    public void printRGraph() {
        for (int i = 0; i < rGraph.length; i++) {
            for (int j = 0; j < rGraph.length; j++) {
                System.out.print(rGraph[i][j] + " ");
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        Graph graph1 = new Graph();


        graph1.makeGraph("demands5.txt");

        System.out.println(graph1.toString());

        graph1.makeRgraph();

        //calculate the demand for this graph.
        int[] sinkRow = graph1.rGraph[graph1.vertexCt-1];
        for(int cap: sinkRow){
            graph1.demand += cap;
        }

        System.out.println("Residual Matrix before: ");
        graph1.printRGraph();

        graph1.findFlow(graph1, 0, graph1.vertexCt - 1);

        System.out.println("Residual Matrix after: ");
        graph1.printRGraph();

    }
}
