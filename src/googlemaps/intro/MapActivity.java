package googlemaps.intro;

import java.util.List;

import com.google.android.gms.maps.MapsInitializer;

import drone_megaddon.communication.CommunicationServer;
import drone_megaddon.ui_services.Drone;
import drone_megaddon.ui_services.MapService;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.internal.widget.ListPopupWindow;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MapActivity extends Activity 
{
	private final int DRONE_MENU_ID = R.id.action_show_selected_drones;
	
	private MapService mapService;
	private CommunicationServer serialCommService;

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);

		MapsInitializer.initialize(getApplicationContext());
		mapService = new MapService(this);
		serialCommService = new CommunicationServer(this);
		serialCommService.openUSBSerial();
	}

	@Override
	public void onDestroy() 
	{
		serialCommService.closeUSBSerial();
		mapService.releaseResources();
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * When the user clicks on the rightmost "menu" button
	 * in the ActionBar, display a dropdown menu with the list
	 * of selected drones.
	 * @param anchor - The "menu" button that the dropdown list
	 * will be anchored/attached to
	 */
	public void showSelectedDrones(MenuItem anchor) 
	{
		// Show nothing if no drones have been selected.
		if (mapService.getSelectedDrones().isEmpty()) {
			return;
		}
		
		ListPopupWindow droneMenu = new ListPopupWindow(this);
		SelectedDroneAdapter adapter = new SelectedDroneAdapter(this);
		
		droneMenu.setAnchorView(findViewById(DRONE_MENU_ID));
		droneMenu.setAdapter(adapter);
		droneMenu.setWidth(300);
		droneMenu.setWidth(400);
		droneMenu.setModal(true);
		droneMenu.setOnItemClickListener(new DroneMenuItemClickListener());
		droneMenu.show();
	}

	/**
	 * Utility function to display a Toast with the given message.
	 * @param message - Message to display in Toast
	 */
	public void showToast(String message)
	{
		Toast t = Toast.makeText(getApplicationContext(), 
				message, Toast.LENGTH_SHORT);
		t.show();
	}

	/**
	 * @return the MapService that handles this Activity's UI tasks
	 */
	public MapService getMapService()
	{
		return mapService;
	}

	/**
	 * @return the CommunicationServer that handles this Activity's
	 * USB-serial interactions with an attached Firefly node
	 */
	public CommunicationServer getCommunicationServer()
	{
		return serialCommService;
	}
	
	/**
	 * Used to populate the select drones menu with the list of selected
	 * drones; each menu item consists of a drone icon and the drone id.
	 */
	private class SelectedDroneAdapter extends BaseAdapter
	{
		private List<Drone> selectedDrones;
		private Context appContext;
		
		public SelectedDroneAdapter(MapActivity activity)
		{
			selectedDrones = activity.getMapService().getSelectedDrones();
			appContext = activity.getApplicationContext();
		}
		
		@Override
		public int getCount() 
		{
			return selectedDrones.size();
		}

		@Override
		public Object getItem(int position) 
		{
			return selectedDrones.get(position);
		}

		@Override
		public long getItemId(int position) 
		{
			return position;
		}

		@Override
		public View getView(int position, View listItem, ViewGroup parent) 
		{
			// drone_menu_item.xml specifies the structure of a menu item.
			// Pull that into the parent (a ListPopupWindow).
			LayoutInflater inflater = (LayoutInflater) appContext.getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			listItem = inflater.inflate(R.layout.drone_menu_item, parent, false);
			
			// Show the right selected-drone icon (SC2 or default) + drone id + coordinates.
			Drone selectedDrone = selectedDrones.get(position);
			ImageView droneIcon = (ImageView) listItem.findViewById(R.id.item_drone_icon);
			TextView droneId = (TextView) listItem.findViewById(R.id.item_drone_id);
			
			droneIcon.setImageResource(Drone.getImageId());
			droneId.setText("" + selectedDrone.getDroneId());

			return listItem;
		}
	}
	
	/**
	 * When the user clicks on an item in the "Selected Drones" menu 
	 * in the action bar, de-select the corresponding drone.
	 */
	private class DroneMenuItemClickListener implements OnItemClickListener
	{
		public DroneMenuItemClickListener() {}
		
		@Override
		public void onItemClick(AdapterView<?> menuAdapter, View clickedItem, 
				int pos, long id) 
		{
			Drone drone = (Drone) menuAdapter.getItemAtPosition(pos);
			drone.toggleSelect();
		}
	}
}