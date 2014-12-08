package drone_megaddon.communication;

public abstract class Command 
{
	protected int droneId;
	
	public Command(int droneId)
	{
		this.droneId = droneId;
	}
	
	public int getDroneId()
	{
		return droneId;
	}
}
