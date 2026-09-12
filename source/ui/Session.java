package source.ui;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.List;

import source.model.Account;
import source.model.Account.AccountType;
import source.model.Account.TransactionType;
import source.model.Business;

import source.service.Transfer;
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
                "\nBALANCE: ₱" + loggedAccount.getBalance() + 
                "\n1: Send, 2: Cash In, 3: Pay Bills, 4: Transaction History"
            );

            String choice = input.nextLine();


            switch (choice) {
                case "1":
                    viewSendMenu();
                    break;
                case "2":     
                    viewCashInMenu();
                    break;

                case "3":
                    viewPayBillsMenu();
                    break;
                case "4":
                    try {
                        transactionHistoryOperations.retrieveTransactionHistories(loggedAccount.getAccountID())
                           .forEach(x -> System.out.println(x));
                    
                    } catch (IllegalArgumentException e) { 
                        System.out.println(e.getMessage());
                    } catch (RuntimeException e) {
                        System.out.println("Retrieving error, " + e.getMessage());
                    }
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

        System.out.println("Input Amount");
        BigDecimal amount = input.nextBigDecimal();
        input.nextLine();

        if(Validator.isInvalidAmount(amount)) {
            System.out.println("Invalid amount.");
            return;
        }

        // Business Logic (Connection guaranteed):
        // Self send 
        // If the target account exist
        // Wallet limit
        // kapag malaki yung amount kesa sa current amount ni sender

        BigDecimal fee = loggedAccount.getSendFee(amount); 
        try {
            // Read Only
            Account receiver = transactionOperations.checkSendDataAndGetReceiverAccount(loggedAccount, receiverCp, amount, fee);
            // BigDecimal totalAmount = amount.add(fee);

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

            transactionOperations.getTransfer(
                loggedAccount,
                receiver,
                amount,
                fee,
                TransactionType.SEND
            );
            System.out.println("Transfer successfully.\n" + transactionHistoryOperations.retrieveReceipt(loggedAccount.getAccountID()));
        } catch (IllegalArgumentException e) { 
            System.out.println(e.getMessage());
        } catch (RuntimeException e) {
            System.out.println("Send error, " + e.getMessage());
        }
    }


    private void viewCashInMenu() {
        List<Account> agents;
        try{
            agents = transactionOperations.retrieveAccounts(AccountType.AGENT);
        } catch (IllegalArgumentException e) { 
            System.out.println(e.getMessage());
            return;
        } catch (RuntimeException e) {
            System.out.println("Cash in error, " + e.getMessage());
            return;
        }
        
        agents.forEach(agent -> System.out.println(agent.getName())); // service

        System.out.println("Enter a agent name.");
        Account targetAgent;
        try{
            targetAgent = transactionOperations.findAccount(agents, input.nextLine()); // service
        } catch (IllegalArgumentException e) { 
            System.out.println(e.getMessage());
            return;
        }
        
        System.out.println("Input Amount");
        BigDecimal inputAmount = input.nextBigDecimal();
        input.nextLine();
        if (Validator.isInvalidAmount(inputAmount)){
            System.out.println("Invalid amount.");
            return;
        }

        try {
            transactionOperations.validateWalletLimit(loggedAccount.getBalance(), inputAmount, loggedAccount.getWalletLimit());
        } catch (IllegalArgumentException e) { 
            System.out.println(e.getMessage());
            return;
        } catch (RuntimeException e) {
            System.out.println("Cash in error, " + e.getMessage());
            return;
        }

        System.out.println("Cash in " + inputAmount + " from " + targetAgent.getName() + "? [Y/N]");
        if (input.nextLine().trim().equalsIgnoreCase("N")) {
            System.out.println("Cancelled Transaction...");
            return;
        }

        try {            
            transactionOperations.getTransfer(
                targetAgent,
                loggedAccount,
                inputAmount,
                BigDecimal.ZERO,
                TransactionType.CASH_IN
            );
            System.out.println("Transfer successfully.\n\n" + transactionHistoryOperations.retrieveReceipt(loggedAccount.getAccountID()));
        } catch (IllegalArgumentException e) { 
            System.out.println(e.getMessage());
            return;
        } catch (RuntimeException e) {
            System.out.println("Cash in error, " + e.getMessage());
            return;
        }
    }

    private void viewPayBillsMenu() {
        List<Account> billers;
        try{
            billers = transactionOperations.retrieveAccounts(AccountType.BILLER);
        } catch (IllegalArgumentException e) { 
            System.out.println(e.getMessage());
            return;
        } catch (RuntimeException e) {
            System.out.println("Pay bills error, " + e.getMessage());
            return;
        }
        
        billers.forEach(biller -> System.out.println(biller.getName()));

        System.out.println("Enter a biller name.");
        Account targetBiller;
        try{
            targetBiller = transactionOperations.findAccount(billers, input.nextLine());
        } catch (IllegalArgumentException e) { 
            System.out.println(e.getMessage());
            return;
        } catch (RuntimeException e) {
            System.out.println("Pay bills error, " + e.getMessage());
            return;
        }

        System.out.println("Input Amount");
        BigDecimal inputAmount = input.nextBigDecimal();
        input.nextLine();
        if (Validator.isInvalidAmount(inputAmount)){
            System.out.println("Invalid amount.");
            return;
        }

        BigDecimal billersFee = loggedAccount.getBillersFee();
        System.out.println("Biller: " + targetBiller.getName() +
            "\nAmount: " + inputAmount +
            "\nFee: " + billersFee +
            "\nTotal: " + inputAmount.add(billersFee) +
            "\nPay this bill? [Y/N]");
        if (input.nextLine().trim().equalsIgnoreCase("N")) {
            System.out.println("Cancelled Transaction...");
            return;
        }

        try {
            transactionOperations.validateSufficientBalance(
                loggedAccount.getBalance(), inputAmount.add(billersFee));

            transactionOperations.getTransfer(
                loggedAccount,        // sender = logged account
                targetBiller,         // receiver = biller
                inputAmount,
                billersFee,           // may fee
                TransactionType.PAY_BILLS);

            System.out.println("Transfer successfully.\n\n" + 
                transactionHistoryOperations.retrieveReceipt(loggedAccount.getAccountID()));

        } catch (IllegalArgumentException e) { 
            System.out.println(e.getMessage());
            return;
        } catch (RuntimeException e) {
            System.out.println("Pay bills error, " + e.getMessage());
            return;
        }
    }
}
