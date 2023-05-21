import java.util.*;
import java.io.*;
/**
 * @author Masumi
 * @version 3/15/2023
 * This is a class to convert text to code.
 * */
public class TurnTextToCodeMain {

    /**
     * @throws IOException throw IOException to handle the exception that might occur when dealing with files and other I/O relates operations.
     * This is the main class for TurnTextToCode
     * */
    public static void main(String[] args) throws IOException{
        PrintStream SO = System.out; //Set SO as System.out.
        //Initialize Scanner to take user input
        Scanner scanner = new Scanner(System.in);
        SO.print("Enter the name of the text file (with .txt extension): ");
        String fileName = scanner.nextLine();
        while (!fileName.endsWith(".txt")) { // while the file name doesn't end with ".txt" ask them to type if again.
            SO.print("Invalid file name. Please enter a file name with .txt extension: ");
            fileName = scanner.nextLine();
        }
        scanner.close(); //close the scanner.

        FileInputStream userFile;
        try { //Try and see if the file they put in exists.
            userFile = new FileInputStream("/Users/lukiee/Desktop/GRC/GRC_Classes/2023Winter/JavaTwo/Assignment/CODE/src/"+fileName);
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + fileName); //If not then throw exception and end the execution.
            return;
        }
        TurnTextToCodeMain x = new TurnTextToCodeMain(userFile); //Initialize the new class with the file.

        //All these SO.println is here for debugging purpose.
        SO.println("Stage 1, Done.");
        List<Character> chars;
        chars = x.addCharToList(userFile);
        SO.println("Stage 2, Done");
        Map<Character, Integer> freqMap;
        freqMap = x.addFreqAndChar(chars);
        SO.println("Item were added");
        SO.println(freqMap.toString());
        PriorityQueue<Node> myPQ;
        myPQ = x.addToPq(freqMap);
        /*
        * while the priority queue has size of more than 1.
        * We take the pair of nodes and make new Node with them by adding the number of frequency for each of them.
        * And add it back to the priority queue.
        * */
        while (myPQ.size() > 1) {
            Node left = myPQ.remove();
            Node right = myPQ.remove();
            Node parent = new Node( left.freq + right.freq, left, right);
            myPQ.add(parent);
        }
        SO.println("Stage 6, Done");
        x.buildHuffmanMapHelper();
        SO.println("Stage 9, Done");
        x.buildFile(fileName);
    }
    PrintStream SO = System.out; //Set keyword "SO" as System.out
    private FileInputStream userFile; //File that use selected.
    private List<Character> charsList; //List of chars that the file has.
    private Map<Character, Integer> freqMap; //Map that has word as key and its frequency as value.
    private PriorityQueue<Node> myPQ; //PriorityQueue for hold use defined node class.
    private Map<Character, String> huffmanMap; //Map that has huffman code as value and word as the key.


    /**
     * @param userFile file that user passed in
     * This is the constructor for TurnTextToCodeMain, it'll take
     * userFile and set it to userFile, set charList to new ArrayList,
     * and frequency map to new TreeMap .
     * */
    public TurnTextToCodeMain(FileInputStream userFile) {
        this.userFile = userFile;
        this.charsList = new ArrayList<>();
        this.freqMap = new TreeMap<>();
    }

    /**
     * @param chars file to take look inside and add letter
     * @throws IOException throw this exception because you're dealing with FileInputStream
     * @return charsList return the list that contains all letters of the file.
     * This method take the file passed in and add letters to List
     * */
    public List<Character> addCharToList(FileInputStream chars) throws IOException {
        int content;
        while ((content = chars.read()) != -1) { //reading character data from FileInputStream.
            charsList.add((char) content); //add them to the list.
        }
        return charsList; //return the list
    }


    /**
     * @param myList List of letters in the file.
     * @return freqMap map that has word as key and its frequency in the file as value
     * This method go through the list and add them to map with the frequency of the letter occurrence.
     * */
    public Map<Character, Integer> addFreqAndChar(List<Character> myList){
        freqMap = new TreeMap<>();
        for(char letter: myList) {
            if (freqMap.containsKey(letter)) { //if the map already have the letter, increment the value by one.
                freqMap.put(letter, freqMap.get(letter) + 1);
            }else{
                freqMap.put(letter, 1); //if this is the first time, add the letter and set value to be 1.
            }
        }
        return freqMap;
    }

    /**
     * @param myMap map that contains frequency of the letter and the letter itself of the file
     * @return myPQ returning the priority queue that have letter and its occurrence as node.
     * This method add key and value of the map as one new node to the priority queue.
     * */
    public PriorityQueue<Node> addToPq(Map<Character, Integer> myMap){
        myPQ = new PriorityQueue<>();
        for(Map.Entry<Character, Integer> entry: myMap.entrySet()){
            //For each element inside of map, add them to priority queue as new node.
            myPQ.offer(new Node(entry.getKey(), entry.getValue()));
        }
        SO.println("Item were added to PriorityQueue");
        SO.println(myPQ);
        //After that, take out two nodes from priority queue, and add them together and make new node, and put it back.
        while(myPQ.size() > 1){ //Do that while myPQ still have one more element.
            Node firstElement = myPQ.remove();
            Node secondElement = myPQ.remove();
            Node combined = new Node(firstElement.freq + secondElement.freq, firstElement, secondElement);
            myPQ.add(combined);
        }
        return myPQ;
    }

    /**
     * This method is a helper method for adding nodes with set of 0 and 1 to map.
     * It calls private method to actually add them.
     * */
    public void buildHuffmanMapHelper(){
        Node root = myPQ.poll();
        huffmanMap = new TreeMap<>();
        buildHuffmanMap(root, "", huffmanMap);
        SO.println("New map was created" + huffmanMap.toString());
    }


    /*
    * This is the private method for building a huffman map.
    * It takes node, string, and map as parameter
    * */
    private void buildHuffmanMap(Node node, String code, Map<Character, String> huffmanMap){
        if(node == null){ //If the node is null them simply stop. Base case.
            return;
        }
        if(node.isLeaf()){ //If the current node is leaf, them put them into map with its character and string.
            huffmanMap.put(node.character, code);
        }

        //recursively to that for left of tree and right of tree.
        buildHuffmanMap(node.left, code+"1", huffmanMap);
        buildHuffmanMap(node.right, code+"0", huffmanMap);
    }


    /**
     * @param fileName file name to construct .huff and .code file
     * This method takes string of file name and create new .huff and .code file
     * */
    public void buildFile(String fileName) throws IOException{
        //Using FileWriter because it's just a character data.
        //It's also efficient since it uses a buffer to write data to the file. Less system calls.
        FileWriter outputFile = new FileWriter((fileName.replace(".txt", ".code")));
        for(Map.Entry<Character, String> entry: huffmanMap.entrySet()){
            //For every key and value in huffmanMap, use key to represent the character.
            outputFile.write((int) entry.getKey() + "\n");
            outputFile.write(entry.getValue() + "\n"); //String representation of the code.
        }
        outputFile.close(); //make sure to close them.

        outputFile = new FileWriter(fileName.replace(".txt", ".huff")); //creating .huff file.
        for(char c: charsList){
            //for each character in charsList, convert character one by one and make one long line.
            outputFile.write(huffmanMap.get(c));
        }
        outputFile.close();
    }

}

class Node implements Comparable<Node> {
    public  Character character; //Data of character
    public Integer freq; //Data of frequency of the character
    Node left, right; //left and right node, will use for tree.

    /**
     * @param character character that shows up in text
     * @param freq frequency of the character
     * @param left node that'll be left of the root
     * @param right node that'll be right on the root
     * This is the constructor for Node where we cunstruct class with given character, frequency, left node and right node.
     * */
    public Node(Character character, Integer freq, Node left, Node right) {
        this.character = character;
        this.freq = freq;
        this.left = left;
        this.right = right;
    }

    /**
     * @param character character that shows up in text
     * @param freq frequency of the character
     * This is a constructor when the only character and frequency is given.
     * */
    public Node(Character character, Integer freq){
        this.character = character;
        this.freq = freq;
    }


    /**
     * @param freq frequency of the character
     * @param left left node
     * @param right right node
     * This is a constructor for just frequency, left node, and right node is given
     * */
    public Node(int freq, Node left, Node right) {
        this.character = null;
        this.freq = freq;
        this.left = left;
        this.right = right;
    }

    /**
     * @return boolean value to see if current node we're looking at is a leaf
     * This method returns boolean value for if the current node is a leaf or not.
     * True if it is, false if not.
     * */
    public boolean isLeaf() {
        return left == null && right == null;
    }

    /**
     * @return freq - other.fre1
     * This method compare with other node and this node's frequency.
     * */
    public int compareTo(Node other) {
        return freq - other.freq;
    }


    /**
     * @return String data that contains character and frequency of that character.
     * */
    public String toString(){
        return "[ "+ character + "/" + freq + " ]";
    }
}
