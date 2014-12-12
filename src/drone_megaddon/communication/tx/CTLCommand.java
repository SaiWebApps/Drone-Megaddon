package drone_megaddon.communication.tx;

public class CTLCommand extends TxCommand
{
    private String controlCommand;
    private int emergencyState;
    
    public CTLCommand(int droneId, String command)
    {
    	super(droneId);
        this.controlCommand = command;
        constructMessage();
    }
    
    public CTLCommand(int droneId, String command, int emergency)
    {
    	super(droneId);
    	this.controlCommand = command;
        this.emergencyState = emergency;
        constructMessage();
    }
    
    /**
     * @return true if the control command is valid, false otherwise
     */
    public boolean validateParameters()
    {
    	return controlCommand.equals("TAKEOFF") || controlCommand.equals("LAND")
    			|| controlCommand.equals("EMERGENCY");
    }
    
    @Override
    public String getCommandPrefix()
    {
    	return "$RADCTL";
    }
    
    /**
     * Create RADCTL command string with following format:
     * $RADCTL,<droneid>,<command>
     */
    @Override
    public void constructMessage() 
    {
        if (!validateParameters()) {
            return;
        }
        
        addField(droneId);
        addField(controlCommand);
        
        if (controlCommand.equals("EMERGENCY")) {
        	addField(emergencyState);
        }
    }
}
