package drone_megaddon.communication;

import drone_megaddon.ui_services.MapService;

public interface Command 
{
	public int getDroneId();
	public void setDroneId(int droneId);
	
	public void execute(MapService mapService);
}
