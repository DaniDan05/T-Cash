package source.ui;

import java.util.Scanner;

import source.model.Account;
import source.model.Business;
import source.service.AccountOperations;
import source.service.TransactionHistoryOperations;
import source.service.TransactionOperations;
import source.util.Database;

final class Session {
    final private Scanner input;
    final private Account loggedAccount;
    final private AccountOperations accountOperations;
    final private TransactionOperations transactionOperations;
    final private TransactionHistoryOperations transactionHistoryOperations;

    public Session(
            Scanner input,
            Account loggedAccount,
            AccountOperations accountOperations,
            TransactionOperations transactionOperations,
            TransactionHistoryOperations transactionHistoryOperations
    ) {
        this.input = input;
        this.loggedAccount = loggedAccount;
        this.accountOperations = accountOperations;
        this.transactionOperations = transactionOperations;
        this.transactionHistoryOperations = transactionHistoryOperations;
    }

    void execute() {
        System.out.print("You are now log in ");
        MENU: while (true) {
            System.out.println(
                loggedAccount.getUsername() +
                "\nAccount Type: " + loggedAccount.getAccountType() +
                (loggedAccount instanceof Business ? "\nBusiness Name: " + ((Business)loggedAccount).getBusinessName() : "") + 
                "\nBALANCE: " + loggedAccount.getAmount() + 
                "\n1: Send, 2: Cash In, 3: Pay Bills, 4: Transaction History"
            );

            int choice = input.nextInt();
            input.nextLine();

            switch (choice) {
                case 1:
                    
                    transactionOperations.send(input);
                    break;
                case 2:
                    /* Cash In */
                    transactionOperations.cashIn(input);
                    break;

                case 3:
                    transactionOperations.payBills(input);
                    break;
                case 4:
                    transactionHistoryOperations.retrieveTransactionHistories(loggedAccount.getAccountID())
                            .forEach(x -> System.out.println(x));
                    break;

                case 5:
                    // Profile Account
                    break MENU;
            
                default:
                    System.out.println("Exit...");
                    break MENU;
            }
        }
    }
}
