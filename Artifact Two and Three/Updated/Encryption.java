/*
 * FILENAME: Encryption.java
 *
 * DESCRIPTION: 
 * 		Create encryption objects to encrypt/decrypt 
 * 		using the XOR cypher. Used by ZooDB.java and 
 * 		ValidateCredentials.java to generate hashed
 * 		keys for more secure data handling.
 * 
 * PUBLIC METHODS: 
 * 		String		encryptDecrypt
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

/*
 * Entire class was newly created for artifact enhancement
 */
package com.snhu;

public class Encryption {
	
	/*
     * Public method to encrypt/decrypt data using the XOR Cypher
     */ 
    public String encryptDecrypt(String input, String key) {
    	String output = "";
    	int dataLen = input.length();
    	int keyLen = key.length();
    	StringBuffer sb = new StringBuffer();
    	
    	/* 
    	 * START XOR CYPHER
    	 */
    	for (int i = 0; i < dataLen; i++) {    		
    		sb.append(input.charAt(i) ^ key.charAt(i % keyLen));
    	}
    	/*
    	 * END XOR CYPHER
    	 */
    	
    	output = sb.toString();
		return output;
    }

}
