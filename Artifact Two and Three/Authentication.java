/*
 * FILENAME: 	Authentication.java
 *
 * DESCRIPTION: 
 * 		Main entry point of program. Provides
 * 		authentication system for employees of 
 * 		a zoo. Allows users to authenticate to 
 * 		access the database and appropriate 
 * 		files based on permissions. 
 * 
 * PUBLIC METHODS:
 * 		void 	main
 * 		void 	userAccess
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
import java.util.Scanner;

/**
 *
 * @author ruben.perez_snhu
 */
public class Authentication {
	static Scanner scnr = new Scanner(System.in);

    public static void main(String[] args) throws Exception {
        int selection = 0;
        boolean input = true;
        
        while (input) {
        	selectionPrompt();
            selection = scnr.nextInt();
        	switch (selection) {
            case 1: 
            	userAccess();
            	break;
            case 2:
            	// add user
            	if (addUser()) {
            		System.out.println("Success!");
            	}
            	else {
            		System.out.println("Please try again later");
            	}
            	break;
            case 3: 
            	// update user info
            	if (updateUser()) {
            		System.out.println("Success!");
            	}
            	else {
            		System.out.println("Please try again later");
            	}
            	break;
            case 4: 
            	// delete user info
            	if (deleteUser()) {
            		System.out.println("Success!");
            	}
            	else {
            		System.out.println("Please try again later");
            	}
            	break;
            case 5: 
            	input = false;;
            default: 
            	break;
            }
        }
        
        System.out.println("Terminating Program.");
    }
    
    /*
     * Delete User
     * 
     * Called when user selects to delete a user. 
     * prompts for authentication then requests
     * information for which user to be deleted. 
     * 
     */
    private static boolean deleteUser() throws Exception {
    	boolean success = false;
		boolean authenticated = false;
		
		ValidateCredentials auth = new ValidateCredentials();
		
		System.out.println("Please login first");
		
		authenticated = authenticate();	
		
		if (authenticated) {
			System.out.println("Login successful");
			String username;
			System.out.println("Please enter the username to be deleted: ");	
			username = scnr.nextLine();		
						
			ZooDB db = new ZooDB();
			
			if (auth.userExists(username)) {
				success = db.deleteUser(username);
			}
			else {
				System.out.println("Please try again later");
			}
		}
		return success;
	}

    /*
     * Update User Function
     * 
     * Called when user selects option to update
     * user information. Prompts user to 
     * login first, then checks if the user exists
     * to modify the data. 
     * 
     */
    private static boolean updateUser() throws Exception {
    	boolean success = false;
		boolean authenticated = false;
		
		ValidateCredentials auth = new ValidateCredentials();
		
		System.out.println("Please login first");
		
		authenticated = authenticate();	
		
		if (authenticated) {
			System.out.println("Login successful");
			String username;
			String password;
			String level;

			System.out.println("Please enter the username to be updated: ");	
			username = scnr.nextLine();		
						
			ZooDB db = new ZooDB();
			
			if (auth.userExists(username)) {
				System.out.println("Please enter current or updated password: ");			
				password = scnr.nextLine();
				
				System.out.println("Please specify the user type: ");
				level = scnr.nextLine();
				
				success = db.updateUser(username, password, level);
			}
			else {
				System.out.println("Error Please try again");
			}
		}
		return success;
	}

    /*
     * Add User Function
     * 
     * Called when the user selects the add 
     * user option. Prompts for login then 
     * gathers the information before checking
     * if a user exists by that username. 
     * 
     */
	private static boolean addUser() throws Exception {
		boolean success = false;
		boolean authenticated = false;
		
		ValidateCredentials auth = new ValidateCredentials();
		
		System.out.println("Please login first");
		
		authenticated = authenticate();		
		
		if (authenticated) {
			System.out.println("Login successful");
			String username;
			String password;
			String level;

			System.out.println("Please create a username: ");	
			username = scnr.nextLine();
			
			System.out.println("Please create a password: ");			
			password = scnr.nextLine();
			
			System.out.println("Please specify the user type: ");
			level = scnr.nextLine();
			
			ZooDB db = new ZooDB();
			if (!auth.userExists(username)) {
				db.addNewUser(username, password, level);
				success = true;
			}
			else {
				System.out.println("Error Please try again");
			}
		}
		return success;
	}

	/*
	 * Authentication Method
	 * 
	 * This method called to authenticate users 
	 * whenever they want to perform any type 
	 * of CRUD operations.
	 */
	private static boolean authenticate() throws Exception {
		// TODO Auto-generated method stub
		int loginAttempt = 3;
		String username = null;
		String password;
		boolean authentication = false;
		char userOption = '0';
		
		ValidateCredentials auth = new ValidateCredentials();
		
		//Login Attempts Loop
    	while (loginAttempt > 0) {
            auth = new ValidateCredentials();

            // username prompt
            System.out.println("Enter username: ");
            System.out.println("(To exit enter \"Q\")");
            username = scnr.nextLine();

            /*
             * ***MODIFIED**
             */
            //checks for blank input
            if (username == "") {
                System.out.println("Please try again");
            }
            
            //checks if username exists
            else if (auth.userExists(username)) {
                System.out.println("Enter a password: ");
                password = scnr.nextLine();
                if (auth.isCredentialsValid(username, password)) {
                	authentication = true;
                	break;
                }
                else {
                	authentication = false;
                	break;
                }
            }
            
            else if (username.length() == 1) {
                userOption = username.charAt(0);
                //checks for quit option selection
                if (userOption == 'Q' || userOption == 'q') {
                    System.out.println("Goodbye");
                    loginAttempt = 0;
                }
                else {
                	System.out.println("Invalid input");
                }
            }
            else {
            	loginAttempt--;
            	System.out.println("Authentication Failed. Login attempts left: " + loginAttempt);
            }

    	}
		return authentication;
	}

	/*
	 * Selection Prompt
	 * 
	 * Simply outputs the menu options
	 * for the user to make a selection. 
	 *
	 */
	public static void selectionPrompt() {
    	System.out.println("Please make a selection: ");
    	System.out.println("    1. Read user access");
    	System.out.println("    2. Add new user");
    	System.out.println("    3. Update user info");
    	System.out.println("    4. Delete user");
    	System.out.println("    5. Quit");
    }
    
    /*
     * User Access
     * 
     * Provides the user with the main 
     * access point for their role. Currently
     * accesses a file for their role but can
     * be modified to display a dashboard 
     * for their role.  
     */
    public static void userAccess() throws FileNotFoundException, Exception {
    	
    	int loginAttempt = 3;
        String username = null;
        char userOption = '0';
        String passWord;
        ValidateCredentials auth;
        
        //Login Attempts Loop
    	while (loginAttempt > 0) {
            auth = new ValidateCredentials();

            // username prompt
            System.out.println("Enter username: ");
            System.out.println("(To exit enter \"Q\")");
            username = scnr.nextLine();

            //checks for blank input
            if (username == "") {
                System.out.println("Please try again");
            }
            
            //checks if username exists
            else if (auth.userExists(username)) {
                System.out.println("Enter a password: ");
                passWord = scnr.nextLine();
                
                //uses isCredentialsValid method within Validate Credentials
                //to see if credentials are valid
                if (auth.isCredentialsValid(username, passWord)) {
                	auth.readData(username);
                	
                	//checks for Q to quit again
                    System.out.println("Enter 'Q' to quit");
                    username = scnr.nextLine();
                    if (username.length() == 1) {
                        userOption = username.charAt(0);
                        if (userOption == 'Q' || userOption == 'q') {
                            System.out.println("Logout Successful. Goodbye");
                            break;
                        }
                    }
                }
                else {
                	loginAttempt--;
                	System.out.println("Authentication Failed. Login attempts left: " + loginAttempt);
                }
            }   
            
            //checks for username length to check for character input
            else if (username.length() == 1) {
                userOption = username.charAt(0);
                //checks for quit option selection
                if (userOption == 'Q' || userOption == 'q') {
                    System.out.println("Goodbye");
                    loginAttempt = 0;
                }
                else {
                	System.out.println("Invalid input");
                }
            }
            
            else {
            	loginAttempt--;
                System.out.println("Authentication Failed. Login attempts left: " + loginAttempt);
            }
        }

    }
}
