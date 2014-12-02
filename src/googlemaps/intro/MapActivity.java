package googlemaps.intro;

import com.google.android.gms.maps.MapsInitializer;

import drone_megaddon.communication.CommunicationServer;
import drone_megaddon.ui_services.MapService;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

public class MapActivity extends Activity 
{
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