package googlemaps.services;

import android.graphics.Point;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
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
		sourceMarker.setRotation(0);
		sourceMarker.setRotation(computeAngle(source, dest));
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
	
	private float computeAngle(Marker source, Marker dest) {
		double x1 = source.getPosition().longitude;
		double y1 = source.getPosition().latitude;
		double x2 = dest.getPosition().longitude;
		double y2 = dest.getPosition().latitude;
		double xdiff = x2 - x1;
		double ydiff = y2 - y1;
		double angle = Math.toDegrees(Math.atan(Math.abs(ydiff)/Math.abs(xdiff)));
		
		if (ydiff < 0 && xdiff < 0) {
			Log.e("Q", "uad3");
			angle = 270 - angle;            // Quad 3
		} else if (ydiff > 0 && xdiff < 0) {
			Log.e("Q", "uad2");
			Log.e("OrigAng", "" + angle);
			angle += 270;                   // Quad 2
		} else if (ydiff < 0 && xdiff > 0) {
			Log.e("Q", "uad4");
			angle += 90;                    // Quad 4
		} else {
			Log.e("Q", "uad1");
			angle = 90 - angle;             // Quad 1
		}

		return (float) angle;
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