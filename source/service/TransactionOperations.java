package source.service;

import java.util.Scanner;
import java.math.BigDecimal;

import source.model.Account;

final public class TransactionOperations {
    final private Transfer transfer;

    public TransactionOperations (Transfer transfer) {
        this.transfer = transfer;
    }

    public void send(Scanner input){

        //─────INPUT AND VALIDATE─────//
        System.out.println("Enter a Cellphone Number.");
        String inputCpNumber = input.nextLine();

        if (this.cpNumber.equals(inputCpNumber)) {
            System.out.println("Cancelled: Self sending is not applicable");
            return;
        }

        Account receiverAccount = Database.readAccountData(inputCpNumber);
        if (receiverAccount == null) {
            System.out.println("Cancelled: Account does not exist.");
            return;
        }

        System.out.println("Input Amount");
        BigDecimal inputAmount = input.nextBigDecimal();
        input.nextLine();

        if (isInvalidAmount(inputAmount) || 
            isInsufficientAmount(inputAmount.add(getSendFee(inputAmount))))
            return;        

        String cp = receiverAccount.getCpNumber();
        System.out.println("Receipient: " + receiverAccount.getUsername() + 
            "\nNumber: " + "******" + cp.substring(cp.length() - 5) + 
            "\nType: " + receiverAccount.getAccountType() +
            "\nFee: " + getSendFee(inputAmount) + 
            "\nAmount: " + inputAmount + 
            "\nDo you want to send it now?[Y/N]"
        );

        if(input.nextLine()
                .trim()
                .equalsIgnoreCase("N")){
            System.out.println("Cancelled Transaction...");
            return;
        }


        //─────MAIN─────//
        try { 
            this.amount = this.amount.subtract(
                Database.transfer(
                    this,
                    receiverAccount,
                    inputAmount,
                    getSendFee(inputAmount),
                    TransactionType.SEND
                )
            ); 
        } catch (SQLException e) { 
            // e.printStackTrace();
            System.out.println("Sending Failed...\n");
        }
    }

    
    public void cashIn(Scanner input) {
        
        //─────GENERATE CONTAINER─────//
        List<Account> agents = Database.getAccountsByType(AccountType.AGENT);
        if (agents.isEmpty()) {
            System.out.println("No active agent available.");
            return;
        }

        //─────DISPLAY─────//
        agents.forEach(x -> System.out.println(x.getUsername()));

        //─────INPUT AND VALIDATE─────//
        System.out.println("Enter a agent name.");
        Account targetAccount = findAccount(agents, input.nextLine());
        if (targetAccount == null){
            System.out.println("Does not exist...");
            return;
        }

        System.out.println("Input Amount");
        BigDecimal inputAmount = input.nextBigDecimal();
        input.nextLine();
        if (isInvalidAmount(inputAmount))
            return;

        //─────MAIN─────//
        try {
            this.amount = this.amount.add(
                Database.transfer(
                    targetAccount,
                    this,
                    inputAmount,
                    BigDecimal.ZERO,
                    TransactionType.CASH_IN
                )
            );
        } catch (SQLException e) { 
            // e.printStackTrace();
            System.out.println("Cash in failed...");
        } 
    }

    
    public void payBills(Scanner input) {
        
        List<Account> billers = Database.getAccountsByType(AccountType.BILLER);
        if (billers.isEmpty()) {
            System.out.println("No active billers available.");
            return;
        }

        billers.forEach(x -> System.out.println(x.getUsername()));

        System.out.println("Enter a biller name.");
        Account targetAccount = findAccount(billers, input.nextLine());
        if (targetAccount == null) {
            System.out.println("Does not exist try again...");
            return;
        }

        System.out.println("Input Amount");
        BigDecimal inputAmount = input.nextBigDecimal();
        input.nextLine();
        if (isInvalidAmount(inputAmount) ||
            isInsufficientAmount(inputAmount.add(getBillersFee())))
            return;

        //─────CORE─────//
        try {
            this.amount = this.amount.subtract(
                Database.transfer(
                    this,
                    targetAccount,
                    inputAmount,
                    getBillersFee(),
                    TransactionType.PAY_BILLS
                )
            );
        } catch (SQLException e) { 
            // e.printStackTrace();
            System.out.println("Paying bills failed...");
        }
    }
}