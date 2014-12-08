package drone_megaddon.communication.rx;

import com.google.android.gms.maps.model.LatLng;


/**
 * @author Sairam Krishnan
 */
public class RxCommandFactory 
{
	private final String MSG_DELIMITER = ",";
	
	private final String GPS = "$RADGPS";
	private final int GPS_COMMAND_LEN = 6;
	
	/**
	 * @param num - String to convert to an integer
	 * @return an Integer containing the number in the given String;
	 * null if the String cannot be converted to an Integer
	 */
	private Integer convertStringToInteger(String num)
	{
		try {
			return Integer.parseInt(num);
		} catch (NumberFormatException e) {
			return null;
		}
	}
	
	/**
	 * @param num - String to convert to a Long
	 * @return an Long containing the number in the given String;
	 * null if the String cannot be converted to a Long
	 */
	private Long convertStringToLong(String num)
	{
		try {
			return Long.parseLong(num);
		} catch (NumberFormatException e) {
			return null;
		}
	}
	
	/**
	 * @param tokens - GPS information ($RADGPS, droneId, latitude, longitude)
	 * @return a GPSCommand with the specified information
	 * and a plan of action
	 */
	private RxCommand createGPSCommand(String[] tokens)
	{
		if (tokens.length != GPS_COMMAND_LEN) {
			return null;
		}
		
		Integer droneId = convertStringToInteger(tokens[1]);
		Long latitude = convertStringToLong(tokens[2]);
		Long longitude = convertStringToLong(tokens[3]);
		Long altitude = convertStringToLong(tokens[4]);
		Long battery = convertStringToLong(tokens[5]);
		
		// If any of the fields are empty/invalid, do nothing.
		if (droneId == null || latitude == null || longitude == null
				|| altitude == null || battery == null) {
			return null;
		}
		// Otherwise, construct GPSCommand using given fields.
		double lat = latitude / 1000000.0;
		double lon = longitude / 1000000.0;	
		return new GPSCommand(droneId, new LatLng(lat, lon), altitude, battery);
	}
	
	/**
	 * @param receivedInfo - Information received from the drone
	 * @return a RxCommand with the parameters in receivedInfo and a plan of action
	 */
	public RxCommand create(String receivedInfo)
	{
		String[] tokens = receivedInfo.split(MSG_DELIMITER);
		if (tokens.length == 0) {
			return null;
		}
		
		String command = tokens[0].trim();
		if (command.contains(GPS)) {
			return createGPSCommand(tokens);
		}
		return null;
	}
}
