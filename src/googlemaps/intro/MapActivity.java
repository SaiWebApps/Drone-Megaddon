package googlemaps.intro;

import com.google.android.gms.maps.MapsInitializer;

import googlemaps.services.MapReceiverServer;
import googlemaps.services.MapService;
import android.app.Activity;
import android.os.Bundle;

public class MapActivity extends Activity 
{
	private MapService mapService;
	private MapReceiverServer mapServer;

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		MapsInitializer.initialize(getApplicationContext());
	}

	@Override
	public void onResume() 
	{
		super.onResume();
		mapService = new MapService(this);
		if (mapServer == null) {
			mapServer = new MapReceiverServer(mapService);
			mapServer.start();
		}
	}

	@Override
	public void onPause() 
	{
		mapService.releaseResources();
		if (mapServer != null) {
			mapServer.close();
			mapServer = null;
		}
		super.onPause();
	}
}