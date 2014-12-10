package drone_megaddon.ui_services;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;

import googlemaps.intro.MapActivity;
import googlemaps.intro.R;
import android.content.Context;
import android.os.Handler;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import drone_megaddon.communication.CommunicationServer;
import drone_megaddon.communication.rx.RxCommand;
import drone_megaddon.communication.tx.FlyCommand;

/**
 * Handles all UI-related activities on the DroneMegaddon Google Map.
 */
public class MapService implements GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener
{	
	private final long COMMAND_PROCESSING_PERIOD = 100; // ms
	private final float DEST_MARKER_COLOR = BitmapDescriptorFactory.HUE_GREEN;
	private final MarkerOptions destinationMarkerOptions = new MarkerOptions();

	private SparseArray<Drone> droneMap = new SparseArray<Drone>();

	private Handler commandHandler;
	private LinkedBlockingDeque<RxCommand> pendingCommands;

	private GoogleMap googleMap;
	private Marker destinationMarker;

	private MapActivity mapActivity;

	public MapService(MapActivity mapActivity) 
	{
		this.mapActivity = mapActivity;
		this.destinationMarkerOptions.icon(BitmapDescriptorFactory.
				defaultMarker(DEST_MARKER_COLOR));
		this.commandHandler = new Handler();
		this.pendingCommands = new LinkedBlockingDeque<RxCommand>();

		initGoogleMap();
		commandHandler.post(new CommandProcessor());
	}

	/**
	 * Initialize the Google Map.
	 */
	private void initGoogleMap()
	{
		this.googleMap = ((MapFragment) mapActivity.getFragmentManager().
				findFragmentById(R.id.map)).getMap();
		googleMap.setMyLocationEnabled(true);
		googleMap.setBuildingsEnabled(true);
		googleMap.setIndoorEnabled(true);
		googleMap.setOnMapClickListener(this);
		googleMap.setOnMarkerClickListener(this);
		googleMap.setInfoWindowAdapter(new InfoWindowAdapter() {

			@Override
			public View getInfoWindow(Marker marker) 
			{
				// Getting view from the layout file
				LayoutInflater inflater = (LayoutInflater) mapActivity
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				View v = inflater.inflate(R.layout.marker_text, null);

				TextView title = (TextView) v.findViewById(R.id.marker_title);
				title.setText(marker.getTitle());

				return v;
			}

			@Override
			public View getInfoContents(Marker arg0) 
			{
				return null;
			}
		});
	}

	/**
	 * Add the specified command to the pendingCommands queue.
	 * @param command - RxCommand to queue; cannot be null
	 */
	public void queueRxCommand(RxCommand command)
	{
		pendingCommands.add(command);
	}

	/**
	 * Create a new drone, or update an existing drone's location.
	 * @param droneId - Id of the drone being created/updated
	 * @param location - New location on the map
	 * @return the Drone that was either added to the map or relocated on the map
	 */
	public Drone addDrone(int droneId, LatLng location)
	{
		Drone target = droneMap.get(droneId);
		if (target == null) {
			target = new Drone(googleMap, droneId);
		}
		target.addToMap(location);
		droneMap.put(droneId, target);
		return target;
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

	/**
	 * @return a list of all Drones on the map
	 */
	public List<Drone> getAllDrones() {
		List<Drone> drones = new ArrayList<Drone>();
		for (int i = 0; i < droneMap.size(); i++) {
			int key = droneMap.keyAt(i);
			Drone d = droneMap.get(key);
			if (d != null) {
				drones.add(d);
			}
		}
		return drones;
	}

	/**
	 * @return a list of Drones that have been selected on the map
	 */
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

	/**
	 * Destroy map and release all resources.
	 */
	public void releaseResources() 
	{
		googleMap = null;		
		mapActivity = null;
	}

	@Override
	public boolean onMarkerClick(Marker clickedMarker) {
		// Show the drone's identifying information (Drone droneId).
		//		clickedMarker.showInfoWindow();

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
		// If no drones were selected, then do nothing.
		if (selectedDrones.isEmpty()) {
			addDrone(42, clickedLocation); //DHT: Crazy test drone!!
			return;
		}

		CommunicationServer server = mapActivity.getCommunicationServer();
		if (selectedDrones.size() == 1) {
			Drone d = selectedDrones.get(0);
			server.queueTxCommand(new FlyCommand(d.getDroneId(), clickedLocation, 1.0));
		}
		else {
			double count = 0;
			
			for (Drone d : selectedDrones) {
				LatLng approxLoc = new LatLng(clickedLocation.latitude + (count / 1000), 
						clickedLocation.longitude + (count / 1000));
				count++;
				server.queueTxCommand(new FlyCommand(d.getDroneId(), approxLoc, 1.0));
			}
		}

		// If at least 1 drone was selected, place a marker at the destination,
		// and then move the drone to that location.
		destinationMarker = googleMap.addMarker(destinationMarkerOptions.
				position(clickedLocation));

		for (Drone selected : selectedDrones) {
			selected.moveToDestMarker(destinationMarker);
		}
	}

	/**
	 * Used to poll the pendingCommands queue every COMMAND_PROCESSING_PERIOD
	 * ms, and process any commands on the queue.
	 */
	private class CommandProcessor implements Runnable
	{
		public CommandProcessor() {}

		@Override
		public void run()
		{
			// If commands queue is not empty, dequeue a command,
			// and execute it.
			if (!pendingCommands.isEmpty()) {
				RxCommand cmd = pendingCommands.removeFirst();
				// MapService.this is used to refer to the outer
				// class's "this."
				cmd.execute(MapService.this);
			}
			// Repeat in 100 ms.
			commandHandler.postDelayed(CommandProcessor.this, 
					COMMAND_PROCESSING_PERIOD);
		}
	}
}