package source.ui;

import java.util.Scanner;

import source.model.Account;

import source.service.AccountOperations;
import source.service.TransactionHistoryOperations;
import source.service.TransactionOperations;

import source.util.Validator;


final class LogIn {
    final private Scanner input;
    final private AccountOperations accountOperations;
    final private TransactionOperations transactionOperations;
    final private TransactionHistoryOperations transactionHistoryOperations;

    LogIn(
        Scanner input,
        AccountOperations accountOperations,
        TransactionOperations transactionOperations,
        TransactionHistoryOperations transactionHistoryOperations
    ) {
        this.input = input;
        this.accountOperations = accountOperations;
        this.transactionOperations = transactionOperations;
        this.transactionHistoryOperations = transactionHistoryOperations;
    }
    

    void execute() {
        for (int attempt = 0; attempt < 3; attempt++) {

            System.out.println("\nLOG IN\n\nCellphone Number");
            String inputCpNumber = input.nextLine();

            if(Validator.isInvalidCpNumber(inputCpNumber)) {
                System.out.println("Length must be 11");
                continue;
            }

            System.out.println("MPIN");
            String inputMpin = input.nextLine();
            if (Validator.isInvalidMpin(inputMpin)) {
                System.out.println("Length must be 6.");
                continue;
            }

            // Open the account menu only when the account exists and
            // the supplied MPIN is valid.
            try {
                Account loggedAccount = accountOperations.authenticate(inputCpNumber, inputMpin);
                new Session(
                    input,
                    loggedAccount,
                    accountOperations,
                    transactionOperations,
                    transactionHistoryOperations
                ).execute();

                System.out.println("Logging out...");
                break; // Para mag log out talaga5
            } catch (RuntimeException e) {
                System.out.println("Log in error, " + e.getMessage());
            }
        }       
    }
}
