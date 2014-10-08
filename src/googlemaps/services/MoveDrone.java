package googlemaps.services;

import android.graphics.Point;
import android.os.Handler;
import android.os.SystemClock;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class MoveDrone implements Runnable
{
	private final int HANDLER_PERIOD = 16;
	private final int DURATION = 10000;
	private final Interpolator linearInterpolator = new LinearInterpolator();
	
	private long startTime;
	
	private Handler movementHandler;
	private Marker sourceMarker;
	private Marker destinationMarker;
	private LatLng startLatLng;
	private LatLng destLatLng;
	
	public MoveDrone(GoogleMap map, Handler handler, Marker source, Marker dest)
	{
		this.movementHandler = handler;
		this.sourceMarker = source;
		this.destinationMarker = dest;
		initialize(map);
	}
	
	private void initialize(GoogleMap map)
	{
		this.startTime = SystemClock.uptimeMillis();
		Projection proj = map.getProjection();
		Point startPoint = proj.toScreenLocation(sourceMarker.getPosition());
		Point endPoint = proj.toScreenLocation(destinationMarker.getPosition());
		this.startLatLng = proj.fromScreenLocation(startPoint);
		this.destLatLng = proj.fromScreenLocation(endPoint);
	}
	
	@Override
	public void run() 
	{
		long elapsed = SystemClock.uptimeMillis() - startTime;
	    float t = linearInterpolator.getInterpolation((float) elapsed / DURATION);
        double lat = t * destLatLng.latitude + (1 - t) * startLatLng.latitude;
	    double lng = t * destLatLng.longitude + (1 - t) * startLatLng.longitude;
        sourceMarker.setPosition(new LatLng(lat, lng));
        
        // We have reached the target when t = 1.
        // t < 1 -> undershoot, so continue moving towards the destinationMarkerOptions
        if (t < 1.0) {
        	movementHandler.postDelayed(this, HANDLER_PERIOD);
        }
        // t >= 1 -> reached destinationMarkerOptions (or overshot it), so stop moving
        else {
        	// Remove destinationMarkerOptions marker from map once source marker reaches/passes it.
        	destinationMarker.remove();
        }
	}
}