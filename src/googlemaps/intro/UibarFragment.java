package googlemaps.intro;

import java.util.ArrayList;
import java.util.List;

import googlemaps.intro.R;
import googlemaps.services.MapService;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.view.View.OnLongClickListener;
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

		actionList.add(new Action(R.drawable.sc2land_or, "takeoff_land"));
		actionList.add(new Action(R.drawable.sc2xor, "emergency_shutdown"));
		actionList.add(new Action(R.drawable.sc2firering, "hover"));
		actionList.add(new Action(R.drawable.sc2twistleftlit, "spinleft"));
		actionList.add(new Action(R.drawable.sc2twistrightlit, "spinright"));
		actionList.add(new Action(R.drawable.sc2rain, "flyup"));
		actionList.add(new Action(R.drawable.sc2missiles, "flydown"));
		actionList.add(new Action(R.drawable.sc2atomicboom, "changeicon"));

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