package jayms.elytra.event;

import org.bukkit.Bukkit;

import jayms.elytra.ElytraPlugin;

public class Updater implements Runnable {

	private static boolean isRunning = false;
	
	public Updater() {
		if (isRunning) {
			return;
		}
		isRunning = true;
		ElytraPlugin.instance().getServer().getScheduler().runTaskTimerAsynchronously(ElytraPlugin.instance(), this, 0L, 1L);
	}

	@Override
	public void run() {
		Bukkit.getPluginManager().callEvent(new UpdateEvent());
	}
}
