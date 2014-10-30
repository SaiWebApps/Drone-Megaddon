package googlemaps.services;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;

import googlemaps.intro.MapActivity;
import googlemaps.intro.R;
import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapService implements GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener
{	
	private final Handler receivedMessageHandler = new Handler();
	private final float DEST_MARKER_COLOR = BitmapDescriptorFactory.HUE_GREEN;
	private final MarkerOptions destinationMarkerOptions = new MarkerOptions();

	private SparseArray<Drone> droneMap = new SparseArray<Drone>();
	private LinkedBlockingDeque<String> incomingInfo = new LinkedBlockingDeque<String>();

	private GoogleMap googleMap;
	private Marker destinationMarker;

	private MapActivity mapActivity;

	public MapService(MapActivity mapActivity) 
	{
		this.mapActivity = mapActivity;
		this.destinationMarkerOptions.icon(BitmapDescriptorFactory.
				defaultMarker(DEST_MARKER_COLOR));
		initGoogleMap();		
		registerHandlerForDroneInformation();
	}

	public Activity getActivity()
	{
		return mapActivity;
	}
	
	private void initGoogleMap()
	{
		this.googleMap = ((MapFragment) mapActivity.getFragmentManager().
				findFragmentById(R.id.map)).getMap();

		googleMap.setMyLocationEnabled(true);
		googleMap.setBuildingsEnabled(true);
		googleMap.setIndoorEnabled(true);
		googleMap.setOnMarkerClickListener(this);

		// Set map listeners.
		googleMap.setOnMapClickListener(this);
		googleMap.setOnMarkerClickListener(this);
	}

	private void registerHandlerForDroneInformation()
	{
		receivedMessageHandler.post(new Runnable() {
			public void run() {
				if (!incomingInfo.isEmpty()) {
					String info = incomingInfo.pop();
					String[] tokens = info.split(",");
					try {
						int id = Integer.parseInt(tokens[0]);
						double coord1 = Double.parseDouble(tokens[1]);
						double coord2 = Double.parseDouble(tokens[2]);
						addDrone(id, new LatLng(coord1, coord2));
					} catch (NumberFormatException e) {
						Log.e("Message Processing Error", "Did not receive valid numeric values.");
					}
				}
				receivedMessageHandler.postDelayed(this, 50);
			}
		});
	}

	public void saveReceivedInfo(String information)
	{
		incomingInfo.push(information);
	}

	public void addDrone(int droneId, LatLng location)
	{
		Drone target = droneMap.get(droneId);
		if (target == null) {
			target = new Drone(googleMap, droneId);
		}
		target.addToMap(location);
		droneMap.put(droneId, target);
	}

	public Drone getFirstDroneAtLocation(LatLng location)
	{		
		for (int i = 0; i < droneMap.size(); i++) {
			int key = droneMap.keyAt(i);
			Drone d = droneMap.get(key);
			LatLng currentLocationOfDrone = d.getCurrentLocationMarker().getPosition();

			if (d != null && currentLocationOfDrone.equals(location)) {
				return d;
			}
		}
		return null;
	}

	public List<Drone> getSelectedDrones()
	{
		List<Drone> selectedDrones = new ArrayList<Drone>();
		for (int i = 0; i < droneMap.size(); i++) {
			int key = droneMap.keyAt(i);
			Drone d = droneMap.get(key);
			if (d != null && d.isSelected()) {
				selectedDrones.add(d);
			}
		}
		return selectedDrones;
	}

	// Destroying the map and releasing resources
	public void releaseResources() 
	{
		googleMap = null;		
		mapActivity = null;
	}

	@Override
	public boolean onMarkerClick(Marker clickedMarker) {
		// Show the drone's identifying information (Drone droneId).
		clickedMarker.showInfoWindow();

		// Find out which drone was selected/unselected, and toggle its
		// "select" status accordingly.
		Drone selectedDrone = getFirstDroneAtLocation(
				clickedMarker.getPosition());
		if (selectedDrone != null) {
			selectedDrone.toggleSelect();
			return true;
		}
		return false;
	}

	@Override
	public void onMapClick(LatLng clickedLocation) 
	{
		// If the previous move command has not completed, then
		// remove the previous destination marker.
		if (destinationMarker != null) {
			destinationMarker.remove();
		}

		List<Drone> selectedDrones = getSelectedDrones();
		// Do nothing if no drones were selected.
		if (selectedDrones.isEmpty()) {
			addDrone(42, clickedLocation); // DHT: ZERG TEST DRONE
			return;
		}
		// If at least 1 drone was selected, place a marker at the destination,
		// and then move the drone to that location.
		destinationMarker = googleMap.addMarker(destinationMarkerOptions.
				position(clickedLocation));
		for (Drone selected : selectedDrones) {
			selected.moveToDestMarker(destinationMarker);
		}
	}
}