package googlemaps.intro;

import googlemaps.intro.R;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout;

public class OnActionClickListener implements OnItemClickListener 
{
	public int[] mActionsOff = new int[] {
			R.drawable.sc2land_or,
			R.drawable.sc2xor,
			R.drawable.sc2firering,
			R.drawable.sc2twistleftlit,
			R.drawable.sc2twistrightlit,
			R.drawable.sc2rain,
			R.drawable.sc2missiles,
			R.drawable.sc2crosshairs,
	};
	
	public int[] mActionsOn = new int[] {
			R.drawable.sc2rain,
			R.drawable.sc2xor_or,
			R.drawable.sc2fire,
			R.drawable.sc2twistleftlit,
			R.drawable.sc2twistrightlit,
			R.drawable.sc2bunker,
			R.drawable.sc2bunker,
			R.drawable.sc2cross,
	};
	
	boolean[] toggleState = new boolean[mActionsOff.length];
	
	public OnActionClickListener() {
		
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
	}
}
