package source.ui;

import java.util.Scanner;

import source.service.AccountOperations;
import source.service.TransactionHistoryOperations;
import source.service.TransactionOperations;


final public class MainMenu {
    final private Scanner input;
    final private AccountOperations accountOperations;
    final private TransactionOperations transactionOperations;
    final private TransactionHistoryOperations transactionHistoryOperations;

    public MainMenu(
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

    public void execute() {
        LogIn logIn =  new LogIn(
                        input,
                        accountOperations,
                        transactionOperations,
                        transactionHistoryOperations
                    );

        Register register = new Register(input,accountOperations);
        
        MAIN_MENU: while(true){
            
            printMainMenu();

            String choice = input.nextLine();

            switch (choice) {
                case "1":
                    logIn.execute();
                    break;

                case "2":
                    register.execute();
                    break;
            
                default:
                    break MAIN_MENU;
            }
        }
    }

    private void printMainMenu() {
        
    System.out.print("""
    ╔═══════════════════════════════════════╗
    ║       💰  WELCOME TO T-CASH  💰       ║
    ╠═══════════════════════════════════════╣
    ║            ⟪ MAIN MENU ⟫              ║
    ╠═══════════════════════════════════════╣
    ║                                       ║
    ║   [1]  🔐  LOG IN                     ║
    ║   [2]  📝  REGISTER                   ║
    ║   [ ]  🚪  EXIT                       ║
    ║                                       ║
    ╚═══════════════════════════════════════╝
    SELECT AN OPTION: """);
    }
}
