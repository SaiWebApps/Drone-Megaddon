package drone_megaddon.communication;

import com.google.android.gms.maps.model.LatLng;

import drone_megaddon.ui_services.MapService;


public class GPSCommand implements Command
{
	private int droneId;
	private LatLng location;
	
	public GPSCommand(int droneId, LatLng location)
	{
		this.droneId = droneId;
		this.location = location;
	}
	
	@Override
	public int getDroneId() 
	{
		return droneId;
	}

	@Override
	public void setDroneId(int droneId) 
	{
		this.droneId = droneId;
	}

	@Override
	public void execute(MapService mapService) 
	{
		if (mapService == null) {
			return;
		}
		mapService.addDrone(droneId, location);
	}
}
