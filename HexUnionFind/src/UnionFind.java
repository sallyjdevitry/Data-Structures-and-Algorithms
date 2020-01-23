import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class UnionFind {
    int[] parent;

    public UnionFind(int[] baseArray){
        parent = baseArray;
    }

    public void union(int a, int b){
        int roota = findWithCompression(a);
        int rootb = findWithCompression(b);
        if(roota ==rootb){
            return;
        }
        if (parent[roota] > parent[rootb]) { //a is parent
            parent[rootb] += parent[roota];
            parent[roota] = rootb;

        }
        else{ //same size
            parent[roota] += parent[rootb];
            parent[rootb] = roota;

        }
    }

    public int findWithCompression(int a){
        if (parent[a] < 0){
            return a;
        }
        else {
            return parent[a] = findWithCompression(parent[a]);
        }
    }

    public static void main(String[] args) {
        //testing the unionFind
        int baseArray[];
        baseArray = new int[200];
        int exampleArr[];
        exampleArr = new int[15];
        String whatColorArr[];
        whatColorArr = new String[200];
        for(int i=0; i<whatColorArr.length; i++){
            whatColorArr[i] = "neither";
        }

        int TOP = 150;
        int BOTTOM = 160;
        int LEFT = 170;
        int RIGHT = 180;


        for(int i=0; i<baseArray.length; i++){
            baseArray[i] = -1;
        }

        for(int i=0; i<exampleArr.length; i++){
            exampleArr[i] = -1;
        }

        UnionFind exampleUnion = new UnionFind(exampleArr);

        UnionFind hexUnion = new UnionFind(baseArray);


        Integer integer0 = 0;
        Integer integer1 = 1;
        Integer integer2 = 2;
        Integer integer3 = 3;
        Integer integer4 = 4;
        Integer integer5 = 5;
        Integer integer10 = 10;
        Integer integer11 = 11;
        Integer integer12 = 12;
        Integer integer13 = 13;

        exampleUnion.union(integer0, integer1);
        exampleUnion.union(integer4, integer3);
        exampleUnion.union(integer1, integer2);
        exampleUnion.union(integer5, integer2);

        System.out.println("After union(0,1), union(4,3), union(1,2), union(5,2)");
        System.out.println("The root for integer 0's group is: " + exampleUnion.findWithCompression(integer0));
        System.out.println("The root for integer 2's group is: " + exampleUnion.findWithCompression(integer2));
        System.out.println("The root for integer 3's group is: " + exampleUnion.findWithCompression(integer3));

        System.out.println();
        System.out.println("A chain of nodes example. After union(10, 11), union(12,11), union(13, 12):  ");
        exampleUnion.union(integer10, integer11);
        exampleUnion.union(integer12, integer11);
        exampleUnion.union(integer13, integer12);

        System.out.println("The root for integer 11's group is: " + exampleUnion.findWithCompression(integer11));
        System.out.println("The root for integer 12's group is: " + exampleUnion.findWithCompression(integer12));
        System.out.println("The root for integer 13's group is: " + exampleUnion.findWithCompression(integer13));


        System.out.println();
        System.out.println("Here are the contents of the array that represents the state of the example: ");
        for (int i=0; i<exampleArr.length; i++){
            System.out.println(i + ": " + exampleArr[i]);
        }

        String movesFile = "src/moves.txt";
        System.out.println();
        System.out.println("Beginning the game with " + movesFile + ".");

        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(movesFile));
            String line = reader.readLine();
            int lineId = 0;
            String thisTurnsColor = null;
            while (line != null) {
                int hexId = Integer.parseInt(line);
                if(lineId%2==0){
                    thisTurnsColor = "blue";
                }
                if(lineId%2!=0){
                    thisTurnsColor = "red";
                }
                if(whatColorArr[hexId].equals("red")||whatColorArr[hexId].equals("blue")){
                    System.out.println("This cell is already occupied! You lost your turn.");
                    continue;
                }

                else if (whatColorArr[hexId].equals("neither")) {
                    System.out.println(thisTurnsColor + " player chose hexagon " + hexId);
                    //if red and on the top, union it with TOP hexagon
                    if (thisTurnsColor.equals("red") && hexId <= 11) {
//                        System.out.println("trying to union " + TOP + " and " + hexId);
                        hexUnion.union(TOP, hexId);
                    }
                    //if red and on the bottom, union it with BOTTOM
                    if (thisTurnsColor.equals("red") && hexId >= 111 && hexId <= 121) {
//                        System.out.println("trying to union " + BOTTOM + " and " + hexId);
                        hexUnion.union(BOTTOM, hexId);
                    }
                    //if blue and on the left, union it with LEFT
                    if (thisTurnsColor.equals("blue") && hexId % 11 == 1) {
//                        System.out.println("trying to union " + LEFT + " and " + hexId);
                        hexUnion.union(LEFT, hexId);
                    }
                    //if blue and on the right, union it with RIGHT
                    if (thisTurnsColor.equals("blue") && hexId % 11 == 0) {
//                        System.out.println("trying to union " + RIGHT + " and " + hexId);
                        hexUnion.union(RIGHT, hexId);
                    }

                    if (thisTurnsColor.equals("blue")) {
                        whatColorArr[hexId] = "blue";
                    }
                    if (thisTurnsColor.equals("red")) {
                        whatColorArr[hexId] = "red";
                    }

                    ArrayList<Integer> currNeighbors = hexUnion.getNeighbors(hexId);

                    //for each neighbor, if it is the same color as me, union us
                    for (int i = 0; i < currNeighbors.size(); i++) {
                        if (whatColorArr[currNeighbors.get(i)].equals(thisTurnsColor)) {
//                            System.out.println("I tried to union" + hexId + " and " + currNeighbors.get(i));
                            hexUnion.union(hexId, currNeighbors.get(i));

                        }
                    }
                }
                if(thisTurnsColor.equals("blue")){
                    int currHexGroupRoot = hexUnion.findWithCompression(hexId);
                    if (currHexGroupRoot == hexUnion.findWithCompression(LEFT) && currHexGroupRoot ==hexUnion.findWithCompression(RIGHT)){
                        System.out.println();
                        System.out.println("BLUE PLAYER WINS THE GAME!!!");
                        break;
                    }
                }
                if(thisTurnsColor.equals("red")){
                    int currHexGroupRoot = hexUnion.findWithCompression(hexId);
                    if(currHexGroupRoot == hexUnion.findWithCompression(TOP) && currHexGroupRoot == hexUnion.findWithCompression(BOTTOM)){
                        System.out.println();
                        System.out.println("RED PLAYER WINS THE GAME!!");
                        break;
                    }
                }

                // read next line
                line = reader.readLine();
                lineId++;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        }
    public ArrayList<Integer> getNeighbors(int item){
        ArrayList<Integer> neighborsList = new ArrayList<>();
        //weird corner cases first
        if(item ==1){
            neighborsList.add(2);
            neighborsList.add(12);
        }
        else if(item ==11){
            neighborsList.add(10);
            neighborsList.add(22);
            neighborsList.add(21);
        }
        else if(item==111){
            neighborsList.add(100);
            neighborsList.add(101);
            neighborsList.add(112);
        }
        else if(item==121){
            neighborsList.add(120);
            neighborsList.add(110);
        }
        //top edge case
        else if(item <11){
            neighborsList.add(item+1);
            neighborsList.add(item-1);
            neighborsList.add(item+10);
            neighborsList.add(item+11);
        }
        //bottom edge case
        else if(item>111 && item<121){
            neighborsList.add(item+1);
            neighborsList.add(item-1);
            neighborsList.add(item-10);
            neighborsList.add(item-11);
        }
        //right side case
        else if(item%11 ==0){
            neighborsList.add(item-1);
            neighborsList.add(item+10);
            neighborsList.add(item+11);
            neighborsList.add(item-11);
        }
        //left side case
        else if(item%11 == 1){
            neighborsList.add(item+1);
            neighborsList.add(item-10);
            neighborsList.add(item-11);
            neighborsList.add(item+11);
        }
        //if none of the previous cases, then it's a middle cell with 6 neighbors
        else{
            neighborsList.add(item+1);
            neighborsList.add(item-1);
            neighborsList.add(item+11);
            neighborsList.add(item-11);
            neighborsList.add(item+10);
            neighborsList.add(item-10);
        }
    return neighborsList;

    }

}


