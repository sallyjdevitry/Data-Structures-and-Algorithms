import java.io.*;
import java.nio.Buffer;
import java.sql.SQLOutput;
import java.util.Scanner;
public class Reviews {
    int numReviews= 0;

    public Reviews() {

        H = new QuadraticProbingHashTable<>();

    }


    public String toString() {
        int LIMIT = 20;
        return name + "\n" + H.toString(LIMIT);
    }

    private String name;
    private QuadraticProbingHashTable<String, WordInfo> H;

    //function that reads the reviews from a txt file and adds/updates words in the table
    public void readReviews(String filename)
            throws FileNotFoundException, IOException {
        BufferedReader in = new BufferedReader(new FileReader(filename));
        String name = null;
        String line;
        String[] words = null;

        int score = -1;
        int line_count = 0;
        while ((line = in.readLine()) != null) {
            line_count++;
            words = line.split("\\s+");
            try {
                score = Integer.parseInt(words[0]);
            } catch (NumberFormatException e) {
                throw new NumberFormatException("Expected integer at line " + line_count + " in file " + filename);
            }
//            ReviewInfo r = new ReviewInfo(score, words);
//            System.out.println(r.toString());

            for(int i=1; i<words.length; i++){
                addToTable(words[i].toLowerCase(), score);
            }
        }

//        System.out.println("Number of Reviews " +  line_count);
        numReviews = line_count;

    }


    public void addToTable(String word, int score){

        if (H.contains(word)){
            Reviews.WordInfo temp = H.findValue(word);
            temp.update(score);
            H.updateValue(word, temp);

        }
        else {
            WordInfo tempInfo = new WordInfo(word);
            tempInfo.update(score);
            H.insert(word, tempInfo);
        }
    }

    private static class ReviewInfo {
        int score;
        String[] words;

        // Constructors
        ReviewInfo(int score, String[] words) {
            this.score = score;
            this.words = words;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Review " + score+ " Length of Review " + (words.length -1) + " ");
            for (int i = 1; i < 11 & i < words.length; i++)
                sb.append(words[i] + " ");
            return sb.toString();
        }
    }

    public static class WordInfo {
        int totalScore;
        int numberOfOccurences;
        String word;

        // Constructors
        WordInfo(String word) {
            this.word = word;
            totalScore=0;
            numberOfOccurences = 0;
        }

        //updates the wordInfo object with new score
        public void update(double score){
            this.totalScore+=score;
            this.numberOfOccurences++;
        }

        public String toString() {
            return "Word: " + word + " [" + totalScore +", " + numberOfOccurences+"]";
        }
    }

    public static void main (String[ ]args ){

        String[] inputList;
        int wordCount = 0;
        float averageScore = 0;

        //ask the user for the review in question
        Scanner input = new Scanner(System.in);
        System.out.println("Enter Review: ");
        String reviewInput = input.nextLine();
        System.out.println(reviewInput);

        Reviews r1 = new Reviews();

        //try to ReadReviews which reads the file and adds words to the Hash table
        try {
            r1.readReviews("src/movieReviews.txt");

        } catch (IOException e){
            e.printStackTrace();
        }


        inputList = reviewInput.split(" ");

        //for each word in the entered review see if it is meaningful, meaning it occurs in less than 10% of reviews.
        //If the word is meaningful, add it's average score to the running total, then divide by number of words.
        for(String word: inputList) {

            if (r1.H.contains(word.toLowerCase())) {
//                System.out.println( "Here is the value of " + word + " " + r1.H.findValue(word.toLowerCase()));
                float numOccur = r1.H.findValue(word.toLowerCase()).numberOfOccurences;
                float totalScore = r1.H.findValue(word.toLowerCase()).totalScore;

                if ((numOccur / r1.numReviews) <= (0.1)) {
                    averageScore += (totalScore) / (numOccur);
                    wordCount += 1;
                }
            }
        }

        float revAvg = averageScore/wordCount;


//        System.out.println("Here is H's size: " + r1.H.size());


        System.out.println("This review has a predicted score of: " + revAvg);

        if (revAvg > 0 && revAvg < 1.75){
            System.out.println("Negative");
        }
        else if (revAvg < 2.25){
            System.out.println("Neutral");
        }
        else if (revAvg < 4 ){
            System.out.println("Positive");
        }


    }

}
