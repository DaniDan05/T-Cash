package source.ui;

import java.util.Scanner;

import org.mindrot.jbcrypt.BCrypt;

import source.service.AccountOperations;
import source.util.Database;

final class Register {
    final private Scanner input;
    final private AccountOperations accountOperations;

    Register(Scanner input, AccountOperations accountOperations) {
        this.input = input;
        this.accountOperations = accountOperations;
    }

    void execute(){
        System.out.println("\nREGISTER\n\nUsername (Min of 4 char and max of 50 char)");
        String inputUserName = input.nextLine();

        if (inputUserName.length() < 4 || 50 < inputUserName.length()){
            System.out.println("Length must be 4 - 50");
            return;
        }

        System.out.println("Cellphone Number");
        String inputCpNumber = input.nextLine();

        if (inputCpNumber.length() != 11){
            System.out.println("Length must be 11");
            return;
        }

        System.out.println("MPIN");
        String inputMpin = input.nextLine().trim();

        if ( inputMpin.length() !=6 ) {
            System.out.println("Length must be 6.");
            return;
        }

        System.out.println("Confirm MPIN");
        if ( ! inputMpin.equals(input.nextLine()) ) {
            System.out.println("Password Credentials is not the same.");
            return;
        }

        System.out.println("Is it Business account? [Y/N]");

        boolean isBusinessAccount = input.nextLine().equalsIgnoreCase("Y");

        String inputBusinessName = null;
        if (isBusinessAccount) {
            System.out.println("Input Business name");
            inputBusinessName = input.nextLine();
            if( inputBusinessName.length() < 4 || 50 < inputBusinessName.length() ) {
                System.out.println("Limited 4 - 50 range of characters.");
                return;
            }
        }

        try {
            accountOperations.generateAccount(
                inputUserName,
                inputCpNumber,
                inputMpin, 
                inputBusinessName,
                isBusinessAccount
            );

            System.out.println("Account Generated.");
        } catch (RuntimeException e) {
            System.out.println("Register error, " + e.getMessage());
        }
        
    }
}
