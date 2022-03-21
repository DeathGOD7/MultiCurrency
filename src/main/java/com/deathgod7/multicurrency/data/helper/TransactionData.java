package com.deathgod7.multicurrency.data.helper;

import com.deathgod7.multicurrency.data.DatabaseManager;

import java.util.ArrayList;
import java.util.List;

public class TransactionData {

    public static List<Column> Transaction (String timestamp, String currency, String amount, String type,
                                            String from, String to, String reason) {
        List<Column> temp = new ArrayList<>();

        Column _timestamp = new Column("Timestamp", timestamp, DatabaseManager.DataType.STRING, 100);
        Column _currency = new Column("Currency", currency, DatabaseManager.DataType.STRING, 100);
        Column _amount = new Column("Amount", amount, DatabaseManager.DataType.STRING, 100);
        Column _type = new Column("Type", type, DatabaseManager.DataType.STRING, 100);
        Column _from = new Column("From", from, DatabaseManager.DataType.STRING, 100);
        Column _to = new Column("To", to, DatabaseManager.DataType.STRING, 100);
        Column _reason = new Column("Reason", reason, DatabaseManager.DataType.STRING, 100);

        temp.add(_timestamp);
        temp.add(_currency);
        temp.add(_amount);
        temp.add(_type);
        temp.add(_from);
        temp.add(_to);
        temp.add(_reason);

        return temp;
    }

}
