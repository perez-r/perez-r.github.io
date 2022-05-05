/*
 * FILENAME: Authentication.java
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;


/**
 *
 * @author ruben.perez_snhu
 */
public class Authentication {
	static Scanner scnr = new Scanner(System.in);
	static String[] roles = new String[]{"zookeeper", "admin", "veterenarian"};
	static List<String> roleList = new ArrayList<>(Arrays.asList(roles));
	
	/*
	 * MAIN FUNCTION
	 * 
	 */

    public static void main(String[] args) throws Exception {
    	boolean state = true;
    	
        User user = new User(authenticate());
        
        if (roleList.contains(user.getRole())) {	
        	while (state) {
        		state = userCases(user);
        	}
        }
        System.out.println("Terminating Program.");
    }
    
    /*
     * END MAIN FUNCTION
     * 
     * 
     */
    
    
    /*
     * Delete User
     * 
     * Called when user selects to delete a user. 
     * prompts for authentication then requests
     * information for which user to be deleted. 
     * 
     */
    private static boolean deleteUser(User user) throws Exception {
    	boolean success = false;
		ValidateCredentials auth = new ValidateCredentials();
		
		if (user.getRole() == "admin") {
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
    private static boolean updateUser(User user) throws Exception {
    	String username;
		String password;
		String role;
    	boolean success = false;
    	ZooDB db = new ZooDB();		
		ValidateCredentials auth = new ValidateCredentials();
		
		switch (user.getRole()) {
			case "admin":
				scnr.nextLine();
				System.out.println("Please enter the username to be updated: ");	
				username = scnr.nextLine();		
			
				if (auth.userExists(username)) {
					System.out.println("Please enter current or updated password: ");			
					password = scnr.nextLine();
				
					System.out.println("Please specify the user type: ");
					role = scnr.nextLine();
				
					success = db.updateUser(username, password, role);
				} 
				break;
			case "zookeeper":
			case "veterinarian":
				scnr.nextLine();
				System.out.println("Please enter current or updated password: ");			
				password = scnr.nextLine();
			
				success = db.updateUser(user.getUsername(), password, user.getRole());
				break;
			default:
				System.out.println("Error Please try again");
				break;			
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
	private static boolean addUser(User user) throws Exception {
		boolean success = false;
		
		ValidateCredentials auth = new ValidateCredentials();	
		
		if (user.getRole() == "admin") {
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
	private static User authenticate() throws Exception {
		int loginAttempt = 3;
		String username = null;
		String password;
		char userOption = '0';
		
		ValidateCredentials auth = new ValidateCredentials();
		User user = new User();
		
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
            else if (username.length() > 1 && username.length() <= 32) {
                System.out.println("Enter a password: ");
                password = scnr.nextLine();
                
                if (auth.userExists(username)) {
                	if (auth.isCredentialsValid(username, password) != null) {
                		user.setUsername(username);
                    	user.setRole(auth.isCredentialsValid(username, password));
                    	break;
                    } else {
                    	loginAttempt--;
                    	System.out.println("Authentication Failed. Login attempts left: " + loginAttempt);
                    }
                } else {
                	loginAttempt--;
                	System.out.println("Authentication Failed. Login attempts left: " + loginAttempt);
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
		return user;
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
	
	public static boolean userCases(User user) throws FileNotFoundException, Exception {
        int selection = 0;
        boolean state = true;
        
        selectionPrompt();
        selection = scnr.nextInt();
    	
        switch (selection) {
        case 1: 
        	userAccess(user.getRole());
        	break;
        case 2:
        	// add user
        	if (addUser(user)) {
        		System.out.println("Success!");
        	}
        	else {
        		unauthorizedAction();
        	}
        	break;
        case 3:
        	if (updateUser(user)) {
        		System.out.println("Success!");
        	} else {
        		unauthorizedAction();
        	}
        	break;
        case 4: 
        	// delete user info
        	if (deleteUser(user)) {
        		System.out.println("Success!");
        	}
        	else {
        		unauthorizedAction();
        	}
        	break;
        case 5: 
        	state = false;
        default: 
        	break;

        }
		return state;
	}

	private static void unauthorizedAction() {
		System.out.println("User not authorized for this action. Please try again later");
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
    public static void userAccess(String role) throws FileNotFoundException, Exception {
        ValidateCredentials auth;
        auth = new ValidateCredentials();
        auth.readData(role);
    }
}
