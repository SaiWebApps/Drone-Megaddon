package drone_megaddon.communication.tx;

import java.util.Arrays;

import drone_megaddon.communication.Command;

public abstract class TxCommand extends Command
{
	private final String DELIMITER = ",";
	private final byte TERMINATING_BYTE = -1; // Indicates end of message
	
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
	 * @return the bytes of the message to send to the drone;
	 * the result byte array will be terminated with TERMINATING_BYTE (-1)
	 */
	public byte[] getMessageBytes()
	{
		byte[] messageBytes = getMessage().getBytes();
		int numBytes = messageBytes.length;
		
		messageBytes = Arrays.copyOf(messageBytes, numBytes + 1);
		messageBytes[numBytes] = TERMINATING_BYTE;
		
		return messageBytes;
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
