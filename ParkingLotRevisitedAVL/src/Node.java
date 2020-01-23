import java.util.*;

public class Node implements Comparable<Node> {

    private String history;  //Sequence of moves to get to this state
    private Node parent;     //Node from which this node was expanded
    private Puzzle puzzle;   //Puzzle this node is trying to solve
    private int varPos[];    //Starting point of the variable location of car
    private String move;     //Most recent move  in format Cx.y  where Car x is moved to location y.
    private int depth;       //Total number of moves in history
    private int grid[][];    //Current contents of grid in terms of car number.
    private int hashcode;    // state of grid in coded form (for easy comparison)
    public Node next;
    private int priority;

    final int GOAL_CAR = 0;
    /**
     * @param parent  the parent of this node
     * @param history the moves needed to get here
     * @param puzzle  Puzzle being solved
     * @param varPos  Variable location of cars
     * @param move    Last Move
     * @param depth   Total number of moves to get to this node
     */
    Node(Node parent, String history, Puzzle puzzle, int[] varPos, String move, int depth
    ) {
        this.history = history;
        this.parent = parent;
        this.puzzle = puzzle;
        this.varPos = varPos;
        this.move = move;
        this.depth = depth;
        getGrid();
        computeHashCode();
        this.priority = depth + getEstimatedRemaining(puzzle);
    }

    public Node getParent() {
        return parent;
    }

    public int getDepth() {
        return depth;
    }

    public Node getNext() { return next;}

    public int getEstimatedRemaining(Puzzle currPuzz){
        int exitRow = currPuzz.getFixedPosition(0);
        int carsInWay = 0;
        //for each slot in exit row, see if there is a car there.....
        for (int row = this.varPos[0]; row<puzzle.getGridSize(); row++){
            if ((Integer.toString(grid[row][exitRow]).compareTo(".") != 0) || (Integer.toString(grid[row][exitRow]).compareTo("0") != 0) ){
                carsInWay ++;
            }
        }
        return carsInWay;
    }


    public int hashCode() {
        return hashcode;
    }

    /**
     * Computes a grid representation of the state of the puzzle.  In particular, an
     * nxn two-dimensional integer array is computed.  The (i,j)
     * element of this grid is equal to -1 if square (i,j) is
     * unoccupied, and otherwise contains the index of the car
     * occupying this square.  Note that the grid is recomputed each
     * time this method is called.
     */
    private void getGrid() {
        int gridsize = puzzle.getGridSize();
        grid = new int[gridsize][gridsize];

        for (int i = 0; i < gridsize; i++) {
            for (int j = 0; j < gridsize; j++) {
                grid[i][j] = -1;
            }
        }

        int num_cars = puzzle.getNumCars();

        for (int v = 0; v < num_cars; v++) {
            boolean orient = puzzle.getCarOrient(v);
            int size = puzzle.getCarSize(v);
            int fp = puzzle.getFixedPosition(v);
            if (v == GOAL_CAR && varPos[v] + size > gridsize) {
                size--;
            }
            if (orient) {
                for (int d = 0; d < size; d++)
                    grid[fp][varPos[v] + d] = v;
            } else {
                for (int d = 0; d < size; d++)
                    grid[varPos[v] + d][fp] = v;
            }
        }

    }

    public int getPriority(){ return priority; }


    public String toString() {
        return puzzle.getName() + " " + history + " [" + depth + "]\n" + displayGrid() + "\n";
    }

    public boolean isGoal() {
        return (varPos[0] == puzzle.getGridSize() - 1);
    }

    public String displayGrid() {
        String symb = "0123456789ABCDEFGHIJKLMNPQRSTUVWXYZ@#$%^&*?!";
        StringBuilder sb = new StringBuilder();
        int gridsize = puzzle.getGridSize();

        // print top line

        sb.append("+-");
        for (int x = 0; x < gridsize; x++) {
            sb.append("--");
        }
        sb.append("+");
        sb.append("\n");

        for (int y = 0; y < gridsize; y++) {
            sb.append("| ");
            for (int x = 0; x < gridsize; x++) {
                int v = grid[x][y];
                if (v < 0) {
                    sb.append(". ");
                } else {
                    sb.append(symb.charAt(v) + " ");
                }
            }
            if (y == puzzle.getFixedPosition(GOAL_CAR))
                sb.append("  \n");
            else sb.append("| \n");
        }
        // print bottom line
        sb.append("+-");
        for (int i = 0; i < gridsize; i++) {
            sb.append("--");
        }
        sb.append("+");
        // When lots of states are printed, I found it helpful to search for them by code.
        sb.append(" HashCode " + hashcode + "\n");

        return sb.toString();
    }

    /**
     * Computes all of the Nodes immediately reachable from this
     * Node and returns them as an array of Nodes.
     */
    public Node[] expand() {
        int gridsize = puzzle.getGridSize();
        int num_cars = puzzle.getNumCars();

        ArrayList<Node> new_nodes = new ArrayList<Node>();

        for (int v = 0; v < num_cars; v++) {
            int p = varPos[v];
            int fp = puzzle.getFixedPosition(v);
            boolean orient = puzzle.getCarOrient(v);
            // Find all locations to left or up from current position
            for (int np = p - 1;
                 np >= 0 && (orient ? grid[fp][np] : grid[np][fp]) < 0;
                 np--) {
                int[] newVarPos = (int[]) varPos.clone();
                newVarPos[v] = np;
                String move = " C" + v + "." + newVarPos[v];
                new_nodes.add(new Node(this, (history + move), puzzle, newVarPos, move, depth + 1));
            }

            // Find all locations to right or down from current position
            //Original code used tertiary operators, so I left it in for "exposure"
            int carsize = puzzle.getCarSize(v);
            for (int np = p + carsize;
                 ((np < gridsize) && ((orient ? grid[fp][np] : grid[np][fp]) < 0))
                         || ((v == GOAL_CAR) && (np == gridsize));
                 np++) {
                int[] newVarPos = (int[]) varPos.clone();
                newVarPos[v] = np - carsize + 1;
                String move = " C" + v + "." + newVarPos[v];
                new_nodes.add(new Node(this, (history + move), puzzle, newVarPos, move, depth + 1));
            }

        }
        return (Node[]) new_nodes.toArray(new Node[0]);
    }


    /**
     * Returns true if and only if this state is considered
     * equal to the given object.
     * Adjust what is checked to satisfy they way you intend to use it.
     */
    public boolean equals(Object o) {
        Node s;
        try {
            s = (Node) o;
        } catch (ClassCastException e) {
            return false;
        }
        if (hashcode != s.hashcode)
            return false;

        for (int i = 0; i < varPos.length; i++)
            if (varPos[i] != s.varPos[i])
                return false;
        return true;
    }

    private void computeHashCode() {
        final int SHIFT=31;
        hashcode = 0;
        for (int i = 0; i < varPos.length; i++) {
            hashcode = SHIFT * hashcode + varPos[i];
        }
    }

    @Override
    public int compareTo(Node t) {
        if (this.priority > t.priority){
            return 1;
        }
        if (this.priority == t.priority){
            return 0;
        }
        return -1;
    }
}
