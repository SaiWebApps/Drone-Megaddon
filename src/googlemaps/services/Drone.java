package googlemaps.services;

import googlemaps.intro.R;
import android.os.Handler;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

public class Drone 
{
	private enum Dir {
		upLeft,
		upRight,
		downLeft,
		downRight,
		up,
		down,
		right,
		left
	};
	
	Dir direction = Dir.up;
	private final int DEFAULT_DRONE_ID = R.drawable.drone;
	private final int SELECTED_DRONE_ID = R.drawable.selected_drone;
	private final int UPLEFT_DRONE_ID = R.drawable.ul_drone;
	private final int UPRIGHT_DRONE_ID = R.drawable.ur_drone;
	private final int DOWNLEFT_DRONE_ID = R.drawable.dl_drone;
	private final int DOWNRIGHT_DRONE_ID = R.drawable.dr_drone;
	private final int UP_DRONE_ID = R.drawable.up_drone;
	private final int DOWN_DRONE_ID = R.drawable.down_drone;
	private final int LEFT_DRONE_ID = R.drawable.left_drone;
	private final int RIGHT_DRONE_ID = R.drawable.right_drone;
	
	private final int SEL_UPLEFT_DRONE_ID = R.drawable.ul_drone;
	private final int SEL_UPRIGHT_DRONE_ID = R.drawable.ur_drone;
	private final int SEL_DOWNLEFT_DRONE_ID = R.drawable.dl_drone;
	private final int SEL_DOWNRIGHT_DRONE_ID = R.drawable.dr_drone;
	private final int SEL_UP_DRONE_ID = R.drawable.up_drone;
	private final int SEL_DOWN_DRONE_ID = R.drawable.down_drone;
	private final int SEL_LEFT_DRONE_ID = R.drawable.left_drone;
	private final int SEL_RIGHT_DRONE_ID = R.drawable.right_drone;
	
	private final BitmapDescriptor DEFAULT_DRONE_IMG = BitmapDescriptorFactory.fromResource(DEFAULT_DRONE_ID);
	private final BitmapDescriptor SELECTED_DRONE_IMG = BitmapDescriptorFactory.fromResource(SELECTED_DRONE_ID);
	private final BitmapDescriptor[] DEFAULT_DRONE_IMGS = new BitmapDescriptor[8];
	private final BitmapDescriptor[] SELECTED_DRONE_IMGS = new BitmapDescriptor[8];
	
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
		this.DEFAULT_DRONE_IMGS[Dir.upLeft.ordinal()] = BitmapDescriptorFactory.fromResource(UPLEFT_DRONE_ID);
		this.DEFAULT_DRONE_IMGS[Dir.upRight.ordinal()] = BitmapDescriptorFactory.fromResource(UPRIGHT_DRONE_ID);
		this.DEFAULT_DRONE_IMGS[Dir.downLeft.ordinal()] = BitmapDescriptorFactory.fromResource(DOWNLEFT_DRONE_ID);
		this.DEFAULT_DRONE_IMGS[Dir.downRight.ordinal()] = BitmapDescriptorFactory.fromResource(DOWNRIGHT_DRONE_ID);
		this.DEFAULT_DRONE_IMGS[Dir.up.ordinal()] = BitmapDescriptorFactory.fromResource(UP_DRONE_ID);
		this.DEFAULT_DRONE_IMGS[Dir.down.ordinal()] = BitmapDescriptorFactory.fromResource(DOWN_DRONE_ID);
		this.DEFAULT_DRONE_IMGS[Dir.left.ordinal()] = BitmapDescriptorFactory.fromResource(LEFT_DRONE_ID);
		this.DEFAULT_DRONE_IMGS[Dir.right.ordinal()] = BitmapDescriptorFactory.fromResource(RIGHT_DRONE_ID);
		
		this.SELECTED_DRONE_IMGS[Dir.upLeft.ordinal()] = BitmapDescriptorFactory.fromResource(SEL_UPLEFT_DRONE_ID);
		this.SELECTED_DRONE_IMGS[Dir.upRight.ordinal()] = BitmapDescriptorFactory.fromResource(SEL_UPRIGHT_DRONE_ID);
		this.SELECTED_DRONE_IMGS[Dir.downLeft.ordinal()] = BitmapDescriptorFactory.fromResource(SEL_DOWNLEFT_DRONE_ID);
		this.SELECTED_DRONE_IMGS[Dir.downRight.ordinal()] = BitmapDescriptorFactory.fromResource(SEL_DOWNRIGHT_DRONE_ID);
		this.SELECTED_DRONE_IMGS[Dir.up.ordinal()] = BitmapDescriptorFactory.fromResource(SEL_UP_DRONE_ID);
		this.SELECTED_DRONE_IMGS[Dir.down.ordinal()] = BitmapDescriptorFactory.fromResource(SEL_DOWN_DRONE_ID);
		this.SELECTED_DRONE_IMGS[Dir.left.ordinal()] = BitmapDescriptorFactory.fromResource(SEL_LEFT_DRONE_ID);
		this.SELECTED_DRONE_IMGS[Dir.right.ordinal()] = BitmapDescriptorFactory.fromResource(SEL_RIGHT_DRONE_ID);
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
		refreshDroneIcon(null, true);
	}
	
	public void refreshDroneIcon(Dir newDirection, boolean select)
	{
		Dir setDirection;
		// Use current direction if newDirection not specified
		if(newDirection == null) {
			setDirection = direction;
		} else {
			setDirection = newDirection;
		}
		
		this.isSelected = !this.isSelected;
		if (this.isSelected()) {
			this.currentLocationMarker.setIcon(SELECTED_DRONE_IMGS[setDirection.ordinal()]);
		} else {
			this.currentLocationMarker.setIcon(DEFAULT_DRONE_IMGS[setDirection.ordinal()]);
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
