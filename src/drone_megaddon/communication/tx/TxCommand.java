package drone_megaddon.communication.tx;

import drone_megaddon.communication.Command;

public abstract class TxCommand extends Command
{
	private final String DELIMITER = ",";
	
	private StringBuffer messageBuffer = new StringBuffer();

	public TxCommand(int droneId) 
	{
		super(droneId);
		messageBuffer.append(getCommandPrefix());
	}
	
	/**
	 * Add the specified object to the collection of fields to send to the drone.
	 * @param newField - Field to add to the message to send to the drone
	 */
	public void addField(Object newField)
	{
		messageBuffer.append(DELIMITER);
		messageBuffer.append(newField);
	}
	
	/**
	 * @return the message to send to the drone
	 */
	public String getMessage()
	{
		return messageBuffer.toString();
	}
	
	/**
	 * @return the first field in the message being sent to the drone (the
	 * command code)
	 */
	public abstract String getCommandPrefix();
	
	/**
	 * Using the specified information, construct the message to send to the drone.
	 */
	public abstract void constructMessage();
}
