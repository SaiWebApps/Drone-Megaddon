package drone_megaddon.communication.rx;

import drone_megaddon.communication.Command;
import drone_megaddon.ui_services.MapService;

public abstract class RxCommand extends Command
{	
	public RxCommand(int droneId) 
	{
		super(droneId);
	}

	/**
	 * Invoke the appropriate method in MapService to handle the
	 * information received from the drone and stored in this command.
	 * @param mapService - MapService instance that is in charge
	 * of handling this command
	 */
	public abstract void execute(MapService mapService);
}
