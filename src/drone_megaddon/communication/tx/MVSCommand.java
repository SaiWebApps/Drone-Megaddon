package drone_megaddon.communication.tx;

public class MVSCommand extends TxCommand
{
	private int droneId;
	private String command;
	private String direction;
	private final double rate = 0.2; // -1.0 < yaw value < 1.0
	private final double time = 500; // ms to run the move/spin command

	/**
	 * Expected arguments:
	 * command: MOVE or SPIN
	 * direction: left, right, forward, backward 
	 */
	public MVSCommand(int droneId, String command, String direction)
	{
		super(droneId);
		this.command = command;
		this.direction = direction;
		constructMessage();
	}

	@Override
	public String getCommandPrefix()
	{
		return "$RADMVS";
	}
	
	public boolean validateParameters()
	{
		if (!command.equals("MOVE") && !command.equals("SPIN")) {
			return false;
		}
		if (command.equals("MOVE") && 
				!direction.equals("LT") && 
				!direction.equals("RT") &&
				!direction.equals("UP") &&
				!direction.equals("DN") &&
				!direction.equals("FW") &&
				!direction.equals("BW")) {
			return false;
		}
		if (command.equals("SPIN") &&
				!direction.equals("LT") &&
				!direction.equals("RT")) {
			return false;
		}
		return true;
	}
	
	/**
	 * Create RADMVS command string with following format:
	 * $RADMVS,<droneid>,<MOVE|SPIN>,<UP|DN|LT|RT|FW|BW>
	 */
	@Override
	public void constructMessage() 
	{
		if (!validateParameters()) {
			return;
		}

		addField(droneId);
		addField(command);
		addField(direction);
		addField(rate);
		addField(time);
	}
}
