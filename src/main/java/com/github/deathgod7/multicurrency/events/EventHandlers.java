package com.github.deathgod7.multicurrency.events;

import com.github.deathgod7.multicurrency.data.DatabaseManager;
import com.github.deathgod7.multicurrency.depends.economy.CurrencyType;
import com.github.deathgod7.multicurrency.depends.economy.treasury.TreasuryAccountManager;
import com.github.deathgod7.multicurrency.depends.economy.treasury.TreasuryManager;
import com.github.deathgod7.multicurrency.MultiCurrency;
import com.github.deathgod7.multicurrency.utils.ConsoleLogger;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class EventHandlers implements Listener {

	MultiCurrency instance;
	TreasuryManager treasuryManager;
	DatabaseManager dbm;
	TreasuryAccountManager tAM;

	public EventHandlers(MultiCurrency instance) {
		this.instance = instance;
		this.treasuryManager = instance.getTreasuryManager();
		this.dbm = instance.getDBM();
		this.tAM = instance.getTreasuryAccountmanager();
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		if (!tAM.hasPlayerAccount(player.getUniqueId())){
			tAM.registerPlayerAccount(player.getUniqueId());
			ConsoleLogger.info("User ("+ player.getName() +") player account created.", ConsoleLogger.logTypes.debug);
		}
		else {
			ConsoleLogger.info("User ("+ player.getName() +") player account already exists.", ConsoleLogger.logTypes.debug);
		}

		for (CurrencyType ctyp : treasuryManager.getCurrencyTypes().values()) {
			if (treasuryManager.getTreasuryCurrency().containsKey(ctyp.getName())) {
				boolean status = dbm.createUser(player, ctyp);

				if (status){
					ConsoleLogger.info("User ("+ player.getName() +") account in " + ctyp.getName() + " currency created.", ConsoleLogger.logTypes.debug);
				}
				else {
					ConsoleLogger.info("User ("+ player.getName() +") account in " + ctyp.getName() + " currency couldn't be created.", ConsoleLogger.logTypes.debug);
				}
			}
		}
	}
}
