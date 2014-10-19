package googlemaps.intro;

import java.util.ArrayList;
import java.util.List;

import googlemaps.intro.R;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
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
	}

	private List<Action> getActions()
	{
		List<Action> actionList = new ArrayList<Action>();
		
		actionList.add(new Action(R.drawable.sc2arrow, ""));
		actionList.add(new Action(R.drawable.sc2bunker, ""));
		actionList.add(new Action(R.drawable.sc2cross, ""));
		actionList.add(new Action(R.drawable.sc2drill, ""));
		actionList.add(new Action(R.drawable.sc2target, ""));
		actionList.add(new Action(R.drawable.sc2wrenchs, ""));
		actionList.add(new Action(R.drawable.sc2xor, ""));
		
		return actionList;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		// Creating view corresponding to the fragment
		View v = inflater.inflate(R.layout.fragment_uibar, container, false);
		ListView actionListView = (ListView) v.findViewById(R.id.action_list);
		
		List<Action> actionList = getActions();
		ActionAdapter adapter = new ActionAdapter(v.getContext(), actionList);
		actionListView.setAdapter(adapter);
		
		return v;
	}
}