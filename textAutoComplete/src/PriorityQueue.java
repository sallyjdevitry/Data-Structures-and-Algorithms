import java.io.BufferedReader;
import java.io.IOException;
import java.net.SocketOption;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;

public class PriorityQueue {
    private Node root;
    private int size;


    public PriorityQueue() {
        root = null;
        size = 0;
    }

    public boolean isEmpty() {
        return root == null;
    }

    public void clear() {
        root = null;
        size = 0;
    }

    public int getSize() {
        return size;
    }

    //method that inserts a Node into the skewHeap
    public void insert(int val, String w) {
        root = merge(root, new Node(val, w));
        size++;
    }

    public Node remove() {
        if (root == null) {
            throw new NoSuchElementException("Node not found");
        }
        Node oldRoot = root;
//        if(merge(root.left, root.right) == null){
//            return oldRoot;
//        }
            root = merge(root.left, root.right);
            size--;
            return root;
    }

    //merging two skew heaps, the root with the bigger weight wins
    private Node merge(Node t1, Node t2) {

        if (t1 == null) {
            return t2;
        }
        if (t2 == null) {
            return t1;
        }
        if (t1.weight > t2.weight) {
            Node temp = t1.left;
            t1.left = merge(t1.right, t2);
            t1.right = temp;
            return t1;
        } else {
            Node temp = t2.right;
            t2.right = merge(t2.left, t1);
            t2.left = temp;
            return t2;
        }
    }

    public void printHeap() {
        System.out.println("Here is the heap: ");
        printHeap(root);
        System.out.println();
    }

    //method to print skewheap in a way that shows its structure
    public String toSideways(Node t, String indent){
        if (t==null){
            return " ";
        }
        else{
            String temp = toSideways(t.right, indent+"  ");
            temp+= indent + myToString(t) + "\n";
            temp+= toSideways(t.left, indent+"    ");
            return temp;
        }
    }

    public String myToString(Node t){
        return("["+ t.word + ", " + t.weight + "]");
    }


    private void printHeap(Node t) {
        if (t != null) {
            printHeap(t.left);
            System.out.println("["+ t.word + ", " + t.weight + "]");
            printHeap(t.right);
        }
    }

    //Binary search for an array of strings that finds all strings that start with specified prefix
    static ArrayList<String> binarySearch(ArrayList<String> arr, String prefix){
        int left = 0;
        int right = arr.size()-1;
        ArrayList<String> myList = new ArrayList<>();
        int mid = (left+(right-1))/2;
        while (left<= right && mid <=right && mid >=left ) {

            if(mid == arr.size()-2){
                myList.add(arr.get(right));
                return myList;
            }
            if (arr.get(mid).startsWith(prefix)){

                myList.add(arr.get(mid));
                int goodIndex = arr.indexOf(arr.get(mid))+1;
                while(arr.get(goodIndex).startsWith(prefix)){
                    myList.add(arr.get(goodIndex));
                    goodIndex++;
                }
                goodIndex = arr.indexOf(arr.get(mid))-1;
                while(goodIndex >=0 && arr.get(goodIndex).startsWith(prefix)){
                    myList.add(arr.get(goodIndex));
                    goodIndex--;
                }
                return myList;
            }
            int result = prefix.compareTo(arr.get(mid));

            if (result>0){
                left= mid+1;
            }
            else{
                right=mid-1;
            }
            mid = (left+(right-1))/2;
        }
        return new ArrayList<String>();
    }

    public static void main(String[] args) throws IOException {

        //testing insert, remove and merge using testHeap and testHeap2
        PriorityQueue testHeap = new PriorityQueue();

        testHeap.insert(7, "cool");
        testHeap.insert(10, "exciting");
        testHeap.insert(13, "wow");
        testHeap.insert(14, "beautiful");
        testHeap.insert(2, "awesome");

        PriorityQueue testHeap2 = new PriorityQueue();

        testHeap2.insert(4, "somber");
        testHeap2.insert(3, "unhappy");
        testHeap2.insert(6, "boohoo");
        testHeap2.insert(9, "yikes");

        System.out.println("Heap1 after inserting 5 Nodes:");
        System.out.println(testHeap.toSideways(testHeap.root, ""));

        testHeap.remove();

        System.out.println("Heap1 after removing max:");
        System.out.println(testHeap.toSideways(testHeap.root, ""));


        System.out.println("Heap2 after inserting 4 Nodes:");
        System.out.println(testHeap2.toSideways(testHeap2.root, ""));

        PriorityQueue heapAfterMerge = new PriorityQueue();
        heapAfterMerge.root = heapAfterMerge.merge(testHeap.root, testHeap2.root);

        System.out.println("Heap after merging the 2 heaps: ");
        System.out.println(heapAfterMerge.toSideways(heapAfterMerge.root, ""));


        //read the sortedwords file into an array list
        ArrayList<String> terms = new ArrayList<>(5000);
        ArrayList<Integer> weights = new ArrayList<>(5000);

        File file = new File("src/SortedWords.txt");

        BufferedReader br = new BufferedReader(new FileReader(file));

        String[] lineList;
        String line;
        while ((line = br.readLine()) != null) {
            lineList = line.split("\\s+");
            for (int i = 1; i < lineList.length; i++) {
                terms.add(lineList[0]);
                if (lineList.length >= 2) {
                    weights.add(Integer.parseInt(lineList[1]));
                }
            }
        }

        System.out.println("length of terms: " + terms.size());
        System.out.println("length of weights: " + weights.size());

        //get user input for prefix and number of terms to be printed
        Scanner input = new Scanner(System.in);
        System.out.println();
        System.out.println("Enter the prefix you want autocompleted: ");
        String prefixInput = input.nextLine();

        Scanner input2 = new Scanner(System.in);
        System.out.println("Enter how many results you'd like to see: ");
        Integer numSeen = input2.nextInt();

        ArrayList<String> resWords = binarySearch(terms, prefixInput);


//        for(String word: resWords){
//            System.out.println(word);
//        }

        PriorityQueue skewHeap = new PriorityQueue();
        for (String word: resWords){
            int id = terms.indexOf(word);
            int weight = weights.get(id);
            skewHeap.insert(weight, word);
        }

//        System.out.println(skewHeap.toSideways(skewHeap.root, ""));

        ArrayList<String> finalWordList = new ArrayList<>();

        for(int i = 0; i< numSeen; i++){
            if (skewHeap.root.right == null && skewHeap.root.left == null){
                finalWordList.add(skewHeap.root.word);
            }
            else{
                finalWordList.add(skewHeap.remove().word);
            }
        }

        System.out.println("Here are your top " + numSeen + " suggestions for the prefix " + prefixInput + ":");
        for(String word : finalWordList){
            System.out.println(word);
        }

    }


}