package com.deathgod7.multicurrency.events;

import com.deathgod7.multicurrency.MultiCurrency;
import com.deathgod7.multicurrency.data.DatabaseManager;
import com.deathgod7.multicurrency.depends.economy.CurrencyType;
import com.deathgod7.multicurrency.depends.economy.treasury.TreasuryManager;
import com.deathgod7.multicurrency.utils.ConsoleLogger;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class EventHandler implements Listener {

    MultiCurrency instance;
    TreasuryManager treasuryManager;

    public EventHandler(MultiCurrency instance) {
        this.instance = instance;
        this.treasuryManager = instance.getTreasuryManager();
    }

    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        DatabaseManager dbm = instance.getDBM();

        for (CurrencyType ctyp : treasuryManager.getCurrencyTypes().values()) {
            if (treasuryManager.getTreasuryCurrency().containsKey(ctyp.getName())) {
                boolean isExists = dbm.doesUserExists(player, ctyp);

                if (!isExists){
                    boolean status = dbm.createUser(player, ctyp);

                    if (status){
                        ConsoleLogger.info("User ("+ player.getName() +") account created.", ConsoleLogger.logTypes.debug);
                    }
                    else {
                        ConsoleLogger.info("User ("+ player.getName() +") account couldnot be created.", ConsoleLogger.logTypes.debug);
                    }
                }
                else {
                    ConsoleLogger.info("User ("+ player.getName() +") already exists. Skipping account creation.", ConsoleLogger.logTypes.debug);
                }
            }
        }
    }
}
