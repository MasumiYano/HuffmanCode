import java.io.*;
import java.util.*;
/**
 * @author Masumi
 * @version 15/3/2023
 * This is the class for decoding part of this assignment.
 * */
public class ConvertCodeToText {

    /**
     * @throws IOException throw IOException to deal with I/O related problem
     * This is the main class for decoding, it asks user for file name and decode.
     * */
    public static void main(String[] args) throws IOException{
        PrintStream SO = System.out; //Set SO as System.out
        Scanner userInput = new Scanner(System.in); //Initialize the scanner
        SO.print("Enter the name of the file without period: ");
        String name = userInput.nextLine(); //take the use input.
        while(name.contains(".")){//as long as the name contains ".", then keep asking to type without it.
            SO.print("Invalid file name. Please enter a file name without period: ");
            name = userInput.nextLine();
        }
        userInput.close();
        File fileHuff, fileCode;
        FileReader readCodeFile, readHuffFile;
        try{ //Try to find .huff and .code file based on the user input.
            fileHuff = new File(name + ".huff");
            fileCode = new File(name + ".code");
            readCodeFile = new FileReader(fileCode);
            readHuffFile = new FileReader(fileHuff);
        }catch (FileNotFoundException e){ //If the file does not exist, throw exception and end the execution.
            SO.println("File name doesn't exist: " + name);
            return;
        }
        ConvertCodeToText y = new ConvertCodeToText(readCodeFile); //If it exists, create new class with .code file.

        //All SO things are there for debug purpose.
        Map<String, Character> myMap = y.addToMap(readCodeFile); //Create new map.
        SO.println("Added to map successfully");
        SO.println(myMap.toString());
        String huffLine = y.readHuff(readHuffFile); //Create a single large string with the data.
        SO.println("Huff file has been read successfully");
        SO.println(huffLine);
        String decodedString = y.convertToString(huffLine); //convert it to string and done.
        SO.println("You've decoded successfully!!");
        SO.println("Result: ");
        SO.println();
        SO.println(decodedString);
    }
    private FileReader file; //file that starts with the user input
    private Map<String, Character> codeMap; //Map for decode.
    private String data; //String that will be used in step 5


    /**
     * @param file .code file with user input name.
     * This is the constructor for this class. It takes .code file
     * */
    public ConvertCodeToText(FileReader file) {
        this.file = file; //Set FileReader as the user input file
        this.data = ""; //Initialize for long line of data.
    }


    /**
     * @param file .code file
     * @return codeMap set of number and character map.
     * this method will create the map to represent huffman code and its correspond letter
     * */
    public Map<String, Character> addToMap(FileReader file) throws IOException {
        codeMap = new TreeMap<>(); //Initialize the map as TreeMap
        //Use BufferedReader to read the file easily
        BufferedReader codeBufferedReader = new BufferedReader(file);
        String line; //
        // as long as there is a line read the line
        while ((line = codeBufferedReader.readLine()) != null) {
            //First line, cast it back into a character
            Character character = (char) Integer.parseInt(line);
            //Second line is just a string representation.
            String representation = codeBufferedReader.readLine();
            //add them to the map.
            codeMap.put(representation, character);
        }
        return codeMap;
    }


    /**
     * @param incFile incoming .huff file
     * @throws IOException
     * @return data String representation of .huff file
     * This method read the .huff file and turn it into large single string
     * */
    public String readHuff(FileReader incFile) throws IOException{
        StringBuilder dataBuild = new StringBuilder(); //Initiate StringBuilder. More efficient and mutable.
        Scanner huffScanner = new Scanner(incFile); //Scanner to scan the file
        //As long as the .huff file have next line (Although I used nextLine, so I can add them all at once)
        while(huffScanner.hasNextLine()){
            //append them to string builder
            dataBuild.append(huffScanner.nextLine());
        }
        //convert the string builder to string and add it to string.
        this.data = dataBuild.toString();
        return data;
    }

    /**
     * @param line the data
     * @return decoded String presentation of the data string.
     * */
    public String convertToString(String line){
        String decoded = ""; //Initialize the empty string. Use it for decoded version of the data.
        StringBuilder miniString = new StringBuilder();
        //Iterate through every letter in data class.
        for(int i = 0; i < data.length(); i++){
            //add it to miniString
            miniString.append(data.charAt(i));
            //if the codeMap contains the miniString as key, then decoded String will add corresponding letter
            if(codeMap.containsKey(miniString.toString())){
                decoded += codeMap.get(miniString.toString());
                miniString.setLength(0); //reset the size of string builder to 0.
            }
        }
        return decoded;
    }

}
