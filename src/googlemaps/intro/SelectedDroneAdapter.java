package googlemaps.intro;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import drone_megaddon.ui_services.Drone;

public class SelectedDroneAdapter extends BaseAdapter
{
	private List<Drone> selectedDrones;
	private Context appContext;
	
	public SelectedDroneAdapter(MapActivity activity)
	{
		selectedDrones = activity.getMapService().getSelectedDrones();
		appContext = activity.getApplicationContext();
	}
	
	@Override
	public int getCount() 
	{
		return selectedDrones.size();
	}

	@Override
	public Object getItem(int position) 
	{
		return selectedDrones.get(position);
	}

	@Override
	public long getItemId(int position) 
	{
		return position;
	}

	@Override
	public View getView(int position, View listItem, ViewGroup parent) 
	{
		// drone_menu_item.xml specifies the structure of a menu item.
		// Pull that into the parent (a ListPopupWindow).
		LayoutInflater inflater = (LayoutInflater) appContext.getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		listItem = inflater.inflate(R.layout.drone_menu_item, parent, false);
		
		// Show the right selected-drone icon (SC2 or default) + drone id + coordinates.
		Drone selectedDrone = selectedDrones.get(position);
		ImageView droneIcon = (ImageView) listItem.findViewById(R.id.item_drone_icon);
		TextView droneId = (TextView) listItem.findViewById(R.id.item_drone_id);
		
		droneIcon.setImageResource(Drone.getImageId());
		droneId.setText("" + selectedDrone.getDroneId());
		
		return listItem;
	}
}