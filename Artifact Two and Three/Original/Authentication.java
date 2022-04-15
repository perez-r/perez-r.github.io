/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package authentication;
import java.util.Scanner;
/**
 *
 * @author ruben.perez_snhu
 */
public class Authentication {

    public static void main(String[] args) throws Exception {
        int loginAttempt = 3;
        String userName;
        Scanner scnr = new Scanner(System.in);
        char userOption = '0';
        String passWord;

        //Login Attempts Loop
        while (loginAttempt > 0) {
            ValidateCredentials auth = new ValidateCredentials();

            //username prompt
            System.out.println("Enter username: ");
            System.out.println("(To exit enter \"Q\")");
            userName = scnr.nextLine();

            //checks for username length to check for character input
            if (userName.length() == 1) {
                userOption = userName.charAt(0);
            }

            //checks for quit option selection
            if (userOption == 'Q' || userOption == 'q') {
                System.out.println("Goodbye");
                loginAttempt = 0;
                break;
            }

            //checks for blank input
            if (userName == "") {
                System.out.println("Please try again");
            }
            
            //checks if username exists
            if (auth.userExists(userName)) {
                System.out.println("Enter a password: ");
                passWord = scnr.nextLine();
                
                //uses isCredentialsValid method within Validate Credentials
                //to see if credentials are valid
                if (auth.isCredentialsValid(userName, passWord)) {
                    //checks for Q to quit again
                    System.out.println("Enter 'Q' to quit");
                    userName = scnr.nextLine();

                    if (userName.length() == 1) {
                        userOption = userName.charAt(0);
                    }

                    if (userOption == 'Q' || userOption == 'q') {
                        System.out.println("Logout Successful. Goodbye");

                    }
                    break;
                } else {
                    System.out.println("Authentication Failed.");
                    loginAttempt--;
                }

            }
        }

       
        scnr.close();
        System.out.println("Terminating Program.");

    }
}
