package drone_megaddon.communication.tx;

import com.google.android.gms.maps.model.LatLng;

public class FlyCommand extends TxCommand
{
    private int droneId;
    private LatLng target;
    private double altitude;
    
    public FlyCommand(int droneId, LatLng target, double altitude)
    {
    	super(droneId);
        this.target = target;
        this.altitude = altitude;
        constructMessage();
    }
    
    @Override
    public String getCommandPrefix()
    {
    	return "$RADFLY";
    }
    
    /**
     * Create RADFLY command string with following format:
     * $RADFLY,<droneid>,<latitude>,<longitude>,<altitude>
     */
    @Override
    public void constructMessage()
    {
    	addField(droneId);
    	addField(target.latitude);
    	addField(target.longitude);
    	addField(altitude);
    }
}