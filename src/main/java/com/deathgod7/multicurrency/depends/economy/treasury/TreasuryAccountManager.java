package com.deathgod7.multicurrency.depends.economy.treasury;

import com.deathgod7.multicurrency.MultiCurrency;
import com.deathgod7.multicurrency.data.helper.AccountTable;
import com.deathgod7.multicurrency.data.helper.Column;
import com.deathgod7.multicurrency.data.helper.Table;
import com.deathgod7.multicurrency.utils.ConsoleLogger;
import me.lokka30.treasury.api.economy.account.NonPlayerAccount;
import me.lokka30.treasury.api.economy.account.PlayerAccount;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class TreasuryAccountManager {
    final MultiCurrency instance;
    final HashMap<String, PlayerAccount> playerAccounts = new HashMap<>();  //  uuid and playeraccount
    final HashMap<String, NonPlayerAccount> nonPlayerAccounts = new HashMap<>();  //  identifier and nonplayeraccount
    final Table accountsTable;

    public TreasuryAccountManager (MultiCurrency instance){
        this.instance = instance;
        accountsTable = instance.getDBM().getTables().get("TreasuryAccounts");
        loadData();
    }

    public void loadData() {
        if (accountsTable != null) {
            List<List<Column>> temp = accountsTable.getAllColumns();

            if (temp == null) {
                ConsoleLogger.severe("Ohh....something is wrong...It seems I couldn't get all accounts.", ConsoleLogger.logTypes.debug);
                return;
            }

            for (List<Column> account : temp) {
                String uuid = account.get(0).getValue().toString();
                String name = account.get(1).getValue().toString();
                String type = account.get(2).getValue().toString();

                if (type.equalsIgnoreCase("PLAYER")) {
                    playerAccounts.put(
                            uuid,
                            new TreasuryPlayerAccount(
                                    instance, UUID.fromString(uuid)
                                    )
                            );
                }
                else {
                    nonPlayerAccounts.put(
                            uuid,
                            new TreasuryNpcAccount(
                                    instance, uuid, name
                            )
                    );
                }
            }
            ConsoleLogger.info("All account data are loaded", ConsoleLogger.logTypes.debug);
        }
        else {
            ConsoleLogger.info("Hmmm...strange, Accounts table seems to be null.", ConsoleLogger.logTypes.debug);
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

        boolean status = accountsTable.insert(
                AccountTable.PlayerAccount(
                        player.getUniqueId().toString(),
                        player.getName()
                )
        );

        if (!status){
            ConsoleLogger.severe("Couldn't register player account for player " + player.getName(), ConsoleLogger.logTypes.log);
            playerAccount = null;
            return playerAccount;
        }

        playerAccount = new TreasuryPlayerAccount(
                instance, player.getUniqueId()
        );

        playerAccounts.put(
                player.getUniqueId().toString(),
                playerAccount
        );

        return playerAccount;

    }

    public boolean hasPlayerAccount(UUID playeruuid){
        return playerAccounts.get(playeruuid.toString()) != null;
    }

    public PlayerAccount getPlayerAccount(UUID playeruuid){
        return playerAccounts.get(playeruuid.toString());
    }

    public HashMap<String, PlayerAccount> getAllPlayerAccounts(){
        return  playerAccounts;
    }


    // NON-PLAYER REGION
    public NonPlayerAccount registerNpcAccount(String identifier, String npcname){
        NonPlayerAccount npcAccount;

        if (hasNpcAccount(identifier)) {
            return null;
        }

        boolean status = accountsTable.insert(
                AccountTable.NonPlayerAccount(
                        identifier,
                        npcname
                )
        );

        if (!status){
            ConsoleLogger.severe("Couldn't register NPC account named " + npcname, ConsoleLogger.logTypes.log);
            npcAccount = null;
            return npcAccount;
        }

        npcAccount = new TreasuryNpcAccount(
                instance, identifier, npcname
        );

        nonPlayerAccounts.put(
                identifier,
                npcAccount
        );

        return npcAccount;

    }

    public boolean hasNpcAccount(String identifier){
        return nonPlayerAccounts.get(identifier) != null;
    }

    public NonPlayerAccount getNpcAccount(String  identifier){
        return nonPlayerAccounts.get(identifier);
    }

    public HashMap<String, NonPlayerAccount> getAllNpcAccounts(){
        return nonPlayerAccounts;
    }



}
