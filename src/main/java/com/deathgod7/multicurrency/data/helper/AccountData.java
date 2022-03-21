package com.deathgod7.multicurrency.data.helper;

import com.deathgod7.multicurrency.data.DatabaseManager;

import java.util.ArrayList;
import java.util.List;

public class AccountData {

    public static List<Column> PlayerAccount(String identifier, String name) {
        List<Column> temp = new ArrayList<>();

        Column _uuid = new Column("UUID", identifier, DatabaseManager.DataType.STRING, 100);
        Column _name = new Column("Name", name, DatabaseManager.DataType.STRING, 100);
        Column _type = new Column("Type", "PLAYER",DatabaseManager.DataType.STRING, 100);

        temp.add(_uuid);
        temp.add(_name);
        temp.add(_type);

        return temp;
    }

    public static List<Column> NonPlayerAccount(String identifier, String npcname) {
        List<Column> temp = new ArrayList<>();

        Column _uuid = new Column("UUID", identifier, DatabaseManager.DataType.STRING, 100);
        Column _name = new Column("Name", npcname, DatabaseManager.DataType.STRING, 100);
        Column _type = new Column("Type", "NPC",DatabaseManager.DataType.STRING, 100);

        temp.add(_uuid);
        temp.add(_name);
        temp.add(_type);

        return temp;
    }

}
