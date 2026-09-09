package source.ui;

import java.util.Scanner;

import source.Main;
import source.model.Account;
import source.service.AccountOperations;
import source.service.TransactionHistoryOperations;
import source.service.TransactionOperations;
import source.util.Database;
import source.util.Database;

final class LogIn {
    final private Scanner input;
    final private AccountOperations accountOperations;
    final private TransactionOperations transactionOperations;
    final private TransactionHistoryOperations transactionHistoryOperations;

    public LogIn(
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

            System.out.println("MPIN");
            String inputMPin = input.nextLine();

            // Open the account menu only when the account exists and
            // the supplied MPIN is valid.
            try {
                Account loggedAccount = accountOperations.authenticate(inputCpNumber, inputMPin);
                new Session(
                    input,
                    loggedAccount,
                    accountOperations,
                    transactionOperations,
                    transactionHistoryOperations
                ).execute();
                System.out.println("Log in successfully.");
            } catch (RuntimeException e) {
                System.out.println("Log in error, " + e.getMessage());
            }
        }       

    }
}
