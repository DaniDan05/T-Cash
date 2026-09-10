package source.ui;

import java.util.Scanner;

import source.service.AccountOperations;

import source.util.Validator;

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

        if (Validator.isInvalidName(inputUserName)) {
            System.out.println("Length must be 4 - 50");
            return; 
        }

        System.out.println("Cellphone Number");
        String inputCpNumber = input.nextLine();

        if (Validator.isInvalidCpNumber(inputCpNumber)) {
            System.out.println("Length must be 11");
            return;
        }

        System.out.println("MPIN");
        String inputMpin = input.nextLine().trim();

        if (Validator.isInvalidMpin(inputMpin)) {
            System.out.println("Length must be 6.");
            return;
        }

        System.out.println("Confirm MPIN");
        if ( !inputMpin.equals(input.nextLine()) ) {
            System.out.println("Password Credentials is not the same.");
            return;
        }

        System.out.println("Is it Business account? [Y/N]");

        boolean isBusinessAccount = input.nextLine().equalsIgnoreCase("Y");

        String inputBusinessName = null;
        if (isBusinessAccount) {
            System.out.println("Input Business name");
            inputBusinessName = input.nextLine();
            if (Validator.isInvalidBusinessName(inputBusinessName)) {
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
