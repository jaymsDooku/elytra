package jayms.elytra;

import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import jayms.elytra.common.collections.Tuple;
import jayms.elytra.particles.ParticleEffect;
import jayms.elytra.protocollib.PacketUtil;
import net.md_5.bungee.api.ChatColor;

public class ElytraListener implements Listener {

	private HashMap<UUID, Long> cooldowns = new HashMap<>();
	private HashMap<UUID, Tuple<Long, Tuple<Float, Tuple<Integer, Integer>>>> start = new HashMap<UUID, Tuple<Long, Tuple<Float, Tuple<Integer, Integer>>>>();
	
	/*private ArrayList<UUID> trails = new ArrayList<>();*/
	
	int particles = 5;

	@EventHandler(priority = EventPriority.NORMAL)
	public void onShfit(PlayerToggleSneakEvent e) {
		Player p = e.getPlayer();
		UUID uuid = p.getUniqueId();
		if (p.isGliding()) {
			if (cooldowns.containsKey(uuid)) {
				if (cooldowns.get(uuid) > System.currentTimeMillis()) {
					double cooldown = (cooldowns.get(uuid) - System.currentTimeMillis()) / 1000;
					if (cooldown >= 1) {
						p.sendMessage(ChatColor.DARK_RED + "You're too tired to fly right now! Try again in: " + cooldown + " seconds");
					}
					return;
				}else {
					cooldowns.remove(uuid);
				}
			}
			if (e.isSneaking()) {
				start.put(uuid, new Tuple<>(System.currentTimeMillis(), new Tuple<>(p.getExp(), new Tuple<>(p.getLevel(), p.getExpToLevel()))));
				new BukkitRunnable() {
					
					@Override
					public void run() {
						System.out.println(start.get(uuid));
						if ((System.currentTimeMillis() - start.get(uuid).getA()) > 1000) {
							PacketUtil.sendFakeExpChange(p, 1, 100, 100);
							this.cancel();
						}
					}
					
				}.runTaskTimer(ElytraPlugin.instance(), 0L, 1L);
			}else {
				if (!start.containsKey(uuid)) {
					return;
				}
				Tuple<Long, Tuple<Float, Tuple<Integer, Integer>>> val = start.remove(uuid);
				long startTime = val.getA();
				long diff = System.currentTimeMillis() - startTime;
				double factor = 1.07;
				boolean pastSecond = diff > 1000;
				if (!pastSecond) {
					factor = 0.5;
					cooldowns.put(uuid, System.currentTimeMillis() + 250);
				}else {
					int foodLevel = p.getFoodLevel();
					if (foodLevel > 1) {
						p.setFoodLevel(foodLevel - 1);
					}else {
						return;
					}
					Tuple<Float, Tuple<Integer, Integer>> expTup = val.getB();
					Tuple<Integer, Integer> expTup2 = expTup.getB();
					PacketUtil.sendFakeExpChange(p, expTup.getA(), expTup2.getA(), expTup2.getB());
				}
				p.setVelocity(p.getEyeLocation().getDirection().multiply(factor));
				if (pastSecond) {
					Location loc = p.getLocation().add(p.getLocation().getDirection().normalize().multiply(-1));
					for (double r = 1; r < 3; r+=0.2) {
						Vector randomVec = getRandomVector();
						loc.add(randomVec);
						ParticleEffect.CLOUD.display(loc, 0f, 0f, 0f, 0.1f, (int) (particles * r));
						loc.subtract(randomVec);
					}
				}
			}
		}
	}
	
	public static final Random random = new Random(System.nanoTime());
	
	public static Vector getRandomVector() {
        double x, y, z;
        x = random.nextDouble() * 2 - 1;
        y = random.nextDouble() * 2 - 1;
        z = random.nextDouble() * 2 - 1;

        return new Vector(x, y, z).normalize();
    }
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		ItemStack chestplate = p.getInventory().getChestplate();
		if (chestplate == null || chestplate.getType() != Material.ELYTRA) {
			p.getInventory().setChestplate(ElytraConstants.getElytraWings());
			p.updateInventory();
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onReSpawn(PlayerRespawnEvent e) {
		Player p = e.getPlayer();
		ItemStack chestplate = p.getInventory().getChestplate();
		if (chestplate == null || chestplate.getType() != Material.ELYTRA) {
			p.getInventory().setChestplate(ElytraConstants.getElytraWings());
			p.updateInventory();
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL) 
	public void onInventoryClick(InventoryClickEvent e) {
		ElytraPlugin.log.info("f" + e.getSlot());
		if (e.getCurrentItem().getType() == Material.ELYTRA) {
			if (e.getWhoClicked() instanceof Player) {
				Player p = (Player) e.getWhoClicked();
				if (p.getGameMode() == GameMode.SURVIVAL) {
					if (e.getSlot() == 38) {
						e.setCancelled(true);
						e.setResult(Result.DENY);
					}
				}else if (p.getGameMode() == GameMode.CREATIVE){
					if (e.getSlot() == 6) {
						e.setCancelled(true);
						e.setResult(Result.DENY);
					}
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onDeath(PlayerDeathEvent e) {
		List<ItemStack> drops = e.getDrops();
		for (ItemStack it : drops) {
			if (it.getType() == Material.ELYTRA) {
				drops.remove(it);
				break;
			}
		}
	}
	
	/*
	@EventHandler(priority = EventPriority.NORMAL)
	public void onUpdate(UpdateEvent e) {
		for (UUID uuid : trails) {
			Player p = ElytraPlugin.instance().getServer().getPlayer(uuid);
			Location loc = p.getLocation().add(p.getLocation().getDirection().normalize().multiply(-1));
			ParticleEffect.CLOUD.display(loc, 0.4f, 0.4f, 0.4f, 0f, 2);
		}
	}*/
}
