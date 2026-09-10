package source.ui;

import java.math.BigDecimal;
import java.util.Scanner;

import source.model.Account;
import source.model.Business;

import source.service.AccountOperations;
import source.service.TransactionHistoryOperations;
import source.service.TransactionOperations;

import source.util.Database;
import source.util.Validator;

final class Session {
    final private Scanner input;
    final private Account loggedAccount;
    final private AccountOperations accountOperations;
    final private TransactionOperations transactionOperations;
    final private TransactionHistoryOperations transactionHistoryOperations;

    Session(
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
        System.out.println("Log in successfully.");
        MENU: while (true) {
            System.out.println(
                loggedAccount.getName() +
                "\nAccount Type: " + loggedAccount.getAccountType() +
                (loggedAccount instanceof Business ? "\nBusiness Name: " + ((Business)loggedAccount).getBusinessName() : "") + 
                "\nBALANCE: " + loggedAccount.getAmount() + 
                "\n1: Send, 2: Cash In, 3: Pay Bills, 4: Transaction History"
            );

            String choice = input.nextLine();


            switch (choice) {
                case "1":
                    viewSendMenu();
                    break;
                case "2":
                    /* Cash In */
                    // transactionOperations.cashIn(input);
                    viewCashInMenu();

                case "3":
                    // transactionOperations.payBills(input);
                    viewPayBillsMenu();
                    System.out.println("Transfer successfully.\n\n" + transactionHistoryOperations.retrieveReceipt("2" == choice ? receiverAccount.getAccountID() : senderAccount.getAccountID()));
                    break;
                case "4":
                    transactionHistoryOperations.retrieveTransactionHistories(loggedAccount.getAccountID())
                           .forEach(x -> System.out.println(x));
                    break;
                default:
                    System.out.println("Exit...");
                    break MENU;
            }
        }
    }

    private void viewSendMenu() {
        System.out.println("Enter a Cellphone Number.");
        String receiverCp = input.nextLine();

        if (Validator.isInvalidCpNumber(receiverCp)) {
            System.out.println("Length must be 11");
            return;
        }


        Account receiver = accountOperations.getAccountByCpNumber(receiverCp);
        if (receiver == null) {
            System.out.println("Account does not exist.");
            return;
        }

        if (loggedAccount.getAccountID() == receiver.getAccountID()) {
            System.out.println("Self sending is not applicable.");
            return;
        }


        System.out.println("Input Amount");
        BigDecimal amount = input.nextBigDecimal();
        input.nextLine();

        if(Validator.isInvalidAmount(amount)) {
            System.out.println("Invalid amount");
        }

        BigDecimal fee = loggedAccount.getSendFee(amount); 

        String censordNumber = 
            "******" + receiver.getCpNumber()
                               .substring(
                                receiver.getCpNumber().length() - 5
                            );
        System.out.println("Recipient: " + receiver.getName() +
            "\nNumber: " + censordNumber +
            "\nType: " + receiver.getAccountType() +
            "\nFee: " + fee +
            "\nAmount: " + amount +
            "\nDo you want to send it now? [Y/N]");

        if (input.nextLine().trim().equalsIgnoreCase("N")) {
            System.out.println("Cancelled Transaction...");
            return;
        }

        try {
            transactionOperations.send(loggedAccount, receiver, amount);
            System.out.println("Transfer successfully.\n" + transactionHistoryOperations.retrieveReceipt(loggedAccount.getAccountID()));
        } catch (RuntimeException e) {
            System.out.println("Send error, " + e.getMessage());
        }
    }

    private void viewCashInMenu() {
        System.out.println("Transfer successfully.\n\n" + transactionHistoryOperations.retrieveReceipt(receiverAccount.getAccountID()));
                    break;
    }

    private void viewPayBillsMenu() {

    }
}
