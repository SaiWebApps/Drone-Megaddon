package googlemaps.services;

import googlemaps.intro.R;
import android.os.Handler;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

public class Drone 
{
	private final int DEFAULT_DRONE_ID = R.drawable.drone;
	private final int SELECTED_DRONE_ID = R.drawable.selected_drone;
	private final BitmapDescriptor DEFAULT_DRONE_IMG = BitmapDescriptorFactory.fromResource(DEFAULT_DRONE_ID);
	private final BitmapDescriptor SELECTED_DRONE_IMG = BitmapDescriptorFactory.fromResource(SELECTED_DRONE_ID);
	private final Handler handler = new Handler();
	private final MarkerOptions currentLocationMarkerOptions = new MarkerOptions();

	private int droneId;
	private boolean isSelected;
	private GoogleMap map;
	private Marker currentLocationMarker;
	private MoveDrone movementCommand;

	public Drone(GoogleMap map, int droneId)
	{
		this.isSelected = false;
		this.map = map;
		this.droneId = droneId;
		this.currentLocationMarkerOptions.icon(DEFAULT_DRONE_IMG).title("Drone " + droneId);		
	}

	public int getDroneId()
	{
		return droneId;
	}

	public Marker getCurrentLocationMarker()
	{
		return currentLocationMarker;
	}
	
	public boolean isSelected()
	{
		return isSelected;
	}
	
	public void toggleSelect()
	{
		this.isSelected = !this.isSelected;
		if (this.isSelected) {
			this.currentLocationMarker.setIcon(SELECTED_DRONE_IMG);
		} else {
			this.currentLocationMarker.setIcon(DEFAULT_DRONE_IMG);
		}
	}
	
	/**
	 * If the drone isn't already on the map, then initialize it at the specified location.
	 * Otherwise, update its current location to the given one.
	 * @param location
	 */
	public void addToMap(LatLng location)
	{
		if (currentLocationMarker == null) {
			currentLocationMarker = map.addMarker(currentLocationMarkerOptions.position(location));
		} else {
			currentLocationMarker.setPosition(location);
		}
	}
	
	public void moveToDestMarker(Marker destinationMarker)
	{
		// Do nothing if we haven't created a source or destinationMarkerOptions marker yet.
		if (currentLocationMarker == null || destinationMarker == null) {
			return;
		}
		// Cancel all previous movement commands.
		if (movementCommand != null) {
			handler.removeCallbacks(movementCommand, null);
		}
		// Issue a new movement command.
		movementCommand = new MoveDrone(map, handler, currentLocationMarker, destinationMarker);
		handler.post(movementCommand);
	}
}
