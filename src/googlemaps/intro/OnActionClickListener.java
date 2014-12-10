package googlemaps.intro;

import java.util.List;

import drone_megaddon.ui_services.Drone;

import googlemaps.intro.R;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout;

public class OnActionClickListener implements OnItemClickListener 
{
	private MapActivity mapActivity; 
	public int[] mActionsOff = new int[] {
			R.drawable.sc2rain,
			R.drawable.sc2xor,
			R.drawable.sc2fire,
			R.drawable.sc2arrowup,
			R.drawable.sc2arrowdown,
			R.drawable.sc2twistleftlit,
			R.drawable.sc2target,
			R.drawable.sc2diag,
	};
	
	public int[] mActionsOn = new int[] {
			R.drawable.sc2land_or,
			R.drawable.sc2xor_or,
			R.drawable.sc2firering,
			R.drawable.sc2arrowup_or,
			R.drawable.sc2arrowdown_or,
			R.drawable.sc2twist_or,
			R.drawable.sc2targetlit,
			R.drawable.sc2diag_or,
	};
	
	boolean[] toggleState = new boolean[mActionsOff.length];

	public OnActionClickListener(MapActivity mapActivity) {
		this.mapActivity = mapActivity;
	}
	
	@Override
	public void onItemClick(AdapterView<?> av, View v, int position, long id) {
		Action action = (Action) av.getItemAtPosition(position);
		RelativeLayout rl = (RelativeLayout) v;
		ImageView iv = (ImageView) rl.findViewById(R.id.command_image);
		toggleState[position] = !toggleState[position];
		if (toggleState[position] == false) {
			iv.setImageResource(mActionsOff[position]);
		} else {
			iv.setImageResource(mActionsOn[position]);	
		}
		
		if(action.getActionName().equals("changeicon")) {
			onToggleIcon();
		}
	}

	// Toggle icon states
	public void onToggleIcon() {
		List <Drone> droneList = mapActivity.getMapService().getAllDrones();
		Drone d;
		Drone.isSC2 = !Drone.isSC2;
		for(int i = 0; i < droneList.size(); ++i) {		
			d = droneList.get(i);
			d.refreshDroneIcon();
		}
	}
}
