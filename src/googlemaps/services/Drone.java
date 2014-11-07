package googlemaps.services;

import googlemaps.intro.R;
import android.os.Handler;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

public class Drone 
{
	private static final BitmapDescriptor DEFAULT_DRONE_IMG = BitmapDescriptorFactory.fromResource(R.drawable.drone);
	private static final BitmapDescriptor SELECTED_DRONE_IMG = BitmapDescriptorFactory.fromResource(R.drawable.selected_drone);
	private static final BitmapDescriptor DEFAULT_SC2DRONE_IMG = BitmapDescriptorFactory.fromResource(R.drawable.sc2drone);
	private static final BitmapDescriptor SELECTED_SC2DRONE_IMG = BitmapDescriptorFactory.fromResource(R.drawable.selected_sc2drone);

	private static BitmapDescriptor defaultDroneImage = DEFAULT_DRONE_IMG;
	private static BitmapDescriptor selectedDroneImage = SELECTED_DRONE_IMG;
	
	private final Handler handler = new Handler();
	private final MarkerOptions currentLocationMarkerOptions = new MarkerOptions();
	
	private int droneId;
	public static boolean isSC2;
	private boolean isSelected;
	private GoogleMap map;
	private Marker currentLocationMarker;
	private MoveDrone movementCommand;

	public Drone(GoogleMap map, int droneId)
	{
		this.isSelected = false;
		this.map = map;
		this.droneId = droneId;
		this.currentLocationMarkerOptions.icon(defaultDroneImage).title("Drone " + droneId);		
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
		isSelected = !isSelected;
		if (isSelected) {
			currentLocationMarker.setIcon(selectedDroneImage);
		} else {
			currentLocationMarker.setIcon(defaultDroneImage);
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
	
	public void refreshDroneIcon() {
		if (isSC2) {
			defaultDroneImage = DEFAULT_SC2DRONE_IMG;
			selectedDroneImage = SELECTED_SC2DRONE_IMG;
		} else {
			defaultDroneImage = DEFAULT_DRONE_IMG;
			selectedDroneImage = SELECTED_DRONE_IMG;
		}
		
		if (isSelected) {
			currentLocationMarker.setIcon(selectedDroneImage);
		} else {
			currentLocationMarker.setIcon(defaultDroneImage);
		}
	}
}
