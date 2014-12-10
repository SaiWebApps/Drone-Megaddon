package googlemaps.intro;

import java.util.ArrayList;
import java.util.List;

import googlemaps.intro.R;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class UibarFragment extends Fragment
{ 
	@Override
	public void onAttach(Activity a)
	{
		super.onAttach(a);
		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception
	}

	private List<Action> getActions()
	{
		List<Action> actionList = new ArrayList<Action>();

		actionList.add(new Action(R.drawable.sc2rain, "takeoff_land"));
		actionList.add(new Action(R.drawable.sc2xor, "emergency_noemergency"));
		actionList.add(new Action(R.drawable.sc2fire, "hover"));
		actionList.add(new Action(R.drawable.sc2arrowup, "move_up"));
		actionList.add(new Action(R.drawable.sc2arrowdown, "move_down"));
		actionList.add(new Action(R.drawable.sc2twistleftlit, "spin_lockon"));
		actionList.add(new Action(R.drawable.sc2target, "drone_deselect"));
		actionList.add(new Action(R.drawable.sc2diag, "change_icon"));

		return actionList;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		MapActivity activity = (MapActivity) getActivity();

		// Creating view corresponding to the fragment
		View v = inflater.inflate(R.layout.fragment_uibar, container, false);
		ListView actionListView = (ListView) v.findViewById(R.id.action_list);

		actionListView.setOnItemClickListener(new OnActionClickListener(activity));
		actionListView.setOnItemLongClickListener(new OnActionLongClickListener());

		List<Action> actionList = getActions();
		ActionAdapter adapter = new ActionAdapter(v.getContext(), actionList);
		actionListView.setAdapter(adapter);

		return v;
	}
}