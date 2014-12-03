package drone_megaddon.ui_services;

import googlemaps.intro.R;
import android.os.Handler;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

public class Drone 
{
	private static final BitmapDescriptor DEFAULT_DRONE_IMG = 
			BitmapDescriptorFactory.fromResource(R.drawable.drone);
	private static final BitmapDescriptor SELECTED_DRONE_IMG = 
			BitmapDescriptorFactory.fromResource(R.drawable.selected_drone);
	private static final BitmapDescriptor DEFAULT_SC2DRONE_IMG = 
			BitmapDescriptorFactory.fromResource(R.drawable.sc2dronecr);
	private static final BitmapDescriptor SELECTED_SC2DRONE_IMG = 
			BitmapDescriptorFactory.fromResource(R.drawable.selected_sc2dronecr);
	private static final BitmapDescriptor HP100_IMG = 
			BitmapDescriptorFactory.fromResource(R.drawable.hp100);
	private static final BitmapDescriptor HP75_IMG = 
			BitmapDescriptorFactory.fromResource(R.drawable.hp75);
	private static final BitmapDescriptor HP50_IMG = 
			BitmapDescriptorFactory.fromResource(R.drawable.hp50);
	private static final BitmapDescriptor HP25_IMG = 
			BitmapDescriptorFactory.fromResource(R.drawable.hp25);
	private static final BitmapDescriptor HP00_IMG = 
			BitmapDescriptorFactory.fromResource(R.drawable.hp00);
	private static BitmapDescriptor defaultDroneImage = DEFAULT_DRONE_IMG;
	private static BitmapDescriptor selectedDroneImage = SELECTED_DRONE_IMG;

	private final Handler handler = new Handler();
	private final MarkerOptions currentLocationMarkerOptions = new MarkerOptions();
	private final MarkerOptions currentHpMarkerOptions = new MarkerOptions();

	private int droneId;
	public static boolean isSC2;
	private boolean isSelected;
	private GoogleMap map;
	private Marker currentLocationMarker;
	private Marker currentHpMarker;
	private MoveDrone movementCommand;

	public Drone(GoogleMap map, int droneId)
	{
		this.isSelected = false;
		this.map = map;
		this.droneId = droneId;
		this.currentLocationMarkerOptions.icon(defaultDroneImage).title("Drone " + droneId).anchor(0.5f, 0.5f);		
		this.currentHpMarkerOptions.icon(HP100_IMG).title("100%");
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
			currentHpMarker.setIcon(HP100_IMG);
		} else {
			currentLocationMarker.setIcon(defaultDroneImage);
			currentHpMarker.setIcon(HP00_IMG);
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
			currentHpMarker = map.addMarker(currentHpMarkerOptions.position(location));
			currentLocationMarker = map.addMarker(currentLocationMarkerOptions.position(location));
		} else {
			currentHpMarker.setPosition(location);
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
		movementCommand = new MoveDrone(map, handler, currentLocationMarker, destinationMarker, currentHpMarker);
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
