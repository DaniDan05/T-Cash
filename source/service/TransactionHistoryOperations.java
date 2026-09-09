package source.service;

import source.data.TransactionHistoryData;

final public class TransactionHistoryOperations {
    final private TransactionHistoryData recordsDB;

    public TransactionHistoryOperations (TransactionHistoryData recordsDB) {
        this.recordsDB = recordsDB;
    }
}
