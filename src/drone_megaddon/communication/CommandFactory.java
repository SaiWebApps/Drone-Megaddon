package drone_megaddon.communication;

import com.google.android.gms.maps.model.LatLng;

/**
 * @author Sairam Krishnan
 */
public class CommandFactory 
{
	private final String MSG_DELIMITER = ",";
	
	private final String GPS = "$RADGPS";
	private final int GPS_COMMAND_LEN = 4;
	
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
	 * @param num - String to convert to a double
	 * @return a Double containing the number in the given String;
	 * null if the String cannot be converted to a Double
	 */
	private Double convertStringToDouble(String num)
	{
		try {
			return Double.parseDouble(num);
		} catch (NumberFormatException e) {
			return null;
		}
	}
	
	/**
	 * @param tokens - GPS information ($RADGPS, droneId, latitude, longitude)
	 * @return a GPSCommand with the specified information
	 * and a plan of action
	 */
	private Command createGPSCommand(String[] tokens)
	{
		if (tokens.length != GPS_COMMAND_LEN) {
			return null;
		}
		
		Integer droneId = convertStringToInteger(tokens[1]);
		Double latitude = convertStringToDouble(tokens[2]);
		Double longitude = convertStringToDouble(tokens[3]);
		
		if (droneId == null || latitude == null || longitude == null) {
			return null;
		}
		return new GPSCommand(droneId, new LatLng(latitude, longitude));
	}
	
	/**
	 * @param receivedInfo - Information received from the drone
	 * @return a Command with the parameters in receivedInfo and a plan of action
	 */
	public Command create(String receivedInfo)
	{
		String[] tokens = receivedInfo.split(MSG_DELIMITER);
		if (tokens.length == 0) {
			return null;
		}
		
		String command = tokens[0];
		if (command == GPS) {
			return createGPSCommand(tokens);
		}
		return null;
	}
}
