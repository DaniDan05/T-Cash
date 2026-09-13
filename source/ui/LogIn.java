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
        printLogIn();

        for (int attempt = 0; attempt < 3; attempt++) {
            printAttemptIndicator(attempt);


            System.out.print("📱 CP NUMBER (0 to cancel): ");
            String inputCpNumber = input.nextLine();

            if (inputCpNumber.equals("0")) {
                System.out.println("👋 Cancelled.\n");
                return;
            }

            if (Validator.isNotNumerical(inputCpNumber)) {
                System.out.println("⚠️  Number symbols only.\n");
                continue;
            }

            if (Validator.isInvalidCpNumber(inputCpNumber)) {
                System.out.println("⚠️  Length must be 11.\n");
                continue;
            }


            System.out.print("🔑 MPIN: ");
            String inputMpin = input.nextLine();

            if (Validator.isNotNumerical(inputMpin)) {
                System.out.println("⚠️  Number symbols only.\n");
                continue;
            }

            if (Validator.isInvalidMpin(inputMpin)) {
                System.out.println("⚠️  Length must be 6.\n");
                continue;
            }


            try {
                Account loggedAccount = accountOperations.authenticate(inputCpNumber, inputMpin);
                printSuccess();

                new Session(
                    input,
                    loggedAccount,
                    accountOperations,
                    transactionOperations,
                    transactionHistoryOperations
                ).execute();

                System.out.println("👋 Logging out...\n");
                return;

            } catch (IllegalArgumentException e) {
                System.out.println("⚠️  " + e.getMessage());
                continue;

            } catch (RuntimeException e) {
                 System.out.println("❌ System error, " + e.getMessage());
                return;
            }
        }
        printLockedOut();
    }

    private void printLogIn() {
        System.out.println("""
        ╔══════════════════════════════════╗
        ║    💰  WELCOME TO T-CASH  💰     ║
        ╠══════════════════════════════════╣
        ║           ⟪ LOG IN ⟫             ║
        ╠══════════════════════════════════╣
        ║                                  ║
        ║   Enter your account details     ║
        ║   to continue.                   ║
        ║                                  ║
        ╚══════════════════════════════════╝
        
        """);
    }

    private void printAttemptIndicator(int attempt) {
        int remaining = 3 - attempt;
        System.out.println();
        System.out.println("  ── Attempt " + (attempt + 1) + " of 3 (" + remaining + " remaining) ──");
    }

    private void printSuccess() {
        System.out.println("""
            ╔══════════════════════════════════════╗
            ║      ✅  LOGGED IN SUCCESSFULLY      ║
            ╚══════════════════════════════════════╝
            """);
    }

    private void printLockedOut() {
        System.out.println("""
            ╔═══════════════════════════════════════════╗
            ║       ❌  TOO MANY FAILED ATTEMPTS        ║
            ║       Returning to main menu...           ║
            ╚═══════════════════════════════════════════╝
            """);
    }
}

