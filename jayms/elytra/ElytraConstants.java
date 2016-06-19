package jayms.elytra;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_9_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import net.minecraft.server.v1_9_R1.NBTTagCompound;

public final class ElytraConstants {

	private static ItemStack elytraWings = new ItemStack(Material.ELYTRA, 1);
	
	public static ItemStack getElytraWings() {
		return elytraWings;
	}
	
	static {
		net.minecraft.server.v1_9_R1.ItemStack stack = CraftItemStack.asNMSCopy(elytraWings); 
		NBTTagCompound tag = new NBTTagCompound();
		tag.setBoolean("Unbreakable", true);
		stack.setTag(tag);
		elytraWings = CraftItemStack.asCraftMirror(stack);
	}
}
