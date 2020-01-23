import java.util.Hashtable;
import java.util.Enumeration;

public class HashTableExample {

    public static void main(String[] args) {

        Enumeration names;
        String key;
        String [] people = {"Alan", "Kent", "John", "Kent", "John", "Myra", "Joan", "Tina","Kent", "John", "Myra", "Sam" };

        // Creating a Hashtable
        Hashtable<String, String> hashtable =
                new Hashtable<String, String>();
        // The Object you pass in for the key must have hashCode define AND equals.

        int i=0;

        // Adding Key and Value pairs to Hashtable
        for (String a:people){
            i++;
            if (!hashtable.containsKey(a))
                hashtable.put(a,a+" " +i);
        }

        names = hashtable.keys();
        while(names.hasMoreElements()) {
            key = (String) names.nextElement();
            System.out.println("Key: " +key+ " & Value: " +
                    hashtable.get(key));
        }
    }
}
