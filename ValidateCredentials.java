/*
 * FILENAME: ValidateCredentials.java
 *
 * DESCRIPTION: 
 * 		Credential validation class used by main 
 * 		Authentication class. Implements the 
 * 		Encryption.java class.
 * 
 * PUBLIC METHODS: 
 * 		boolean		isCredentialsValid
 * 		void 		readData
 * 		boolean		userExists
 * 
 * AUTHOR INFO: 		
 * 		ORGANIZATION: 	Southern New Hampshire University
 * 		COURSE: IT-145 Foundations in Application Development
 * 		
 * 		INSTRUCTOR: Joe Parker
 * 		
 * 		STUDENT: 	Ruben Perez		START DATE: 		04/17/2019
 * 
 */
package com.snhu;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 *
 * @author ruben.perez_snhu
 */

public class ValidateCredentials {
	// Private fields for class objects
    private boolean isValid;
    private String filePath;

    private boolean userExists;
    private String key;

    
    Encryption encrypt = new Encryption();
	ZooDB db = new ZooDB();
	
    public ValidateCredentials() {
        /* Note: 
          
          If you place your .txt datafiles on the same
          level as your .java files are at then you will
          not need to specify a 'filePath' like:
          
          filePath = "C:\\Users\\...\\Authentication\\";
         */
        filePath = "";

        // Variable initialization
        isValid = false;
        
        
       /* 
        * Key for testing purposes.
        * A key will be generated for each user and stored in updated version
        */
        key = "portfolio";
    }

    
    /*
     * Public method for validating credentials. Implements 
     * Encryption object and readDataFiles method
     */
    public boolean isCredentialsValid(String username, String password) throws Exception {   	
    	/*
    	 * ***MODIFIED***
    	 */
    	// Encryption object
    	/*
    	 * START XOR ENCRYPTION 
    	 */
    	String hashed = encrypt.encryptDecrypt(password, key);
    	/*
    	 * END XOR ENCRYPTION
    	 */
    	
    	if (hashed.equals(db.getHash(username))) {
    		isValid = true;
    	}
    	else {
    		isValid = false;
    	}    	
        return isValid;
    }

    /*
     * Private method used provide access to
     * users depending on their access level. 
     * 
     */
    public void readData(String userName) throws IOException {    	    	
    	String textFileName = db.getLevel(userName);        	
        FileAccess fa = new FileAccess(textFileName, filePath);
        
        fa.accessFile();
    }

    /*
     * Public method for checking if a user exists in the user
     * user database.
     * 
     */
    public boolean userExists(String userName) throws FileNotFoundException {    	
    	ZooDB db = new ZooDB();
    	userExists = db.userExists(userName);
    	
    	return userExists;
    }
}
