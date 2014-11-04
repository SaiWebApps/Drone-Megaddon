package googlemaps.intro;

import com.google.android.gms.maps.MapsInitializer;

import googlemaps.services.CommunicationServer;
import googlemaps.services.MapService;
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

	public void showToast(String message)
	{
		Toast t = Toast.makeText(getApplicationContext(), 
				message, Toast.LENGTH_SHORT);
		t.show();
	}
	
	public void notifyMapService(String receivedInformation)
	{
		mapService.saveReceivedInfo(receivedInformation);
	}
	
	@Override
	public void onDestroy() 
	{
		serialCommService.closeUSBSerial();
		mapService.releaseResources();
		super.onDestroy();
	}
	
	// Progress Bar fragment update 
	public void onAltitudeChange(double altitude) {
		ProgbarFragment progFrag = (ProgbarFragment)
                getFragmentManager().findFragmentById(R.id.progbar_fragment);
		progFrag.setAltProgress(altitude);
		
	}
}