package drone_megaddon.communication.rx;

import com.google.android.gms.maps.model.LatLng;

import drone_megaddon.ui_services.Drone;
import drone_megaddon.ui_services.MapService;

public class GPSCommand extends RxCommand
{
	private LatLng location;
	private long altitude;
	private long battery;
	
	public GPSCommand(int droneId, LatLng location, long altitude, long battery)
	{
		super(droneId);
		this.location = location;
		this.altitude = altitude;
		this.battery = battery;
	}
	
	public long getAltitude()
	{
		return altitude;
	}
	
	public long getBattery()
	{
		return battery;
	}
	
	@Override
	public void execute(MapService mapService) 
	{
		if (mapService == null) {
			return;
		}
		
		Drone target = mapService.addDrone(droneId, location);
		target.updateDroneHealthBar(battery);
	}
}
