public class Node {
    int weight;
    String word;
    Node left;
    Node right;

    public Node(int val, String w){
        this.weight = val;
        this.word = w;
        this.left = null;
        this.right = null;
    }
}
