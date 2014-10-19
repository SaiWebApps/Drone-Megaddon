package googlemaps.intro;

public class Action 
{
	private int imageId;
	private String actionName;

	public Action(int imageId, String actionName)
	{
		this.setImageId(imageId);
		this.setActionName(actionName);
	}

	public int getImageId() {
		return imageId;
	}

	public void setImageId(int imageId) {
		this.imageId = imageId;
	}

	public String getActionName() {
		return actionName;
	}

	public void setActionName(String actionName) {
		this.actionName = actionName;
	}
}
