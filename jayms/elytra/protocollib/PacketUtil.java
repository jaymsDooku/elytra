package jayms.elytra.protocollib;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;

public final class PacketUtil {

	private static final ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
	
	public static void sendFakeExpChange(Player p, float expBar, int level, int totalExp) {
		PacketContainer container = protocolManager.createPacket(PacketType.Play.Server.EXPERIENCE);
		container.getFloat().write(0, expBar);
		container.getIntegers().write(0, level);
		container.getIntegers().write(1, totalExp);
		try {
			protocolManager.sendServerPacket(p, container);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
}
