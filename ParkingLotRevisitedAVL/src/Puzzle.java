import java.io.*;
import java.sql.SQLOutput;
import java.util.*;

/**
 * This is the class for representing a particular ParkingLot puzzle.
 * Methods are provided for accessing information about a puzzle, and
 * also for reading in a list of puzzles from a data file.
 * <p>
 * Every car is constrained to only move horizontally or vertically.
 * Therefore, each car has one dimension along which it is fixed, and
 * another dimension along which it can be moved.  The fixed dimension
 * is stored here as part of the puzzle.  Also stored here are the
 * sizes and orientations of the cars, the size of the puzzle grid,
 * the name of the puzzle and the initial (root) search node of the
 * puzzle.
 * <p>
 * The goal car is always assigned index 0.
 */
public class Puzzle {

    private String name;
    private Node initNode;
    private int numCars;
    private int fixedPos[];
    private int carSize[];
    private boolean carOrient[];
    private int gridSize;

    /**
     * The main constructor for constructing a puzzle.  You probably
     * will never need to use this constructor directly, since
     * ordinarily puzzles will be constructed by reading them in from
     * a datafile using the readPuzzlesFromFile method.  It
     * is assumed that the goal car is always assigned index 0.
     *
     * @param name     the name of the puzzle
     * @param gridSize the size of one side of the puzzle grid
     * @param numCars  the number of cars on this puzzle
     * @param orient   the orientations of each car (true if vertical)
     * @param size     the sizes of each car
     * @param x        the x-coordinates of each car
     * @param y        the y-coordinates of each car
     */
    Puzzle(String name,
           int gridSize,
           int numCars,
           boolean orient[],
           int size[],
           int x[],
           int y[]) {
        this.name = name;
        this.numCars = numCars;
        this.gridSize = gridSize;
        if (numCars <= 0) {
            throw new IllegalArgumentException("Each puzzle must have a positive number of cars");
        }
        carOrient = new boolean[numCars];
        carSize = new int[numCars];
        fixedPos = new int[numCars];
        int varPos[] = new int[numCars];

        boolean grid[][] = new boolean[gridSize][gridSize];

        for (int v = 0; v < numCars; v++) {
            carOrient[v] = orient[v];
            carSize[v] = size[v];
            if (size[v] <= 0) {
                throw new IllegalArgumentException("Cars must have positive size");
            }

            if (x[v] < 0 || y[v] < 0
                    || (orient[v] && y[v] + size[v] > gridSize)
                    || (!orient[v] && x[v] + size[v] > gridSize)) {
                throw new IllegalArgumentException("Cars must be within bounds of grid");
            }

            for (int d = 0; d < size[v]; d++) {
                int xv = x[v], yv = y[v];
                if (orient[v]) yv += d;
                else xv += d;
                if (grid[xv][yv])
                    throw new IllegalArgumentException("Cars cannot overlap");
                grid[xv][yv] = true;
            }

            if (orient[v]) {
                fixedPos[v] = x[v];
                varPos[v] = y[v];
            } else {
                fixedPos[v] = y[v];
                varPos[v] = x[v];
            }

        }


        initNode = new Node(null, "", this, varPos, "Initial", 0);
        //System.out.println(initNode.toString());
    }

    /**
     * A static method for reading in a list of puzzles from the data
     * file called filename.  Each puzzle is described in the
     * data file using the format described on the assignment.  The
     * set of puzzles is returned as an array of Puzzle's.
     */
    public static Puzzle[] readPuzzlesFromFile(String filename)
            throws FileNotFoundException, IOException {
        BufferedReader in = new BufferedReader(new FileReader(filename));

        ArrayList<Puzzle> puzzles = new ArrayList<Puzzle>();
        ArrayList<CarRec> car_list = null;

        String name = null;
        String line;
        String[] words = null;

        int read_mode = 0;
        int line_count = 0;
        int gridsize = 0;

        while ((line = in.readLine()) != null) {
            line_count++;
            line = line.trim();
            words = line.split("\\s+");
            if (line.equals(""))
                continue;

            if (read_mode == 0) {   // reading name
                name = line;
                car_list = new ArrayList<CarRec>();
                read_mode = 1;
            } else if (read_mode == 1) { // reading grid size
                if (words.length != 1)
                    throw new RuntimeException("Expected single integer for grid size at line " + line_count + " in file " + filename);
                try {
                    gridsize = Integer.parseInt(words[0]);
                } catch (NumberFormatException e) {
                    throw new NumberFormatException("Expected integer grid size at line " + line_count + " in file " + filename);
                }
                if (gridsize <= 0)
                    throw new RuntimeException("Expected positive grid size at line " + line_count + " in file " + filename);

                read_mode = 2;
            } else if (line.equals(".")) { // end of puzzle description
                int numcars = car_list.size();
                boolean orient[] = new boolean[numcars];
                int size[] = new int[numcars];
                int x[] = new int[numcars];
                int y[] = new int[numcars];

                for (int v = 0; v < numcars; v++) {
                    CarRec carrec = (CarRec) car_list.get(v);
                    orient[v] = carrec.orient;
                    size[v] = carrec.size;
                    x[v] = carrec.x;
                    y[v] = carrec.y;
                }
                puzzles.add(new Puzzle(name, gridsize, numcars, orient, size, x, y));
                read_mode = 0;
            } else {
                final int CAR_DESCRIPTION_COUNT = 4;
                CarRec carrec = new CarRec();
                if (words.length != CAR_DESCRIPTION_COUNT) {
                    throw new RuntimeException("Expected four arguments at line " + line_count + " in file " + filename);
                }
                try {
                    carrec.x = Integer.parseInt(words[0]);
                    carrec.y = Integer.parseInt(words[1]);
                    carrec.size = Integer.parseInt(words[3]);
                } catch (NumberFormatException e) {
                    throw new NumberFormatException("Expected integer at line " + line_count + " in file " + filename);
                }
                String orientation = words[2].toLowerCase();

                if (!orientation.equals("v") && !orientation.equals("h")) {
                    throw new RuntimeException("Expected orientation to be 'v' or 'h' at line " + line_count + " in file " + filename);
                }
                carrec.orient = orientation.equals("v");
                car_list.add(carrec);
            }
        }

        if (read_mode != 0)
            throw new RuntimeException("Puzzle description ended prematurely in file " + filename);

        return (Puzzle[]) puzzles.toArray(new Puzzle[0]);
    }


    public int getFixedPosition(int v) {
        return fixedPos[v];
    }

    public int getCarSize(int v) {
        return carSize[v];
    }

    public int getNumCars() {
        return numCars;
    }

    /**
     * Returns the orientation of car v, where true
     * means that the car is vertically oriented.
     */
    public boolean getCarOrient(int v) {
        return carOrient[v];
    }

    public String getName() {
        return name;
    }

    public int getGridSize() {
        return gridSize;
    }

    public Node getInitNode() {
        return initNode;
    }

    public void solve(boolean doPrint) {
        System.out.println("========================");
        System.out.println(initNode.toString());
        int enqueuedCount = 0;

        //create queue that will store all nodes, and add the initial puzzle node
        Queue myQueue = new Queue();
        myQueue.enqueue(initNode);

        Node solution = null;


        //while we haven't found the solution, add all possible states to the queue and remove their parents.
        while (solution == null) {
            Node currentNode = myQueue.dequeue();
            Node[] possibilities = currentNode.expand();
            for (Node node : possibilities) {
                if (node.isGoal()) {
                    solution = node;
                } else {
                    myQueue.enqueue(node);
                    enqueuedCount++;
                }
            }
        }

        System.out.println("\n\nSOLUTION  of Depth " + solution.getDepth() + " ");
        System.out.println("Number of Nodes enqueued: " + enqueuedCount);
        //System.out.println(" Total Nodes Expanded " + allNodes.size() + "\n");
        printSolution(solution);

    }


    public void aStarSolve(boolean doPrint) {
        System.out.println("=================");
        System.out.println(initNode.toString());

        //Initialize AVL Tree to store my puzzle states
        AVLTree1<Node> myPriorityQueue = new AVLTree1<>();
        myPriorityQueue.insert(getInitNode());
        Node solution = null;
        int enqueuedCount = 0;
        Hashtable<Node, Node> hashtable = new Hashtable<Node, Node>();
        hashtable.put(initNode, initNode);


        //while we haven't found a solution, loop through each possibility asking if we've seen it.
        while (solution == null) {
            Node currNode = myPriorityQueue.findMin();
            myPriorityQueue.deleteMin();
            Node[] possibilities = currNode.expand();

            for (Node node : possibilities) {
                if (node.isGoal()) {
                    solution = node;
                }
                //check the puzzle already there's depth... compare to currents depth.
                //if depth of the new one is less, put the new one in the queue.
                else {
                    Node s = hashtable.get(node);
                    if ((s == null) || (s.getDepth() > node.getDepth())) { //didn't find it in hashtable
                        myPriorityQueue.insert(node);
                        enqueuedCount++;
                        hashtable.put(node, node);
                    }
                }
            }
        }
        System.out.println("\n\nSOLUTION  of Depth " + solution.getDepth() + " ");
        System.out.println("Number of Nodes Enqueued: " + enqueuedCount);

        printSolution(solution);

    }


    /**
     * Print the series of moves (in order) to find the solution
     *
     * @param n: ending node in the chain of nodes which solves the puzzle
     */
    public void printSolution(Node n) {

        if (n == null) {
            return;
        }
        printSolution(n.getParent());
        System.out.println("Move " + n.toString());
    }

    private static class CarRec {
        boolean orient;
        int size;
        int x;
        int y;
    }

    public static void main(String[] args) throws FileNotFoundException, IOException {
        // read all the puzzles in file.  Only the first few are solvable without additional strategies
        Puzzle[] puzzles = Puzzle.readPuzzlesFromFile("src/jamsAll.txt");
        //int num_puzzles = puzzles.length;
        int num_puzzles1 = 6;
        int num_puzzles2 = 15;

        boolean doPrint = true;
        // solve each of the first six puzzles.  The others will likely take too long
        System.out.println("THE FIRST 6 PUZZLES USING OLD SOLVE METHOD: ");
        for (int i = 0; i < num_puzzles1; i++) {
            puzzles[i].solve(doPrint);
        }
        doPrint = true;
        System.out.println("THE FIRST 14 PUZZLES USING A*SOLVE:------------ THE FIRST 14 PUZZLES USING A*SOLVE:--------------THE FIRST 14 PUZZLES USING A*SOLVE");
        for (int i = 0; i < num_puzzles2; i++) {
            puzzles[i].aStarSolve(doPrint);
        }
    }

}
