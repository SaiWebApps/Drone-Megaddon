package googlemaps.intro;

import com.google.android.gms.maps.MapsInitializer;

import drone_megaddon.communication.CommunicationServer;
import drone_megaddon.ui_services.MapService;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.internal.widget.ListPopupWindow;
import android.view.Menu;
import android.view.MenuItem;
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
		droneMenu.setAnchorView(findViewById(DRONE_MENU_ID));
		droneMenu.setAdapter(new SelectedDroneAdapter(this));
		droneMenu.setWidth(300);
		droneMenu.setWidth(400);
		droneMenu.setModal(true);
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
}