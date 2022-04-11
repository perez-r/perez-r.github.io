/*
 * FILENAME: FileAccess.java
 *
 * DESCRIPTION: 
 * 		Create new objects to access the specified file
 * 		depending on the type of user. Separate class
 * 		created as code is reusable. 
 * 
 * PUBLIC METHODS: 
 * 		void	accessFile
 * 
 * AUTHOR INFO: 		
 * 		ORGANIZATION: 	Southern New Hampshire University
 * 		COURSE: CS-499 Computer Science Capstone
 * 		
 * 		INSTRUCTOR: Brooke Goggin
 * 		
 * 		STUDENT: 	Ruben Perez		START DATE: 		03/26/2022
 * 
 */

package com.snhu;
import java.io.FileInputStream;
import java.util.Scanner;
import java.io.IOException;

/*
 * Entire class was newly created for artifact enhancement
 */

public class FileAccess {
		String textLine = null;
		String textFileName = null;
		String filePath = null;
		Scanner inFS = null;
		FileInputStream fileByteStream = null;
		
		// Public constructor to create new object 
		public FileAccess(String fileName, String path) {
			textFileName = fileName;
			filePath = path;
		}
		
		/*
		 * File Access function
		 * 
		 * Public method to get contents of file depending on 
		 * user role.
		 */
		public void accessFile() throws IOException{
			 // Try to open file
            System.out.println("");
            System.out.println("Opening file " + textFileName + ".txt");

            fileByteStream = new FileInputStream(filePath + textFileName + ".txt");
            inFS = new Scanner(fileByteStream);

            System.out.println("");

            while (inFS.hasNextLine()) {
                textLine = inFS.nextLine();
                System.out.println(textLine);
            }

            // Done with file, so try to close it
            System.out.println("");
            System.out.println("Closing file " + textFileName + ".txt");

            if (textLine != null) {
                fileByteStream.close(); // close() may throw IOException if fails
            }
		}

}
