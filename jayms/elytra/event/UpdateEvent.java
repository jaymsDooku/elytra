package jayms.elytra.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class UpdateEvent extends Event {

	private static final HandlerList handlers = new HandlerList(); 
	
	public UpdateEvent() {
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
}
