package source;
import java.util.Scanner;

import source.ui.MainMenu;
import source.data.AccountData;
import source.data.TransactionHistoryData;
import source.service.AccountOperations;
import source.service.TransactionHistoryOperations;
import source.service.TransactionOperations;
import source.service.Transfer;


public class Main {    

    
    public static void main(String[] args) {
        AccountData accountData = new AccountData();
        TransactionHistoryData transactionHistoryData = new TransactionHistoryData();
        Transfer transfer = new Transfer(accountData, transactionHistoryData);

        Scanner input = new Scanner(System.in);
        new MainMenu(
            input,
            new AccountOperations(accountData),
            new TransactionOperations(
                accountData,
                transfer
            ),
            new TransactionHistoryOperations(transactionHistoryData)
        ).execute();
        input.close();
    }
}