package googlemaps.intro;

import java.util.List;

import drone_megaddon.communication.CommunicationServer;
import drone_megaddon.communication.tx.CTLCommand;
import drone_megaddon.communication.tx.MVSCommand;
import drone_megaddon.ui_services.Drone;
import drone_megaddon.ui_services.MapService;

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

	private void handleTakeOffAndLand(boolean toggleState)
	{
		CommunicationServer server = mapActivity.getCommunicationServer();
		MapService mapService = mapActivity.getMapService();
		List<Drone> selectedDrones = mapService.getSelectedDrones();
		String command = "TAKEOFF";
		
		if (toggleState == false) {
			command = "LAND";
		}
		for (Drone d : selectedDrones) {
			server.queueTxCommand(new CTLCommand(d.getDroneId(), command));
		}
	}

	private void handleEmergency(boolean toggleState)
	{
		CommunicationServer server = mapActivity.getCommunicationServer();
		MapService mapService = mapActivity.getMapService();
		List<Drone> selectedDrones = mapService.getSelectedDrones();
		int emergency = 0;
		String command = "EMERGENCY";
		
		if (toggleState == true) {
			emergency = 1;
		}
		
		for (Drone d : selectedDrones) {
			server.queueTxCommand(new CTLCommand(d.getDroneId(), command, emergency));
		}
	}
	
	private void handleMove(String actionName)
	{
		CommunicationServer server = mapActivity.getCommunicationServer();
		MapService mapService = mapActivity.getMapService();
		List<Drone> selectedDrones = mapService.getSelectedDrones();
		String command = "MOVE";
		String direction = "DN";
		
		if (actionName.equals("move_up")) {
			direction = "UP";
		}
		
		for (Drone d : selectedDrones) {
			server.queueTxCommand(new MVSCommand(d.getDroneId(), command, direction));
		}
	}
	
	private void handleSpin()
	{
		CommunicationServer server = mapActivity.getCommunicationServer();
		MapService mapService = mapActivity.getMapService();
		List<Drone> selectedDrones = mapService.getSelectedDrones();
	
		for (Drone d : selectedDrones) {
			server.queueTxCommand(new MVSCommand(d.getDroneId(), "SPIN", "LT"));
		}
	}
	
	private void handleCommand(Action action, boolean toggleState)
	{
		String actionName = action.getActionName();
		
		if (actionName.equals("change_icon")) {
			onToggleIcon();
		}
		else if (actionName.equals("takeoff_land")) {
			handleTakeOffAndLand(toggleState);
		}
		else if (actionName.equals("move_up") || actionName.equals("move_down")) {
			handleMove(actionName);
		}
		else if (actionName.equals("spin_lockon")) {
			handleSpin();
		}
		else if (actionName.equals("emergency_noemergency")) {
			handleEmergency(toggleState);
		}
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
		
		handleCommand(action, toggleState[position]);
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
