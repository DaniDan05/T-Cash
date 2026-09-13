package source.ui;

import java.math.BigDecimal;

import java.util.Scanner;
import java.util.List;

import source.model.Account;
import source.model.Account.AccountType;
import source.model.Account.TransactionType;
import source.model.Business;

import source.service.AccountOperations;
import source.service.TransactionHistoryOperations;
import source.service.TransactionOperations;

import source.util.Validator;

final class Session {
    final private Scanner input;
    final private Account loggedAccount;
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
        this.transactionOperations = transactionOperations;
        this.transactionHistoryOperations = transactionHistoryOperations;
    }

    void execute() {
        MENU: while (true) {
            
            printSession();

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
                        System.out.println("⚠️  " + e.getMessage());
                    } catch (RuntimeException e) {
                        System.out.println("❌ Retrieving error, " + e.getMessage());
                    }
                    break;
                    
                default:
                    break MENU;
            }
        }
    }
// Business Logic (Connection guaranteed):
        // Self send 
        // If the target account exist
        // Wallet limit
        // kapag malaki yung amount kesa sa current amount ni sender
    private void viewSendMenu() {
        printSend();
        System.out.print("📱 Enter a Cellphone Number: ");
        String receiverCp = input.nextLine();

        if (Validator.isInvalidCpNumber(receiverCp)) {
            System.out.println("⚠️  Length must be 11\n");
            return;
        }

        System.out.print("💵 Input Amount: ");
        BigDecimal amount = input.nextBigDecimal();
        input.nextLine();

        if (Validator.isInvalidAmount(amount)) {
            System.out.println("⚠️  Invalid amount.\n");
            return;
        }

        BigDecimal fee = loggedAccount.getSendFee(amount);

        try {
            Account receiver = transactionOperations.checkSendDataAndGetReceiverAccount(
                loggedAccount, receiverCp, amount, fee);

            String censordNumber = "******" + receiver.getCpNumber()
                .substring(receiver.getCpNumber().length() - 5);

            System.out.println("👤 Recipient: " + receiver.getName() +
                "\n📱 Number: " + censordNumber +
                "\n📋 Type: " + receiver.getAccountType() +
                "\n💸 Fee: " + fee +
                "\n💵 Amount: " + amount +
                "\nDo you want to send it now? [Y/N]");

            if (input.nextLine().trim().equalsIgnoreCase("N")) {
                System.out.println("👋 Cancelled Transaction...\n");
                return;
            }

            transactionOperations.getTransfer(
                loggedAccount,
                receiver,
                amount,
                fee,
                TransactionType.SEND
            );

            printSuccess();
            System.out.println(transactionHistoryOperations.retrieveReceipt(loggedAccount.getAccountID()));

        } catch (IllegalArgumentException e) {
            System.out.println("⚠️  " + e.getMessage());
        } catch (RuntimeException e) {
            System.out.println("❌ Send error, " + e.getMessage());
        }
    }


    private void viewCashInMenu() {
    printCashInHeader();

        List<Account> agents;
        try{
            agents = transactionOperations.retrieveAccounts(AccountType.AGENT);
        } catch (IllegalArgumentException e) { 
            System.out.println("⚠️  " + e.getMessage());
            return;
        } catch (RuntimeException e) {
            System.out.println("❌ Cash in error, " + e.getMessage());
            return;
        }
        
        agents.forEach(agent -> System.out.println("🏪 " + agent.getName()));

        System.out.print("👤 Enter a agent name: ");
        Account targetAgent;
        try{
            targetAgent = transactionOperations.findAccount(agents, input.nextLine());
        } catch (IllegalArgumentException e) { 
            System.out.println("⚠️  " + e.getMessage());
            return;
        }
        
        System.out.print("💵 Input Amount: ");
        BigDecimal inputAmount = input.nextBigDecimal();
        input.nextLine();
        if (Validator.isInvalidAmount(inputAmount)){
            System.out.println("⚠️  Invalid amount.\n");
            return;
        }

        try {
            transactionOperations.validateWalletLimit(loggedAccount.getBalance(), inputAmount, loggedAccount.getWalletLimit());
        } catch (IllegalArgumentException e) { 
            System.out.println("⚠️  " + e.getMessage());
            return;
        } catch (RuntimeException e) {
            System.out.println("❌ Cash in error, " + e.getMessage());
            return;
        }

        System.out.println("💵 Cash in " + inputAmount + " from " + targetAgent.getName() + "? [Y/N]");
        if (input.nextLine().trim().equalsIgnoreCase("N")) {
            System.out.println("👋 Cancelled Transaction...\n");
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

            printSuccess();
            System.out.println(transactionHistoryOperations.retrieveReceipt(loggedAccount.getAccountID()));

        } catch (IllegalArgumentException e) { 
            System.out.println("⚠️  " + e.getMessage());
            return;
        } catch (RuntimeException e) {
            System.out.println("❌ Cash in error, " + e.getMessage());
            return;
        }
    }


    private void viewPayBillsMenu() {
        printPayBills();

        List<Account> billers;
        try{
            billers = transactionOperations.retrieveAccounts(AccountType.BILLER);
        } catch (IllegalArgumentException e) { 
            System.out.println("⚠️  " + e.getMessage());
            return;
        } catch (RuntimeException e) {
            System.out.println("❌ Pay bills error, " + e.getMessage());
            return;
        }
        
        billers.forEach(biller -> System.out.println("🏢 " + biller.getName()));

        System.out.print("👤 Enter a biller name: ");
        Account targetBiller;
        try{
            targetBiller = transactionOperations.findAccount(billers, input.nextLine());
        } catch (IllegalArgumentException e) { 
            System.out.println("⚠️  " + e.getMessage());
            return;
        } catch (RuntimeException e) {
            System.out.println("❌ Pay bills error, " + e.getMessage());
            return;
        }

        System.out.print("💵 Input Amount: ");
        BigDecimal inputAmount = input.nextBigDecimal();
        input.nextLine();
        if (Validator.isInvalidAmount(inputAmount)){
            System.out.println("⚠️  Invalid amount.");
            return;
        }

        BigDecimal billersFee = loggedAccount.getBillersFee();
        System.out.println("🏢 Biller: " + targetBiller.getName() +
            "\n💵 Amount: " + inputAmount +
            "\n💸 Fee: " + billersFee +
            "\n💰 Total: " + inputAmount.add(billersFee) +
            "\nPay this bill? [Y/N]");
        if (input.nextLine().trim().equalsIgnoreCase("N")) {
            System.out.println("👋 Cancelled Transaction...\n");
            return;
        }

        try {
            transactionOperations.validateSufficientBalance(
                loggedAccount.getBalance(), inputAmount.add(billersFee));

            transactionOperations.getTransfer(
                loggedAccount,
                targetBiller,
                inputAmount,
                billersFee,
                TransactionType.PAY_BILLS);

            printSuccess();
            System.out.println(transactionHistoryOperations.retrieveReceipt(loggedAccount.getAccountID()));

        } catch (IllegalArgumentException e) { 
            System.out.println("⚠️  " + e.getMessage());
            return;
        } catch (RuntimeException e) {
            System.out.println("❌ Pay bills error, " + e.getMessage());
            return;
        }
    }
    private void printSession() {
        System.out.println("""
        ╔═════════════════════════════════════════╗
        ║       💰  WELCOME TO T-CASH  💰         ║
        ╠═════════════════════════════════════════╣
        ║             ⟪ MAIN MENU ⟫               ║
        ╠═════════════════════════════════════════╣
        ║                                         ║
        ║   [1]  💸  SEND                         ║
        ║   [2]  💵  CASH IN                      ║
        ║   [3]  🧾  PAY BILLS                    ║
        ║   [4]  📜  TRANSACTION HISTORY          ║
        ║   [5]  🚪  LOG OUT                      ║
        ║                                         ║
        ╚═════════════════════════════════════════╝
        """);

        System.out.println("👤 " + loggedAccount.getName());
        System.out.println("📋 " + loggedAccount.getAccountType());

        if (loggedAccount instanceof Business) {
            System.out.println("🏢 " + ((Business) loggedAccount).getBusinessName());
        }

        System.out.println("💵 Balance: ₱" + loggedAccount.getBalance().toPlainString());
        System.out.println("");
    }

    private void printSend() {
        System.out.println("""
        ╔══════════════════════════════════╗
        ║    💰  WELCOME TO T-CASH  💰     ║
        ╠══════════════════════════════════╣
        ║           ⟪ 💸 SEND ⟫            ║
        ╠══════════════════════════════════╣
        ║                                  ║
        ║  Send money to another account.  ║
        ║                                  ║
        ╚══════════════════════════════════╝

        """);
    }

    private void printCashInHeader() {
        System.out.println("""
        ╔═════════════════════════════════╗
        ║    💰  WELCOME TO T-CASH  💰    ║
        ╠═════════════════════════════════╣
        ║         ⟪ 💵 CASH IN ⟫          ║
        ╠═════════════════════════════════╣
        ║                                 ║
        ║  Cash in from an active agent.  ║
        ║                                 ║
        ╚═════════════════════════════════╝

        """);
    }

    private void printPayBills() {
        System.out.println("""
        ╔═════════════════════════════════╗
        ║    💰  WELCOME TO T-CASH  💰    ║
        ╠═════════════════════════════════╣
        ║         ⟪ 🧾 PAY BILLS ⟫        ║
        ╠═════════════════════════════════╣
        ║                                 ║
        ║   Pay your bills to a biller.   ║
        ║                                 ║
        ╚═════════════════════════════════╝

        """);
    }

    private void printSuccess() {
        System.out.println("""
        
        ╔══════════════════════════════════════╗
        ║      ✅  TRANSFER SUCCESSFUL         ║
        ╚══════════════════════════════════════╝
        """);
    }

}
