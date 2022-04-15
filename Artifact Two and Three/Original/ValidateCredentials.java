/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package authentication;
import java.security.MessageDigest;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

/**
 *
 * @author ruben.perez_snhu
 */
public class ValidateCredentials {

    private boolean isValid;
    private String filePath;
    private String credentialsFileName;
    private boolean userExists;

    public ValidateCredentials() {
        /* Note: 
          
          If you place your .txt datafiles on the same
          level as your .java files are at then you will
          not need to specify a 'filePath' like:
          
          filePath = "C:\\Users\\...\\Authentication\\";
         */
        filePath = "";

        isValid = false;
        credentialsFileName = "credentials";
    }

    public boolean isCredentialsValid(String userName, String passWord) throws Exception {
        String original = passWord;
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(original.getBytes());
        byte[] digest = md.digest();
        StringBuffer sb = new StringBuffer();
        for (byte b : digest) {
            sb.append(String.format("%02x", b & 0xff));
        }

        System.out.println("");
        //System.out.println("original:" + original);
        //System.out.println("digested:" + sb.toString()); //sb.toString() is what you'll need to compare password strings

        isValid = readDataFiles(userName, sb.toString());

        return isValid;
    }

    public boolean readDataFiles(String userName, String passWord) throws IOException {
        FileInputStream fileByteStream1 = null; // File input stream
        FileInputStream fileByteStream2 = null; // File input stream

        Scanner inFS1 = null;                   // Scanner object
        Scanner inFS2 = null;                   // Scanner object

        String textLine = null;
        String textFileName = null;

        boolean foundCredentials = false;

        // Try to open file
        System.out.println("");
        System.out.println("Opening file " + credentialsFileName + ".txt");
        fileByteStream1 = new FileInputStream(filePath + "credentials.txt");
        inFS1 = new Scanner(fileByteStream1);

        System.out.println("");
        System.out.println("Reading lines of text.");

        while (inFS1.hasNextLine()) {
            textLine = inFS1.nextLine();
            //System.out.println(textLine);

            String[] words = textLine.split("\\s");//splits the string based on whitespace

            if (words[0].equals(userName) && textLine.contains(passWord)) {
                foundCredentials = true;
                int last = words.length - 1;
                textFileName = words[last];
                break;
            }
        }

        // Done with file, so try to close it
        System.out.println("");
        System.out.println("Closing file " + credentialsFileName + ".txt");

        if (textLine != null) {
            fileByteStream1.close(); // close() may throw IOException if fails
        }

        if (foundCredentials == true) {
            // Try to open file
            System.out.println("");
            System.out.println("Opening file " + textFileName + ".txt");

            fileByteStream2 = new FileInputStream(filePath + textFileName + ".txt");
            inFS2 = new Scanner(fileByteStream2);

            System.out.println("");

            while (inFS2.hasNextLine()) {
                textLine = inFS2.nextLine();
                System.out.println(textLine);
            }

            // Done with file, so try to close it
            System.out.println("");
            System.out.println("Closing file " + textFileName + ".txt");

            if (textLine != null) {
                fileByteStream2.close(); // close() may throw IOException if fails
            }
        }

        return foundCredentials;
    }

    //new method for checking userlist file
    public boolean userExists(String userName) throws FileNotFoundException {
        String textLine;
        FileInputStream fileByteStream3 = null;
        fileByteStream3 = new FileInputStream(filePath + "userList.txt");
        Scanner inFS3 = new Scanner(fileByteStream3);

        while (inFS3.hasNextLine()) {
            textLine = inFS3.nextLine();
            if (userName.equals(textLine)) {
                userExists = true;
                break;
            } else {
                userExists = false;
            }
        }
        return userExists;
    }

}
