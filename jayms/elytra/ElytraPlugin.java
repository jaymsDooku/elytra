package jayms.elytra;

import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

import jayms.elytra.event.Updater;


public class ElytraPlugin extends JavaPlugin {

	public static Logger log = Logger.getLogger("Minecraft");
	private static ElytraPlugin instance;
	
	public static ElytraPlugin instance() {
		return instance;
	}
	
	@Override
	public void onEnable() {
		instance = this;
		log.info("ElytraFlying enabling...");
		getServer().getPluginManager().registerEvents(new ElytraListener(), this);
		new Updater();
		log.info("ElytraFlying has been enabled!");
	}
	
	@Override
	public void onDisable() {
		log.info("ElytraFlying disabling...");
		log.info("ElytraFlying has been disabled!");
	}
}
