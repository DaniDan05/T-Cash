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
        printRegister();
        while (true) {
            System.out.print("👤 USERNAME (0 to cancel): ");
            String inputUserName = input.nextLine();

            if (inputUserName.equals("0")) {
                System.out.println("👋 Cancelled.\n");
                return;
            }

            if (Validator.isNotFormatName(inputUserName)) {
                System.out.println("⚠️  Alphanumerical, space and underscore only.\n");
                continue;
            }

            if (Validator.isInvalidName(inputUserName)) {
                System.out.println("⚠️  Length must be 4 - 50.\n");
                continue;
            }

            System.out.print("📱 CELLPHONE NUMBER: ");
            String inputCpNumber = input.nextLine();

            if (inputCpNumber.equals("0")) {
                System.out.println("👋 Cancelled.");
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
            String inputMpin = input.nextLine().trim();

            if (inputMpin.equals("0")) {
                System.out.println("👋 Cancelled.\n");
                return;
            }

            if (Validator.isNotNumerical(inputMpin)) {
                System.out.println("⚠️  Number symbols only.\n");
                continue;
            }

            if (Validator.isInvalidMpin(inputMpin)) {
                System.out.println("⚠️  Length must be 6.\n");
                continue;
            }

            System.out.print("🔑 CONFIRM MPIN: \n");
            if (!inputMpin.equals(input.nextLine())) {
                System.out.println("⚠️  MPIN does not match.\n");
                continue;
            }

            System.out.print("🏢 Is it Business account? [Y/N]: \n");
            boolean isBusinessAccount = input.nextLine().equalsIgnoreCase("Y");

            String inputBusinessName = null;
            if (isBusinessAccount) {
                System.out.print("🏢 BUSINESS NAME: \n");
                inputBusinessName = input.nextLine();

                if (inputBusinessName.equals("0")) {
                    System.out.println("👋 Cancelled.\n");
                    return;
                }

                if (Validator.isNotFormatName(inputBusinessName)) {
                    System.out.println("⚠️  Alphanumerical, space and underscore only.\n");
                    continue;
                }

                if (Validator.isInvalidBusinessName(inputBusinessName)) {
                    System.out.println("⚠️  Length must be 4 - 50.\n");
                    continue;
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

                printSuccess();
                return;
            } catch (IllegalArgumentException e) {
                System.out.println("⚠️  " + e.getMessage());
                return;

            } catch (RuntimeException e) {
                System.out.println("❌ System error, " + e.getMessage());
                return;
            }
        }
       
    }

    private void printRegister() {
        System.out.println("""
            ╔═══════════════════════════════════════╗
            ║       💰  WELCOME TO T-CASH  💰       ║
            ╠═══════════════════════════════════════╣
            ║             ⟪ REGISTER ⟫              ║
            ╠═══════════════════════════════════════╣
            ║                                       ║
            ║   Create your account to start        ║
            ║   using T-Cash.                       ║
            ║                                       ║
            ╚═══════════════════════════════════════╝
            """);
    }

    private void printSuccess() {
        System.out.println("""
        ╔═══════════════════════════════════════════════╗
        ║       ✅  ACCOUNT CREATED SUCCESSFULLY        ║
        ╚═══════════════════════════════════════════════╝
        """);
    }

}
