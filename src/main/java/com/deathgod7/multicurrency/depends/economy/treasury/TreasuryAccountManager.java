package com.deathgod7.multicurrency.depends.economy.treasury;

import com.deathgod7.multicurrency.MultiCurrency;
import com.deathgod7.multicurrency.data.helper.Column;
import com.deathgod7.multicurrency.data.helper.Table;
import com.deathgod7.multicurrency.data.sqlite.SQLite;
import com.deathgod7.multicurrency.utils.ConsoleLogger;
import me.lokka30.treasury.api.economy.account.Account;
import me.lokka30.treasury.api.economy.account.PlayerAccount;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class TreasuryAccountManager {
    final MultiCurrency instance;
    final HashMap<String, PlayerAccount> playerAccounts = new HashMap<>();  //  name and playeraccount
    final HashMap<String, Account> nonPlayerAccounts = new HashMap<>();  //  name and nonplayeraccount
    final Table accountsTable;

    public TreasuryAccountManager (MultiCurrency instance){
        this.instance = instance;
        accountsTable = instance.getDBM().getTables().get("TreasuryAccounts");
        loadData();
    }

    public void loadData() {
        if (accountsTable != null) {
            for (List<Column> account : accountsTable.getAll()) {
                if (account.get(2).getValue().toString().toUpperCase().equals("PLAYER")){
                    playerAccounts.put(
                            account.get(1).getValue().toString(),
                            new TreasuryPlayerAccount(
                                    instance, UUID.fromString(account.get(0).toString())
                                    )
                            );
                }
                else {
                    nonPlayerAccounts.put(
                            account.get(1).getValue().toString(),
                            new TreasuryNpcAccount(
                                    instance, account.get(0).toString()
                            )
                    );
                }
            }
        }
    }

    // PLAYER REGION
    public PlayerAccount registerPlayerAccount(UUID playeruuid){
        PlayerAccount playerAccount;
        Player player = (Player) Bukkit.getOfflinePlayer(playeruuid);

        if (hasPlayerAccount(playeruuid)) {
            playerAccount = null;
            return playerAccount;
        }

        List<Column> temp = new ArrayList<>();
        Column uuid = new Column("UUID", player.getUniqueId().toString(), SQLite.DataType.STRING, 100);
        Column name = new Column("Name", player.getName(), SQLite.DataType.STRING, 100);
        Column type = new Column("Type", "PLAYER",SQLite.DataType.STRING, 100);

        temp.add(uuid);
        temp.add(name);
        temp.add(type);

        boolean status = accountsTable.insert(temp);

        if (!status){
            ConsoleLogger.severe("Couldn't register player account for player " + player.getName(), ConsoleLogger.logTypes.log);
            playerAccount = null;
            return playerAccount;
        }

        playerAccount = new TreasuryPlayerAccount(
                instance, UUID.fromString(player.getUniqueId().toString())
        );

        playerAccounts.put(
                player.getName(),
                playerAccount
        );

        return playerAccount;

    }

    public boolean hasPlayerAccount(UUID playeruuid){
        Player player = (Player) Bukkit.getOfflinePlayer(playeruuid);
        return playerAccounts.get(player.getName()) != null;
    }

    public PlayerAccount getPlayerAccount(UUID playeruuid){
        Player player = (Player) Bukkit.getOfflinePlayer(playeruuid);
        return playerAccounts.get(player.getName());
    }

    public HashMap<String, PlayerAccount> getAllPlayerAccounts(){
        return  playerAccounts;
    }


    // NON-PLAYER REGION
    public Account registerNpcAccount(String npcname){
        Account npcAccount;

        if (hasNpcAccount(npcname)) {
            npcAccount = null;
            return npcAccount;
        }

        List<Column> temp = new ArrayList<>();
        Column uuid = new Column("UUID", npcname, SQLite.DataType.STRING, 100);
        Column name = new Column("Name", npcname, SQLite.DataType.STRING, 100);
        Column type = new Column("Type", "NPC",SQLite.DataType.STRING, 100);

        temp.add(uuid);
        temp.add(name);
        temp.add(type);

        boolean status = accountsTable.insert(temp);

        if (!status){
            ConsoleLogger.severe("Couldn't register NPC account named " + npcname, ConsoleLogger.logTypes.log);
            npcAccount = null;
            return npcAccount;
        }

        npcAccount = new TreasuryNpcAccount(
                instance, npcname
        );

        nonPlayerAccounts.put(
                npcname,
                npcAccount
        );

        return npcAccount;

    }

    public boolean hasNpcAccount(String npcname){
        return nonPlayerAccounts.get(npcname) != null;
    }

    public Account getNpcAccount(String  npcname){
        return nonPlayerAccounts.get(npcname);
    }

    public HashMap<String, Account> getAllNpcAccounts(){
        return nonPlayerAccounts;
    }



}
